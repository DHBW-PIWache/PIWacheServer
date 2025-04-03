package PiVideos.Controller;
import PiVideos.Model.Network;
import org.springframework.ui.Model;

public interface ConfigController {

    //Website Gets
    public String getConfigNetwork(Model model);
    public String getConfigServer(Model model);

    public String saveNetwork(Model model);

    public String startServer(Model model);
    public String stopServer(Model Model);


}
