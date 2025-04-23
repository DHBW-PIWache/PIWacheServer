package PiVideos.Controller;

import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import PiVideos.Repository.NetworkRepository;
import PiVideos.Service.FeatureService;
import PiVideos.Service.SocketSerivce;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/******************************************************************************************************* 
Autor: Julian Hecht
Datum letzte Änderung: 22.04.2025
Änderung: Kommentare hinzugefügt + Refactoring
*******************************************************************************************************/

@Controller
@RequestMapping("features/config")
public class ConfigControllerImpl implements ConfigController{


    SocketSerivce socketSerivce;


    FeatureService featureService;

    //!!!! Sollte mit einem Service ersetzt werden

    NetworkRepository networkRepository;


    public ConfigControllerImpl(SocketSerivce socketSerivce, FeatureService featureService, NetworkRepository networkRepository) {
        this.socketSerivce = socketSerivce;
        this.featureService = featureService;
        this.networkRepository = networkRepository;
    }


    // Übersicht über Clients und Mögichkeit zum Anlegen neuer Clients
    // !! CLients müssen noch gelöscht werden können !!
    @Override
    @GetMapping("/client")
    public String getConfigClient(HttpSession session, Model model) {

        model.addAttribute("clients", featureService.getAllClientPis((Network) session.getAttribute("network")));
        model.addAttribute("clientPi",new ClientPi());
        return "features/configuration/clientPi.html";
    }

    // Neuen Client speichern
    @Override
    @PostMapping("/client/save")
    public String saveConfigClient(@ModelAttribute ClientPi clientPi,HttpSession session ,Model model) {
        clientPi.setNetwork((Network) session.getAttribute("network"));
        featureService.saveClient(clientPi);
        model.addAttribute("message","juhu");
        model.addAttribute("networks", featureService.getAllNetworks());
        model.addAttribute("clients", featureService.getAllClientPis((Network) session.getAttribute("network")));
        return "features/configuration/clientPi.html";
    }

    @Override
    @GetMapping("/network")
    public String getNetworkConifg(Model model) {
        return "features/configuration/network.html";
    }


    // Socket für das Netzwerk Starten und Stoppen
    // !!!! Muss noch dynamisch gemacht werden, damit mehrere Netzwerke
    // auf verschiedenen Ports laufen können
    @PostMapping("/startServer")
    public String startServer(HttpSession session, Model model) {
        Network network = (Network) session.getAttribute("network");
        socketSerivce.startServer(network);
        session.setAttribute("socket", true);
        return "index.html";
    }

    @Override
    @PostMapping("/stopServer")
    public String stopServer(HttpSession session, Model Model) {
        socketSerivce.stopServer();
        session.setAttribute("socket", false);
        return "index.html";
    }


}
