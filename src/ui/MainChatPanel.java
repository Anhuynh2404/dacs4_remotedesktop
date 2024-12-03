package ui;

import chat.ChatBus;
import common.CommonManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class MainChatPanel extends VBox {

    private Label connectionsLabel;
    private ArrayList<ChatPanel> chatPanels;
    private CommonManager commonBus;
    private int count;

    public MainChatPanel(CommonManager commonBus) {
        this.commonBus = commonBus;
        initializeUI();
    }

    private void initializeUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #f5f5f5;"); // Soft gray background

        this.count = 0;
        this.chatPanels = new ArrayList<>();

        // Panel hiển thị số lượng kết nối
        FlowPane connectionsPanel = new FlowPane();
        connectionsPanel.setPadding(new Insets(10));
        connectionsPanel.setStyle("-fx-background-color: #4682b4;"); // Steel blue for title bar
        connectionsLabel = new Label("All connections (" + this.count + ")");
        connectionsLabel.setTextFill(Color.YELLOW);
        connectionsLabel.setFont(Font.font("Arial", 14));
        connectionsPanel.getChildren().add(connectionsLabel);

        getChildren().add(connectionsPanel);
    }

    public void addCount(int n) {
        this.count += n;
        Platform.runLater(() -> connectionsLabel.setText("All connections (" + this.count + ")"));
    }

    public ArrayList<ChatPanel> getChatPanels() {
        return this.chatPanels;
    }

    public void addNewConnection(ChatBus chatBus) {
        Platform.runLater(() -> {
            ChatPanel chatPanel = new ChatPanel(this.commonBus, chatBus);
            chatPanel.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-border-radius: 5;");
            getChildren().add(chatPanel);
            chatPanels.add(chatPanel);

            this.addCount(1);
        });
    }
}
