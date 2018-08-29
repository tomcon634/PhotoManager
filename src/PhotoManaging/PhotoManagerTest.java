package PhotoManaging;

import junit.framework.TestCase;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PhotoManagerTest extends TestCase {
    public PhotoManagerTest() throws Exception {
    }

    // Testing of PhotoManager.uniqueName

    @Test
    public void testUniqueName0() {
        String originalName = "testName @t1";
        ArrayList<String> existingNames = new ArrayList<>();
        String newName = PhotoManager.uniqueName(originalName, existingNames);
        assertEquals("testName @t1", newName);
    }

    @Test
    public void testUniqueName1() {
        String originalName = "testName @t1 @t2";
        ArrayList<String> existingNames = new ArrayList<>();
        existingNames.add("testName @random1");
        String newName = PhotoManager.uniqueName(originalName, existingNames);
        assertEquals("testName (2) @t1 @t2", newName);
    }

    @Test
    public void testUniqueName2() {
        String originalName = "testName @t1 @t2 @t3";
        ArrayList<String> existingNames = new ArrayList<>();
        existingNames.add("testName @random1");
        existingNames.add("testName (2) @random1 @random2");
        String newName = PhotoManager.uniqueName(originalName, existingNames);
        assertEquals("testName (3) @t1 @t2 @t3", newName);
    }

    @Test
    public void testUniqueName3() {
        String originalName = "testName @t1 @t2 @t3 @t4";
        ArrayList<String> existingNames = new ArrayList<>();
        existingNames.add("testName");
        existingNames.add("testName (2) @random1 @random2 @random3");
        existingNames.add("testName (3)");
        String newName = PhotoManager.uniqueName(originalName, existingNames);
        assertEquals("testName (4) @t1 @t2 @t3 @t4", newName);
    }

    String dir = System.getProperty("user.dir");
    String test = dir + "/TestPhotoManager";
    Path path = Paths.get(test);
    PhotoManager pm = new PhotoManager(path);

    @Test
    public void testGetPath() throws Exception {
        assertEquals(pm.getPath(), path);
    }

    @Test
    public void testAddTag() throws Exception {
        pm.addTag("test1");
        assertTrue(pm.getTagMaster().contains("test1"));
    }

    @Test
    public void testRemoveTag() throws Exception {
        pm.addTag("test1");
        pm.removeTag("test1");
        assertFalse(pm.getTagMaster().contains("test1"));
    }

    @Test
    public void testFavPhoto() throws Exception {
        Photo p = pm.getPhotoMaster().get(0);
        pm.setFavourite(p, true);
        assertTrue(pm.getFavPhotos().contains(p));
    }

    @Test
    public void testUnfavPhoto() throws Exception {
        Photo p = pm.getPhotoMaster().get(0);
        pm.setFavourite(p, true);
        pm.setFavourite(p, false);
        assertFalse(pm.getFavPhotos().contains(p));
    }
}