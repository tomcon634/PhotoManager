package Application.PhotoList;

import Application.Controller;
import Application.DataModel;
import PhotoManaging.Photo;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

/**
 * A controller for the list of Photos section of the UI, located to the left and centre
 */
public class ListController extends Controller implements Observer {
    /* The list of Photos that the user can view and select from */
    @FXML
    private ListView<Cell> listView;
    /* The checkbox "View Favourites Only" which determines if only favourites are displayed or not */
    @FXML
    private CheckBox favCheckBox;
    /* The menu which pops up when the user right clicks on a listView cell */
    private ContextMenu menu = new ContextMenu();

    /**
     * Initialize the DataModel that this ListController will control and set up the listView.
     * <p>
     * Adapted from
     * https://stackoverflow.com/questions/28843858/javafx-8-listview-with-checkboxes (22/11/17)
     *
     * @param model the DataModel to initialize
     */
    @Override
    public void initialize(DataModel model) {
        super.initialize(model);
        model.addObserver(this);

        initMenu();
        initListView();

        // Toggle the favourite view when "View Favourites Only" checkbox is checked
        favCheckBox.selectedProperty().addListener((observable, oldChecked, newChecked) ->
                toggleViewFavourites(newChecked));

    }

    /**
     * Initialize the listView, where the user will view GraphicalCells which contain Photos.
     */
    private void initListView() {
        DataModel model = this.getModel();

        // Keep track of which Photo the user has selected
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldCell, newCell) ->
                model.setCurrentCell(newCell));

        // Creating custom cells for the listView
        Callback<Cell, ObservableValue<Boolean>> cellToBoolean = Cell::onProperty;
        listView.setCellFactory(lv -> new GraphicalCell(cellToBoolean));

        // Set the list of Photos so the user can view and select them
        listView.setItems(model.getCells());

        // Show the menu on right mouse click
        listView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                if (model.getCurrentCell() != null) {
                    menu.show(listView, event.getScreenX(), event.getScreenY());
                }
            }
        });
    }


    /**
     * Initialize menu, the ContextMenu that the user will see when right-clicking a GraphicalCell in the listView.
     * This includes creating each MenuItem which will populate the ContextMenu and
     * setting the onAction methods for each MenuItem.
     * <p>
     * Adapted from:
     * http://o7planning.org/en/11115/javafx-contextmenu-tutorial (30-11-17)
     * https://stackoverflow.com/questions/12516871/javafx-listview-contextmenu-getselecteditem-returns-null
     * (30-11-17)
     * https://stackoverflow.com/questions/28264907/javafx-listview-contextmenu (30-11-17)
     */
    private void initMenu() {
        DataModel model = this.getModel();

        MenuItem addTag = new MenuItem("Add Tag");
        MenuItem removeTag = new MenuItem("Remove Tag");
        MenuItem restoreName = new MenuItem("Restore To A Previous Name");
        MenuItem movePhoto = new MenuItem("Move To Another Directory");
        MenuItem viewPhoto = new MenuItem("View Full Size Photo");
        MenuItem openDir = new MenuItem("View In File Explorer");
        menu.getItems().setAll(addTag, removeTag, restoreName, movePhoto, viewPhoto, openDir);

        // Set the action taken when each menu option is selected
        addTag.setOnAction(event -> setStage("/Application/PhotoAction/AddTag/AddTagView.fxml",
                "Add Tag", 300, 500));
        removeTag.setOnAction(event -> setStage("/Application/PhotoAction/RemoveTag/RemoveView.fxml",
                "Remove Tag", 280, 400));
        restoreName.setOnAction(event -> setStage("/Application/PhotoAction/RestoreName/RestoreView.fxml",
                "Restore Name", 250, 150));
        movePhoto.setOnAction(event -> setStage("/Application/PhotoAction/MoveImage/MoveView.fxml",
                "Move Image", 400, 150));
        viewPhoto.setOnAction(event -> {
            // Getting the dimensions of the image so the pop-up can conform to it
            String path = model.getCurrentCell().getPhoto().getPath().toString();
            BufferedImage img = null;
            try {
                img = ImageIO.read(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert img != null;
            int width = img.getWidth();
            int height = img.getHeight();

            setStage("/Application/PhotoAction/ViewImage/ImageView.fxml",
                    "Viewing image " + path, width, height);
        });
        openDir.setOnAction(event -> {
            // Get the parent directory for the selected photo
            String strDir = model.getCurrentCell().getPhoto().getPath().toString();
            File dir = new File(strDir);
            dir = new File(dir.getParent());

            if (dir.isDirectory()) {
                // Open the parent directory in the OS's file explorer
                String userOS = System.getProperty("os.name");
                if (userOS.equals("Linux")) {   // Open directory in Linux
                    try {
                        Runtime.getRuntime().exec("xdg-open " + dir.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {                        // Open directory in Windows/Mac
                    try {
                        Desktop.getDesktop().open(dir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * Toggle whether the listView displays all Photos in the directory or only favourite Photos.
     *
     * @param isChecked the bool value that determines what listView is changed to
     */
    private void toggleViewFavourites(boolean isChecked) {

        DataModel model = this.getModel();
        if (model.getPhotoList() != null) {
            if (isChecked) {     // change listView to show only favourited Photos in directory
                ArrayList<Photo> favPhotos = new ArrayList<>();
                for (Photo photo : model.getPhotoList().getPhotoMaster()) {
                    if (photo.getIsFavourite()) {
                        favPhotos.add(photo);
                    }
                }
                model.setCells(favPhotos);
                addListenersToCells();
            } else {            // change listView to show all Photos in directory
                model.setCells(model.getPhotoList().getPhotoMaster());
                addListenersToCells();
            }
        }
    }

    /**
     * Add a listener to each cell that will change the cell's Photo's isFavourite value when the checkbox value
     * changes.
     */
    private void addListenersToCells() {
        DataModel model = this.getModel();
        for (Cell c : listView.getItems()) {
            c.onProperty().addListener((obs, wasChecked, isChecked) -> {
                // Set the isFavourite value of the checked/unchecked Photo to reflect the state of the check box
                c.getPhoto().setIsFavourite(isChecked);
                try {
                    // tracks change in the favList.txt
                    model.getPhotoList().setFavourite(c.getPhoto(), isChecked);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Update the list of Cells when the model changes.
     */
    @Override
    public void update(Observable o, Object arg) {
        DataModel model = this.getModel();
        Cell selectedCell = model.getCurrentCell();

        // Sort alphabetically
        ArrayList<Photo> photos = model.getPhotoList().getPhotoMaster();
        photos.sort(Comparator.comparing(Photo::toString));

        // Set the list of Photos for the user to view
        model.setCells(photos);

        // Retain the selected cell
        model.setCurrentCell(selectedCell);

        addListenersToCells();
    }
}
