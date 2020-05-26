package com.jiuqi.oauth.web;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZLM
 * @date 2020/05/26
 */
@RestController
public class Controller {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
     * 学校基本参数
     */
    String baseUrl = "http://sso.cqcet.edu.cn/test";
    String clientId = "zc-legal-web";
    String redirectUri = "http://127.0.0.1:9999/getAccessToken";
    String responseType = "code";
    String state = "uLbXml";
    String grantType = "authorization_code";
    String clientSecret = "ZCweb45DG5ASd34";

    /**
     * 获取code地址
     */
    String getCodeUrl = baseUrl + "/oauth/authorize?client_id=" + clientId + "&redirect_uri=" + redirectUri
        + "&response_type=" + responseType + "&state=" + state;
    /**
     * 获取token地址
     */
    String accessTokenUrl = "http://10.150.11.36:50001/test/oauth/token";

    /**
     * 验证token，获取登录用户
     */
    String userNameUrl = "http://10.150.11.36:50001/test/oauth/check_token";

    /**
     * 获取用户信息
     */
    String userInfoUrl = "http://10.150.11.36:8902/out/v1/user/{loginname}?access_token={access_token}";

    /**
     * 获取code
     * 
     * @throws IOException
     */
    @GetMapping("/getCode")
    private void getCode() throws IOException {
        OAuthClientRequest oaRequest = null;
        try {
            oaRequest = OAuthClientRequest.authorizationLocation(getCodeUrl).buildQueryMessage();
            System.out.println(oaRequest.getLocationUri());
            String redirectUrl = oaRequest.getLocationUri();
            response.sendRedirect(redirectUrl);
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/getAccessToken")
    private void getAccessToken() {

        System.out.println(this.getClass().getName() + "的service方法");

        String code = request.getParameter("code");
        System.out.println("获取到的code：" + code);

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthClientRequest accessTokenRequest = null;
        try {
            accessTokenRequest =
                OAuthClientRequest.tokenLocation(accessTokenUrl).setGrantType(GrantType.AUTHORIZATION_CODE)
                    // redirectUri表示重定向URI。必选项，且必须与上一步骤中的该参数值保持一致。
                    .setClientId(clientId).setClientSecret(clientSecret).setCode(code).setRedirectURI(redirectUri)
                    .buildQueryMessage();

            accessTokenRequest.addHeader("Accept", "application/json");
            accessTokenRequest.addHeader("Content-Type", "application/json");
            OAuthAccessTokenResponse oAuthResponse =
                oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.POST, OAuthJSONAccessTokenResponse.class);
            String accessToken = oAuthResponse.getAccessToken();
            System.out.println("获取到的token：" + accessToken);

            accessUser(request, response, accessToken);

        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据token获取用户信息
     * 
     * @param request
     * @param response
     * @param accessToken
     * @throws Exception
     */
    private void accessUser(HttpServletRequest request, HttpServletResponse response, String accessToken)
        throws Exception {

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        try {
            OAuthClientRequest userInfoRequest =
                new OAuthBearerClientRequest(userInfoUrl).setAccessToken(accessToken).buildQueryMessage();
            OAuthResourceResponse resourceResponse =
                oAuthClient.resource(userInfoRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
            String body = resourceResponse.getBody();
            System.out.println("body：" + body);

            // JSONObject jo = new JSONObject(body);
            // JSONArray jsonArray = new JSONArray(jo.get("profiles").toString());
            // for(int i = 0; i < jsonArray.length(); i++){
            // JSONObject _jo = jsonArray.getJSONObject(i);
            // if(_jo.getString("isprimary").equalsIgnoreCase("true")){
            // uid = _jo.getString("stno");
            // break;
            // }
            // }
            //
            // System.out.println("uid：" + uid);

            // redirectToAssetService(request, response, "V9S");

        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }

    }

    /*    private void redirectToAssetService(HttpServletRequest request, HttpServletResponse response, String username)
        throws UnsupportedEncodingException, ServletException, IOException {
        if (true) {
            String loginSuffix = "https://github.com";
            request
                .getRequestDispatcher(
                    loginSuffix + username + "," + URLEncoder.encode(username, "UTF-8"))
                .forward(request, response);
        }
    }*/
}
