package PiVideos.Service;

import PiVideos.Model.Network;
import PiVideos.Model.Video;
import PiVideos.Repository.ClientPiRepository;
import PiVideos.Repository.NetworkRepository;
import PiVideos.Repository.VideoRepository;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
                System.out.println(Inet4Address.getLocalHost());

                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Neuer Client verbunden: " + clientSocket.getInetAddress());

                    executorService.submit(() -> handleClient(clientSocket,network));
                }
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    e.printStackTrace();
                }
            }
        });
    }




    public void handleClient(Socket clientSocket,Network network) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String clientMessage = in.readLine();
            System.out.println("Empfangen: " + clientMessage);

            if ("SEND_VIDEO".equalsIgnoreCase(clientMessage)) {
                out.println("READY_TO_RECEIVE");

                // Lies die Metadaten (also den Dateinamen)
                String videoName = null;
                String line;
                while ((line = in.readLine()) != null && !line.equals("END_HEADER")) {
                    if (line.startsWith("PI_NAME:")) {
                        videoName = line.substring("PI_NAME:".length());
                    }
                }

                receiveVideo(clientSocket, videoName,network); // Übergib den Namen

                out.println("VIDEO_RECEIVED");
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

    public void receiveVideo(Socket clientSocket, String piName, Network network) {

        //Hier Sollte noch Fehlerbehandlung rein!!!!!!

        File videoDir = new File(network.getRootPath() + "\\" + piName);
        String localDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        if (!videoDir.exists()) {
            videoDir.mkdirs();
        }

        if (piName == null || piName.isBlank()) {
            piName = "null";
        }
        Video video = new Video();
        video.setDate(LocalDateTime.now());
        video.setClientPi(clientPiRepository.findByName(piName));
        video.setFavorite(false);

        videoRepository.save(video);


        String finalName = video.get_id() + ".mp4";
        File finalFile = new File(videoDir, finalName);

        try (
                InputStream inputStream = clientSocket.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(finalFile)
        ) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }


            video.setName(finalName);
            video.setPath(finalFile.getPath());
            video.setBytes(finalFile.length());
            videoRepository.save(video);

            System.out.println("Video gespeichert unter: " + finalFile.getAbsolutePath());

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
