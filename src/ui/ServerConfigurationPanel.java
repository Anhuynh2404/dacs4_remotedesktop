package ui;

import common.CommonManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.SocketException;
import java.util.Objects;
import java.util.Vector;
import javafx.geometry.Pos;

public class ServerConfigurationPanel implements Runnable {

    private ComboBox<String> serverIpComboBox;
    private TextField serverPortField;
    private TextField serverPasswordField;
    private Label statusLabel;
    private Button startListeningButton;
    private Button stopListeningButton;

    private CommonManager commonBus;
    private Thread listenThread;
    private VBox rootLayout;

    public ServerConfigurationPanel(CommonManager commonBus) {
        this.commonBus = commonBus;
        initializeUI();
    }

    private void initializeUI() {
        rootLayout = new VBox(10);
        rootLayout.setPadding(new Insets(10));
        rootLayout.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: white;");

        Label sectionTitle = new Label("Cho phép điều khiển");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #87CEEB; -fx-padding: 10;");
        rootLayout.getChildren().add(sectionTitle);

        serverIpComboBox = new ComboBox<>();
        try {
            Vector<String> ipv4Addresses = commonBus.getTcpServer().getAllIpv4AddressesOnLocal();
            serverIpComboBox.getItems().addAll(ipv4Addresses);
        } catch (SocketException exception) {
            exception.printStackTrace();
        }
        serverIpComboBox.setStyle("-fx-font-size: 14px");

        serverPortField = new TextField("1111");
        serverPortField.setStyle("-fx-font-size: 14px");
        serverPasswordField = new TextField("12345");
        serverPasswordField.setStyle("-fx-font-size: 14px");
        statusLabel = new Label("Stopped");
        statusLabel.setStyle("-fx-font-size: 14px");
        statusLabel.setFont(new javafx.scene.text.Font("Arial", 12));
        statusLabel.setTextFill(Color.RED);

        startListeningButton = new Button("Khởi chạy");
        startListeningButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        startListeningButton.setPrefWidth(150); // Chiều rộng
        startListeningButton.setPrefHeight(30); // Chiều cao

        stopListeningButton = new Button("Dừng");
        stopListeningButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        stopListeningButton.setPrefWidth(150); // Chiều rộng
        stopListeningButton.setPrefHeight(30); // Chiều cao
        stopListeningButton.setDisable(true);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        Label ipLabel = new Label("IP của bạn:");
        ipLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        gridPane.add(ipLabel, 0, 0);
        gridPane.add(serverIpComboBox, 1, 0);
        Label portLb = new Label("Cổng:");
        portLb.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        gridPane.add(portLb, 0, 1);
        gridPane.add(serverPortField, 1, 1);
        Label passLb = new Label("Cài đặt mật khẩu:");
        passLb.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        gridPane.add(passLb, 0, 2);
        gridPane.add(serverPasswordField, 1, 2);
        Label statusLb = new Label("Trạng thái:");
        statusLb.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        gridPane.add(statusLb, 0, 3);
        gridPane.add(statusLabel, 1, 3);

        HBox buttonBox = new HBox(10, startListeningButton, stopListeningButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(Pos.CENTER);

        VBox formBox = new VBox(10, gridPane, buttonBox);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-border-color: lightgray; "
                + // Màu viền
                "-fx-border-width: 2px; "
                + // Độ dày viền
                "-fx-border-radius: 10px; "
                + // Bo góc viền
                "-fx-background-radius: 10px; "
                + // Bo góc nền
                "-fx-background-color: white;");  // Nền trắng
        formBox.setAlignment(Pos.CENTER);
        formBox.setPrefHeight(300);
        formBox.setPrefWidth(400);

        rootLayout.getChildren().add(formBox);

        startListeningButton.setOnAction(e -> startListening());
        stopListeningButton.setOnAction(e -> stopListening());
    }

    private void startListening() {
        try {
            String host = Objects.requireNonNull(serverIpComboBox.getValue()).trim();
            int port = Integer.parseInt(serverPortField.getText().trim());
            String password = serverPasswordField.getText().trim();
            commonBus.startListeningOnServer(host, port, password);

            listenThread = new Thread(this);
            listenThread.setDaemon(true);
            listenThread.start();

            startListeningButton.setDisable(true);
            stopListeningButton.setDisable(false);
            statusLabel.setText("Listening...");
            statusLabel.setTextFill(Color.GREEN);

        } catch (Exception ex) {
            showAlert("Server Error", "Unable to start server.\n" + ex.getMessage());
        }
    }

    private void stopListening() {
        try {
            commonBus.stopListeningOnServer();
            stopListeningButton.setDisable(true);
            startListeningButton.setDisable(false);
            statusLabel.setText("Stopped");
            statusLabel.setTextFill(Color.RED);

        } catch (Exception ex) {
            showAlert("Server Error", "Unable to stop server.\n" + ex.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    @Override
    public void run() {
        Thread tcpThread = new Thread(() -> {
            while (commonBus.getTcpServer().isListening()) {
                try {
                    commonBus.getTcpServer().waitingConnectionFromClient();
                } catch (Exception e) {
                    System.err.println("Error TCP: " + e.getMessage());
                }
            }
        });

        Thread chatThread = new Thread(() -> {
            while (commonBus.getChatServer().isListening()) {
                try {
                    commonBus.getChatServer().waitingConnectionFromClient();
                } catch (Exception e) {
                    System.err.println("Error Chat: " + e.getMessage());
                }
            }
        });

        tcpThread.start();
        chatThread.start();
    }

    public VBox getContent() {
        return rootLayout;
    }
}
