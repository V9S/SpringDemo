 package oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.GrantType;



public class AccessTokenServlet extends HttpServlet {

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
        // TODO Auto-generated method stub
        service(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        service(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
System.out.println(this.getClass().getName() + "的service方法");
        
        String code = req.getParameter("code");
        System.out.println("获取到的code：" + code);
        
        OAuthClient oAuthClient =new OAuthClient(new URLConnectionClient());
        OAuthClientRequest accessTokenRequest = null;
        try {
            accessTokenRequest = OAuthClientRequest.tokenLocation(accessTokenUrl)
            .setGrantType(GrantType.AUTHORIZATION_CODE)
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setCode(code)
            .setRedirectURI(redirectUri)// redirect_uri表示重定向URI。必选项，且必须与上一步骤中的该参数值保持一致。
            .buildQueryMessage();
            
            accessTokenRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
            OAuthAccessTokenResponse oAuthResponse = oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.POST);
            String accessToken = oAuthResponse.getAccessToken();
            System.out.println("获取到的token：" + accessToken);
            
            accessUser(req, resp, accessToken);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据token获取用户信息
     * @param request
     * @param response
     * @param accessToken
     * @throws Exception
     */
    private void accessUser(HttpServletRequest request, HttpServletResponse response, String accessToken) throws Exception {
        
        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        try{
            OAuthClientRequest userInfoRequest = new OAuthBearerClientRequest(userInfoUrl).setAccessToken(accessToken).buildQueryMessage();
            OAuthResourceResponse resourceResponse = oAuthClient.resource(userInfoRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
            String body = resourceResponse.getBody();
            System.out.println("body：" + body);
            
            redirectToAssetService(request, response, "V9S");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    /**
     * 跳转到资产系统
     * @param request
     * @param response
     * @param username
     * @throws UnsupportedEncodingException
     * @throws ServletException
     * @throws IOException
     */
    private void redirectToAssetService(HttpServletRequest request, HttpServletResponse response, String username) throws UnsupportedEncodingException, ServletException, IOException {
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Authorization,"
            + " Content-Type, Accept, Connection, User-Agent, Cookie");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Accept", "application/json");
        response.setContentType("application/json; charset=utf-8");
        System.out.println(request.getMethod());
        System.out.println("redirectToAssetService");
        String url = "http://127.0.0.1:8080/slogin?username=V9S";
        System.out.println("url:"+url);
        request.getRequestDispatcher(url).forward(request, response);
//        response.sendRedirect(url);
        
    }
}
