package jp.ac.aiit.detector;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
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
        hm.setImageColorType(CV_LOAD_IMAGE_COLOR);
        hm.setAllowableValue(0.99);
        DetectorResult result = hm.run();

        //画像リスト
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

        //groupPics.add(group1);
        //groupPics.add(group2);

        //タブ定義　重複画像のグループ数と同じ
        List<Tab> groupTabs = new ArrayList<Tab>();
        //タブに表示画像
        Image image1;

        //重複画像のグループ数とグループなかの画像フィアル
        for (int i = 1; i < groupPics.size()+1; i++) {
            Tab tab = new Tab();
            tab.setText("グループ" + i);
            //タブの中にAnchorPane を貼り付けます
            AnchorPane pane =new AnchorPane();
            tab.setContent(pane);
            //重複画像入れる
            ObservableList images1= FXCollections.observableArrayList();
            //画像ファイルの変更
            List<String> pics = (List)groupPics.get(i-1);
            //画像表示
            ListView<String> listView1 = new ListView<String>();
            for (String imagePath :pics) {
                File imageFile = new File(imagePath);
                image1 = new Image(imageFile.toURI().toString());
                ImageView imageView = new ImageView();
                imageView.setFitHeight(150);
                imageView.setFitWidth(150);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(false);
                imageView.setCache(true);
                imageView.setImage(image1);
                images1.addAll(imageView);
                listView1.setItems(images1);
                listView1.setPrefWidth(1000);
                listView1.setFixedCellSize(100);
            }
            pane.getChildren().add(listView1);
            groupTabs.add(tab);
        }
        //グループ化した画像ファイルのタブを保存する
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(groupTabs);
        //TabPane を表示する
        final Pane pane= new Pane();
        pane.getChildren().add(tabPane);
        Scene scene = new Scene(pane, 1000, 800);
        Main.stage.setScene(scene);
        Main.stage.setTitle("Detector");
        Main.stage.show();
    }
}
