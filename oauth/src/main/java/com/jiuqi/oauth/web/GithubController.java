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
public class GithubController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
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
    String redirectUri = "http://127.0.0.1:9999/getGithubAccessToken";
    String state = String.valueOf(Math.random());
    /**
     * 获取code地址
     */
    String getCodeUrl = baseUri + "/authorize?" + "client_id=" + clientId + "&redirectUri=" + redirectUri + "&state=";
    /**
     * 获取token地址
     */
    String accessTokenUrl = baseUri + "/access_token";
    /**
     * 获取用户数据的api
     */
    String userInfoUrl = "https://api.github.com/user";

    /**
     * 获取code
     * 
     * @throws IOException
     */
    @GetMapping("/getGithubCode")
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

    /**
     * 获取access_token
     */
    @RequestMapping("/getGithubAccessToken")
    private void getAccessToken() {

        System.out.println(this.getClass().getName() + "的service方法");

        String code = request.getParameter("code");
        String state = request.getParameter("state");
        System.out.println("获取到的code：" + code);
        System.out.println("获取到的state：" + state);

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

        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        }

    }

}
