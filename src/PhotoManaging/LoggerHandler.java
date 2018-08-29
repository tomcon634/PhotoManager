package PhotoManaging;

import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.logging.*;

/**
 * A LoggerHandler which allows Photo and PhotoManager to write externally
 */
class LoggerHandler {

    /* The Logger each Photo will use to log its name changes to an external file */
    private static final Logger logger = Logger.getLogger("External Writer");

    /* The Path to write to */
    private Path path;

    /* The name of the log file */
    private String fileName;

    /*
     * If true, functions in order to write to NameHistory.txt
     * If false, functions in order to write to TagsList.txt/FavList.txt
     */
    private boolean append;

    /**
     * Initialize a LoggerHandler to do all writing to external files
     *
     * @param path     see above instance parameter
     * @param fileName see above instance parameter
     * @param append   see above instance parameter
     */
    LoggerHandler(Path path, String fileName, boolean append) {
        this.path = path;
        this.fileName = fileName;
        this.append = append;
    }

    /**
     * Create a new FileHandler, log the text of msg with it, then close
     *
     * @param msg the String to be logged
     * @throws IOException if errors are encountered creating the FileHandler
     */
    private void log(String msg) throws IOException {
        // Creates a FileHandler to use to write the the appropriate file/path
        String logPath = append ? path.getParent().toString() + "/" + fileName :
                path.toString() + "/" + fileName;
        FileHandler FILEHANDLER = new FileHandler(logPath, append);
        FILEHANDLER.setFormatter(new Formatter() {
            public String format(LogRecord record) {
                if (append) {                           // Returns format for NameHistory.txt
                    return (record.getMessage() + " ["
                            + new Date(record.getMillis()) + "]"
                            + System.lineSeparator() + "" + System.lineSeparator());
                } else {
                    return record.getMessage();         // Returns format for TagsList.txt/FavList.txt
                }
            }
        });
        logger.addHandler(FILEHANDLER);
        logger.setLevel(Level.ALL);

        // Logs to the appropriate file then closes the FileHandler to prevent overlapping errors
        logger.log(Level.ALL, msg);
        FILEHANDLER.close();
    }

    /**
     * Builds the new contents of TagsList.txt or favList.txt from scratch, then rewrites the file with the updated
     * contents of tagMaster or the names of the photos in favPhotos
     *
     * @param pm    the photoManager associated with the TagsList.txt or favList.txt
     * @param isTag the boolean value to determine which file to write to
     */
    void logToText(PhotoManager pm, boolean isTag) throws Exception {
        StringBuilder newWrite = new StringBuilder();
        // if true writes to TagList.txt
        if (isTag) {
            for (String tag : pm.getTagMaster()) {
                newWrite.append(tag).append(System.lineSeparator());
            }
            this.log(newWrite.toString());
        }
        // else writes to favList.txt
        else {
            for (Photo photo : pm.getFavPhotos()) {
                newWrite.append(photo.toString()).append(System.lineSeparator());
            }
            this.log(newWrite.toString());
        }
    }

    /**
     * Logs the change from old to new name of p in NameHistory.txt,
     * and adds the new name to p's nameHistory
     *
     * @param p the Photo associated with the nameHistory
     */
    void logPhoto(Photo p) throws Exception {
        this.log(p.getNameHistory().get(p.getNameHistory().size() - 1) + " --> " + p.toString());
        if (!p.getNameHistory().contains(p.toString())) p.getNameHistory().add(p.toString());
    }
}
