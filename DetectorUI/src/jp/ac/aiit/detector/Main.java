package jp.ac.aiit.detector;

import jp.ac.aiit.Detector.DetectorResult;
import jp.ac.aiit.Detector.matcher.HistogramMatcher;
import jp.ac.aiit.Detector.util.Tool;
import jp.ac.aiit.detector.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.io.File;

import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_COLOR;

public class Main extends Application {

    public static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("detector.fxml"));
        primaryStage.setTitle("Detector");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
        System.out.println(System.getProperty("javafx.version"));
        stage=primaryStage;

    }


    public static void main(String[] args)
    {

        launch(args);
    }
}
