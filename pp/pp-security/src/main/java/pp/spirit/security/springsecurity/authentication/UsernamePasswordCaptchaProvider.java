package pp.spirit.security.springsecurity.authentication;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.cache.util.CacheUtil;
import pp.spirit.security.springsecurity.exception.CaptchaCheckException;
import pp.spirit.security.springsecurity.utils.AesEncryptUtils;
import pp.spirit.security.springsecurity.utils.RSAUtils;
import pp.spirit.security.springsecurity.utils.SecurityAuthenticationCacheUtil;

public class UsernamePasswordCaptchaProvider extends DaoAuthenticationProvider {
    public UsernamePasswordCaptchaProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        this.setPasswordEncoder(passwordEncoder);
        this.setUserDetailsService(userDetailsService);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        //校验验证码
        DefaultKaptcha defaultKaptcha = ContextUtils.getBean(DefaultKaptcha.class);
        String captchaId = ContextUtils.getRequest().getParameter("captchaId");

        String mode = ContextUtils.getRequest().getParameter("___for__swagger__login___");
        //提供swagger支持的访问
        if("dev".equalsIgnoreCase(mode)){
            super.additionalAuthenticationChecks(userDetails,new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),authentication.getCredentials().toString(),authentication.getAuthorities()));
            return;
        }

        //正常登录流程
        String verificationCode = ContextUtils.getRequest().getParameter("verifyCode");
        String sessionKey = defaultKaptcha.getConfig().getSessionKey();
        Object o = CacheUtil.get(sessionKey+":"+captchaId);
        CacheUtil.del(sessionKey+":"+captchaId);
        if (o==null) {
            this.logger.debug("验证码失效，验证码认证失败！");
            throw new CaptchaCheckException("验证码失效，验证码认证失败！");
        }else if(!verificationCode.equalsIgnoreCase(o+"")){
            this.logger.debug("验证码输入错误，验证码认证失败！");
            throw new CaptchaCheckException("验证码输入错误，验证码认证失败！");
        }
        //RSA处理
        String aesKey = ContextUtils.getRequest().getParameter("key1");
        String publicKey = ContextUtils.getRequest().getParameter("key2");
        //解密
        try {
            //解密aeskey
            String privateKey = RSAUtils.getPrivateKey(SecurityAuthenticationCacheUtil.getKeyPair(publicKey));
            SecurityAuthenticationCacheUtil.deleteKeyPair(publicKey);
            aesKey = RSAUtils.decryptDataOnJava(aesKey,privateKey);
        } catch (Exception e) {
            this.logger.debug("Failed to decode rsa");
            throw new BadCredentialsException("Failed to decode rsa");
        }

        if (authentication.getCredentials() == null) {
            this.logger.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        } else {
            String presentedPassword = authentication.getCredentials().toString();
            try {
                presentedPassword = AesEncryptUtils.decrypt(presentedPassword,aesKey);
            } catch (Exception e) {
                this.logger.debug("密码解密失败");
                throw new BadCredentialsException("密码解密失败");
            }
            //父类执行验证
            super.additionalAuthenticationChecks(userDetails,new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),presentedPassword,authentication.getAuthorities()));
        }
    }
}
