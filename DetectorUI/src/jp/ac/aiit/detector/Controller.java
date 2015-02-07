package jp.ac.aiit.detector;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import jp.ac.aiit.Detector.DetectorResult;
import jp.ac.aiit.Detector.matcher.HistogramMatcher;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.*;
import javafx.event.ActionEvent;

import static org.apache.commons.io.FileUtils.getUserDirectory;
import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_COLOR;


public class Controller
{

    public static final ObservableList images= FXCollections.observableArrayList();

    HistogramMatcher hm;
    @FXML private Label d_path;
    @FXML private AnchorPane imaepane;
    @FXML private Image image;

    /**
     * Chooser The Directoryボタンのaction
     * listView :imageviewを表示するビュー
     * filelist :getImageFileList()メソッドから選択した全て画像フィアルのリスト
     * 画像の流れは以下の通り：
     * filelistーー＞ imagePath ーー＞imageFileーー＞image-->imageview-->images-->listViewーー＞imaepane
     *
     */
    public  void opendirectory(ActionEvent event)
    {
        hm = new HistogramMatcher();

        final ListView<String> listView =new ListView<String>();
        String[] filelist = getImageFileList();
        for (String imagePath : filelist) {
                System.out.println("length is: " + filelist.length);
                File imageFile = new File(imagePath);
                System.out.println("pic_name is: " +imageFile.getPath());
                image = new Image(imageFile.toURI().toString());
                ImageView imageView = new ImageView();
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(false);
                imageView.setCache(true);
                imageView.setImage(image);
                images.addAll(imageView);

                hm.addImage(imageFile.getPath());
            }
        listView.setItems(images);
        listView.setPrefWidth(1000);
        listView.setFixedCellSize(100);
        //listView.setOrientation(Orientation.HORIZONTAL);
        imaepane.getChildren().add(listView);


    }

    /**
     *
     * @return 画像path リスト
     */
    public String[] getImageFileList()
    {

        ArrayList<String> fileList = new ArrayList<String>();
        String[] fileExList=new String[]{"jpg","gif","png","JPG","GIF","PNG","jpeg","JPEG"};
        Iterator<File> files = FileUtils.iterateFiles(getFolderPath(), fileExList, true);
        while (files.hasNext())
        {
            fileList.add(files.next().getAbsolutePath());

        }
        return fileList.toArray(new String[fileList.size()]);

    }

    /**
     *
     * open Directory
     */
    private File getFolderPath()
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder Containing Images ");
        File file = directoryChooser.showDialog(Main.stage);
        directoryChooser.setInitialDirectory(getUserDirectory());
        if(file != null)
        {
            d_path.setText("path:" + file.getAbsolutePath());
            return file.getAbsoluteFile();
        }
        else
        {
            System.out.println("No folder selected ,exiting...");
            System.exit(0);
        }
        return  null;
    }


    /**
     *
     * @param event
     */
    public void indetector(ActionEvent event)
    {

        //detectorライブラリー開始
        hm.setImageColorType(CV_LOAD_IMAGE_COLOR);
        hm.setAllowableValue(0.95);
        DetectorResult result = hm.run();

        //イベント処理開始
        //---------------新しい-------------------//

        //テスト用画像リスト
        System.out.println("重複処理中です。");//画像リスト
        List groupPics = new ArrayList();

        for (Map.Entry<String, Map<String, Double>> entry: result.toMap().entrySet()) {

            List<String> innergroup = new ArrayList<String>();
            if(entry.getValue().size() ==1) continue;
            for (Map.Entry<String, Double> inner: entry.getValue().entrySet()) {
                //テスト用画像リスト
                System.out.println("重複処理中です。");
                //重複画像グループ１、中身は二つの画像フィアル
                innergroup.add(inner.getKey());
            }
            groupPics.add(innergroup);
        }


        System.out.println("groupPics length is"+ groupPics.size());

        Stage stageOverlapimage =new Stage();
        stageOverlapimage.setTitle("重複画像グループ");
        //Group　View　に borderpane 追加
        Group rootstageOverlapimage =new Group();
        //画面の大きさと色設定
        Scene scene =new Scene(rootstageOverlapimage,800,1000, Color.WHITE);
        //tabpaneにtab　追加
        TabPane tabPane =new TabPane();
        //borderpnaeにtabpane　追加
        BorderPane borderPane= new BorderPane();
        //groupPics.size()は重複したいる画像グループの数
        for(int i=1; i<groupPics.size()+1; i++ )
        {
            //tab にhbox 追加
            Tab tab = new Tab();
            tab.setText("グループ"+i);
            //hboxにtab追加
            HBox hBox=new HBox();
            //重複画像リスト
            ObservableList Overlapimages= FXCollections.observableArrayList();

            //処理画像をリスト　DetectorPics　に保存
            List<String> DetectorImageList = (List)groupPics.get(i-1);

            //重複画像をリストビューを使って表示する
            ListView<String> OverlapimageslistView = new ListView<String>();
            //画像リストをループで　imagePath　に保存
            for (String imagePath :DetectorImageList) {
                //

                //重複処理画像の流れ
                File imageFile = new File(imagePath);
                Image DetectorImage =new Image(imageFile.toURI().toString());

                //imageView　に重複画像保存、imageViewの設定
                ImageView imageView = new ImageView();
                imageView.setFitHeight(150);
                imageView.setFitWidth(150);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(false);
                imageView.setCache(true);
                imageView.setImage(DetectorImage);
                Overlapimages.addAll(imageView);

                OverlapimageslistView.setItems(Overlapimages);
                OverlapimageslistView.setPrefWidth(1000);
                OverlapimageslistView.setFixedCellSize(150);
            }

            hBox.getChildren().add(OverlapimageslistView);
            tab.setContent(hBox);
            tabPane.getTabs().add(tab);
        }
        //Windowsを表示、中に重複画像ある
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefHeightProperty().bind(scene.widthProperty());
        borderPane.setCenter(tabPane);
        rootstageOverlapimage.getChildren().add(borderPane);
        stageOverlapimage.setScene(scene);
        stageOverlapimage.show();



    }
}
