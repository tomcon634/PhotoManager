package PhotoManaging;

import java.io.*;
import java.util.ArrayList;
import java.nio.file.*;

/**
 * A PhotoManager for a chosen directory.
 * Contains collections of all Photos, favourited photos and tags in this directory.
 */
public class PhotoManager {

    /* The path of this PhotoManager */
    private Path path;

    /* The collection of all Photos in this PhotoManager*/
    private ArrayList<Photo> photoMaster = new ArrayList<>();

    /* The master list of tags associated with all Photos in this PhotoManager */
    private ArrayList<String> tagMaster = new ArrayList<>();

    /* The list of all favourited photos in this PhotoManager */
    private ArrayList<Photo> favPhotos = new ArrayList<>();

    /* The list of names of all the favourited Photos in this PhotoManager */
    private ArrayList<String> favPhotoNames = new ArrayList<>();

    /* A LoggerHandler object to handle all services where writing tags to a log file is needed. */
    private LoggerHandler TH;

    /* A LoggerHandler object to handle all services where writing the names of favourited photos to a log file
    is needed. */
    private LoggerHandler FH;

    /**
     * Initialize a new PhotoManager.
     * Upon initialization, adds all photo files in given directory to photoMaster and adds each of the photos tags to
     * tagMaster
     * <p>
     * Adapted from Oracle's Java documentation on java.nio.file on 11/07/2017.
     * https://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html#probeContentType(java.nio.file.Path)
     *
     * @param path the Path for the given PhotoManager
     */
    public PhotoManager(Path path) throws Exception {
        this.path = path;
        TH = new LoggerHandler(path, "TagsList.txt", false);
        FH = new LoggerHandler(path, "FavList.txt", false);
        ReadFileHandler.readTextFile(path, true, this);  // Restores tagMaster to previous state before exit
        ReadFileHandler.readTextFile(path, false, this); // Restores favPhotos to previous state before exit
        initializePhotos(this.path, this.favPhotoNames);
    }

    /**
     * Generates a unique name for the Photo with originalName by
     * adding a (number) after the base name if found in existingNames
     *
     * @param originalName  the name read in from the filepath
     * @param existingNames the collection of names already in this PhotoManager
     * @return a possibly modified name that will be unique in this PhotoManager
     */
    static String uniqueName(String originalName, ArrayList<String> existingNames) {
        String baseName;
        String suffix = "";
        if (!(originalName.contains("@"))) baseName = originalName;
        else {
            baseName = originalName.substring(0, originalName.indexOf('@') - 1);
            suffix = originalName.substring(originalName.indexOf('@') - 1);
        }
        int duplicateCount = 0;
        for (String existingName : existingNames) {
            if (Photo.baseName(existingName).matches(baseName + "(\\s\\([0-9]*\\))*")) duplicateCount++;
        }
        if (duplicateCount == 0) return originalName;
        else {
            return baseName + " (" + ++duplicateCount + ")" + suffix;
        }
    }

    /**
     * Recursively searches root directory located at dirPath for all valid Photos,
     * and subsequently favorites them if they are found in favList.
     *
     * @param dirPath the root path to search for Photos
     * @param favList the list of favorites to validate created Photos against
     * @throws Exception if DirectoryStream cannot be created at dirPath
     */
    private void initializePhotos(Path dirPath, ArrayList favList) throws Exception {
        String IMAGE_TYPES = ".*\\.(jpg|png|gif|bmp)";
        DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath);
        for (Path entry : stream) {
            if (entry.toString().matches(IMAGE_TYPES)) {
                // removing the file type from the file name
                String photoName = entry.getFileName().toString().substring(0,
                        entry.getFileName().toString().lastIndexOf("."));
                photoName = uniqueName(photoName, getPhotoMasterNames());
                String[] sections = photoName.split("@");
                Photo newPhoto = new Photo(entry, photoName);
                // adding tags to the Photo from the file name
                for (int x = 1; x < sections.length; x++) {
                    newPhoto.addTagInit(sections[x].trim());
                }
                this.addPhoto(newPhoto);
                // upon initialization if the photo is in the favPhotoName list it will change the status of isFavourite
                if (favList.contains(newPhoto.toString())) {
                    this.favPhoto(newPhoto);
                }
            } else if (entry.toFile().isDirectory()) {
                initializePhotos(entry, favList);
            }
        }
    }

    /**
     * Changes isFavourite property of specified photo to true and adds the photo this photo managers
     * favPhotos array list if it doesnt already exist.
     *
     * @param photo the Photo to favourite.
     */
    private void favPhoto(Photo photo) throws Exception {
        photo.setIsFavourite(true);
        if (!this.favPhotos.contains(photo)) {
            this.favPhotos.add(photo);
            FH.logToText(this, false);
        }
    }

    /**
     * Changes isFavourite property of specified photo to false and removes the photo from this photo managers
     * favPhotos array list if it exists.
     *
     * @param photo the Photo to unfavourite.
     */
    private void unFavPhoto(Photo photo) throws Exception {
        photo.setIsFavourite(false);
        if (this.favPhotos.contains(photo)) {
            this.favPhotos.remove(photo);
            FH.logToText(this, false);
        }
    }

    /**
     * Changes the specified photos isFavourite value depending on the boolean value passed in
     *
     * @param photo the Photo of interest.
     * @param isFav boolean value indicating the specified photos isFavourite property
     */
    public void setFavourite(Photo photo, boolean isFav) throws Exception {
        if (isFav) {
            this.favPhoto(photo);
        } else {
            this.unFavPhoto(photo);
        }
    }

    /**
     * Adds the specified Photo to this PhotoManager,
     * also adding its tags to tagMaster.
     *
     * @param photo the Photo to add to this PhotoManager.
     */
    private void addPhoto(Photo photo) throws Exception {
        this.photoMaster.add(photo);
        for (String tag : photo.getTags()) {
            if (!tagMaster.contains(tag)) this.addTag(tag, photo);
        }
    }

    /**
     * Adds the specified Tag to this PhotoManager.
     *
     * @param tag the Tag to add to this PhotoManager.
     */
    public void addTag(String tag) throws Exception {
        if (!this.tagMaster.contains(tag)) {
            if (tag.matches(".*[<>:\"/|?*\\\\].*") ||       // Illegal char in general
                    tag.matches(".*[\\s.]") ||                  // Name ends in " " or "."
                    tag.matches("^$")) {                        // tag is an empty string
                throw new IOException("Illegal character in tag.");
            } else {
                this.tagMaster.add(tag);
                // records the change into the config file
                TH.logToText(this, true);
            }
        }
    }

    /**
     * Adds the specified Tag to the specified Photo
     * and to this PhotoManager.
     *
     * @param tag   the Tag to add to this PhotoManager.
     * @param photo the Photo to add the given Tag to.
     */
    public void addTag(String tag, Photo photo) throws Exception {
        if (!photo.getTags().contains(tag)) {
            photo.addTag(tag);
        }
        addTag(tag);
        FH.logToText(this, false);
    }

    /**
     * Removes the specified Tag from this PhotoManager
     * and any Photo containing it
     *
     * @param tag the Tag to remove from this PhotoManager.
     */
    public void removeTag(String tag) throws Exception {
        // Removes tag from any Photo containing it
        for (Photo p : photoMaster) {
            if (p.getTags().contains(tag)) {
                p.removeTag(tag);
            }
        }
        if (this.tagMaster.contains(tag)) {
            this.tagMaster.remove(tag);
            TH.logToText(this, true);
        }
    }

    /**
     * Removes the specified Tag from the given Photo and this PhotoManager
     * if not in use by any other Photo
     *
     * @param tag   the Tag to remove from this PhotoManager.
     * @param photo the Photo to remove tag from.
     */
    public void removeTag(String tag, Photo photo) throws Exception {
        // checks the desired Photo to see if it contains tag for removal
        if (photo.getTags().contains(tag)) {
            photo.removeTag(tag);
        }
        FH.logToText(this, false);
    }

    /**
     * Restores the given Photo's name and tags to a previous version.
     * Precondition: index is a valid index within this Photo's nameHistory.
     *
     * @param photo the Photo to restore to a previous version
     * @param index the index of the nameHistory to restore to
     */
    public void restoreName(Photo photo, int index) throws Exception {
        ArrayList<String> oldTags = photo.getTags();
        photo.restoreName(index);

        // Clears old tags from tagMaster that may/may not still be needed
        for (String tagClear : oldTags) {
            boolean tagPresent = false;
            // checks through photoMaster to see if there are any Photos containing tag
            for (Photo p : photoMaster) {
                if (p.getTags().contains(tagClear)) {
                    tagPresent = true;
                    break;
                }
            }
            // if no Photos contain tag then remove tag from tagMaster
            if (!tagPresent && this.tagMaster.contains(tagClear)) {
                this.tagMaster.remove(tagClear);
                TH.logToText(this, true);
            }
        }

        // Re-adds all tags currently used by the renamed Photo
        for (String tagReturn : photo.getTags()) {
            addTag(tagReturn);
        }
        FH.logToText(this, false);
    }

    /**
     * Moves the photo to a different directory
     * <p>
     * design similar to the updateFileName in photo
     * accessed the links below as reference on Nov 16, 2017
     * https://www.tutorialspoint.com/javaexamples/file_rename.htm
     *
     * @param directory the directory to change to
     * @param photo     the photo to move
     */

    public void movePhoto(String directory, Photo photo) throws Exception {
        if (this.photoMaster.contains(photo)) {
            File oldFile = photo.getPath().toFile();
            if (!directory.contains(path.toString())) {  // Only removes Photo from PhotoManager if moved outside root
                ArrayList<String> tagsToClear = photo.getTags();
                this.photoMaster.remove(photo);
                if (this.favPhotos.contains(photo)) {
                    this.favPhotos.remove(photo);
                }

                // Removes any tag from this PhotoManager used exclusively by the moved Photo
                for (String clearedTag : tagsToClear) {
                    removeTag(clearedTag);
                }
            }

            // Relocates the Photo to its new directory
            String oldName;
            // generates the name of file based on os
            if (path.toString().contains("/")) {
                oldName = "/" + photo.getPath().getFileName().toString();
            } else {
                oldName = "\\" + photo.getPath().getFileName().toString();
            }
            // sets the new path of the photo
            photo.setPath(Paths.get(directory + oldName));
            File newName = new File(directory + oldName);
            boolean success = oldFile.renameTo(newName);
            if (!success) throw new IOException("Error moving photo to directory.");
        }
        FH.logToText(this, false);
    }

    /**
     * Return the photoMaster of this PhotoManager as an ArrayList of
     * Photo to be viewed by a client interacting with DirectoryManager.
     *
     * @return an ArrayList of the photoMaster of this PhotoManager.
     */
    public ArrayList<Photo> getPhotoMaster() {
        return this.photoMaster;
    }

    /**
     * Returns an ArrayList containing all the names of the Photos contained
     * in this PhotoManager's photoMaster.
     *
     * @return an ArrayList of the names of the Photos in this PhotoManager.
     */
    private ArrayList<String> getPhotoMasterNames() {
        ArrayList<String> photoMasterNames = new ArrayList<>(getPhotoMaster().size());
        for (Photo eachPhoto : getPhotoMaster()) {
            photoMasterNames.add(eachPhoto.toString());
        }
        return photoMasterNames;
    }

    /**
     * Returns the path of the photo manager
     *
     * @return the Path of this PhotoManager
     */
    public Path getPath() {
        return this.path;
    }

    /**
     * Return the tagMaster of this PhotoManager as an ArrayList of String representing the tags
     *
     * @return an ArrayList of the photoMaster of this PhotoManager.
     */
    public ArrayList<String> getTagMaster() {
        return this.tagMaster;
    }

    /**
     * Returns the list of favourite photos of the photo manager
     *
     * @return Array list of favourited photos
     */
    ArrayList<Photo> getFavPhotos() {
        return this.favPhotos;
    }

    /**
     * Returns the list of favourite photos names of the photo manager
     *
     * @return the Array list of favourite photo names
     */
    ArrayList<String> getFavPhotosNames() {
        return this.favPhotoNames;
    }
}
