package PiVideos.Controller;


import PiVideos.Model.Network;
import PiVideos.Repository.NetworkRepository;
import PiVideos.Service.SocketSerivce;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    @Autowired
    NetworkRepository networkRepository;

    @Autowired
    SocketSerivce socketSerivce;


    public HomeControllerImpl(NetworkRepository networkRepository, SocketSerivce socketSerivce) {
        this.networkRepository = networkRepository;
        this.socketSerivce = socketSerivce;
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
    public String postRegister(@ModelAttribute Network network,Model model, HttpSession session){
        networkRepository.save(network);
        session.setAttribute("message","Netzwerk mit ID:" + network.get_id() + " angelegt");
        return "redirect:/login";
    }


    /// Netzwerk Anmeldung und Abmeldung.
    ///  Netzwerk wird in einer HTTP Session gespeichert
    ///
    @PostMapping("/login")
    public String postLogin(@ModelAttribute Network network, HttpSession session){
        session.setAttribute("network",networkRepository.findById(network.get_id()).get());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        session.setAttribute("currentDate", LocalDate.now().format(formatter));
        return "index.html";
    }

    @PostMapping("/logout")
    public String postLogout(@ModelAttribute Network network, HttpSession session,Model model){
        model.addAttribute("networks",networkRepository.getAllNetworks());
        session.removeAttribute("network");
        return "login.html";
    }



    //Index seite
    @GetMapping("/")
    public String getHome(HttpSession session){

        if(socketSerivce.isServerRunning()){
            session.setAttribute("socket",true);
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
