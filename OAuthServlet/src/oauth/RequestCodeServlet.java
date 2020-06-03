package oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;

public class RequestCodeServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * github基本参数
     */
    String baseUri = "https://github.com/login/oauth";
    String clientSecret = "5f2c67d2c36f491c3aaf5d6fa67fbce5e7c3a67d";

    /**
     * 获取code所需参数
     */
    String clientId = "73a227a4b2c76761d91b";
    String responseType = "code";
    String redirectUri = "http://127.0.0.1:9999/gams2/login/oauth/getGithubAccessToken";
    String state = String.valueOf(Math.random());
    /**
     * 获取code地址
     */
    String getCodeUrl = baseUri + "/authorize?" + "client_id=" + clientId + "&redirectUri=" + redirectUri;
    /**
     * 获取token地址
     */
    String accessTokenUrl = baseUri + "/access_token";
    /**
     * 获取用户数据的api
     */
    String userInfoUrl = "https://api.github.com/user";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service(req, resp);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        System.out.println(this.getClass().getName() + "的service方法");
        OAuthClientRequest oaRequest = null;
        try {
            oaRequest = OAuthClientRequest.authorizationLocation(getCodeUrl).buildQueryMessage();
            System.out.println(oaRequest.getLocationUri());
            String redirectUrl = oaRequest.getLocationUri();
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
