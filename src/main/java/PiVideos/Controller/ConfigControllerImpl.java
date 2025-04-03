package PiVideos.Controller;

import PiVideos.Model.ClientPi;
import PiVideos.Model.Network;
import PiVideos.Service.ConfigService;
import PiVideos.Service.SocketSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
        model.addAttribute("network",new Network());

        return "features/configuration/network.html";
    }


    @PostMapping("/network/save")
    public String saveNetwork(@ModelAttribute Network network, Model model) {
        configService.saveNetwork(network);
        model.addAttribute("message","Neues Netwerk " + network.getName() + " angelegt!");
        return "features/configuration/network.html";
    }



    @Override
    @GetMapping("/server")
    public String getConfigServer(Model model) {

        return "features/configuration/server.html";
    }

    @Override
    @GetMapping("/client")
    public String getConfigClient(Model model) {

        model.addAttribute("networks",configService.getAllNetworks());
        model.addAttribute("clientPi",new ClientPi());

        System.out.println(configService.getAllNetworks().get(1).get_id());
        return "features/configuration/clientPi.html";
    }

    @PostMapping("/client/save")
    public String saveConfigClient(@ModelAttribute ClientPi clientPi, Model model) {
        configService.saveClient(clientPi);
        model.addAttribute("message","juhu");
        model.addAttribute("networks",configService.getAllNetworks());

        return "features/configuration/clientPi.html";
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
