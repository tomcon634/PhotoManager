package Application;

import Application.PhotoList.Cell;
import PhotoManaging.Photo;
import PhotoManaging.PhotoManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * The DataModel which is viewed by the user and manipulated by the Controllers
 */
public class DataModel extends Observable {
    /* The PhotoManager associated with this DataModel */
    private PhotoManager photoList;
    /* The list of Cells associated with photoList */
    private final ObservableList<Cell> cells = FXCollections.observableArrayList();
    /* The current working directory for this DataModel */
    private String directory;
    /* The Cell that this user currently has selected */
    private Cell currentCell;

    /**
     * Set the working directory for the UI.
     *
     * @param directory new working directory path for this DataModel
     */
    public void setDirectory(String directory) throws Exception {
        // Create a new PhotoManager for this directory
        this.directory = directory;
        photoList = new PhotoManager(Paths.get(directory));

        // Notify all observers that this model has changed
        setChanged();
        notifyObservers();
    }

    /**
     * Get the working directory for the UI.
     *
     * @return this DataModel's working directory path
     */
    public String getDirectory() {
        return this.directory;
    }

    /**
     * Set the PhotoManager for the UI.
     *
     * @param photoList new PhotoManager for this DataModel
     */
    public void setPhotoList(PhotoManager photoList) {
        this.photoList = photoList;

        // Notify all observers that this model has changed
        setChanged();
        notifyObservers();
    }

    /**
     * Get the PhotoManager for the UI.
     *
     * @return this DataModel's PhotoManager
     */
    public PhotoManager getPhotoList() {
        return this.photoList;
    }

    /**
     * Set the current Cell selected by the user.
     *
     * @param cell new current selected photo for this DataModel
     */
    public void setCurrentCell(Cell cell) {
        this.currentCell = cell;
    }

    /**
     * Get the current Cell selected by the user.
     *
     * @return this DataModel's current photo
     */
    public Cell getCurrentCell() {
        return this.currentCell;
    }

    /**
     * Takes a list of Photos and creates a list of Cells in order to populate the listView
     *
     * @param photos new list of Photos for this DataModel
     */
    public void setCells(List<Photo> photos) {
        ArrayList<Cell> cells = new ArrayList<>();
        for (Photo p : photos) {
            cells.add(new Cell(p, p.getIsFavourite()));
        }
        Cell selectedCell = this.getCurrentCell();
        this.cells.setAll(cells);

        // Retain the selected cell
        this.setCurrentCell(selectedCell);
    }

    /**
     * Get the list of Cells to be viewed by the user.
     *
     * @return this DataModel's current list of Photos
     */
    public ObservableList<Cell> getCells() {
        return this.cells;
    }
}
