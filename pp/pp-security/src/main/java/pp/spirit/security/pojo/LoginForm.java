package pp.spirit.security.pojo;

import lombok.Data;

@Data
public class LoginForm {
    private String username;
    private String password;
    private String captchaId;
    private String verifyCode;
    private String key1; // aes
    private String key2; // rsa public
}
