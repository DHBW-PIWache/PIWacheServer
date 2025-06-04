package PiVideos.Service;

import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import PiVideos.Model.Video;
import PiVideos.Repository.ClientPiRepository;
import PiVideos.Repository.NetworkRepository;
import PiVideos.Repository.VideoRepository;
import jakarta.annotation.PreDestroy;

import org.mp4parser.IsoFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SocketServiceImpl implements SocketService {

    private final ConcurrentHashMap<Integer, ServerHandle> servers = new ConcurrentHashMap<>();

    @Autowired
    NetworkRepository networkRepository;

    @Autowired
    ClientPiRepository clientPiRepository;

    @Autowired
    VideoRepository videoRepository;

    // Startet den Socket-Server für ein bestimmtes Netzwerk
    public synchronized void startServer(Network network) {
        Integer networkId = network.get_id();

        if (servers.containsKey(networkId)) {
            System.out.println("Server für Netzwerk " + network.getName() + " läuft bereits!");
            return;
        }

        int port = network.getPort();
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            ServerHandle handle = new ServerHandle(serverSocket, executorService);
            servers.put(networkId, handle);

            System.out.println("Socket-Server für Netzwerk " + network.getName() + " läuft auf Port " + port);

            executorService.submit(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("Neuer Client verbunden auf Netzwerk " + network.getName() + ": "
                                + clientSocket.getInetAddress());
                        executorService.submit(() -> handleClient(clientSocket, network));
                    } catch (IOException e) {
                        if (!serverSocket.isClosed()) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Verarbeitet eingehende Client-Verbindungen
    public void handleClient(Socket clientSocket, Network network) {
        try (
                DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream())) {
            String command = dataIn.readUTF();
            System.out.println("Empfangen: " + command);

            if ("REGISTER_CLIENT".equalsIgnoreCase(command)) {
                handleRegisterClient(dataIn, dataOut, network);
            } else if ("SEND_VIDEO".equalsIgnoreCase(command)) {
                dataOut.writeUTF("READY_TO_RECEIVE");

                String piName = "unknown";
                long fileSize = 0;

                while (true) {
                    String line = dataIn.readUTF();
                    if ("END_HEADER".equals(line))
                        break;
                    if (line.startsWith("PI_NAME:")) {
                        piName = line.substring("PI_NAME:".length());
                    } else if (line.startsWith("FILE_SIZE:")) {
                        fileSize = Long.parseLong(line.substring("FILE_SIZE:".length()));
                    }
                }

                if (checkClient(piName, network)) {
                    createNewClient(piName, network);
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

    // Registriert einen neuen Client, wenn er noch nicht existiert
    private void handleRegisterClient(DataInputStream dataIn, DataOutputStream dataOut, Network network)
            throws IOException {
        String piName = "unknown";

        while (true) {
            String line = dataIn.readUTF();
            if ("END_HEADER".equals(line))
                break;
            if (line.startsWith("PI_NAME:")) {
                piName = line.substring("PI_NAME:".length());
            }
        }

        if (checkClient(piName, network)) {
            createNewClient(piName, network);
            System.out.println("Neuer Client registriert: " + piName);
        } else {
            System.out.println("Client bereits registriert: " + piName);
        }

        dataOut.writeUTF("CLIENT_REGISTERED");
    }

    // Erstellt einen neuen ClientPi Eintrag in der Datenbank
    private void createNewClient(String piName, Network network) {
        ClientPi clientPi = new ClientPi();
        clientPi.setName(piName);
        clientPi.setNetwork(network);
        clientPi.setLocation("unknown");
        clientPi.setDate(LocalDateTime.now(ZoneId.of("GMT+02:00")));
        clientPiRepository.save(clientPi);
    }

    // Empfängt ein Video von einem Client und speichert es in der Datenbank
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
                if (bytesRead == -1)
                    break;
                fileOut.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
            }

            video.setName(finalName);
            video.setPath(finalFile.getAbsolutePath());
            video.setRelativePath(piName + "/" + finalName);
            video.setMb((double) finalFile.length() / (1024 * 1024));

            try (FileInputStream fis = new FileInputStream(finalFile)) {
                IsoFile isoFile = new IsoFile(fis.getChannel());
                double durationInSeconds = (double) isoFile.getMovieBox().getMovieHeaderBox().getDuration()
                        / isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
                video.setDuration(durationInSeconds); // <--- setter vorausgesetzt
                isoFile.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Fehler beim Berechnen der Videodauer");
            }

            videoRepository.save(video);
            System.out.println("Video gespeichert unter: " + finalFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Stoppt alle laufenden Server beim Beenden der Anwendung
    @PreDestroy
    public synchronized void stopAllServers() {
        servers.forEach((networkId, handle) -> {
            try {
                handle.stop();
                System.out.println("Server für Netzwerk " + networkId + " gestoppt.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        servers.clear();
    }

    // Stoppt den Server für ein bestimmtes Netzwerk
    public synchronized void stopServerForNetwork(Network network) {
        int networkId = network.get_id();
        ServerHandle handle = servers.remove(networkId);
        if (handle != null) {
            try {
                handle.stop();
                System.out.println("Server für Netzwerk " + network.getName() + " gestoppt.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Kein laufender Server für Netzwerk " + network.getName());
        }
    }

    // Überprüft, ob ein ClientPi mit dem angegebenen Namen im Netzwerk existiert
    public boolean checkClient(String clientName, Network network) {
        return clientPiRepository.findByNameAndNetwork(clientName, network).isEmpty();
    }

    // Überprüft, ob der Server für ein bestimmtes Netzwerk läuft
    public synchronized boolean isServerRunning(Network network) {
        return servers.containsKey(network.get_id());
    }
}

// Interne Klasse zum Verwalten des Server-Sockets und des ExecutorService
class ServerHandle {
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;

    public ServerHandle(ServerSocket serverSocket, ExecutorService executorService) {
        this.serverSocket = serverSocket;
        this.executorService = executorService;
    }

    public void stop() throws IOException {
        serverSocket.close();
        executorService.shutdown();
    }
}
