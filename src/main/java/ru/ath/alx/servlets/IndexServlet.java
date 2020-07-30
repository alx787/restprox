package ru.ath.alx.servlets;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IndexServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(HttpServlet.class);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        log.warn("from servlet");

        req.setAttribute("name", "alx");
        req.getRequestDispatcher("/pages/index.jsp").forward(req, resp);
//        super.doGet(req, resp);
    }
}
