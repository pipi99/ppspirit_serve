package pp.spirit.security.springsecurity.utils;

import org.springframework.stereotype.Component;

/**
 * 密码加密处理
 */
@Component
public class BCryptPasswordEncoder extends  org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder{
}
