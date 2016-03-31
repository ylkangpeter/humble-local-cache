package com.peter.humble.cache.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Created by ylkang on 3/30/16.
 */
public class FileHandlerWrapper {

    private Logger logger = LoggerFactory.getLogger(FileHandlerWrapper.class);

    private RandomAccessFile readHandler;

    private OutputStream writeHandler;

    public FileHandlerWrapper(RandomAccessFile readHandler, OutputStream writeHandler) {
        this.readHandler = readHandler;
        this.writeHandler = writeHandler;
    }

    public RandomAccessFile getReadHandler() {
        return readHandler;
    }

    public void setReadHandler(RandomAccessFile readHandler) {
        this.readHandler = readHandler;
    }

    public OutputStream getWriteHandler() {
        return writeHandler;
    }

    public void setWriteHandler(OutputStream writeHandler) {
        this.writeHandler = writeHandler;
    }

    public long getOffset() {
        try {
            return readHandler.length();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return 0;
    }
}
