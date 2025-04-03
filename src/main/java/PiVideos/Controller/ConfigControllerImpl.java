package PiVideos.Controller;

import PiVideos.Model.Network;
import PiVideos.Service.ConfigService;
import PiVideos.Service.SocketSerivce;
import PiVideos.Service.SocketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("features/config")
public class ConfigControllerImpl implements ConfigController{

    @Autowired
    SocketSerivce socketSerivce;

    @Autowired
    ConfigService configService;


    public ConfigControllerImpl(SocketSerivce socketSerivce, ConfigService configService) {
        this.socketSerivce = socketSerivce;
        this.configService = configService;
    }


    @Override
    @GetMapping("/network")
    public String getConfigNetwork(Model model) {
        return "features/configuration/network.html";
    }
    @Override
    @GetMapping("/server")
    public String getConfigServer(Model model) {

        return "features/configuration/server.html";
    }

    @Override
    public String saveNetwork(Model model) {
        return "";
    }

    @Override
    @PostMapping("/startServer")
    public String startServer(Model model) {
         socketSerivce.startServer();
        return "redirect:/features/config";
    }


    @Override
    @PostMapping("/stopServer")
    public String stopServer(Model Model) {
        socketSerivce.stopServer();
        return "redirect:/features/config";
    }


}
