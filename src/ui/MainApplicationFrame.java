package ui;

import common.CommonManager;
import chat.ChatBus;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApplicationFrame extends Application {

    private CommonManager commonBus;
    private ServerConfigurationPanel serverPanel;
    private ClientConnectionPanel clientPanel;
    private MainChatPanel mainChatPanel;
    private TabPane tabPane;

    @Override
    public void start(Stage primaryStage) {
        commonBus = new CommonManager();

        serverPanel = new ServerConfigurationPanel(commonBus);
        clientPanel = new ClientConnectionPanel(commonBus, this);
        mainChatPanel = new MainChatPanel(commonBus);

        commonBus.setMainChatPanel(mainChatPanel);
        tabPane = new TabPane();
        Tab configTab = new Tab("Configuration");
        configTab.setClosable(false);

        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.5); // Chia 50/50
        splitPane.getItems().addAll(serverPanel.getContent(), clientPanel.getContent());

        configTab.setContent(splitPane);

        Tab chatTab = new Tab("Chat");
        chatTab.setClosable(false);
        chatTab.setContent(mainChatPanel);

        tabPane.getTabs().addAll(configTab, chatTab);
        MenuBar menuBar = createMenuBar();
        BorderPane rootLayout = new BorderPane();
        rootLayout.setTop(menuBar);
        rootLayout.setCenter(tabPane);
        Scene scene = new Scene(rootLayout, 1000, 600);
        primaryStage.setTitle("AH Remote");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

//    public void openChatWindow(ChatConnection chatConnection) {
//        ChatPanel chatPanel = new ChatPanel(mainChatPanel,  chatConnection.getChatBus());
//        Stage chatStage = new Stage();
//        chatStage.setTitle("Chat");
//        chatStage.setScene(new Scene(chatPanel, 600, 400));
//        chatStage.show();
//    }

    private MenuBar createMenuBar() {
        // Tạo menu File
        Menu fileMenu = new Menu("File");
        fileMenu.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setStyle("-fx-font-size: 14px;");
        exitItem.setOnAction(e -> System.exit(0)); // Thoát ứng dụng
        fileMenu.getItems().add(exitItem);

        // Tạo menu Settings
        Menu settingsMenu = new Menu("Settings");

        settingsMenu.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        MenuItem preferencesItem = new MenuItem("Preferences...");
        preferencesItem.setStyle("-fx-font-size: 14px;");
        settingsMenu.getItems().add(preferencesItem);

        // Tạo menu Help
        Menu helpMenu = new Menu("Help");
        helpMenu.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setStyle("-fx-font-size: 14px;");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);

        // Tạo MenuBar và thêm các menu
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, settingsMenu, helpMenu);
        menuBar.setStyle("-fx-background-color: #679ce2; -fx-pref-height: 35px;");

        return menuBar;
    }

    private void showAboutDialog() {
        Alert aboutDialog = new Alert(Alert.AlertType.INFORMATION);
        aboutDialog.setTitle("About AH Remote");
        aboutDialog.setHeaderText("AH Remote Application");
        aboutDialog.setContentText("This is a remote desktop application built with JavaFX.");
        aboutDialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
