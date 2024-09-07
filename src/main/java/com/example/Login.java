package com.example;

import java.io.IOException;
import java.util.UUID;

import com.listeners.LoginLogoutListener;
import com.managers.EntityMgr;
import com.model.User;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


public class Login extends HttpServlet {

    private EntityMgr db;

    @Override
    public void init() throws ServletException {
        super.init();
        db = EntityMgr.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        String username = request.getParameter("username");
        String pass = request.getParameter("pass");
        User user = (User) db.getUserByCredentials(username, pass);

        if (user != null) {
            // Successful login
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user.getUsername());

            // Generate CSRF token
            String csrfToken = UUID.randomUUID().toString();
            session.setAttribute("csrfToken", csrfToken);

            LoginLogoutListener.userLoggedIn();

            RequestDispatcher rd = request.getRequestDispatcher("/Welcome");
            rd.forward(request, response);
        } else {
            // Failed login
            response.sendRedirect("login.jsp?color=red");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showLoginPage(request, response);
    }

    private void showLoginPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().invalidate();
        RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
        dispatcher.include(request, response);
    }
}
