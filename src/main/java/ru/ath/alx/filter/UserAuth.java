package ru.ath.alx.filter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import ru.ath.alx.util.AuthUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserAuth implements Filter {

    private static final Logger log = Logger.getLogger(UserAuth.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 1 - для получения/обновления токена надо слать запрос на info/newauth, он фильтром не обрабатывается
        // post данные - login password

        // 2 - авторизуемся везде с помощью токена
        // post данные - token

        // 3 - в дальнейшем используется токен
        // post данные - token

        // в теле запроса должны быть post данные в формате json
        // {"userid":"...", "token":"..."} - просто авторизация


        log.warn(" ========== filter =========");

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        RequestWrapper requestWrapper = new RequestWrapper(httpServletRequest);
        String postData = requestWrapper.getBody();


        if (postData == null || postData.isEmpty()) {
//            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/info/errorauth");
            RequestDispatcher rd = servletRequest.getRequestDispatcher("/info/errorauth");
            rd.forward(servletRequest, servletResponse);
            return;
        }

        JsonParser parser = new JsonParser();
        JsonObject jsonPostData = parser.parse(postData).getAsJsonObject();


        String token = null;
        String userid = null;


        if (jsonPostData.has("userid")) {
            userid = jsonPostData.get("userid").getAsString();
        }
        if (jsonPostData.has("token")) {
            token = jsonPostData.get("token").getAsString();
        }

        log.warn("userid: " + userid);
        log.warn("token: " + token);

        // проверка токена
        if (userid != null && token != null) {
            // если токен есть то его надо проверить на валидность
            //
            if (!AuthUtil.checkToken(userid, token)) {
                // если токен неправильный то отправить на сервис ошибочной авторизации
                RequestDispatcher rd = servletRequest.getRequestDispatcher("/info/errorauth");
                rd.forward(servletRequest, servletResponse);
//                httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/info/errorauth");
                return;
            }

        } else {
            RequestDispatcher rd = servletRequest.getRequestDispatcher("/info/errorauth");
            rd.forward(servletRequest, servletResponse);
//            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/info/errorauth");
            return;
        }

        // если дошло до этого места то авторизация успешна

        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {

    }
}
