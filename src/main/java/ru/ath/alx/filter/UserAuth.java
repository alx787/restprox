package ru.ath.alx.filter;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserAuth implements Filter {

    private static final Logger log = Logger.getLogger(UserAuth.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // если сессия не создана то нужно перенаправить на сервис где будет создана сессия
        // он должен вернуть ид сессии, и его нужно будет использовать, потому как к сессии
        // прицепим логин и пароль

        // если получится


        log.warn(" ========== filter =========");

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpSession session = httpServletRequest.getSession(true);

        log.warn(httpServletRequest.getRequestURI());


        if ((session != null) && (!session.isNew())) {

            log.warn(" ======== exist session ");

        } else {
            log.warn(" ======== new session ");
            ((HttpServletResponse)servletResponse).sendRedirect("info/noneauth");
        }


//            String sessUser = (String)session.getAttribute("user");
//            String sessToken = (String)session.getAttribute("token");
//
//            log.warn(" ======== ");
//            log.warn(" session id: " + session.getId());
//            log.warn(" sess user from filter:  " + sessUser);
//            log.warn(" sess token from filter: " + sessToken);

            //((HttpServletResponse) servletResponse).sendRedirect("restprox/info/noneauth");
    //        MessageDigest digest = null;
    //        try {
    //            digest = MessageDigest.getInstance("SHA-256");
    //
    //            byte[] encodedhash = digest.digest("userName".getBytes(StandardCharsets.UTF_8));
    //
    //            StringBuffer hexString = new StringBuffer();
    //
    //            for (int i = 0; i < encodedhash.length; i++) {
    //                String hex = Integer.toHexString(0xff & encodedhash[i]);
    //                if(hex.length() == 1) hexString.append('0');
    //                hexString.append(hex);
    //            }
    //
    //            log.warn("hash");
    //            log.warn(hexString.toString());
    //
    //
    //        } catch (NoSuchAlgorithmException e) {
    //            e.printStackTrace();
    //        }


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



        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {

    }
}
