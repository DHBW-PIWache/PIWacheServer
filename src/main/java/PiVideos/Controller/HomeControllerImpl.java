package PiVideos.Controller;

import PiVideos.Model.Network;
import PiVideos.Model.Video;
import PiVideos.Model.ClientPi;
import PiVideos.Service.FeatureService;
import PiVideos.Service.SocketService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*******************************************************************************************************
 * Autor: Julian Hecht
 * Datum letzte Änderung: 22.04.2025
 * Änderung: Kommentare hinzugefügt
 *******************************************************************************************************/

@Controller
public class HomeControllerImpl implements HomeController {

    @Autowired
    SocketService socketSerivce;
    @Autowired
    FeatureService featureService;

    // Login Seite holen
    @GetMapping("/login")
    public String getLogin(Model model) {
        model.addAttribute("networks", featureService.getAllNetworks());
        model.addAttribute("network", new Network());

        return "login.html";
    }

    /// Register Seite holen
    @GetMapping("/register")
    public String getRegister(Model model) {
        model.addAttribute("network", new Network());
        return "register.html";
    }

    /// Neues Netzwerk anlegen
    @PostMapping("/register")
    public String postRegister(@ModelAttribute Network network, RedirectAttributes redirectAttributes) {
        if(featureService.saveNetwork(network)){
            redirectAttributes.addFlashAttribute("message", "Netzwerk mit ID:" + network.get_id() + " angelegt");
        } else {
            redirectAttributes.addFlashAttribute("error", "Fehler beim Anlegen des Netzwerks. Bitte versuchen Sie es erneut.");
        }
        return "redirect:/login";
    }

    // Login eines Netzwerks
    @PostMapping("/login")
    public String postLogin(@ModelAttribute Network networkOpt, HttpSession session) {
        Network network = featureService.getNetworkById(networkOpt.get_id());
        session.setAttribute("network", network);
        return "redirect:/";
    }

    // Logout eines Netzwerks
    @PostMapping("/logout")
    public String postLogout(@ModelAttribute Network network, HttpSession session, Model model) {
        model.addAttribute("networks", featureService.getAllNetworks());
        session.removeAttribute("network");
        return "login.html";
    }

    // Dashboard seite
    @GetMapping("/")
    public String getHome(@RequestParam(name = "stoppedClientId", required = false) String stoppedClientId,
            @RequestParam(name = "startedClientId", required = false) String startedClientId,
            HttpSession session,
            Model model) {

        Network network = (Network) session.getAttribute("network");
        Video newestVid = featureService.getNewestVideo(network);

        List<ClientPi> clients = featureService.getAllClientPis(network);
        Map<Integer, Boolean> clientStatusMap = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();

        for (ClientPi client : clients) {
            boolean isActive = false;
            try {
                String hostname = client.getName(); // z.B. raspberrypi.local
                String url = "http://" + hostname + ".local:5000/status";
                Map<String, String> response = restTemplate.getForObject(url, Map.class);
                if (response != null && "running".equalsIgnoreCase(response.get("detection"))) {
                    isActive = true;
                }
            } catch (Exception e) {
                // Client nicht erreichbar oder aus
                isActive = false;
            }
            clientStatusMap.put(client.get_id(), isActive);
        }
        model.addAttribute("clients", clients);
        model.addAttribute("clientStatusMap", clientStatusMap);
        model.addAttribute("video", newestVid);
        if (featureService.countVids(network) != 0) {
            model.addAttribute("countVids", featureService.countVids(network));
            model.addAttribute("totalMb", featureService.totalMBForNetwork(network));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        session.setAttribute("currentDate", LocalDate.now().format(formatter));

        if (socketSerivce.isServerRunning(network)) {
            session.setAttribute("socket", true);
        } else {
            session.setAttribute("socket", false);
        }
        return "index.html";
    }

    // Error seite
    @GetMapping("/error")
    public String getError(Model model) {
        return "error.html";
    }

}
