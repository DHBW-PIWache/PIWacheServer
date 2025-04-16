package PiVideos.Controller;


import PiVideos.Service.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/features")
public class FeatureControllerImpl implements FeatureController {

    @Autowired
    FeatureService featureService;

    public FeatureControllerImpl(FeatureService featureService) {
        this.featureService = featureService;
    }

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
        model.addAttribute("clients", featureService.getAllClientPis());
        model.addAttribute("videos", featureService.getAllVideos());
        return "features/datastorage.html";
    }

    @PostMapping("/dataStorage/{_id}")
    public String deleteVideo(@PathVariable("_id") Integer id, Model model) {
        featureService.deleteVideoByID(id);
        model.addAttribute("clients", featureService.getAllClientPis());
        model.addAttribute("videos", featureService.getAllVideos());
        return "features/datastorage.html";
    }
}
