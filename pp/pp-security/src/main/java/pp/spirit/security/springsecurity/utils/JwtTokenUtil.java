package pp.spirit.security.springsecurity.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pp.spirit.base.incrementer.SnowFlake;
import pp.spirit.base.properties.PPProperties;
import pp.spirit.base.utils.ContextUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Data
public class JwtTokenUtil {
    private String TOKEN_HEADER = "pp-spirit-token";
    // 注入自己的jwt配置
    @Resource
    private PPProperties ppProperties;

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_JWT_ID = "jwt_id";
    static final String CLAIM_KEY_AUDIENCE = "audience";
    static final String CLAIM_KEY_CREATED = "created";

    private static final String AUDIENCE_UNKNOWN = "unknown";
    private static final String AUDIENCE_WEB = "web";
    private static final String AUDIENCE_MOBILE = "mobile";
    private static final String AUDIENCE_TABLET = "tablet";

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public String getJwtIdFromToken(String token) {
        String jwtId;
        try {
            final Claims claims = getClaimsFromToken(token);
            jwtId = claims.get(CLAIM_KEY_JWT_ID)+"";
        } catch (Exception e) {
            jwtId = null;
        }
        return jwtId;
    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            //得到token的有效期
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    public String getAudienceFromToken(String token) {
        String audience;
        try {
            final Claims claims = getClaimsFromToken(token);
            audience = (String) claims.get(CLAIM_KEY_AUDIENCE);
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(ppProperties.getTokenSecretKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    //设置过期时间
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + ppProperties.getTokenTokenExpiresMinutes()*60*1000);
//        return new Date(30 * 24 * 60);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    // Device用户检测当前用户的设备，用不到的话可以删掉（使用这个需要添加相应的依赖）
//    private String generateAudience(Device device) {
//        String audience = AUDIENCE_UNKNOWN;
//        if (device.isNormal()) {
//            audience = AUDIENCE_WEB;
//        } else if (device.isTablet()) {
//            audience = AUDIENCE_TABLET;
//        } else if (device.isMobile()) {
//            audience = AUDIENCE_MOBILE;
//        }
//        return audience;
//    }

    private Boolean ignoreTokenExpiration(String token) {
        String audience = getAudienceFromToken(token);
        return (AUDIENCE_TABLET.equals(audience) || AUDIENCE_MOBILE.equals(audience));
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_CREATED, new Date());
        claims.put(CLAIM_KEY_JWT_ID, "PPSPIRITSESSION"+ UUID.randomUUID().toString().replaceAll("-","").toUpperCase()+ContextUtils.getBean(SnowFlake.class).nextId());
        return generateToken(claims);
    }

    /**
     * 生成token（最关键）
     * @param claims
     * @return
     */
    String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)  //设置声明信息（用户名等）
                .setExpiration(generateExpirationDate()) //设置过期时间
                .signWith(SignatureAlgorithm.HS512, ppProperties.getTokenSecretKey()) //设置签名
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getCreatedDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    //TODO,验证当前的token是否有效
    public Boolean validateToken(String token, String userName) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userName)&& !isTokenExpired(token));
    }

    public Map<String,Object> responseToken(HttpServletResponse response, Authentication authentication) throws IOException {
        //生成token
        final String token = this.generateToken(authentication.getName());

        if(ppProperties.getTokenAutoBindCookie()){
            //Cookie存储token
            Cookie cookie = new Cookie(TOKEN_HEADER, token);
            //值大于0, 将cookie存储于本地磁盘, 过期后删除；值小于0, cookie不会保存于本地, 浏览器会话结束后, 将会删除
            cookie.setMaxAge(-1);
            //前端不可修改
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        //将token暴露到response
        response.setContentType("text/json;charset=utf-8");
        response.setHeader("Access-Control-Expose-Headers", TOKEN_HEADER);
        response.setHeader(TOKEN_HEADER, token);

        //返回前端
        String jwtId = this.getJwtIdFromToken(token);
        //缓存refreshtoken
        /**
         *
         * refreshtoken 其实就是jwtId
         *
         * **/
        SecurityAuthenticationCacheUtil.putToken(token);
        //缓存 authentication
        SecurityAuthenticationCacheUtil.putAuthentication(jwtId,authentication);

        //将生成的authentication放入容器中，生成安全的上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //token返回前端
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        //返回前端的项目token比后台提前过期，给前端过期判断的机会
        map.put("expire",ppProperties.getTokenTokenExpiresMinutes()-.5);
        map.put("refreshToken",jwtId);
        map.put("refreshExpire",ppProperties.getTokenRefreshTokenTimeoutMinutes()-.5);
        return map;
    }

    /**
     * 获取请求中的token,首先从请求头中获取,如果没有,则尝试从请求参数中获取
     *
     * @param httpReq
     * @return
     */
    public String getRequestToken(HttpServletRequest httpReq) {

        String token = getCookieJwtToken(httpReq);

        if (StringUtils.isBlank(token)) {
            token = httpReq.getHeader(TOKEN_HEADER);
        }

        if (StringUtils.isBlank(token)) {
            token = httpReq.getParameter(TOKEN_HEADER);
        }
        return token;
    }
    /**
     * @Author: LiV
     * @Date: 2020.4.22 16:05
     * @Description: 获取cookie的jwt
     **/
    private String getCookieJwtToken(HttpServletRequest req){
        Cookie[] cookies = req.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                String cookieValue = cookie.getValue();//根据Cookie的名字获取对应的请求头的值
                if(TOKEN_HEADER.equals(cookieName)){
                    return cookieValue;
                }
            }
        }
        return null;
    }
}
