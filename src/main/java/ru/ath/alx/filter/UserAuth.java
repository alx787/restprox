package ru.ath.alx.filter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
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
        // 1 - сначала передаем логин и пароль, получаем токен
        // post данные - login password

        // 2 - авторизуемся везде с помощью токена
        // post данные - token

        // 3 - в дальнейшем используется токен
        // post данные - token

        // проверять валидность токена или получать свежий токен можно при начале работы приложения


        log.warn(" ========== filter =========");

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;


        StringBuffer stringBuffer = new StringBuffer();
        String postLine = null;

        try {
            BufferedReader reader = servletRequest.getReader();
            while ((postLine = reader.readLine()) != null)
                stringBuffer.append(postLine);
        } catch (Exception e) { /*report an error*/ }

        JsonParser parser = new JsonParser();
        JsonObject jsonPostData = parser.parse(stringBuffer.toString()).getAsJsonObject();

        String login = null;
        String pass = null;
        String token = null;

        login = jsonPostData.get("login").getAsString();
        pass = jsonPostData.get("pass").getAsString();
        token = httpServletRequest.getParameter("token");

        if (token != null) {

        }

        if (login != null && pass != null) {
            log.warn("login: " + login);
            log.warn("pass:  " + pass);
            ((HttpServletResponse)servletResponse).sendRedirect("info/auth");
        }


//        HttpSession session = httpServletRequest.getSession(true);
//
//        log.warn(httpServletRequest.getRequestURI());
//
//
//
//
//        if ((session != null) && (!session.isNew())) {
//
//            log.warn(" ======== exist session ");
//
//        } else {
//            log.warn(" ======== new session ");
//            //((HttpServletResponse)servletResponse).sendRedirect("info/noneauth");
//        }


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
