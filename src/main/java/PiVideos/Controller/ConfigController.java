package PiVideos.Controller;
import PiVideos.Model.Network;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/******************************************************************************************************* 
Autor: Julian Hecht
Datum letzte Änderung: 21.04.2025
Änderung: Kommentare hinzugefügt 
*******************************************************************************************************/
public interface ConfigController {

    

    public String getConfigClient(Model model);

    public String startServer(HttpSession session, Model model );
    public String stopServer(HttpSession session, Model Model);


}
