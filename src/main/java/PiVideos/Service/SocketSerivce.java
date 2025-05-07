package PiVideos.Service;

import PiVideos.Model.Network;
import jakarta.servlet.http.HttpSession;

import java.net.Socket;

public interface SocketSerivce {

    void startServer(Network network);
    void handleClient(Socket clientSocket,Network network);
    void stopServerForNetwork(Network network);
    boolean  isServerRunning(Network network);

}
