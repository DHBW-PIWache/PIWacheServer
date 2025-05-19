package PiVideos.Service;

import PiVideos.Model.ClientPi;

public interface ClientService {
        // Startet den Client-Prozess (z.B. das Python-Skript zur Video- und Audioaufnahme)
        boolean startClient(ClientPi clientPi);

        // Stoppt den Client-Prozess (z.B. das Python-Skript)
        boolean stopClient(ClientPi clientPi);
    
        // Überprüft, ob der Client-Prozess noch läuft
        boolean isClientActive(ClientPi clientPi);
}
