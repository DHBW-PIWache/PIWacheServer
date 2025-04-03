package PiVideos.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeControllerImpl implements HomeController{

    @GetMapping("/")
    public String getHome(Model model){
        return "index.html";
    }

    @GetMapping("/error")
    public String getError(Model model){

        return "error.html";
    }



}
