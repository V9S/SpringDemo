package com.jiuqi.oauth.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author ZLM
 * @date 2020/05/26
 */
@Component
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/gams2/login")
@RestController
public class GithubController {

    @Value("${oauth.base.clientSecret}")
    private String clientSecret;

    @Value("${oauth.base.clientId}")
    private String clientId;

    @Value("${oauth.code.responseType}")
    private String responseType;

    @Value("${oauth.base.redirectUri}")
    private String redirectUri;

    @Value("${oauth.code.CodeUrl}")
    private String codeUrl;

    @Value("${oauth.token.TokenUrl}")
    private String tokenUrl;

    @Value("${oauth.token.grantType}")
    private String grantType;

    @Value("${oauth.userInfo.UserInfoUrl}")
    private String userInfoUrl;

    @Value("${oauth.target.targetUrl}")
    private String targetUrl;

    @Value("${oauth.encryption:true}")
    private boolean encryption;

    private static String password;

    @Value("${oauth.base.password}")
    private void setPassword(String password) {
        GithubController.password = password;
    }

    private String state = String.valueOf(Math.random());

    private String getCodeUrl = null;

    private final Logger logger = LoggerFactory.getLogger(GithubController.class);

    /**
     * 获取Authorization code
     * 
     * @throws IOException
     */
    @GetMapping("/oauth/getGithubCode")
    private void getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {

        getCodeUrl = codeUrl + "?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&response_type="
            + responseType + "&state=" + state;
        logger.info(getCodeUrl);
        OAuthClientRequest oaRequest = null;
        try {
            oaRequest = OAuthClientRequest.authorizationLocation(getCodeUrl).buildQueryMessage();
            String redirectUrl = oaRequest.getLocationUri();
            response.sendRedirect(redirectUrl);
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取access_token
     * 
     * @throws Exception
     */
    @RequestMapping("/oauth/getGithubAccessToken")
    private void getAccessToken(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String code = request.getParameter("code");
        String state = request.getParameter("state");
        logger.info("coed:" + code);
        logger.info("state:" + state);

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthClientRequest accessTokenRequest = null;
        String accessToken = null;
        try {
            accessTokenRequest = OAuthClientRequest.tokenLocation(tokenUrl).setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(clientId).setClientSecret(clientSecret).setCode(code).setRedirectURI(redirectUri)
                .buildQueryMessage();

            accessTokenRequest.addHeader("Accept", "application/json");
            accessTokenRequest.addHeader("Content-Type", "application/json");
            OAuthAccessTokenResponse oAuthResponse =
                oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.POST, OAuthJSONAccessTokenResponse.class);
            accessToken = oAuthResponse.getAccessToken();
        } catch (OAuthSystemException e) {
            e.printStackTrace();
        } catch (OAuthProblemException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        accessUser(request, response, accessToken);
    }

    /**
     * 根据token获取用户信息
     * 
     * @param request
     * @param response
     * @param accessToken
     * @throws Exception
     */
    private synchronized void accessUser(HttpServletRequest request, HttpServletResponse response, String accessToken)
        throws Exception {

        String username = null;
        RestTemplate restTemplate = new RestTemplate();

        // 构建请求体
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();

        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("token", accessToken);

        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<LinkedMultiValueMap<String, String>> httpEntity =
            new HttpEntity<LinkedMultiValueMap<String, String>>(body, headers);
        // 发送HTTP请求
        ResponseEntity<String> strbody = restTemplate.exchange(userInfoUrl, HttpMethod.POST, httpEntity, String.class);
        String responsebody = strbody.getBody();

        JSONObject json = new JSONObject(responsebody);

        username = json.getString("user_name");

        logger.info("username:" + username);
        redirectToAssetService(request, response, username);

    }

    /**
     * 跳转到资产系统
     * 
     * @param request
     * @param response
     * @param username
     * @throws UnsupportedEncodingException
     * @throws ServletException
     * @throws IOException
     */
    private void redirectToAssetService(HttpServletRequest request, HttpServletResponse response, String username)
        throws UnsupportedEncodingException, ServletException, IOException {
        String urlCode = username;
        if (encryption) {
            urlCode = URLEncoder.encode(URLEncoder.encode(encrypt(username), "UTF-8"), "UTF-8");
        }
        String npUrl = targetUrl + urlCode;
        logger.info(npUrl);
        response.sendRedirect(npUrl);
        // request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * 加密数据
     * 
     * @param data
     * @return
     */
    private static String encrypt(String data) { // 对string进行BASE64Encoder转换
        System.out.println("password:" + password);
        byte[] bt = encryptByKey(data.getBytes(), password);
        Base64 base64en = new Base64(true);
        String strs = new String(base64en.encode(bt));
        return strs;
    }

    /**
     * DES加密
     * 
     * @param datasource
     * @param key
     * @return
     */
    private static byte[] encryptByKey(byte[] datasource, String key) {
        try {
            SecureRandom random = new SecureRandom();

            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            // 现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(datasource);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}
