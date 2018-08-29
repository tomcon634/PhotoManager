package Application.PhotoList;

import PhotoManaging.Photo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * The basic information associated with each row in the listView.
 * <p>
 * Adapted from
 * https://stackoverflow.com/questions/28843858/javafx-8-listview-with-checkboxes (22/11/17)
 */
public class Cell {
    /* The Photo associated with this Cell */
    private Photo photo;
    /* The boolean value of the checkbox of this Cell */
    private final BooleanProperty checked = new SimpleBooleanProperty();

    /**
     * Initialize a new Cell.
     *
     * @param photo   the Photo to be associated w/ this Cell
     * @param checked whether or not this Cell is checked
     */
    public Cell(Photo photo, boolean checked) {
        this.photo = photo;
        this.checked.set(checked);
    }

    /**
     * Return the boolean value which represents whether or not this Cell is checked by the user.
     *
     * @return the boolean checkbox value
     */
    BooleanProperty onProperty() {
        return this.checked;
    }

    /**
     * Get the Photo associated with this Cell for manipulation.
     *
     * @return the Photo associated with this Cell
     */
    public Photo getPhoto() {
        return this.photo;
    }

    /**
     * Return the name of the Photo associated with this Cell.
     *
     * @return a string representation of this Cell
     */
    @Override
    public String toString() {
        return this.photo.toString();
    }
}
