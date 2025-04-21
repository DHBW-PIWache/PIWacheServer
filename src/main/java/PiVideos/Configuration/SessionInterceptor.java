package PiVideos.Configuration;

import PiVideos.Model.Network;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/****************************************************************************************************** 
Autor: Julian Hecht
Datum letzte Änderung: 21.04.2025
Änderung: Kommentare hinzugefügt 
 ******************************************************************************************************/

public class SessionInterceptor implements HandlerInterceptor {

    // Umleitung auf Loginseite, falls kein Netzwerk in der Session ist 
    // Netzwerk wird immer gebraucht
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Object network = request.getSession().getAttribute("network");
        if (network == null) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}

