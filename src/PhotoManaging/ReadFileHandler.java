package PhotoManaging;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


/**
 * A ReadFileHandler which allows PhotoManager, and photo to read from text files
 */
class ReadFileHandler {

    /**
     * Finds the text file located at path (if it exists) and if TagsList.txt adds all tags within to this
     * PhotoManager's tagMaster, if favList.txt will add all the favourited photos names to this PhotoManager's
     * favPhotos
     *
     * @param path  where the method should read existing text file, if possible
     * @param isTag boolean value to determine which file to read
     * @param pm    PhotoManager that takes the info from the text file and copies over the info to the appropriate list
     * @throws Exception if either creating inputTags or calling addTag, or fails
     */
    static void readTextFile(Path path, boolean isTag, PhotoManager pm) throws Exception {
        String filePath;
        // if the text file is TagsList.txt
        if (isTag) {
            filePath = path.toString() + "/" + "TagsList.txt";
            if (Files.exists(Paths.get(filePath))) {
                Stream<String> inputTags = Files.lines(Paths.get(filePath));
                inputTags.forEach((inputTag) -> {
                    try {
                        pm.addTag(inputTag);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        // if the text file is favList.txt
        else {
            filePath = path.toString() + "/" + "FavList.txt";
            if (Files.exists(Paths.get(filePath))) {
                Stream<String> inputNames = Files.lines(Paths.get(filePath));
                inputNames.forEach((inputName) -> {
                    try {
                        pm.getFavPhotosNames().add(inputName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    /**
     * Finds the NameHistory.txt file located at path (if it exists)
     * and adds all entries involving p to its nameHistory
     *
     * @param path where the method should read existing names from, if possible
     * @param p    Photo that takes information from the text file and adds it to the nameHistory of p
     * @throws Exception if a Stream cannot be constructed from NameHistory.txt
     */
    static void readNameHistory(Path path, Photo p) throws Exception {
        if (Files.exists(Paths.get(path.getParent().toString() + "/" + "NameHistory.txt"))) {
            Stream<String> inputNames = Files.lines(Paths.get(path.getParent().toString() + "/" +
                    "NameHistory.txt"));
            inputNames.forEach((inputName) -> {
                if (inputName.length() > 1) {
                    String baseName;
                    if (p.toString().contains("@")) baseName = p.toString().substring(0, p.toString().indexOf(" @"));
                    else {
                        baseName = p.toString();
                    }
                    if (inputName.contains(baseName) &&
                            !p.getNameHistory().contains(inputName.substring(0, inputName.indexOf(" -->"))))
                        p.getNameHistory().add(inputName.substring(0, inputName.indexOf(" -->")));
                }
            });
        }
        if (!p.getNameHistory().contains(p.toString())) p.getNameHistory().add(p.toString());
    }
}
