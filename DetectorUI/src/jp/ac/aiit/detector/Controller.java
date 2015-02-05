package jp.ac.aiit.detector;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import jp.ac.aiit.Detector.DetectorResult;
import jp.ac.aiit.Detector.matcher.HistogramMatcher;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import static org.apache.commons.io.FileUtils.getUserDirectory;
import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_COLOR;


public class Controller
{

    private TableView<myImageList> tableView =new TableView<myImageList>();



    private final ObservableList<myImageList> data = FXCollections.observableArrayList();



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
        //detectorライブラリー
        hm = new HistogramMatcher();


        String[] filelist = getImageFileList();
        List imagesList = new ArrayList();


        for (String imagePath : filelist) {

            System.out.println("length is: " + filelist.length);
            File file = new File(imagePath);
            System.out.println("pic_name is: " +file.getPath());

            Image image =new Image(file.toURI().toString());
            int imageHeight=(int)image.getHeight();
            int imageWidth=(int)image.getWidth();
            //画像サイズ
            String ima=imageHeight+" × "+imageWidth;

            imagesList.add(new myImageList(file.getAbsoluteFile().toString(), file.getName(), file.getParentFile().toString(), file.length() / 1024 + "KB", ima));

            hm.addImage(file.getPath());
            }
        d_path.setText("  imageFile: "+ filelist.length+ " p");
        data.addAll(imagesList);
        tableView.setEditable(true);
        tableView.setPrefWidth(1000);
        tableView.setPrefHeight(500);

        TableColumn imageFileCol=new TableColumn("画像");
        imageFileCol.setMinWidth(300);
        imageFileCol.setCellValueFactory(
                new PropertyValueFactory<myImageList, String>("imageFile"));

        TableColumn imageNameCol=new TableColumn("ファイル名");
        imageNameCol.setMinWidth(100);
        imageNameCol.setCellValueFactory(
                new PropertyValueFactory<myImageList, String>("imageName"));

        TableColumn imageFolderCol=new TableColumn("フォルダ ");
        imageFolderCol.setMinWidth(250);
        imageFolderCol.setCellValueFactory(
                new PropertyValueFactory<myImageList, String>("imageFolder"));

        TableColumn imageLengthCol=new TableColumn("サイズ");
        imageLengthCol.setMinWidth(100);
        imageLengthCol.setCellValueFactory(
                new PropertyValueFactory<myImageList, String>("imageLength"));

        TableColumn imageSizeCol=new TableColumn("画像サイズ");
        imageSizeCol.setMinWidth(100);
        imageSizeCol.setCellValueFactory(
                new PropertyValueFactory<myImageList, String>("imageSize"));

        tableView.setItems(data);
        tableView.getColumns().addAll(imageFileCol,imageNameCol,imageFolderCol,imageLengthCol,imageSizeCol);
        final VBox vbox = new VBox();
        //vbox.setSpacing(5);
        //vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(tableView);
        imaepane.getChildren().add(vbox);
    }

    /**
     *
     * @return 画像path リスト
     */
    public String[] getImageFileList() {

        ArrayList<String> fileList = new ArrayList<String>();
        String[] fileExList = new String[]{"jpg", "gif", "png", "JPG", "GIF", "PNG", "jpeg", "JPEG"};
        Iterator<File> files = FileUtils.iterateFiles(getFolderPath(), fileExList, true);
        while (files.hasNext()) {
            fileList.add(files.next().getAbsolutePath());

        }
        return fileList.toArray(new String[fileList.size()]);

    }



    /**
     *
     * open Directory
     */
    private File getFolderPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder Containing Images ");
        File file = directoryChooser.showDialog(Main.stage);
        directoryChooser.setInitialDirectory(getUserDirectory());
        if(file != null) {
            return file.getAbsoluteFile();
        } else {
            System.out.println("No folder selected ,exiting...");
            System.exit(0);
        }
        return  null;
    }

    /**
     *
     * @param event
     */
    public void indetector(ActionEvent event) {




        //detectorライブラリー開始
        hm.setImageColorType(CV_LOAD_IMAGE_COLOR);
        hm.setAllowableValue(0.95);
        DetectorResult result = hm.run();
        System.out.println(result);


        System.out.println("重複処理中です。");//画像リスト
        List groupPics = new ArrayList();

        for (Map.Entry<String, Map<String, Double>> entry: result.toMap().entrySet()) {

            List<String> innergroup = new ArrayList<String>();
            if(entry.getValue().size() ==1) continue;
            for (Map.Entry<String, Double> inner: entry.getValue().entrySet()) {

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
        Scene scene =new Scene(rootstageOverlapimage,800,630, Color.WHITE);
        //tabpaneにtab　追加
        TabPane tabPane =new TabPane();
        //borderpnaeにtabpane　追加
        BorderPane borderPane= new BorderPane();
        //groupPics.size()は重複したいる画像グループの数
        //int countgroup=groupPics.size();
        //重複画像リスト
        //ObservableList Overlapimages= FXCollections.observableArrayList();


        for(int i=1; i<groupPics.size()+1; i++ )
        {
            //tab にhbox 追加
            Tab tab = new Tab();
            tab.setText("グループ"+i);
            //hboxにtab追加
            HBox hBox=new HBox();

            ObservableList<myImageList1> data1 = FXCollections.observableArrayList();

            //処理画像をリスト　DetectorPics　に保存
             List<String> DetectorImageList = (List)groupPics.get(i-1);



            //重複画像をリストビューを使って表示する
            TableView<myImageList1> tableView1 =new TableView<myImageList1>();
            //画像リストをループで　imagePath　に保存

            for (String imagePath :DetectorImageList) {

                    //重複処理画像の流れ
                File file = new File(imagePath);
                //Image DetectorImage =new Image(file.toURI().toString());
                String imageName=file.getName();
                System.out.println("xx"+imageName);
                List imagesList1 = new ArrayList();

                //imageView　に重複画像保存、imageViewの設定
//                ImageView imageView = new ImageView();
//                imageView.setFitHeight(150);
//                imageView.setFitWidth(150);
//                imageView.setPreserveRatio(true);
//                imageView.setSmooth(false);
//                imageView.setCache(true);
//                imageView.setImage(DetectorImage);
               // Overlapimages.addAll(imageView);
                imagesList1.add(new myImageList1(file.getAbsoluteFile().toString(), file.getName()));
                data1.addAll(imagesList1);
                tableView1.setItems(data1);

                tableView1.setEditable(true);
                tableView1.setPrefWidth(700);
                tableView1.setPrefHeight(500);
            }

            hBox.getChildren().add(tableView1);
            tab.setContent(hBox);
            tabPane.getTabs().add(tab);

            TableColumn imageFileCol=new TableColumn("画像");
            imageFileCol.setMinWidth(300);
            imageFileCol.setCellValueFactory(
                    new PropertyValueFactory<myImageList1, String>("imageFile"));

            TableColumn imageNameCol=new TableColumn("ファイル名");
            imageNameCol.setMinWidth(100);
            imageNameCol.setCellValueFactory(
                    new PropertyValueFactory<myImageList1, String>("imageName"));

            tableView1.getColumns().addAll(imageFileCol,imageNameCol);





        Button buttonright=new Button();
        buttonright.setPrefWidth(100);
        buttonright.setPrefHeight(550);
        buttonright.setText("delete");
        buttonright.setAlignment(Pos.CENTER);
        buttonright.setFont(Font.font(20));


//        buttonright.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent t) {
//                if (tableView1.getSelectionModel().getSelectedItems() != null) {
//                    data1.removeAll(getUniqueItemList(tableView1.getSelectionModel().getSelectedItems()));
//
//                }
//            }
//        });
            HBox hBoxright=new HBox();

            hBoxright.getChildren().add(buttonright);
            borderPane.setRight(hBoxright);
        }




        //int countimage=data1.size();
        //Windowsを表示、中に重複画像ある
        Label labelhead=new Label();
        labelhead.setPrefWidth(700);
        labelhead.setPrefHeight(30);
        labelhead.setText("Detector");
        labelhead.setAlignment(Pos.CENTER);

        //画像情報表示
        Label labelbottom=new Label();
        labelbottom.setPrefWidth(700);
        labelbottom.setPrefHeight(30);
        //String str = String.valueOf(countgroup);
        //String str1 = String.valueOf(countimage);
       // labelbottom.setText("検索した画像は: "+ str1+ " p , 重複画像グループ: " +str+" 個");
        labelbottom.setAlignment(Pos.CENTER_RIGHT);


        HBox hBoxhead=new HBox();
        HBox hBoxbottom=new HBox();


        hBoxhead.getChildren().add(labelhead);
        hBoxbottom.getChildren().add(labelbottom);


        borderPane.setTop(hBoxhead);
        borderPane.setCenter(tabPane);
        borderPane.setBottom(hBoxbottom);


        rootstageOverlapimage.getChildren().add(borderPane);
        stageOverlapimage.setScene(scene);
        stageOverlapimage.show();
    }


    public static ObservableList getUniqueItemList(ObservableList list){
        Set set = new HashSet(list);
        return FXCollections.observableArrayList(set);
    }

    //システム退出
    public  void detec_exit(ActionEvent event)
    {
        System.exit(0);
    }

    //-------------------画像の各プロパティーを設定する------------
    public static class myImageList {

        private SimpleStringProperty imageFile;
        private SimpleStringProperty imageName;
        private SimpleStringProperty imageFolder;
        private SimpleStringProperty imageLength;
        private SimpleStringProperty imageSize;


        private myImageList(String iFile, String iName, String iFolder,String iLength,String iSize) {
            this.imageFile=new SimpleStringProperty(iFile);
            this.imageName=new SimpleStringProperty(iName);
            this.imageFolder=new SimpleStringProperty(iFolder);
            this.imageLength=new SimpleStringProperty(iLength);
            this.imageSize=new SimpleStringProperty(iSize);


        }

        public String getImageFile() {
            return imageFile.get();
        }

        public void setImageFile(String iFile) {
            imageFile.set(iFile);
        }

        public String getImageName() {
            return imageName.get();
        }

        public void setImageName(String iFile) {
            imageName.set(iFile);
        }
        public String getImageFolder() {
            return imageFolder.get();
        }

        public void setImageFolder(String iFile) {
            imageFolder.set(iFile);
        }
        public String getImageLength() {
            return imageLength.get();
        }

        public void setImageLength(String iFile) {
            imageLength.set(iFile);
        }
        public String getImageSize() {
            return imageSize.get();
        }

        public void setImageSize(String iFile) {
            imageSize.set(iFile);
        }
    }
    //-----------------------------//

    //----------------重複-------------//
    public static class myImageList1 {

        private SimpleStringProperty imageFile;
        private SimpleStringProperty imageName;




        private myImageList1(String iFile, String iName) {
            this.imageFile=new SimpleStringProperty(iFile);
            this.imageName=new SimpleStringProperty(iName);




        }

        public String getImageFile() {
            return imageFile.get();
        }

        public void setImageFile(String iFile) {
            imageFile.set(iFile);
        }

        public String getImageName() {
            return imageName.get();
        }

        public void setImageName(String iFile) {
            imageName.set(iFile);
        }

    }
    //------------------------------------------------


}
