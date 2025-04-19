package PiVideos.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

public interface FeatureController {
    public String getConfig(Model model);
    public String getLive(@PathVariable Integer id, Model model);
    public String getDataStorage(Model model);
}
