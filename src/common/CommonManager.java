package common;

import client.TcpClientHandler;
import chat.ChatClient;
import chat.ChatServer;
import server.TcpServerHandler;
import ui.MainChatPanel;



public class CommonManager {
    // TODO: for server
    private TcpServerHandler tcp_server;
    private ChatServer chat_server;

    // TODO: for client
    private TcpClientHandler tcp_client;
    private ChatClient chat_client;

    public CommonManager() {
        this.tcp_server = new TcpServerHandler();
        this.tcp_client = new TcpClientHandler();
    }

    public void setMainChatPanel(MainChatPanel main_chat_panel) {
        this.chat_server = new ChatServer(main_chat_panel);
        this.chat_client = new ChatClient(main_chat_panel);
    }

    public TcpServerHandler getTcpServer() {
        return this.tcp_server;
    }

    public ChatServer getChatServer() { return this.chat_server; }

    public TcpClientHandler getTcpClient() { return this.tcp_client; }

    public ChatClient getChatClient() { return this.chat_client; }

    // TODO: handle events of server
    public void startListeningOnServer(String host, int port, String password) throws Exception {
        if(!this.tcp_server.isListening() && !this.chat_server.isListening()) {
            // Port chat = port tcp + 1
            this.tcp_server.startListeningOnTcpServer(host, port, password);
            this.chat_server.startListeningOnChatServer(host, port + 1);
        }
    }

    public void stopListeningOnServer() throws Exception {
        if(this.tcp_server.isListening() && this.chat_server.isListening()) {
            this.tcp_server.stopListeningOnTcpServer();
            this.chat_server.stopListeningOnChatServer();
        }
    }

    public void startConnectingToServer(String host, int port, String password) throws Exception {
        if(this.tcp_client.isConnectedServer()) throw new Exception("You are remoting!");
        this.tcp_client.startConnectingToTcpServer(host, port, password);
        this.chat_client.startConnectingToChatServer(host, port + 1);
    }
}
