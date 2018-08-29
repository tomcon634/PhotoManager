package PhotoManaging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.*;

/**
 * A Photo with given filepath and name.
 * Contains collections of tags and of past names.
 * <p>
 * Accessed Oracle Java documentation for LogRecord, FileHandler, and Date usage on 11/16/2017.
 * https://docs.oracle.com/javase/6/docs/api/java/util/logging/LogRecord.html#getMillis()
 * https://docs.oracle.com/javase/7/docs/api/java/util/logging/FileHandler.html
 * https://docs.oracle.com/javase/8/docs/api/java/util/Date.html?is-external=true
 */
public class Photo {

    /* The full name of this Photo, INCLUDING all tags */
    private String name;

    /* The Path of the image file that this Photo is associated with */
    private Path path;

    /* The boolean value denoting whether this Photo is a favourite or not */
    private boolean isFavourite;

    /* The list of tags associated with this Photo */
    private ArrayList<String> tags = new ArrayList<>();

    /* The list of all previous names of this Photo */
    private ArrayList<String> nameHistory = new ArrayList<>();

    /* A LoggerHandler object to handle all services where writing name history to a log file is needed. */
    private LoggerHandler LH;

    /**
     * Initialize a new Photo.
     * <p>
     * Accessed Oracle Java documentation for path usage on 11/07/2017.
     * https://docs.oracle.com/javase/tutorial/essential/io/pathOps.html
     *
     * @param path the filepath for the given Photo
     * @param name the name of this Photo
     */
    public Photo(Path path, String name) {
        this.path = path;
        this.name = name;
        this.setIsFavourite(false);
        // This call to readNameHistory builds nameHistory from an existing NameHistory.txt file
        // and also adds the current name to the end of nameHistory
        try {
            ReadFileHandler.readNameHistory(path, this);
            updateFileName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LH = new LoggerHandler(path, "NameHistory.txt", true);
    }

    static String baseName(String originalName) {
        if (!(originalName.contains("@"))) return originalName;
        else {
            return originalName.substring(0, originalName.indexOf('@') - 1);
        }
    }

    /**
     * Updates the name of this Photo to correctly include all available tags
     *
     * @param oldName the original name to be modified
     * @param newTags the collection of tags to build the new name with
     * @return the updated name with correct tags added
     */
    static String updateName(String oldName, ArrayList<String> newTags) {
        StringBuilder baseName = new StringBuilder().append(baseName(oldName));
        for (String tag : newTags) {
            baseName.append(" @").append(tag);
        }

        return baseName.toString();
    }

    /**
     * returns this photos tags
     *
     * @return this Photo's list of tags
     */
    public ArrayList<String> getTags() {
        return new ArrayList<>(this.tags);
    }

    /**
     * returns this photos path
     *
     * @return the path of the photo file associated with this Photo
     */
    public Path getPath() {
        return path;
    }

    /**
     * Changes the path of the Photo
     *
     * @param path the path to move Photo to
     */
    public void setPath(Path path) {
        this.path = path;
    }

    /**
     * Returns true if this is a valid tag to add to a Photo with tags existingTags,
     * otherwise returns false.
     *
     * @param tag          the tag to check for duplication and naming violation
     * @param existingTags the tags to check against for duplication
     * @return whether or not this is a valid tag to add
     */
    static boolean validateTag(String tag, ArrayList<String> existingTags) {
        return !(existingTags.contains(tag)) &&
                !tag.matches(".*[<>:\"/|?*\\\\].*") &&      // Tag contains illegal char
                !tag.matches(".*[\\s.]") &&                 // Tag ends in " " or "."
                !tag.matches("^$");                         // Tag is empty string
    }

    /**
     * Only adds the given tag to this Photo's list of tags.
     * Used when building the Photo upon PhotoManager initialization,
     * and is also used within standard addTag method.
     *
     * @param tag the tag to add to the Photo's name
     */
    void addTagInit(String tag) {
        if (validateTag(tag, getTags())) {
            tags.add(tag);
        }
    }

    /**
     * Adds the given tag to this Photo's list of tags and updates the Photo's name.
     *
     * @param tag the tag to add to the Photo's name
     */
    public void addTag(String tag) throws Exception {
        if (validateTag(tag, getTags())) {
            tags.add(tag);
            this.name = updateName(name, getTags());
            updateFileName();
            // Only logs a change if the Photo is renamed to something different
            if (!(name.equals(nameHistory.get(nameHistory.size() - 1)))) LH.logPhoto(this);
        }
    }

    /**
     * Removes the given tag from this Photo's list of tags and updates the Photo's name.
     * Precondition: this Photo has the tag attached to it.
     *
     * @param tag the tag to remove to the Photo's name
     */
    void removeTag(String tag) throws Exception {
        this.tags.remove(tag);
        this.name = updateName(name, getTags());
        updateFileName();
        LH.logPhoto(this);
    }

    /**
     * Restores this Photo's name and tags to a previous version.
     * Precondition: index is a valid index within this Photo's nameHistory.
     *
     * @param index the index of the nameHistory to restore to
     */
    void restoreName(int index) throws Exception {
        this.name = nameHistory.get(index);
        String[] sections = this.name.split("@");
        this.tags.clear();
        for (int x = 1; x < sections.length; x++) {
            this.tags.add(sections[x].trim());
        }

        this.name = updateName(name, getTags());
        updateFileName();
        LH.logPhoto(this);
    }

    /**
     * @return this Photo's nameHistory
     */
    public ArrayList<String> getNameHistory() {
        return nameHistory;
    }

    /**
     * Changes the system name of the Photo file.
     * Adapted from https://www.tutorialspoint.com/javaexamples/file_rename.htm
     * and https://stackoverflow.com/questions/14526260/how-do-i-get-the-file-name-from-a-
     * string-containing-the-absolute-file-path on 11/16/17
     */
    private void updateFileName() throws IOException {
        File oldName = path.toFile();

        // getting the new path name
        // grabs the file type including '.'
        String ext = path.toString().substring(path.toString().lastIndexOf("."));
        String newFileName = "/" + name + ext;

        String newPathName;
        if (path.toString().contains("/")) {
            newPathName = path.toString().substring(0, path.toString().lastIndexOf("/")) + newFileName;
        } else {
            newPathName = path.toString().substring(0, path.toString().lastIndexOf("\\")) + newFileName;
        }

        path = Paths.get(newPathName);
        File newName = new File(newPathName);

        boolean success = oldName.renameTo(newName);
        if (!success) throw new IOException("Error writing to filepath.");
    }

    /**
     * Return true if this Photo is a favourite, false if this Photo is not a favourite.
     *
     * @return bool value denoting whether or not this Photo is favourite
     */
    public boolean getIsFavourite() {
        return this.isFavourite;
    }

    /**
     * Set this Photo's isFavourite value to true/false.
     *
     * @param newIsFavourite the new true/false value for this Photo's isFavourite
     */
    public void setIsFavourite(boolean newIsFavourite) {
        this.isFavourite = newIsFavourite;
    }

    /**
     * Return the name of this Photo.
     *
     * @return a String representation of this Photo
     */
    @Override
    public String toString() {
        return name;
    }
}