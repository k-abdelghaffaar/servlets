package com.cookies;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginCookie")
public class LoginCookie extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user_name = (String) request.getAttribute("user");
        String id = request.getAttribute("id").toString();

        Cookie[] cookies = request.getCookies();
        Cookie theCookie;
        for (Cookie cookie : cookies) {
            if ("logCookie".equals(cookie.getName())) {
                cookie.setValue(user_name);
                cookie.setMaxAge(0);
                break;
            }
        }
        theCookie = new Cookie("logCookie", URLEncoder.encode(user_name, "UTF-8"));
        theCookie.setMaxAge(60 * 60);

        theCookie.setSecure(true); // Ensure the cookie is only sent over HTTPS
        theCookie.setHttpOnly(true); // Prevent JavaScript access to cookies

        response.addCookie(theCookie);

        HttpSession session = request.getSession(false);
        // if (session != null) {
        //     session.invalidate();
        // }

        String sesCsrfToken;
        String csrfToken;
        if (session == null) {
            session = request.getSession(true);
            //sesCsrfToken = null;
            csrfToken = UUID.randomUUID().toString();
        } else {
            sesCsrfToken = (String) session.getAttribute("csrfToken");
            csrfToken = sesCsrfToken;
        }
        session.setMaxInactiveInterval(60 * 30);
        session.setAttribute("user", user_name);
        session.setAttribute("id", id);
        session.setAttribute("csrfToken", csrfToken);
        request.setAttribute("csrfToken", csrfToken); // Store CSRF token in session

        request.getRequestDispatcher("/Welcome").forward(request, response);

    }
}
