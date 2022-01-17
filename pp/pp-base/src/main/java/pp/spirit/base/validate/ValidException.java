package pp.spirit.base.validate;


/**
 * @Title: ValidException
 * @Description: 校验逻辑处理
 * @author LW
 * @date 2021.11.20 19:34
 */
public class ValidException extends RuntimeException{
    public ValidException(){
        super();
    }
    public ValidException(String msg){
        super(msg);
    }
}
