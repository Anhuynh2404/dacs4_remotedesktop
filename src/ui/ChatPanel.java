package ui;

import chat.*;
import common.CommonManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class ChatPanel extends VBox implements Runnable {

    private VBox chatDisplay;
    private ScrollPane scrollPane;
    private TextField messageField;
    private CommonManager commonBus;
    private ChatBus chatBus;

    public ChatPanel(CommonManager commonBus, ChatBus chatBus) {
        this.commonBus = commonBus;
        this.chatBus = chatBus;

        initializeUI();
        new Thread(this).start();
    }

    private void initializeUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: white;");

        chatDisplay = new VBox(10);
        chatDisplay.setPadding(new Insets(10));
        scrollPane = new ScrollPane(chatDisplay);
        scrollPane.setFitToWidth(true);

        HBox inputRow = new HBox(10);
        inputRow.setPadding(new Insets(10));

        messageField = new TextField();
        messageField.setPromptText("Enter your message...");
        messageField.setPrefWidth(400);

        Button sendFileButton = new Button("Send File");
        Button sendMessageButton = new Button("Send");

        inputRow.getChildren().addAll(messageField, sendMessageButton, sendFileButton);

        getChildren().addAll(scrollPane, inputRow);

        sendMessageButton.setOnAction(e -> sendMessage());
        sendFileButton.setOnAction(e -> sendFile());
    }

    private void sendMessage() {
        try {
            String content = messageField.getText().trim();
            if (!content.isEmpty()) {
                StringMessage strMessage = new StringMessage(InetAddress.getLocalHost().getHostName(), content);
                chatBus.sendMessage(strMessage);
                addMessageToPanel("You", content, "right");
                messageField.clear();
            }
        } catch (IOException e) {
            showAlert("Error", "Unable to send message: " + e.getMessage());
        }
    }

    private void sendFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Send");
        File file = fileChooser.showOpenDialog(null);

        if (file != null && file.length() <= 30 * 1024 * 1024) {
            try (FileInputStream fis = new FileInputStream(file)) {
                FileMessage fileMessage = new FileMessage(
                        InetAddress.getLocalHost().getHostName(),
                        file.getName(),
                        file.length(),
                        fis.readAllBytes()
                );
                chatBus.sendMessage(fileMessage);
                addMessageToPanel("You", file.getName(), "right");
            } catch (IOException e) {
                showAlert("Error", "Unable to send file: " + e.getMessage());
            }
        }
    }

    private void addMessageToPanel(String sender, String message, String position) {
        Platform.runLater(() -> {
            Label messageLabel = new Label(sender + ": " + message);
            messageLabel.setWrapText(true);
            messageLabel.setPadding(new Insets(5));
            messageLabel.setStyle(position.equals("right")
                    ? "-fx-background-color: lightgreen; -fx-text-fill: black;"
                    : "-fx-background-color: lightblue; -fx-text-fill: black;");

            HBox messageContainer = new HBox();
            if (position.equals("right")) {
                messageContainer.setStyle("-fx-alignment: center-right;");
            } else {
                messageContainer.setStyle("-fx-alignment: center-left;");
            }

            messageContainer.getChildren().add(messageLabel);
            chatDisplay.getChildren().add(messageContainer);
            scrollPane.setVvalue(1.0);
        });
    }

    private void addFileToPanel(FileMessage fileMessage) {
        Platform.runLater(() -> {
            Label fileLabel = new Label(fileMessage.getSender() + ": " + fileMessage.getName());
            fileLabel.setWrapText(true);
            fileLabel.setPadding(new Insets(5));
            fileLabel.setStyle("-fx-background-color: lightblue; -fx-underline: true;");

            fileLabel.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    saveFile(fileMessage);
                }
            });

            HBox fileContainer = new HBox(fileLabel);
            fileContainer.setStyle("-fx-alignment: center-left;");
            chatDisplay.getChildren().add(fileContainer);
            scrollPane.setVvalue(1.0);
        });
    }

    private void saveFile(FileMessage fileMessage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.setInitialFileName(fileMessage.getName());
        File saveFile = fileChooser.showSaveDialog(null);

        if (saveFile != null) {
            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                fos.write(fileMessage.getData());
                showAlert("File Saved", "File saved successfully.");
            } catch (IOException e) {
                showAlert("Error", "Unable to save file: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (commonBus.getChatServer().isHasPartner() || commonBus.getChatClient().isConnectedServer()) {
                    Message message = chatBus.recvMessage();
                    if (message instanceof StringMessage) {
                        StringMessage strMessage = (StringMessage) message;
                        addMessageToPanel(strMessage.getSender(), strMessage.getContent(), "left");
                    } else if (message instanceof FileMessage) {
                        FileMessage fileMessage = (FileMessage) message;
                        addFileToPanel(fileMessage);
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
