package jp.ac.aiit.detector;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.Initializable;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import static java.lang.Thread.sleep;
import static org.apache.commons.io.FileUtils.getUserDirectory;

public class Controller
{
    //@FXML private ImageView imageView;
    @FXML private Label d_path;
    @FXML private AnchorPane picpanel;
    private List<String> imagesList;

    public  void opendirectory(ActionEvent event)
    {
        String[] filelist = getImageFileList();
        for(String imagePath : filelist)
        {
            System.out.println("length is:"+filelist.length);
            File imageFile= new File(imagePath);
            Label lb= new Label();
            lb.setText("xxx" + imageFile.getName());
            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView =new ImageView();
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(false);
            imageView.setCache(true);
            imageView.setImage(image);
            picpanel.getChildren().add(imageView);
            lb.setAlignment(Pos.TOP_LEFT);
            d_path.setText("path:"+getFolderPath());
        }
    }

    public String[] getImageFileList()
    {
        ArrayList<String> fileList = new ArrayList<String>();
        String[] fileExList=new String[]{"jpg","gif","png","JPG","GIF","PNG","jpeg","JPEG"};
        Iterator<File> files = FileUtils.iterateFiles(getFolderPath(), fileExList, true);
        while (files.hasNext())
        {
            fileList.add(files.next().getAbsolutePath());

        }
        System.out.println("大きさは"+fileList.size());
        return fileList.toArray(new String[fileList.size()]);

    }


    private File getFolderPath()
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder Containing Images ");
        directoryChooser.setInitialDirectory(getUserDirectory());
        File file = directoryChooser.showDialog(null);
        if(file != null)
        {

            System.out.println(file.getAbsoluteFile());
            return file.getAbsoluteFile();

        }
        else
        {
            System.out.println("No folder selected ,exiting...");
            System.exit(0);
        }
        return  null;
    }







}
