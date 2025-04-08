package PiVideos.Controller;


import PiVideos.Repository.NetworkRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeControllerImpl implements HomeController{

    @Autowired
    NetworkRepository networkRepository;

    public HomeControllerImpl(NetworkRepository networkRepository) {
        this.networkRepository = networkRepository;
    }

    @GetMapping("/")
    public String getHome(HttpSession session){
        session.setAttribute("networks",networkRepository.getAllNetworks());

        return "index.html";
    }

    @GetMapping("/error")
    public String getError(Model model){

        return "error.html";
    }



}
