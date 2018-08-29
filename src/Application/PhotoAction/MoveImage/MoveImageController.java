package Application.PhotoAction.MoveImage;

import Application.Controller;
import Application.DataModel;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Controller for the pop-up that displays when the user clicks "Move Image" under Photo Actions in the UI.
 */
public class MoveImageController extends Controller {
    /* The text input of the user denoting the directory the user wishes to move the image to */
    @FXML
    private TextField dirChoice;

    /**
     * Initialize this MoveImageController.
     *
     * @param model the DataModel that this MoveImageController will control
     */
    @Override
    public void initialize(DataModel model) {
        super.initialize(model);

        // Makes sure user cannot only edit the directory using the "Select" button
        dirChoice.setEditable(false);
        // Prevents TextField from changing cursor
        dirChoice.setCursor(Cursor.DEFAULT);
    }

    /**
     * Action taken when the user clicks the TextField:
     * Open a directory choosing pop-up to select the directory to move the currently selected Cell's Photo to
     */
    @FXML
    public void handleSelectAction() {
        DataModel model = this.getModel();

        // Open the directory choosing pop-up
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(new File(model.getDirectory()));
        dirChooser.setTitle("Select the new directory for this photo");
        Stage stage = (Stage) dirChoice.getScene().getWindow();
        File directory = dirChooser.showDialog(stage);

        if (directory != null) {
            dirChoice.setText(directory.toString());
        }
    }

    /**
     * Action taken when the user clicks the "Move" button:
     * Move the currently selected Cell's Photo to the new directory.
     */
    @FXML
    public void handleMoveAction() throws Exception {
        DataModel model = this.getModel();

        if (dirChoice != null) {
            model.getPhotoList().movePhoto(dirChoice.getCharacters().toString(), model.getCurrentCell().getPhoto());

            // Update the list of Photos for the user to view
            model.setCells(model.getPhotoList().getPhotoMaster());

            // Close pop-up
            Stage stage = (Stage) dirChoice.getScene().getWindow();
            stage.close();
        }
    }
}
