package Application.PhotoAction.AddTag;

import Application.Controller;
import Application.DataModel;
import PhotoManaging.Photo;
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
 * Controls the pop-up that displays when the user selects "Add Tag".
 */
public class AddTagController extends Controller implements Observer {
    /* The text the user inputs to add a new tag to the selected Cell's Photo */
    @FXML
    private TextField tagInput;
    /* The selectable ListView of tags */
    @FXML
    private ListView<String> tagsToChoose;
    /* The master list of tags associated with the current working directory, used to populate tagsToChoose*/
    private final ObservableList<String> tags = FXCollections.observableArrayList();

    /**
     * Initialize the AddTagController.
     *
     * @param model the DataModel that this AddTagController will control
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
     * Action taken when the user selects the "Add" button under the list of tags:
     * Add one or more tags to the selected Cell's Photo from the master list of tags.
     */
    @FXML
    public void handleAddTagFromList() throws Exception {
        DataModel model = this.getModel();
        PhotoManager tempPhotoList = model.getPhotoList();

        // Loop through every selected tag and add each to the selected Cell's Photo
        ObservableList<String> selectedTags = tagsToChoose.getSelectionModel().getSelectedItems();
        for (String tag : selectedTags) {
            Photo photo = model.getCurrentCell().getPhoto();
            tempPhotoList.addTag(tag, photo);
        }

        // Update the list of Cells for the user to view
        model.setCells(model.getPhotoList().getPhotoMaster());
        model.setPhotoList(tempPhotoList);

        errorMessage("");
    }

    /**
     * Action taken when the user selects the "Add" button under the TextField:
     * Add a new tag to the selected Cell's Photo that isn't already in the master list of tags.
     */
    @FXML
    public void handleAddTagFromInput() {
        DataModel model = this.getModel();
        PhotoManager tempPhotoList = model.getPhotoList();

        try {
            tempPhotoList.addTag(tagInput.getCharacters().toString(), model.getCurrentCell().getPhoto());

            // Update the list of Photos for the user to view
            model.setCells(tempPhotoList.getPhotoMaster());
            model.setPhotoList(tempPhotoList);

            errorMessage("");
        } catch (Exception e) {
            errorMessage("Invalid tag");
        }
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
