package pp.spirit.security.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pp.spirit.base.base.ResultBody;
import pp.spirit.base.base.ResultCodeMessage;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.cache.util.CacheUtil;
import pp.spirit.security.springsecurity.utils.JwtTokenUtil;
import pp.spirit.security.springsecurity.utils.RSAUtils;
import pp.spirit.security.springsecurity.utils.SecurityAuthenticationCacheUtil;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(value = "/o/p/auth")
@Api(tags = "认证相关，允许匿名访问",value = "认证相关")
public class AuthOpenController  {

    @Autowired
    DefaultKaptcha defaultKaptcha;

    /**
     * post表单提交，登录
     * @return
     */
    @ApiOperation(value = "用户登录", notes="用户登录")
    @PostMapping("/login")
    @ConditionalOnProperty(value = "spring.profiles.active", havingValue = "dev")
    public ResultBody login(@RequestParam String username, @RequestParam String password,HttpServletRequest request,HttpServletResponse rep) throws ServletException, IOException {
        //请求spring-security 登录链接
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("username", username));
        formParams.add(new BasicNameValuePair("password",  password));
        formParams.add(new BasicNameValuePair("___for__swagger__login___", "dev"));

        HttpEntity reqEntity = new UrlEncodedFormEntity(formParams, "utf-8");

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .build();

        CloseableHttpClient client = HttpClients.createDefault();

        String uri = request.getScheme ()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/signin";
        HttpPost post =  new HttpPost(uri);
        post.setEntity(reqEntity);
        post.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity resEntity = response.getEntity();
                String message = EntityUtils.toString(resEntity, "utf-8");
//            System.out.println(message);
                JSONObject jsonObject = JSONObject.parseObject(message);

                if(jsonObject.getInteger("code") == 200){
                    //Cookie存储token
                    Cookie cookie = new Cookie("pp-spirit-token", jsonObject.getObject("data",Map.class).get("token")+"");
                    //值大于0, 将cookie存储于本地磁盘, 过期后删除；值小于0, cookie不会保存于本地, 浏览器会话结束后, 将会删除
                    cookie.setMaxAge(-1);
                    //前端不可修改
                    cookie.setHttpOnly(true);
                    cookie.setPath(ContextUtils.getRequest().getContextPath());
                    cookie.setDomain(ContextUtils.getRequest().getServerName());
                    rep.addCookie(cookie);
                    return ResultBody.result(jsonObject.getInteger("code"),jsonObject.getString("message"),jsonObject.getObject("data",Map.class));
                }else{
                    return ResultBody.result(jsonObject.getInteger("code"),jsonObject.getString("message"),jsonObject.getString("data"));
                }
            } else {
                return ResultBody.fail("请求失败");
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ResultBody.fail("请求失败"+e.getMessage());
        }
    }

    @ApiOperation(value = "生成验证码")
    @GetMapping(value = "/captcha")
    public ResultBody captchaBase64() throws Exception {
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        // 生产验证码字符串并保存到redis中
        String createText = defaultKaptcha.createText();

        String sessionKey = defaultKaptcha.getConfig().getSessionKey();
        String captchaId = UUID.randomUUID().toString();

        // 使用生成的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
        BufferedImage challenge = defaultKaptcha.createImage(createText);
        ImageIO.write(challenge, "jpg", jpegOutputStream);

        //存储验证码
        CacheUtil.set(sessionKey+":"+captchaId,createText,60);

        Map<String,Object> m = new HashMap<>();
        m.put("captchaId",captchaId);
        m.put("data","data:image/jpg;base64,"+new BASE64Encoder().encode(jpegOutputStream.toByteArray()));
        return ResultBody.success(m);
    }

    @ApiOperation(value = "获取RSA公钥")
    @GetMapping("/getRsaPublicKey")
    public ResultBody getRsaPublicKey() throws Exception {
        Map<String,Object> rsaKeyPair = RSAUtils.genKeyPair();
        //存到缓存
        SecurityAuthenticationCacheUtil.putKeyPair(rsaKeyPair);
        return ResultBody.success(RSAUtils.getPublicKey(rsaKeyPair));
    }


    @ApiOperation(value = "校对验证码")
    @PostMapping("/checkVerificationCode")
    public ResultBody checkVerificationCode(@RequestParam(value = "captchaId") String captchaId, @RequestParam(value = "verificationCode") String verificationCode) {

        String sessionKey = defaultKaptcha.getConfig().getSessionKey();

        Object o = CacheUtil.get(sessionKey+":"+captchaId);
        CacheUtil.del(sessionKey+":"+captchaId);
        if (o==null) {
            return ResultBody.fail("验证码失效，验证码认证失败！");
        }else if(!verificationCode.equalsIgnoreCase(o+"")){
            return ResultBody.fail("输入错误，验证码认证失败！");
        }
        return ResultBody.success("认证通过");
    }

    /**
     * post表单提交，登录
     * @return
     */
    @ApiOperation(value = "重新获取token", notes="重新获取token")
    @GetMapping("/refreshToken")
    public ResultBody refreshToken(@RequestParam String refreshToken, HttpServletResponse response) throws ServletException, IOException {
        String token = SecurityAuthenticationCacheUtil.getToken(refreshToken);
        //token存在则代表 refreshToken 在有效期内
        //重新生成token
        if(token!=null){
            Authentication authentication = SecurityAuthenticationCacheUtil.getAuthentication(refreshToken);

            //生成新的token
            JwtTokenUtil jwtTokenUtil = ContextUtils.getBean(JwtTokenUtil.class);
            Map<String,Object> result = jwtTokenUtil.responseToken(response,authentication);
            String newToken = result.get("token")+"";
            String newRefreshToken = result.get("refreshToken")+"";

            //刷新缓存时间与 jwt 过期时间
            //存储新的用户凭证
            SecurityAuthenticationCacheUtil.putAuthentication(newRefreshToken,authentication);
            //存储新的token
            SecurityAuthenticationCacheUtil.putToken(newRefreshToken,newToken);

            //清除老的凭证
            if(!refreshToken.equalsIgnoreCase(newRefreshToken)){
                SecurityAuthenticationCacheUtil.deleteAuthentication(refreshToken);
                SecurityAuthenticationCacheUtil.deleteToken(token);
            }
            return ResultBody.success(result);
        }
        return ResultBody.result(ResultCodeMessage.UNAUTHORIZED,"请重新登录！");
    }
}
