package pp.spirit.security.springsecurity.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码校验异常
 */
public class CaptchaCheckException  extends AuthenticationException {

    public CaptchaCheckException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CaptchaCheckException(String msg) {
        super(msg);
    }
}
