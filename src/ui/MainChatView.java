//package ui;
//
//import communication.ChatConnection;
//import common.CommonManager;
//import javafx.application.Platform;
//import javafx.geometry.Insets;
//import javafx.scene.control.Label;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.FlowPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//
//import java.util.ArrayList;
//
//public class MainChatView {
//
//    private Label connectionsLabel;
//    private ArrayList<ChatView> chatPanels;
//    private CommonManager commonBus;
//    private int count;
//    private VBox rootLayout;
//
//    public MainChatView(CommonManager commonBus) {
//        this.commonBus = commonBus;
//        this.count = 0;
//        this.chatPanels = new ArrayList<>();
//        initializeUI();
//    }
//
//    private void initializeUI() {
//        rootLayout = new VBox(10);
//        rootLayout.setPadding(new Insets(10));
//        rootLayout.setStyle("-fx-background-color: #f5f5f5;"); // Soft gray background
//
//        // Panel hiển thị số lượng kết nối
//        FlowPane connectionsPanel = new FlowPane();
//        connectionsPanel.setPadding(new Insets(10));
//        connectionsPanel.setStyle("-fx-background-color: #4682b4;"); // Steel blue for title bar
//        connectionsLabel = new Label("All connections (" + this.count + ")");
//        connectionsLabel.setTextFill(Color.YELLOW);
//        connectionsLabel.setFont(Font.font("Arial", 14));
//        connectionsPanel.getChildren().add(connectionsLabel);
//
//        rootLayout.getChildren().add(connectionsPanel);
//    }
//
//    public VBox getLayout() {
//        return rootLayout;
//    }
//
//    public void addCount(int n) {
//        this.count += n;
//        Platform.runLater(() -> connectionsLabel.setText("All connections (" + this.count + ")"));
//    }
//
//    public ArrayList<ChatView> getChatPanels() {
//        return this.chatPanels;
//    }
//
//    public void addNewConnection(ChatConnection chatBus) {
//        Platform.runLater(() -> {
//            ChatView chatPanel = new ChatView(this, this.commonBus, chatBus);
//            chatPanel.getContent().setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-border-radius: 5;");
//            rootLayout.getChildren().add(chatPanel.getContent());
//            chatPanels.add(chatPanel);
//
//            this.addCount(1);
//        });
//    }
//
//    public void removeChatView(ChatView chatView) {
//        Platform.runLater(() -> {
//            chatPanels.remove(chatView);
//            rootLayout.getChildren().remove(chatView.getContent());
//            this.addCount(-1);
//        });
//    }
//}
