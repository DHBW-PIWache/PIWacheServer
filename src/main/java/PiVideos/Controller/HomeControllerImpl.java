package PiVideos.Controller;


import PiVideos.Model.Network;
import PiVideos.Model.Video;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/*******************************************************************************************************
 Autor: Julian Hecht
 Datum letzte Änderung: 22.04.2025
 Änderung: Kommentare hinzugefügt
 *******************************************************************************************************/

@Controller
public class HomeControllerImpl implements HomeController{


    // !!!! Sollte mit einem Service Ersetzt werden!

    NetworkRepository networkRepository;
    SocketSerivce socketSerivce;
    FeatureService featureService;


    public HomeControllerImpl(NetworkRepository networkRepository, SocketSerivce socketSerivce, FeatureService featureService) {
        this.networkRepository = networkRepository;
        this.socketSerivce = socketSerivce;
        this.featureService = featureService;
    }

    // Login Seite holen
    @GetMapping("/login")
    public String getLogin(Model model){
        model.addAttribute("networks",networkRepository.getAllNetworks());
        model.addAttribute("network",new Network());

        return "login.html";
    }

    /// Register Seite holen
    /// !!! Noch nicht fertig, muss noch Netzwerke registieren können
    ///
    @GetMapping("/register")
    public String getRegister(Model model){
        model.addAttribute("network", new Network());
        return "register.html";
    }
    /// Register Network Seite
    /// !!! Noch nicht fertig, muss noch Netzwerke registieren können
    @PostMapping("/register")
    public String postRegister(@ModelAttribute Network network,Model model, RedirectAttributes redirectAttributes){
        networkRepository.save(network);
        redirectAttributes.addFlashAttribute("message","Netzwerk mit ID:" + network.get_id() + " angelegt");
        return "redirect:/login";
    }


    /// Netzwerk Anmeldung und Abmeldung.
    ///  Netzwerk wird in einer HTTP Session gespeichert
    ///
    @PostMapping("/login")
    public String postLogin(@ModelAttribute Network networkOpt, HttpSession session, Model model){
        Network network = networkRepository.findById(networkOpt.get_id()).get();
        session.setAttribute("network",network);

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String postLogout(@ModelAttribute Network network, HttpSession session,Model model){
        model.addAttribute("networks",networkRepository.getAllNetworks());
        session.removeAttribute("network");
        return "login.html";
    }



    //Index seite
    @GetMapping("/")
    public String getHome(HttpSession session, Model model){
        Network network = (Network) session.getAttribute("network");
        Video newestVid = featureService.getNewestVideo(network);


        model.addAttribute("video", newestVid);


        model.addAttribute("countVids", featureService.countVids(network));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        session.setAttribute("currentDate", LocalDate.now().format(formatter));


        if(socketSerivce.isServerRunning(network)){
            session.setAttribute("socket", true);
        } else{
            session.setAttribute("socket",false);
        }
        return "index.html";
    }

    //Error seite
    @GetMapping("/error")
    public String getError(Model model){
        return "error.html";
    }



}
