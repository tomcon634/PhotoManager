package Application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * A controller which manipulates the model based on user input.
 */
public class Controller {
    /* Section of UI where error message is displayed, if one exists */
    @FXML
    private Text errorText;
    /* The model which this Controller controls */
    private DataModel model;

    /**
     * Initialize this Controller.
     *
     * @param model the DataModel that this Controller will control
     */
    public void initialize(DataModel model) {
        this.model = model;
    }

    /**
     * Get the DataModel that this Controller controls.
     *
     * @return this Controller's model
     */
    public DataModel getModel() {
        return this.model;
    }

    /**
     * Displays an error message.
     */
    protected void errorMessage(String message) {
        if (errorText != null) {
            errorText.setFill(Color.RED);
            errorText.setText(message);
        }
    }

    /**
     * Open a new stage (pop-up) and hand off the model to its controller
     *
     * @param FXMLFile the FXML file containing the pop-up stage and specifying the new stage's controller
     * @param title    the title of the pop-up stage
     * @param width    the width of the pop-up stage
     * @param height   the height of the pop-up stage
     */
    protected void setStage(String FXMLFile, String title, int width, int height) {
        DataModel model = this.getModel();

        try {
            if (model.getPhotoList() != null) {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLFile));
                BorderPane root = new BorderPane();

                root.setCenter(loader.load());

                // Passing the model to the new stage's controller
                Controller control = loader.getController();
                control.initialize(model);

                // Opening the new stage
                Scene scene = new Scene(root, width, height, Color.GREY.brighter());
                stage.setTitle(title);
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
