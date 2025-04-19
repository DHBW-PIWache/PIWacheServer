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
import java.time.LocalDateTime;
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

    public synchronized void startServer(Network network) {
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

                    executorService.submit(() -> handleClient(clientSocket, network));
                }
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void handleClient(Socket clientSocket, Network network) {
        try (
            DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream())
        ) {
            String command = dataIn.readUTF();
            System.out.println("Empfangen: " + command);

            if ("SEND_VIDEO".equalsIgnoreCase(command)) {
                dataOut.writeUTF("READY_TO_RECEIVE");

                String piName = "unknown";
                long fileSize = 0;

                // Header verarbeiten
                while (true) {
                    String line = dataIn.readUTF();
                    if ("END_HEADER".equals(line)) break;

                    if (line.startsWith("PI_NAME:")) {
                        piName = line.substring("PI_NAME:".length());
                    } else if (line.startsWith("FILE_SIZE:")) {
                        fileSize = Long.parseLong(line.substring("FILE_SIZE:".length()));
                    }
                }

                receiveVideo(dataIn, piName, fileSize, network);
                dataOut.writeUTF("VIDEO_RECEIVED");
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

    public void receiveVideo(DataInputStream dataIn, String piName, long fileSize, Network network) {
        if (piName == null || piName.isBlank()) {
            piName = "unknown";
        }

        File videoDir = new File(network.getRootPath() + piName);
        if (!videoDir.exists()) {
            videoDir.mkdirs();
        }

        Video video = new Video();
        video.setDate(LocalDateTime.now());
        video.setClientPi(clientPiRepository.findByName(piName));
        video.setFavorite(false);
        videoRepository.save(video);

        String finalName = video.get_id() + ".mp4";
        File finalFile = new File(videoDir, finalName);

        try (FileOutputStream fileOut = new FileOutputStream(finalFile)) {
            byte[] buffer = new byte[4096];
            long totalRead = 0;

            while (totalRead < fileSize) {
                int bytesRead = dataIn.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalRead));
                if (bytesRead == -1) break;
                fileOut.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
            }

            video.setName(finalName);
            video.setPath(finalFile.getAbsolutePath());
            video.setRelativePath(piName + "/" + finalName);
            video.setBytes(finalFile.length());
            videoRepository.save(video);

            System.out.println("📁 Video gespeichert unter: " + finalFile.getAbsolutePath());

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
