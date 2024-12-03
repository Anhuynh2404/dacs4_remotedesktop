//package ui;
//
//import communication.*;
//import common.CommonManager;
//
//import javafx.application.Platform;
//import javafx.geometry.Insets;
//import javafx.scene.control.*;
//import javafx.scene.input.MouseButton;
//import javafx.scene.layout.*;
//import javafx.stage.FileChooser;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.InetAddress;
//
//public class ChatView implements Runnable {
//
//    private VBox chatDisplay;
//    private ScrollPane scrollPane;
//    private TextField messageField;
//    private MainChatView root;
//    private BorderPane mainLayout;
//
//    private CommonManager commonBus;
//    private ChatConnection chatBus;
//
//    public ChatView(MainChatView root, CommonManager commonBus, ChatConnection chatBus) {
//        this.commonBus = commonBus;
//        this.chatBus = chatBus;
//        this.root = root;
//        this.mainLayout = new BorderPane(); 
//
//        initializeUI();
//
//        new Thread(this).start();
//    }
//
//    private void initializeUI() {
//        chatDisplay = new VBox(10);
//        chatDisplay.setPadding(new Insets(10));
//        scrollPane = new ScrollPane(chatDisplay);
//        scrollPane.setFitToWidth(true);
//
//        // Khu vực nhập tin nhắn và các nút
//        HBox chatInputRow = new HBox(10);
//        chatInputRow.setPadding(new Insets(10));
//
//        messageField = new TextField();
//        messageField.setPromptText("Enter your message...");
//        Button sendFileButton = new Button("Send File");
//        Button sendMessageButton = new Button("Send");
//
//        chatInputRow.getChildren().addAll(messageField, sendMessageButton, sendFileButton);
//
//        mainLayout.setCenter(scrollPane);
//        mainLayout.setBottom(chatInputRow);
//
//        sendMessageButton.setOnAction(e -> this.sendMessage());
//        sendFileButton.setOnAction(e -> this.sendFile());
//    }
//
//    public BorderPane getContent() {
//        return mainLayout;
//    }
//
//    private void sendMessage() {
//        try {
//            String content = messageField.getText().trim();
//            if (!content.isEmpty()) {
//                TextMessage strMessage = new TextMessage(InetAddress.getLocalHost().getHostName(), content);
//                chatBus.sendMessage(strMessage);
//                addMessageToPanel("You: ", content, "right");
//                messageField.clear();
//            }
//        } catch (IOException e) {
//            showAlert("Connection Error", "Unable to send message.\n" + e.getMessage());
//        }
//    }
//
//    private void sendFile() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Select File to Send");
//        File file = fileChooser.showOpenDialog(null);
//
//        if (file != null && file.length() <= 30 * 1024 * 1024) {
//            try (FileInputStream fis = new FileInputStream(file)) {
//                FileTransferMessage fileMessage = new FileTransferMessage(
//                        InetAddress.getLocalHost().getHostName(),
//                        file.getName(),
//                        file.length(),
//                        fis.readAllBytes()
//                );
//                chatBus.sendMessage(fileMessage);
//                addMessageToPanel("You: ", file.getName(), "right");
//            } catch (IOException e) {
//                showAlert("File Sending Error", "Unable to send file.\n" + e.getMessage());
//            }
//        }
//    }
//
//    public void addMessageToPanel(String sender, String message, String position) {
//        Platform.runLater(() -> {
//            Label messageLabel = new Label(sender + "\n" + message);
//            messageLabel.setWrapText(true);
//            messageLabel.setPadding(new Insets(5));
//            messageLabel.setStyle(position.equals("right") ? "-fx-background-color: lightgreen;" : "-fx-background-color: lightblue;");
//
//            HBox messageContainer = new HBox();
//            messageContainer.setPadding(new Insets(5, 0, 5, 0));
//            if (position.equals("right")) {
//                messageContainer.setStyle("-fx-alignment: center-right;");
//            } else {
//                messageContainer.setStyle("-fx-alignment: center-left;");
//            }
//            messageContainer.getChildren().add(messageLabel);
//
//            chatDisplay.getChildren().add(messageContainer);
//            scrollPane.setVvalue(1.0);
//        });
//    }
//
//    public void addFileToPanel(FileTransferMessage fileMessage) {
//        Platform.runLater(() -> {
//            Label fileLabel = new Label(fileMessage.getSender() + "\n" + fileMessage.getName());
//            fileLabel.setWrapText(true);
//            fileLabel.setPadding(new Insets(5));
//            fileLabel.setStyle("-fx-background-color: lightblue; -fx-underline: true;");
//
//            fileLabel.setOnMouseClicked(event -> {
//                if (event.getButton() == MouseButton.PRIMARY) {
//                    saveFile(fileMessage);
//                }
//            });
//
//            HBox fileContainer = new HBox();
//            fileContainer.setPadding(new Insets(5, 0, 5, 0));
//            fileContainer.setStyle("-fx-alignment: center-left;");
//            fileContainer.getChildren().add(fileLabel);
//
//            chatDisplay.getChildren().add(fileContainer);
//            scrollPane.setVvalue(1.0);
//        });
//    }
//
//    private void saveFile(FileTransferMessage fileMessage) {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Save File");
//        fileChooser.setInitialFileName(fileMessage.getName());
//        File saveFile = fileChooser.showSaveDialog(null);
//
//        if (saveFile != null) {
//            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
//                fos.write(fileMessage.getData());
//                showAlert("File Saved", "File saved successfully.");
//            } catch (IOException e) {
//                showAlert("File Saving Error", "Unable to save file.\n" + e.getMessage());
//            }
//        }
//    }
//
//    private void showAlert(String title, String content) {
//        Platform.runLater(() -> {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle(title);
//            alert.setHeaderText(null);
//            alert.setContentText(content);
//            alert.showAndWait();
//        });
//    }
//
//    @Override
//    public void run() {
//        while (true) {
//            try {
//                if (commonBus.getChatServer().isHasPartner() || commonBus.getChatClient().isConnectedServer()) {
//                    AbstractMessage message = chatBus.recvMessage();
//                    if (message != null) {
//                        if (message instanceof TextMessage) {
//                            TextMessage strMessage = (TextMessage) message;
//                            addMessageToPanel(strMessage.getSender() + ": ", strMessage.getContent(), "left");
//                        } else if (message instanceof FileTransferMessage) {
//                            FileTransferMessage fileMessage = (FileTransferMessage) message;
//                            addFileToPanel(fileMessage);
//                        }
//                    }
//                }
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                Platform.runLater(() -> {
//                    this.root.removeChatView(this);
//                    try {
//                        this.chatBus.getSocket().close();
//                    } catch (Exception ignored) {
//                    }
//                });
//                break;
//            }
//        }
//    }
//}
