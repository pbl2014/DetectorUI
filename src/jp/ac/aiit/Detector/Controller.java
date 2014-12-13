package jp.ac.aiit.Detector;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Controller {

    @FXML
    private Label dirlabel;

    @FXML
    private void dirButtonAction(ActionEvent event) {
        // Button was clicked, do something...
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Please choose the path for target pictures!");

        //Show open file dialog
        File file = directoryChooser.showDialog(null);

        if(file!=null){
            //labelFile.setText(file.getPath());
            System.out.println(file.getPath());
            dirlabel.setText(file.getPath());
        }

    }


}
