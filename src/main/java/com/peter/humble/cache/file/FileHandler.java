package com.peter.humble.cache.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ylkang on 3/30/16.
 */
public class FileHandler {

    private Logger logger = LoggerFactory.getLogger(FileHandler.class);

    // register all file handles
    private Map<Character, FileHandlerWrapper> handlerFactory = new HashMap<Character, FileHandlerWrapper>();

    private char curDataFileBlock = 0;

    public void init(String dataFile) {
        try {
            registerDataFiles(dataFile);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public char getCurrentBlock() {
        return curDataFileBlock;
    }

    public FileHandlerWrapper get(int fileBlock) {
        return handlerFactory.get((char)fileBlock);
    }

    public FileHandlerWrapper getCurrentHandler() {
        return handlerFactory.get(curDataFileBlock);
    }

    private void registerDataFiles(final String dataFilePrefix) throws FileNotFoundException {
        File f = new File(dataFilePrefix);
        File[] files = f.getAbsoluteFile().getParentFile().listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return dir.isFile() && name.startsWith(dataFilePrefix);
            }
        });
        if (files == null || files.length == 0) {
            logger.info("---------no data file yet, create a new one: ----------" + dataFilePrefix + "." + (int) curDataFileBlock);
            handlerFactory.put(curDataFileBlock, createNewWrapper(curDataFileBlock, dataFilePrefix));
            return;
        }
        logger.info("----------start loading data files----------totally: " + files.length);
        long modifyTime = 0;
        for (int i = 0; i < files.length; i++) {
            handlerFactory.put(curDataFileBlock, createNewWrapper(curDataFileBlock, dataFilePrefix));
            if (files[i].lastModified() > modifyTime) {
                modifyTime = files[i].lastModified();
                curDataFileBlock = (char) Integer.parseInt(files[i].getName().split("\\.")[1]);
            }
        }
        logger.info("----------end loading data files------------");
    }

    private FileHandlerWrapper createNewWrapper(int curDataFileBlock, String dataFilePrefix) throws FileNotFoundException {
        OutputStream ops = new BufferedOutputStream(new FileOutputStream(new File(dataFilePrefix + "." + curDataFileBlock), true));
        RandomAccessFile raf = new RandomAccessFile(dataFilePrefix + "." + curDataFileBlock, "r");
        return new FileHandlerWrapper(raf, ops);
    }
    // create new file handles
    // lazy mode vs pre load mode
}
