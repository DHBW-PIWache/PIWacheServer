package PiVideos.Controller;
import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/******************************************************************************************************* 
Autor: Julian Hecht
Datum letzte Änderung: 22.04.2025
Änderung: Kommentare hinzugefügt 
*******************************************************************************************************/
public interface ConfigController {


    @GetMapping("/client")
    public String getConfigClient(HttpSession session, Model model);

    @PostMapping("/client/save")
    public String saveConfigClient(@ModelAttribute ClientPi clientPi, HttpSession session , Model model);

    @GetMapping("/network")
    public String getNetworkConifg(Model model);


    //Sockets Starten und Stoppen
    // !!!! Muss noch dynamisch gemacht werden, damit mehrere Netzwerke
    // auf verschiedenen Ports laufen können
    public String startServer(HttpSession session, RedirectAttributes model );
    public String stopServer(HttpSession session, RedirectAttributes Model);


}
