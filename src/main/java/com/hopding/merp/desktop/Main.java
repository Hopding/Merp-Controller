package com.hopding.merp.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is the main class that launches the GUI.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/merp-desktop-v1.fxml"));
        primaryStage.setTitle("MERP Controller");
        primaryStage.setScene(new Scene(root, 500, 350));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
