package com.peter.humble.cache.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 * Created by ylkang on 3/29/16.
 */
public class TestUtil {

    private static final String STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public static String[] randomStrGenerator(int maxLen, int size, boolean unique) {

        String[] result = new String[size];
        Set<String> set = new HashSet<String>();

        for (int ele = 0; ele < size; ) {
            int totalLen = (int) (Math.random() * maxLen);
            totalLen = totalLen == 0 ? 1 : totalLen;
            StringBuilder bb = new StringBuilder();
            for (int len = 0; len < totalLen; len++) {
                bb.append(STR.charAt((int) (Math.random() * STR.length())));
            }
            if (unique) {
                if (set.contains(bb.toString())) {
                    continue;
                }
                set.add(bb.toString());
            }
            result[ele] = bb.toString();
            ele++;
        }
        return result;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String[] str = randomStrGenerator(10, 50, false);
        for (String s : str) {
            System.out.println(s);
        }

        FileOutputStream fos = new FileOutputStream(new File("a.lock"));
        FileLock lock = fos.getChannel().tryLock();
        System.out.println(lock.isValid());
        Thread.currentThread().wait();

    }
}
