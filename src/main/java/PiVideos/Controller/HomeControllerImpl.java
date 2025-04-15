package PiVideos.Controller;


import PiVideos.Model.Network;
import PiVideos.Repository.NetworkRepository;
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

@Controller
public class HomeControllerImpl implements HomeController{

    @Autowired
    NetworkRepository networkRepository;

    public HomeControllerImpl(NetworkRepository networkRepository) {
        this.networkRepository = networkRepository;
    }

    @GetMapping("/login")
    public String getLogin(Model model){
        model.addAttribute("networks",networkRepository.getAllNetworks());
        model.addAttribute("network",new Network());
        return "login.html";
    }

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

    @GetMapping("/")
    public String getHome(HttpSession session){
        return "index.html";
    }



    @GetMapping("/error")
    public String getError(Model model){
        return "error.html";
    }



}
