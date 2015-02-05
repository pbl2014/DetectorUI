package jp.ac.aiit.detector;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;



public class Main extends Application {


    public static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("detector.fxml"));
        primaryStage.setTitle("Detector");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
        System.out.println(System.getProperty("javafx.version"));
        stage = primaryStage;
    }







    public static void main(String[] args)
    {

        launch(args);
    }
}
