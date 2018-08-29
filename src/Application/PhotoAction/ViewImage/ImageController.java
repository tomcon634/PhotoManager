package Application.PhotoAction.ViewImage;

import Application.Controller;
import Application.DataModel;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.nio.file.Path;

/**
 * Controller for the pop-up that displays when the user clicks "View Image" under Photo Actions in the UI.
 */
public class ImageController extends Controller {
    /* The image associated with the Photo for the user to view */
    @FXML
    private ImageView imageBox;

    /**
     * Display the selected Cell's Photo for the user.
     * <p>
     * Adapted from:
     * https://stackoverflow.com/questions/22635021/javafx-imageview-via-fxml-does-not-work (17/11/17)
     *
     * @param model the DataModel that this ImageController will control
     */
    @Override
    public void initialize(DataModel model) {
        Path photoPath = model.getCurrentCell().getPhoto().getPath();
        Image image = new Image("file:" + photoPath.toString());
        imageBox.setImage(image);
    }
}
