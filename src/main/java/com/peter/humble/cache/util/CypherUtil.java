package com.peter.humble.cache.util;

import java.util.zip.CRC32;

/**
 * Created by ylkang on 3/28/16.
 */
public class CypherUtil {

    public static long crc(byte[] message) {
        CRC32 crc = new CRC32();
        crc.update(message);
        return crc.getValue();
    }
}
