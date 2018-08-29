package Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The Photo Manager application.
 * <p>
 * All the code within the Application package was influenced by:
 * https://docs.oracle.com/javafx/2/get_started/fxml_tutorial.htm (11/11/18)
 * http://www.javafxtutorials.com/tutorials/switching-to-different-screens-in-javafx-and-fxml/(11/11/18)
 * https://stackoverflow.com/questions/32342864/applying-mvc-with-javafx (11/11/18)
 */
public class Main extends Application {

    /**
     * Start the application.
     *
     * @param stage the application's stage
     * @throws IOException if any FXML file cannot be found
     */
    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();
        BorderPane sidePanel = new BorderPane();

        // Loading the Directory Choice section of the UI, located at the top right
        FXMLLoader dirLoader = new FXMLLoader(getClass().getResource("Directory/DirectoryView.fxml"));
        sidePanel.setTop(dirLoader.load());
        Controller dirControl = dirLoader.getController();

        // Loading the Photo Manager Actions section of the UI, located at the bottom right
        FXMLLoader managerLoader = new FXMLLoader(getClass().getResource("ManagerAction/ManagerView.fxml"));
        sidePanel.setBottom(managerLoader.load());
        Controller managerControl = managerLoader.getController();

        // Loading the view of the list of Photos section of the UI, located to the left and centre
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("PhotoList/ListView.fxml"));
        root.setCenter(mainLoader.load());
        Controller listControl = mainLoader.getController();

        root.setRight(sidePanel);

        // Passing the DataModel to the Controllers
        DataModel model = new DataModel();
        dirControl.initialize(model);
        listControl.initialize(model);
        managerControl.initialize(model);


        Scene scene = new Scene(root, 1000, 620);

        stage.setTitle("Photo Manager");
        stage.setScene(scene);
        stage.show();
    }
}
