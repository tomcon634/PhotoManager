package Application.Directory;

import Application.Controller;
import Application.DataModel;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;

/**
 * Controller for the directory choice section of the UI, located in the top right corner
 */
public class DirectoryController extends Controller {
    /* Displays the current working directory, if one exists */
    @FXML
    private TextField dirChoice;

    /**
     * Initialize this DirectoryController.
     *
     * @param model the DataModel that this DirectoryController will control
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
     * Action taken when the user clicks the TextField at the directory choice section of the UI:
     * Open a directory choosing pop-up and set the current working directory.
     * <p>
     * Adapted from:
     * https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm (29/11/17)
     */
    @FXML
    void handleSelectAction() {
        DataModel model = this.getModel();
        DirectoryChooser dirChooser = new DirectoryChooser();

        // Set the initial directory in the chooser to the current one, if it exists
        if (model.getDirectory() != null) {
            dirChooser.setInitialDirectory(new File(model.getDirectory()));
        }

        // Open the directory choosing pop-up
        dirChooser.setTitle("Select the directory to browse");
        Stage stage = (Stage) dirChoice.getScene().getWindow();
        File directory = dirChooser.showDialog(stage);

        if (directory != null) {
            try {
                model.setDirectory(directory.toString());
                dirChoice.setText(directory.toString());

                // Reset the title of the stage to reflect the directory
                stage.setTitle("Photo Manager: " + model.getDirectory());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Action taken when the user clicks the "Open Selected Directory In File Explorer" button:
     * Open current working directory in OS-specific file explorer.
     * <p>
     * Adapted from:
     * https://stackoverflow.com/questions/7357969/how-to-use-java-code-to-open-windows-file-explorer-and-highlight
     * -the-specified-f (18/11/17)
     * https://stackoverflow.com/questions/15492240/how-to-open-a-folder-path-on-windows-and-linux-environment
     * (18/11/17)
     */
    @FXML
    void handleOpenDirAction() {
        DataModel model = this.getModel();

        try {
            if (model.getDirectory() != null) {
                String userOS = System.getProperty("os.name");
                if (userOS.equals("Linux")) {   // Open directory in Linux
                    Runtime.getRuntime().exec("xdg-open " + model.getDirectory());
                } else {                        // Open directory in Windows/Mac
                    Desktop.getDesktop().open(new File(model.getDirectory()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}