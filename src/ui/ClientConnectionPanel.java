package ui;

import chat.ChatBus;
import common.CommonManager;
import chat.ChatBus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ClientConnectionPanel {

    private TextField clientIpField;
    private TextField clientPortField;
    private PasswordField clientPasswordField;
    private CommonManager commonManager;
    private VBox rootLayout;
    private MainApplicationFrame mainApp;

    public ClientConnectionPanel(CommonManager commonManager, MainApplicationFrame mainApp) {
        this.commonManager = commonManager;
        this.mainApp = mainApp;
        initializeUI();
    }

    private void initializeUI() {
        rootLayout = new VBox(10);
        rootLayout.setPadding(new Insets(10));
        rootLayout.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: white;");

        Label sectionTitle = new Label("Điều khiển máy tính khác");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #87CEEB; -fx-padding: 10;");
        rootLayout.getChildren().add(sectionTitle);

        clientIpField = new TextField("192.168.1.7");
        clientIpField.setStyle("-fx-font-size: 14px");

        clientPortField = new TextField("1111");
        clientPortField.setStyle("-fx-font-size: 14px");

        clientPasswordField = new PasswordField();
        clientPasswordField.setText("12345");
        clientPasswordField.setStyle("-fx-font-size: 14px");

        Button connectButton = new Button("Kết nối");
        connectButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        connectButton.setPrefWidth(150); // Chiều rộng
        connectButton.setPrefHeight(30); // Chiều cao
        connectButton.setOnAction(e -> connectToServer());

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        Label ipLb = new Label("IP của đối tác:");
        ipLb.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        gridPane.add(ipLb, 0, 0);
        gridPane.add(clientIpField, 1, 0);
        Label portLb = new Label("Cổng:");
        portLb.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        gridPane.add(portLb, 0, 1);
        gridPane.add(clientPortField, 1, 1);
        Label passLb = new Label("Mật khẩu:");
        passLb.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        gridPane.add(passLb, 0, 2);
        gridPane.add(clientPasswordField, 1, 2);

        VBox formBox = new VBox(10, gridPane, connectButton);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-border-color: lightgray; "
                + // Màu viền
                "-fx-border-width: 2px; "
                + // Độ dày viền
                "-fx-border-radius: 10px; "
                + // Bo góc
                "-fx-background-radius: 10px; "
                + // Bo góc nền
                "-fx-background-color: white;");  // Nền trắng
        formBox.setAlignment(Pos.CENTER);
        formBox.setPrefHeight(300);
        formBox.setPrefWidth(400);

        VBox layout = new VBox(formBox);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);
        rootLayout.getChildren().add(layout);
    }

    private void connectToServer() {
        try {
            String host = clientIpField.getText().trim();
            int port = Integer.parseInt(clientPortField.getText().trim());
            String password = clientPasswordField.getText().trim();

            commonManager.startConnectingToServer(host, port, password);
   

        } catch (Exception ex) {
            showAlert("Connection Error", "Unable to connect to server.\n" + ex.getMessage());
        }
    }

    public VBox getContent() {
        return rootLayout;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
