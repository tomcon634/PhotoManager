package Application.ManagerAction.OpenTagManager;

import Application.Controller;
import Application.DataModel;
import PhotoManaging.PhotoManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

import java.util.Observable;
import java.util.Observer;

/**
 * Controller for the pop-up that displays when the user clicks "Open Tag Manager" under Photo Manager in the UI.
 */
public class OpenController extends Controller implements Observer {
    /* The text the user inputs to add a tag to the master list for this directory*/
    @FXML
    private TextField tagInput;
    /* The selectable ListView of tags */
    @FXML
    private ListView<String> tagsToChoose;
    /* The master list of tags associated with the current working directory, used to populate tagsToChoose*/
    private final ObservableList<String> tags = FXCollections.observableArrayList();

    /**
     * Initialize this OpenController.
     *
     * @param model the DataModel that this OpenController will control
     */
    @Override
    public void initialize(DataModel model) {
        super.initialize(model);
        model.addObserver(this);

        // Allow multiple tags to be chosen at once
        tagsToChoose.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Get the tags so the user can view and select them
        tags.setAll(model.getPhotoList().getTagMaster());
        tagsToChoose.setItems(tags);
    }

    /**
     * Add a tag to the master list of tags.
     */
    @FXML
    public void handleAddTagToMaster() {
        DataModel model = this.getModel();
        try {
            PhotoManager tempPhotoList = model.getPhotoList();
            tempPhotoList.addTag(tagInput.getCharacters().toString());
            model.setPhotoList(tempPhotoList);

            errorMessage("");
        } catch (Exception e) {
            errorMessage("Invalid tag");
        }
    }

    /**
     * Remove a tag from the master list of tags.
     */
    @FXML
    public void handleRemoveTagFromMaster() throws Exception {
        DataModel model = this.getModel();

        // Loop through every selected tag and remove it from the Photo Manager
        ObservableList<String> selectedTags = tagsToChoose.getSelectionModel().getSelectedItems();
        PhotoManager tempPhotoList = model.getPhotoList();
        for (String tag : selectedTags) {
            tempPhotoList.removeTag(tag);
        }

        // Update the list of Photos for the user to view
        model.setCells(tempPhotoList.getPhotoMaster());
        model.setPhotoList(tempPhotoList);

        errorMessage("");
    }

    /**
     * Update the list of tags when the model changes.
     */
    @Override
    public void update(Observable o, Object arg) {
        DataModel model = this.getModel();
        tags.setAll(model.getPhotoList().getTagMaster());
    }
}
