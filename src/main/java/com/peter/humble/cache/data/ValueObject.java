package com.peter.humble.cache.data;

import com.peter.humble.cache.util.CypherUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * <pre>
 *   8bytes | 2bytes | <128bytes
 *    crc   | size   |    data
 *
 * </pre>
 * <p>
 * value size should be less than 128 bytes.
 * this may be configured later.
 * <p>
 * Created by ylkang on 3/25/16.
 */
public class ValueObject {


    private static Logger logger = LoggerFactory.getLogger(ValueObject.class);

    private static final byte _sizeof_data_len = 2;

    private static final int _sizeof_prefix_data = _sizeof_data_len + 10;   // 8+2+x


    public static byte[] toBytes(byte[] message) {
        ByteBuffer bb = ByteBuffer.allocate(_sizeof_prefix_data + message.length);
        bb.putLong(CypherUtil.crc(message));
        bb.put((byte) message.length);
        bb.put(message);
        return bb.array();
    }

    public static byte[] readData(long offset, RandomAccessFile raf) throws IOException {
        raf.seek(offset);
        // 1.crc

        byte[] tmp = new byte[8];
        raf.read(tmp);
        long crc = 0;
        for (int i = 0; i < tmp.length; i++) {
            crc = crc << 8;
            crc |= (0xFF & tmp[i]);
        }

        // 2. data length
        int len = raf.read();

        // 3. data
        byte[] data = new byte[len];
        raf.read(data);
        long currentCrc = CypherUtil.crc(data);
        if (currentCrc == crc) {
            return data;
        }
        logger.error(String.format("corrupted data: %d", offset));
        return null;
    }
}
