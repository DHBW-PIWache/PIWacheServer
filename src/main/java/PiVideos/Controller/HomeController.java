package PiVideos.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;


/*******************************************************************************************************
 Autor: Julian Hecht
 Datum letzte Änderung: 22.04.2025
 Änderung: Kommentare hinzugefügt
 *******************************************************************************************************/
public interface HomeController {

    public String getHome(@RequestParam(name = "stoppedClientId", required = false)String stoppedClientId,
                          @RequestParam(name = "startedClientId", required = false)String startedClientId,
                          HttpSession session,
                          Model model);
    public String getError(Model model);



}
