package PiVideos.Service;

import PiVideos.Model.ClientPi;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*******************************************************************************************************
 Autor: Julian Hecht
 Datum letzte Änderung: 22.04.2025
 Änderung: Kommentare hinzugefügt
 *******************************************************************************************************/

@Service
public class SocketServiceImpl implements SocketSerivce {

    // port sollte dynamsich gemacht werden, je nach netzwerk
    private int port;
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


    /// Socket Server starten, mit port des Netzwerkes
    /// !!!!!! Falls der Port schon belegt ist, fehlermeldung ausgeben
    /// !!!!!! Mussw noch eingebaut werden
    public synchronized void startServer(Network network) {
        if (running) {
            System.out.println("Server läuft bereits!");
            return;
        }
        executorService = Executors.newFixedThreadPool(10);
        running = true;
        int port = network.getPort();
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


    //Client Anfrage annehmen
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

                // Header verarbeiten -> PiName ist notwendig für Dateiverwaltung
                while (true) {
                    String line = dataIn.readUTF();
                    if ("END_HEADER".equals(line)) break;
                    if (line.startsWith("PI_NAME:")) {
                        piName = line.substring("PI_NAME:".length());

                        System.out.println(piName);
                    } else if (line.startsWith("FILE_SIZE:")) {
                        fileSize = Long.parseLong(line.substring("FILE_SIZE:".length()));
                    }
                }
                

                //Client prüfen und anlegen
                if(!checkClient(piName, network)){
                    ClientPi clientPi = new ClientPi();
                    clientPi.setName(piName);
                    clientPi.setNetwork(network);
                    clientPi.setLocation("unknown");
                    clientPiRepository.save(clientPi);
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

    ///  Video annehmen, auf datenbank speichern und auf storage
    ///  Metadaten verwenden um File richtig zu speichern
    public void receiveVideo(DataInputStream dataIn, String piName, long fileSize, Network network) {
        File videoDir = new File(network.getRootPath() + piName);
        if (!videoDir.exists()) {
            videoDir.mkdirs();
        }
        Video video = new Video();
        video.setDate(LocalDateTime.now(ZoneId.of("GMT+02:00")));
        video.setClientPi(clientPiRepository.findByNameAndNetwork(piName, network).get());
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

    public boolean checkClient(String clientName, Network network){
        return clientPiRepository.findByNameAndNetwork(clientName, network).isPresent();
    }

    // Ping server
    public synchronized boolean isServerRunning() {
        return running;
    }

}
