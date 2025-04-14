package PiVideos.Service;

import java.net.Socket;

public interface SocketSerivce {
    void startServer();
    void handleClient(Socket clientSocket);
    void stopServer();

}
