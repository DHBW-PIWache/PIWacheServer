package PiVideos.Service;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SocketServiceImpl implements SocketSerivce {

    private final int port = 8081;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private boolean running = false;

    public SocketServiceImpl() {

    }

    public synchronized void startServer() {
        if (running) {
            System.out.println("Server läuft bereits!");
            return;
        }

        executorService = Executors.newFixedThreadPool(10);
        running = true;

        executorService.submit(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Socket-Server läuft auf Port " + port);

                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Neuer Client verbunden: " + clientSocket.getInetAddress());

                    executorService.submit(() -> handleClient(clientSocket));
                }
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                System.out.println("Empfangen: " + clientMessage);
                out.println("Server: " + clientMessage.toUpperCase()); // Antwort zurücksenden
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PreDestroy
    public synchronized void stopServer() {
        if (!running) {
            System.out.println("Server ist bereits gestoppt!");
            return;
        }

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Socket-Server gestoppt.");
            }
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            running = false;
        }
    }
}
