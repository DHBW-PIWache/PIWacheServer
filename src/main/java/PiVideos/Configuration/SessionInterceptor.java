package PiVideos.Configuration;

import PiVideos.Model.Network;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class SessionInterceptor implements HandlerInterceptor {

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

