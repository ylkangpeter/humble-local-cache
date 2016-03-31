package com.peter.humble.cache.data;

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Benchmark:
 * <p>
 * cpu:     intel core 2 Duo 2.26GHZ
 * memory:  4G*2 1067 MHz DDR3
 * disk:    5400rpm sata
 * <p>
 * single thread: 140~150k/s ordered read or write
 * read index: 100k/s
 * <p>
 * <p>
 * Created by ylkang on 3/29/16.
 */
public class TestFilePerformance {

    @Test
    public void testCache_random_key() throws IOException {
        String fileName = "test_index." + System.currentTimeMillis();
        int total = 1000000;  // file size: around 31M

        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.getChannel().force(true);
        String[] keys =
                TestUtil.randomStrGenerator(10, total, true);
        List<Long> offsetList = new ArrayList<Long>();

        long start = System.currentTimeMillis();
        long curOffset = 0;
        for (int i = 0; i < keys.length; i++) {
            offsetList.add(curOffset);
            byte[] bytes = IndexObject.toBytes(keys[i].getBytes(), '1', i);
            raf.write(bytes);
            curOffset += bytes.length;
        }

        long end = System.currentTimeMillis();
        System.out.println(String.format("save %d objects: %ds (%.2f/s)", total, (end - start) / 1000, total * 1000.0 / (end - start)));

        start = System.currentTimeMillis();
        for (int i = 0; i < offsetList.size(); i++) {
            long offset = offsetList.get(i);
            IndexWrapper wrapper = IndexObject.readData(offset, raf);
//            System.out.println(new String(wrapper.getKey()));
            Assert.assertEquals(keys[i], new String(wrapper.getKey()));
        }
        end = System.currentTimeMillis();
        System.out.println(String.format("fetch %d objects: %ds (%.2f/s)", total, (end - start) / 1000, total * 1000.0 / (end - start)));
    }

    @Test
    public void testCache_random_Value() throws IOException {
        String fileName = "test_value." + System.currentTimeMillis();
        int total = 1000000;       // file size: around 62M

        String[] values =
                TestUtil.randomStrGenerator(100, total, true);
        List<Long> offsetList = new ArrayList<Long>();
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        raf.getChannel().force(true);

        long start = System.currentTimeMillis();
        long curOffset = 0;
        for (int i = 0; i < values.length; i++) {
            offsetList.add(curOffset);
            byte[] bytes = ValueObject.toBytes(values[i].getBytes());
            raf.write(bytes);
            curOffset += bytes.length;
        }

        long end = System.currentTimeMillis();
        System.out.println(String.format("save %d objects: %ds (%.2f/s)", total, (end - start) / 1000, total * 1000.0 / (end - start)));

        start = System.currentTimeMillis();
        raf.getChannel().force(true);
        for (int i = 0; i < offsetList.size(); i++) {
            long offset = offsetList.get(i);
            byte[] value = ValueObject.readData(offset, raf);
//            System.out.println(new String(value));
            Assert.assertEquals(values[i], new String(value));
        }
        end = System.currentTimeMillis();
        System.out.println(String.format("fetch %d objects: %ds (%.2f/s)", total, (end - start) / 1000, total * 1000.0 / (end - start)));
    }
}
