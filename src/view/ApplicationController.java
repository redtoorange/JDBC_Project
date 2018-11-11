package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ApplicationController extends Application {
    private MainViewController mainView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ApplicationController.class.getResource("MainView.fxml"));
        Pane root = loader.load();

        mainView = loader.getController();
        mainView.setController(this);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ITEC340 - HW11");
        primaryStage.show();
    }
}