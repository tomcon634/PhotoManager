package Application.PhotoList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * A graphical assembly of the information found in Cell, used to populate the listView.
 * <p>
 * Adapted from:
 * http://grepcode.com/file/repo1.maven.org/maven2/net.java.openjfx.backport/
 * openjfx-78-backport/1.8.0-ea-b96.1/javafx/scene/control/cell/CheckBoxListCell.java (29/11/2017)
 * http://www.billmann.de/2013/07/03/javafx-custom-listcell/ (29/11/2017)
 * https://stackoverflow.com/questions/39840921/ (29/11/2017)
 * create-checkboxlistcell-with-custom-background-color-in-javafx-8 (29/11/2017)
 * https://stackoverflow.com/questions/33592308/javafx-how-to-put-imageview-inside-listview (29/11/2017)
 */
class GraphicalCell extends CheckBoxListCell<Cell> {
    /* The skeleton of this GraphicalCell, where the ImageView and CheckBox will be placed */
    private final GridPane grid = new GridPane();
    /* A CheckBox which is modifiable by the user */
    private final CheckBox check = new CheckBox();
    /* An ImageView of the photo file associated with the photo */
    private final ImageView imageView = new ImageView();
    /* The boolean which check is to be bound to */
    private ObservableValue<Boolean> booleanProperty;

    /**
     * Initialize a new GraphicalCell by setting up the callback used to bind booleanProperty and check, and initializing
     * the grid skeleton.
     *
     * @param getSelectedProperty the bool value for this GraphicalCell's booleanProperty to follow
     */
    GraphicalCell(Callback<Cell, ObservableValue<Boolean>> getSelectedProperty) {
        setSelectedStateCallback(getSelectedProperty);

        grid.setHgap(10);
        grid.setVgap(4);
        grid.setPadding(new Insets(0, 10, 0, 10));
        grid.add(imageView, 1, 0);
        grid.add(check, 0, 0);
        setGraphic(grid);
    }

    /**
     * Populate this GraphicalCell with information so it is ready to be displayed.
     *
     * @param item  the Cell object used to populate the GraphicalCell with info
     * @param empty whether or not this row is empty
     */
    @Override
    public void updateItem(Cell item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.toString());

            // Configure imageView to display this Cell's associated photo file
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            Image image = new Image("file:" + item.getPhoto().getPath().toString());
            imageView.setImage(image);

            // Bind check with booleanProperty using a Callback
            Callback<Cell, ObservableValue<Boolean>> callback = getSelectedStateCallback();
            if (booleanProperty != null) {
                check.selectedProperty().unbindBidirectional((BooleanProperty) booleanProperty);
            }
            booleanProperty = callback.call(item);
            check.selectedProperty().bindBidirectional((BooleanProperty) booleanProperty);

            setGraphic(grid);
        }
    }
}