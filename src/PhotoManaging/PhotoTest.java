package PhotoManaging;


import junit.framework.TestCase;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PhotoTest extends TestCase {

    // Testing of Photo.baseName

    @Test
    public void testBaseName0() {
        String oldName = "ExamplePhoto";
        String newName = Photo.baseName(oldName);
        assertEquals("ExamplePhoto", newName);
    }

    @Test
    public void testBaseName1() {
        String oldName = "ExamplePhoto @test1";
        String newName = Photo.baseName(oldName);
        assertEquals("ExamplePhoto", newName);
    }

    @Test
    public void testBaseName2() {
        String oldName = "ExamplePhoto @test1 @test2";
        String newName = Photo.baseName(oldName);
        assertEquals("ExamplePhoto", newName);
    }

    // Testing of Photo.updateName

    @Test
    public void testUpdateNameNoTags() {
        String oldName = "ExamplePhoto";
        ArrayList<String> newTags = new ArrayList<>();
        newTags.add("tag1");
        newTags.add("tag2");
        newTags.add("tag3");
        String newName = Photo.updateName(oldName, newTags);
        assertEquals("ExamplePhoto @tag1 @tag2 @tag3", newName);
    }

    @Test
    public void testUpdateNameYesTags() {
        String oldName = "ExamplePhoto @old1 @old2";
        ArrayList<String> newTags = new ArrayList<>();
        newTags.add("new1");
        newTags.add("new2");
        String newName = Photo.updateName(oldName, newTags);
        assertEquals("ExamplePhoto @new1 @new2", newName);
    }

    // Testing of Photo.validateTag

    @Test
    public void testValidateTagLegal() {
        ArrayList<String> newTags = new ArrayList<>();
        newTags.add("tag1");
        newTags.add("tag2");
        newTags.add("tag3");
        assertTrue(Photo.validateTag("tag4", newTags));
    }

    @Test
    public void testValidateTagIllegalRepeat() {
        ArrayList<String> newTags = new ArrayList<>();
        newTags.add("tag1");
        newTags.add("tag2");
        newTags.add("tag3");
        assertFalse(Photo.validateTag("tag2", newTags));
    }

    @Test
    public void testValidateTagIllegalChar1() {
        ArrayList<String> newTags = new ArrayList<>();
        assertFalse(Photo.validateTag("tag<4", newTags));
    }

    @Test
    public void testValidateTagIllegalChar2() {
        ArrayList<String> newTags = new ArrayList<>();
        assertFalse(Photo.validateTag("tag>4", newTags));
    }

    @Test
    public void testValidateTagIllegalChar3() {
        ArrayList<String> newTags = new ArrayList<>();
        assertFalse(Photo.validateTag("tag:4", newTags));
    }

    @Test
    public void testValidateTagIllegalChar4() {
        ArrayList<String> newTags = new ArrayList<>();
        assertFalse(Photo.validateTag("tag\"4", newTags));
    }

    @Test
    public void testValidateTagIllegalChar5() {
        ArrayList<String> newTags = new ArrayList<>();
        assertFalse(Photo.validateTag("tag/4", newTags));
    }

    @Test
    public void testValidateTagIllegalChar6() {
        ArrayList<String> newTags = new ArrayList<>();
        assertFalse(Photo.validateTag("tag|4", newTags));
    }

    @Test
    public void testValidateTagIllegalChar7() {
        ArrayList<String> newTags = new ArrayList<>();
        assertFalse(Photo.validateTag("tag?4", newTags));
    }

    @Test
    public void testValidateTagIllegalChar8() {
        ArrayList<String> newTags = new ArrayList<>();
        assertFalse(Photo.validateTag("tag*4", newTags));
    }

    @Test
    public void testValidateTagIllegalChar9() {
        ArrayList<String> newTags = new ArrayList<>();
        assertFalse(Photo.validateTag("tag\\4", newTags));
    }

    @Test
    public void testValidateTagIllegalEnding1() {
        ArrayList<String> newTags = new ArrayList<>();
        assertFalse(Photo.validateTag("tag4 ", newTags));
    }

    @Test
    public void testValidateTagIllegalEnding2() {
        ArrayList<String> newTags = new ArrayList<>();
        assertFalse(Photo.validateTag("tag4.", newTags));
    }

    @Test
    public void testValidateTagIllegalEmpty() {
        ArrayList<String> newTags = new ArrayList<>();
        assertFalse(Photo.validateTag("", newTags));
    }

    String dir = System.getProperty("user.dir");
    String test = dir + "/TestPhoto/photo4.jpg";
    Path path = Paths.get(test);
    Photo p = new Photo(path, "photo4");

    @Test
    public void testAddTag() throws Exception {
        p.addTag("test1");
        assertTrue(p.getTags().contains("test1"));
    }
}
