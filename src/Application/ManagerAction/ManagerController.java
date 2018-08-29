package Application.ManagerAction;

import Application.Controller;
import Application.DataModel;
import javafx.fxml.FXML;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Controller for the Photo Manager section of the UI, located centre right
 */
public class ManagerController extends Controller {

    /**
     * Action taken when the user clicks the "Open Tag Manager" button:
     * Open the tag manager pop-up and hand over control to OpenController
     *
     * @throws IOException when FXML file cannot be found
     */
    @FXML
    public void handleOpenTagManager() throws IOException {
        setStage("/Application/ManagerAction/OpenTagManager/OpenView.fxml", "Tag Manager", 300,
                500);
    }

    /**
     * Action taken when the user clicks the "View name change log" button:
     * Open the name change log .txt file (NameHistory.txt), if it exists.
     * <p>
     * Adapted from:
     * https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java (11/18/19)
     *
     * @throws IOException when FXML file cannot be found
     */
    @FXML
    public void handleViewNamesAction() throws IOException {
        DataModel model = this.getModel();

        if (model.getPhotoList() != null) {
            String path = model.getPhotoList().getPath().toString();

            // Conforms to either back slashes or forward slashes depending on the OS
            if (path.contains("/")) {
                path += "/NameHistory.txt";
            } else {
                path += "\\NameHistory.txt";
            }

            File f = new File(path);
            // Only try opening NameHistory if it exists
            if (f.exists() && !f.isDirectory()) {
                String userOS = System.getProperty("os.name");
                if (userOS.equals("Linux")) {       // Open name change log in Linux
                    Runtime.getRuntime().exec("xdg-open " + path);
                } else {                            // Open name change log in Windows/Mac
                    Desktop.getDesktop().open(new File(path));
                }
            }
        }
    }
}
