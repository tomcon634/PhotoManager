package Application.PhotoAction.RemoveTag;

import Application.Controller;
import Application.DataModel;
import PhotoManaging.Photo;
import PhotoManaging.PhotoManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.util.Observable;
import java.util.Observer;

/**
 * Controller for the pop-up that displays when the user clicks "Remove Tag" under Photo Actions in the UI.
 */
public class RemoveTagController extends Controller implements Observer {
    /* The selectable ListView of tags */
    @FXML
    private ListView<String> tagsToChoose;
    /* The master list of tags associated with the current working directory, used to populate tagsToChoose*/
    private final ObservableList<String> tags = FXCollections.observableArrayList();

    /**
     * Initialize this RemoveTagController.
     *
     * @param model the DataModel that this RemoveTagController will control
     */
    @Override
    public void initialize(DataModel model) {
        super.initialize(model);
        model.addObserver(this);

        // Allow multiple tags to be chosen at once
        tagsToChoose.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Set the list of tags so the user can view and select them
        tags.setAll(model.getCurrentCell().getPhoto().getTags());
        tagsToChoose.setItems(tags);
    }

    /**
     * Action taken when the user clicks the "Remove" button:
     * Remove the currently selected tag(s) from the currently selected Photo.
     */
    @FXML
    public void handleRemoveTagAction() throws Exception {
        DataModel model = this.getModel();
        PhotoManager tempPhotoList = model.getPhotoList();

        // Loop through every selected tag and remove it from the Photo
        ObservableList<String> selectedTags = tagsToChoose.getSelectionModel().getSelectedItems();
        for (String tag : selectedTags) {
            Photo photo = model.getCurrentCell().getPhoto();
            tempPhotoList.removeTag(tag, photo);
        }

        // Update the list of Photos for the user to view
        model.setCells(model.getPhotoList().getPhotoMaster());
        model.setPhotoList(model.getPhotoList());
    }

    /**
     * Update the list of tags when the model changes.
     */
    @Override
    public void update(Observable o, Object arg) {
        DataModel model = this.getModel();
        tags.setAll(model.getCurrentCell().getPhoto().getTags());
    }
}
