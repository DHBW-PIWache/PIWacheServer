package PiVideos.Controller;

import org.springframework.ui.Model;

public interface HomeController {

    public String getHome(Model model);
    public String getError(Model model);

}
