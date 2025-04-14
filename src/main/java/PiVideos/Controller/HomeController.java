package PiVideos.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

public interface HomeController {

    public String getHome(HttpSession session);
    public String getError(Model model);



}
