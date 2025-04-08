package PiVideos.Service;

import PiVideos.Model.Network;
import PiVideos.Model.Video;
import PiVideos.Repository.ClientPiRepository;
import PiVideos.Repository.NetworkRepository;
import PiVideos.Repository.VideoRepository;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SocketServiceImpl implements SocketSerivce {





    private final int port = 8081;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private boolean running = false;

    @Autowired
    NetworkRepository networkRepository;

    @Autowired
    ClientPiRepository clientPiRepository;

    @Autowired
    VideoRepository videoRepository;

    public SocketServiceImpl(NetworkRepository networkRepository, ClientPiRepository clientPiRepository, VideoRepository videoRepository) {
        this.networkRepository = networkRepository;
        this.clientPiRepository = clientPiRepository;
        this.videoRepository = videoRepository;
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
            String clientMessage = in.readLine();
            System.out.println("Empfangen: " + clientMessage);

            if ("SEND_VIDEO".equalsIgnoreCase(clientMessage)) {
                out.println("READY_TO_RECEIVE");

                receiveVideo(clientSocket);

                out.println("VIDEO_RECEIVED");
            } else {
                out.println("UNKNOWN_COMMAND");
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

    public void receiveVideo(Socket clientSocket) {

        //undynnamisch
        Optional<Network> network = networkRepository.findById(1);
        File videoDir = new File(network.get().getRootPath());

        if (!videoDir.exists()) {
            videoDir.mkdirs();
        }

        File videoFile = new File(videoDir, "empfangenesVideo.mp4");

        try (
                InputStream inputStream = clientSocket.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(videoFile)
        ) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            videoRepository.save(new Video(videoFile.getName(),videoFile.getPath(),clientPiRepository.findById(1).get()));

            System.out.println("Video wurde gespeichert unter: " + videoFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
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
