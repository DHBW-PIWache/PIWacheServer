package PiVideos.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;


/*******************************************************************************************************
 Autor: Julian Hecht
 Datum letzte Änderung: 22.04.2025
 Änderung: Kommentare hinzugefügt
 *******************************************************************************************************/
public interface HomeController {

    public String getHome(HttpSession session);
    public String getError(Model model);



}
