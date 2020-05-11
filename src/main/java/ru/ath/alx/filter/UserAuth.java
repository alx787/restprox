package ru.ath.alx.filter;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class UserAuth implements Filter {

    private static final Logger log = Logger.getLogger(UserAuth.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        log.warn(" ========== filter =========");

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpSession session = httpServletRequest.getSession(true);


        if ((session != null) && (!session.isNew())) {
            String sessUser = (String)session.getAttribute("user");
            String sessToken = (String)session.getAttribute("token");

            log.warn(" ======== ");
            log.warn(" sess user from filter:  " + sessUser);
            log.warn(" sess token from filter: " + sessToken);

            //((HttpServletResponse) servletResponse).sendRedirect("restprox/info/noneauth");


//            if (!checkAuth(sessUser, sessToken)) {
//                ((HttpServletResponse)response).sendRedirect("portalAction!auth.jspa");
//                return;
//            }
//
//            if ((sessUser == null) || (sessToken == null)) {
//
//                ((HttpServletResponse)response).sendRedirect("portalAction!auth.jspa");
//                return;
//            }
//
//
//            if (!sessUser.equals("alx") && sessToken.equals("123")) {
////                HttpServletResponse httpResponse =
//
//                log.warn(" ======== ");
//                log.warn(" unsuccessful attempt ");
//
//                ((HttpServletResponse)response).sendRedirect("portalAction!auth.jspa");
//                return;
//
//            }
//
        } else {
            log.warn(" ======== ");
            log.warn(" session not found ");

            //((HttpServletResponse) servletResponse).sendRedirect("restprox/info/noneauth");
            //return;
        }


        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {

    }
}
