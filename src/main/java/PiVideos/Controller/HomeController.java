package PiVideos.Controller;

import PiVideos.Model.Network;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface HomeController {

    String getLogin(Model model);

    String getRegister(Model model);

    String postRegister(Network network, RedirectAttributes redirectAttributes);

    String postLogin(Network networkOpt, HttpSession session);

    String postLogout(Network network, HttpSession session, Model model);

    String getHome(String stoppedClientId, String startedClientId, HttpSession session, Model model);

    String getError(Model model);
}
