package Application.PhotoAction.RestoreName;

import Application.Controller;
import Application.DataModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

/**
 * Controller for the pop-up that displays when the user clicks "Restore to a previous name"
 */
public class RestoreController extends Controller {
    /* The selectable list of previous names associated with the currently selected Cell's Photo */
    @FXML
    private ChoiceBox<String> namesToChoose;

    /**
     * Initialize this RestoreController.
     *
     * @param model the DataModel that this MoveImageController will control
     */
    @Override
    public void initialize(DataModel model) {
        super.initialize(model);

        // NOTE: This list includes all names (even repeats) in chronological order
        // Get the list of restorable names associated with the currently selected Cell's Photo
        ObservableList<String> names = FXCollections.observableArrayList(model.getCurrentCell().getPhoto()
                .getNameHistory().subList(0, model.getCurrentCell().getPhoto().getNameHistory().size()));

        // Set the list of names so the user can view and select them
        namesToChoose.setItems(names);

        // Setting default name in ChoiceBox
        if (names.size() > 0) {
            namesToChoose.setValue(names.get(0));
        }
    }

    /**
     * Action taken when the user clicks the "Restore Name" button in the "Restore Name" window:
     * Restore the currently selected Photo to a previous name.
     */
    @FXML
    public void handleRestoreName() throws Exception {
        DataModel model = this.getModel();

        model.getPhotoList().restoreName(model.getCurrentCell().getPhoto(),
                namesToChoose.getSelectionModel().getSelectedIndex());

        // Update the list of Photos for the user to view
        model.setCells(model.getPhotoList().getPhotoMaster());

        // Close pop-up
        Stage stage = (Stage) namesToChoose.getScene().getWindow();
        stage.close();
    }
}
