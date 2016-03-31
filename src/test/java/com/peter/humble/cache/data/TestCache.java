package com.peter.humble.cache.data;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ylkang on 3/25/16.
 */


public class TestCache {

    @Test
    public void testCache() throws IOException {
        HumbleCache<String> humbleCache = new HumbleCache<String>("inx", "data", 1, false);

        int total = 1;
        String[] keys =
                TestUtil.randomStrGenerator(10, total, true);

        long start = System.currentTimeMillis();

        for (int i = 0; i < keys.length; i++) {
            humbleCache.save(keys[i], keys[i].getBytes());
        }
        long end = System.currentTimeMillis();
        System.out.println(String.format("save %d objects: %ds (%.2f/s)", total, (end - start) / 1000, total * 1000.0 / (end - start)));

        start = System.currentTimeMillis();
        for (int i = 0; i < keys.length; i++) {
            Assert.assertEquals(keys[i], new String(humbleCache.get(keys[i])));
        }
        end = System.currentTimeMillis();
        System.out.println(String.format("read %d objects: %ds (%.2f/s)", total, (end - start) / 1000, total * 1000.0 / (end - start)));


    }
}
