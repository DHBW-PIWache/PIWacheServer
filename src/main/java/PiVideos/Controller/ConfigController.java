package PiVideos.Controller;
import PiVideos.Model.Network;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

public interface ConfigController {

    //Website Gets
    public String getConfigNetwork(Model model);

    @PostMapping("/network/save")
    String saveNetwork(@ModelAttribute Network network, Model model);

    public String getConfigServer(Model model);

    public String getConfigClient(Model model);

    public String startServer(HttpSession session, Model model );
    public String stopServer(HttpSession session, Model Model);


}
