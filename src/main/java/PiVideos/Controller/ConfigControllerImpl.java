package PiVideos.Controller;

import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import PiVideos.Repository.NetworkRepository;
import PiVideos.Service.FeatureService;
import PiVideos.Service.SocketSerivce;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;


/******************************************************************************************************* 
Autor: Julian Hecht
Datum letzte Änderung: 24.04.2025
Änderung: Clients anlegen + löschen
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
        Network network = (Network) session.getAttribute("network");

        model.addAttribute("clients", featureService.getAllClientPis(network));
        model.addAttribute("clientPi",new ClientPi());
        return "features/configuration/clientPi.html";
    }

    // Neuen Client speichern
    @Override
    @PostMapping("/client/save")
    public String saveConfigClient(@ModelAttribute ClientPi clientPi,HttpSession session ,Model model) {
        Network network = (Network) session.getAttribute("network");

        clientPi.setNetwork(network);
        featureService.saveClient(clientPi);

        model.addAttribute("networks", featureService.getAllNetworks());
        model.addAttribute("clients", featureService.getAllClientPis(network));
        return "features/configuration/clientPi.html";
    }


    @PostMapping("/client/delete/{_id}")
    public String deleteConfigClient(@PathVariable("_id") Integer _id, RedirectAttributes redirectAttributes) {
        featureService.deleteClientPiById(_id);
        redirectAttributes.addFlashAttribute(   "message", "ClientPi mit ID: " +_id +"  und alle zugehörigen Videos gelöscht");
        return "redirect:/features/config/client";
    }

    @PostMapping("/client/update/{_id}")
    public String updateConfigClient(@PathVariable("_id") Integer _id,RedirectAttributes redirectAttributes, @RequestParam String location, @RequestParam String comment){
        Optional<ClientPi> optClient = featureService.getClientBy_id(_id);

        if(optClient.isPresent()){
            ClientPi clientPi = optClient.get();
            clientPi.setLocation(location);
            clientPi.setComment(comment);

            if(featureService.updateClient(clientPi)){
                redirectAttributes.addAttribute("message", "Client mit der ID " + _id + " wurde geändert.");
            } else{
                redirectAttributes.addAttribute("error", "Client mit der ID " + _id + " konnte nicht geändert werden (nicht gefunden?).");
            }
        }
        return "redirect:/features/config/client";
    }

    @Override
    @GetMapping("/network")
    public String getNetworkConifg(Model model) {
        return "features/configuration/network.html";
    }


    @PostMapping("/network/update")
    public String updateNetwork(@RequestParam Integer id,
                                @RequestParam String name,
                                @RequestParam String rootPath,
                                @RequestParam int port,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Network net = networkRepository.findById(id).orElseThrow();
        net.setName(name);
        net.setRootPath(rootPath);
        net.setPort(port);
        networkRepository.save(net);
        session.setAttribute("network", net);

        redirectAttributes.addFlashAttribute("message","Netzwerk mit ID: "+ id+ " geändert");

        return "redirect:/features/config/network";
    }

    @PostMapping("/network/delete")
    public String deleteNetwork(@RequestParam Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        featureService.deleteNetworkByID(id);
        session.setAttribute("network", null);
        redirectAttributes.addFlashAttribute("message","Netzwerk mit ID: "+ id+ " gelöscht");
        return "redirect:/login";
    }




    // Socket für das Netzwerk Starten und Stoppen
    // !!!! Muss noch dynamisch gemacht werden, damit mehrere Netzwerke
    // auf verschiedenen Ports laufen können
    @PostMapping("/startServer")
    public String startServer(HttpSession session, Model model) {
        Network network = (Network) session.getAttribute("network");
        model.addAttribute("countVids", featureService.countVids(network));

        socketSerivce.startServer(network);
        session.setAttribute("socket", true);
        return "index.html";
    }

    @Override
    @PostMapping("/stopServer")
    public String stopServer(HttpSession session, Model model) {
        Network network = (Network) session.getAttribute("network");

        socketSerivce.stopServerForNetwork(network);
        model.addAttribute("countVids", featureService.countVids(network));

        session.setAttribute("socket", false);
        return "index.html";
    }


}
