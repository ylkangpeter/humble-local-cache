package com.peter.humble.cache.data;

import com.peter.humble.cache.file.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileLock;

/**
 * Use FileOutputStream instead of BufferedOutputStream for more robust consistency.
 * <p>
 * Created by ylkang on 3/30/16.
 */
public class CacheDelegator<T> {

    private Logger logger = LoggerFactory.getLogger(CacheDelegator.class);

    private BufferedOutputStream indexOutput = null;

    private RandomAccessFile indexInput = null;

    private String indexFile;

    private String dataFile;

    private int curIndexOffset;

    private boolean useDiskCache = false;

    private static final String LOCK_FILE_SURFIX = ".humble_lock";

    private static FileHandler dataFileHandler;

    protected CacheDelegator(String indexFile, String dataFile) {
        this.indexFile = indexFile;
        this.dataFile = dataFile;
    }

    protected void init() {
        // open file
        try {
            // try lock file
            FileOutputStream fos = new FileOutputStream(new File(indexFile + LOCK_FILE_SURFIX));
            FileLock lock = fos.getChannel().tryLock();

            if (lock == null) {
                logger.error("can't own lockFile! ");
                return;
            }

            // open index file
            indexOutput = new BufferedOutputStream(new FileOutputStream(new File(indexFile), true));
            logger.info(String.format("open index file for write: %s, fileLen %d", new File(indexFile).getAbsolutePath(), new File(indexFile).length()));
            indexInput = new RandomAccessFile(indexFile, "r");
            logger.info("open index file for read: " + new File(indexFile).getAbsolutePath());

            // register all data blocks
            dataFileHandler = new FileHandler();
            dataFileHandler.init(dataFile);

        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        //register data blocks

        // write lock file
    }

    private synchronized void rotate() {

    }

    /**
     * @param key
     * @param val
     * @return offset
     */
    public synchronized IndexWrapper save(T key, byte[] val) throws IOException {
        // update data file
        byte[] bytes = ValueObject.toBytes(val);
        long offset = dataFileHandler.getCurrentHandler().getOffset();
        logger.debug(String.format("writing from %d to %d: %s", offset,
                dataFileHandler.getCurrentHandler().getOffset() + bytes.length, new String(val)));

        dataFileHandler.getCurrentHandler().getWriteHandler().write(bytes);
        dataFileHandler.getCurrentHandler().getWriteHandler().flush();
        // update index file
        byte[] indexBytes = IndexObject.toBytes(key.toString().getBytes(),
                dataFileHandler.getCurrentBlock(), offset);

        indexOutput.write(indexBytes);
        indexOutput.flush();
        // update index memory
        IndexWrapper wrapper = new IndexWrapper();
        wrapper.setDataOffset(offset);
        wrapper.setBlock(dataFileHandler.getCurrentBlock());
        return wrapper;
    }

    protected ValueWrapper get(long indexOffset, int block) throws IOException {
        byte[] val = ValueObject.readData(indexOffset, dataFileHandler.get(block).getReadHandler());
        if (val != null) {
            ValueWrapper value = new ValueWrapper(val);
            return value;
        }
        return null;
    }
}
