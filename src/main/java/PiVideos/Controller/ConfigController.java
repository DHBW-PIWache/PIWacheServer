package PiVideos.Controller;

import PiVideos.Model.ClientPi;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface ConfigController {

    // Clients anzeigen
    String getConfigClient(HttpSession session, Model model);

    // Neuen Client speichern
    String saveConfigClient(ClientPi clientPi, HttpSession session);

    // Client löschen
    String deleteConfigClient(Integer _id, RedirectAttributes redirectAttributes);

    // Client aktualisieren
    String updateConfigClient(Integer _id, RedirectAttributes redirectAttributes, String location, String comment);

    // Client starten
    String startClient(String id, RedirectAttributes redirectAttributes);

    // Client stoppen
    String stopClient(String id, RedirectAttributes redirectAttributes);

    // Client neustarten
    String restartClient(String id, RedirectAttributes redirectAttributes);

    // Netzwerkseite anzeigen
    String getNetworkConifg(Model model);

    // Netzwerk aktualisieren
    String updateNetwork(Integer id, String name, String rootPath, int port, HttpSession session,
            RedirectAttributes redirectAttributes);

    // Netzwerk löschen
    String deleteNetwork(Integer id, HttpSession session, RedirectAttributes redirectAttributes);

    // Socket starten
    String startServer(HttpSession session, RedirectAttributes model);

    // Socket stoppen
    String stopServer(HttpSession session, RedirectAttributes model);

}
