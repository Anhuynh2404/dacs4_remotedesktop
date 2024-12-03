package server;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerInitializer {
    DataInputStream dis = null;
    DataOutputStream dos = null;
    String width = "";
    String height = "";

    public ServerInitializer(ServerSocket socket, String password) {
        Robot robot = null;
        Rectangle rectangle = null;
        try {
            // Lấy thông tin màn hình
            GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gDev = gEnv.getDefaultScreenDevice();
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            width = "" + dim.getWidth();
            height = "" + dim.getHeight();
            rectangle = new Rectangle(dim);
            robot = new Robot(gDev);

            while (true) {
                Socket sc = socket.accept();
                System.out.println("Server kết nối với " + sc);
                dis = new DataInputStream(sc.getInputStream());
                dos = new DataOutputStream(sc.getOutputStream());

                String psword = dis.readUTF();
                if (psword.equals(password)) {
                    dos.writeUTF("valid");
                    dos.writeUTF(width);
                    dos.writeUTF(height);

                    // Khởi tạo luồng gửi màn hình và nhận sự kiện
                    new ScreenBroadcaster(sc, robot, rectangle);
                    new EventReceiver(sc, robot);
                } else {
                    dos.writeUTF("invalid");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Đảm bảo rằng ServerSocket được đóng khi kết thúc
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
