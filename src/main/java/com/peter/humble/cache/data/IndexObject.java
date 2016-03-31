package com.peter.humble.cache.data;

import com.peter.humble.cache.util.CypherUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

/**
 * <pre>
 *  8bytes  | 8bytes    | 2bytes    | 6bytes |  1byte | x bytes
 *   crc32  | timestamp | fileBlock | offset |  len   |  key
 *
 * </pre>
 * <p>
 * <p
 * Created by ylkang on 3/25/16.
 */
public class IndexObject {

    private static final Logger logger = LoggerFactory.getLogger(IndexObject.class);

    private static final int _sizeof_appendix_data = 8 + 8 + 2 + 6;

    public static byte[] toBytes(byte[] key, char fileBlock, long dataOffset) {
        ByteBuffer bb = ByteBuffer.allocate(_sizeof_appendix_data + 1 + key.length);
        bb.putLong(CypherUtil.crc(key));
        bb.putLong(System.currentTimeMillis());
        bb.putChar(fileBlock);
        byte[] offsetInBytes = new byte[6];
        for (int i = 0; i < offsetInBytes.length; i++) {
            offsetInBytes[i] = (byte) ((dataOffset >> i * 8) & 0xFF);
        }
        bb.put(offsetInBytes);
        bb.put((byte) key.length);
        bb.put(key);
        return bb.array();
    }

    public static IndexWrapper readData(long offset, RandomAccessFile raf) throws IOException {
        raf.seek(offset);
        // 1. crc
        byte[] tmp = new byte[8];
        raf.read(tmp);
        long crc = 0;
        for (int i = 0; i < tmp.length; i++) {
            crc = crc << 8;
            crc |= (0xFF & tmp[i]);
        }

        // 2. timestamp
        raf.read(tmp);
        long timestamp = 0;
        for (int i = 0; i < tmp.length; i++) {
            timestamp = timestamp << 8;
            timestamp |= (0xFF & tmp[i]);
        }
        // 3. fileBlock
        tmp = new byte[2];
        raf.read(tmp);
        char fileBlock = 0;
        for (int i = 0; i < tmp.length; i++) {
            fileBlock = (char) (fileBlock << 8);
            fileBlock |= (0xFF & tmp[i]);
        }

        // 4. dataOffset
        tmp = new byte[6];
        raf.read(tmp);
        long dataOffset = 0;
        for (int i = 0; i < tmp.length; i++) {
            dataOffset = (char) (dataOffset << 8);
            dataOffset |= (0xFF & tmp[i]);
        }

        // 5. len
        int len = raf.read();

        // 6. data
        tmp = new byte[len];
        raf.read(tmp);

        // validate

        long currentCrc = CypherUtil.crc(tmp);
        if (currentCrc == crc) {
            IndexWrapper wrapper = new IndexWrapper(tmp);
            wrapper.setBlock(fileBlock);
            wrapper.setDataOffset(dataOffset);
            wrapper.setIndexOffset(offset);
            wrapper.setTimeStamp(timestamp);
            return wrapper;
        }

        logger.error(String.format("corrupted index: %d", offset));
        return null;
    }

    public static void main(String[] args) {
        long a = Integer.MAX_VALUE * 16L;
        System.out.println(a);
        System.out.println(Long.toBinaryString(a));
        byte[] offsetInBytes = new byte[6];
        for (int i = 0; i < offsetInBytes.length; i++) {
            offsetInBytes[i] = (byte) ((a >> i * 8) & 0xFF);
            System.out.println((byte) ((a >> i * 8) & 0xFF));
            System.out.println((Integer.toBinaryString((byte) ((a >> i * 8) & 0xFF))));
        }

        long b = 0L;
        for (int i = 5; i >= 0; i--) {
            b = b << 8;
            b = b | (0xff & offsetInBytes[i]);
            System.out.println(b);
            System.out.println(Long.toBinaryString(b));
        }
    }
}
