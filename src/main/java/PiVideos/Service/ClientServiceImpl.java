package PiVideos.Service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import PiVideos.Model.ClientPi;
import PiVideos.Repository.ClientPiRepository;
import PiVideos.Repository.NetworkRepository;
import PiVideos.Repository.VideoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientPiRepository clientPiRepository;

    @Autowired
    private NetworkRepository networkRepository;

    @Autowired
    private VideoRepository videoRepository;

    private static final String USERNAME = "berry";  // Benutzername für den Raspberry Pi
    private static final String PASSWORD = "Raspberry";  // Passwort für den Raspberry Pi
    private static final String HOST = "piwacheserver.local";  // IP-Adresse oder Hostname des Raspberry Pi

    // Verbindung zum Raspberry Pi herstellen
    private Session connectSSH() throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(USERNAME, HOST, 22);
        session.setPassword(PASSWORD);
        session.setConfig("StrictHostKeyChecking", "no"); // Verhindert Host-Key-Warnungen
        session.connect();
        return session;
    }

    // Befehl über SSH ausführen
    private void executeCommand(String command) {
        Session session = null;
        try {
            session = connectSSH();  // SSH-Verbindung aufbauen
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();
    
            // Ausgabe des Befehls anzeigen
            try (InputStream in = channel.getInputStream()) {
                byte[] buffer = new byte[1024];
                while (in.read(buffer) != -1) {
                    System.out.print(new String(buffer));
                }
            }
    
            channel.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Sicherstellen, dass die Session immer geschlossen wird
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    

    // Starten des Python-Prozesses auf dem Raspberry Pi
    public boolean startClientProcess(ClientPi clientPi) {
        // Beispiel: Startbefehl für das Python-Skript
        String command = "python3 /home/berry/PIWacheClient/src/main/java/PiVideos/Detect_audio_and_record.py";
        try {
            executeCommand(command);
            System.out.println("Python-Prozess gestartet!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Stoppen des Python-Prozesses auf dem Raspberry Pi (mit pkill)
    public boolean stopClientProcess(ClientPi clientPi) {
        // Beispiel: Stoppen des Prozesses anhand des Skriptnamens
        String command = "pkill -f 'Detect_audio_and_record.py'";
        try {
            executeCommand(command);
            System.out.println("Python-Prozess gestoppt!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Überprüfen, ob der Python-Prozess noch läuft
    public boolean isClientProcessRunning(ClientPi clientPi) {
        Session session = null;
        try {
            session = connectSSH();  // SSH-Verbindung aufbauen
    
            // Befehl, um zu überprüfen, ob das Skript noch läuft (z.B. mit `ps aux` oder `pgrep`)
            String command = "pgrep -f 'Detect_audio_and_record.py'"; // Beispielbefehl, der nach dem Prozess sucht
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();
    
            // Ausgabe des Befehls lesen
            try (InputStream in = channel.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead = in.read(buffer);
    
                // Wenn der Befehl ein Ergebnis liefert, bedeutet das, dass der Prozess noch läuft
                if (bytesRead > 0) {
                    String output = new String(buffer, 0, bytesRead).trim();
                    return !output.isEmpty();  // Wenn es eine Prozess-ID gibt, läuft der Prozess
                } else {
                    return false; // Kein Output bedeutet, der Prozess läuft nicht
                }
            }
    
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Bei einem Fehler nehmen wir an, dass der Prozess nicht läuft
        } finally {
            // Sicherstellen, dass die Session immer geschlossen wird
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    
    // Beispiel für den Start des Clients (Videoaufnahme)
    public boolean startClient(ClientPi clientPi) {
        System.out.println("Versuche, den Python-Prozess zu starten...");
        return startClientProcess(clientPi);
    }

    // Beispiel für den Stopp des Clients
    public boolean stopClient(ClientPi clientPi) {
        System.out.println("Versuche, den Python-Prozess zu stoppen...");
        return stopClientProcess(clientPi);
    }

    // Beispiel für die Überprüfung des Clients
    public boolean isClientActive(ClientPi clientPi) {
        return isClientProcessRunning(clientPi);
    }
}
