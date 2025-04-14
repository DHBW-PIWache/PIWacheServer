package PiVideos.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/features")
public class FeatureControllerImpl implements FeatureController {
    @Override
    @GetMapping("/config")
    public String getConfig(Model model) {
        return  "features/configuration.html";
    }

    @Override
    @GetMapping("/live")
    public String getLive(Model model) {
        return "features/live.html";
    }

    @Override
    @GetMapping("/dataStorage")
    public String getDataStorage(Model model) {
        return "features/datastorage.html";
    }
}
