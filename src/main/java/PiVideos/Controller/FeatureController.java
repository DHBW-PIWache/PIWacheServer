package PiVideos.Controller;

import org.springframework.ui.Model;

public interface FeatureController {
    public String getConfig(Model model);
    public String getLive(Model model);
    public String getDataStorage(Model model);
}
