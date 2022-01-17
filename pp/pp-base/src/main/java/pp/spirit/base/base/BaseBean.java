package pp.spirit.base.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import pp.spirit.base.utils.ThreadLocalContextHolder;
import pp.spirit.base.validate.BaseConstraintValidator;
import pp.spirit.base.validate.ValidException;
import pp.spirit.base.validate.annotation.BaseConstraint;

import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.base.base
 * @Description: 基础 bean
 * @date 2020.6.9  10:03
 * @email 453826286@qq.com
 */
@Data
@BaseConstraint(by = BaseConstraintValidator.class)
public abstract class BaseBean<T> implements java.io.Serializable{
    private static final long serialVersionUID = 8545996863226528798L;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected transient int size;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected transient int current;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected transient List<com.baomidou.mybatisplus.core.metadata.OrderItem> orders;

    /**
     * @Description: 子类实现，提供复杂逻辑校验
     * @date 2021.11.20 14:43
     */
    @JsonIgnore
    protected void valid(T value) throws ValidException {}

    /**
     * 见 pp.spirit.base.validate.annotation.BaseConstraint
     * @date 2021.11.20 14:43
     */
    @JsonIgnore
    public boolean doValid(T value){
        try {
            this.valid(value);
        }catch (Exception e){
            ConstraintValidatorContext context = (ConstraintValidatorContext)ThreadLocalContextHolder.get();
            ThreadLocalContextHolder.remove();
            //禁用默认提示
            context.disableDefaultConstraintViolation();
            //修改校验信息
            context.buildConstraintViolationWithTemplate(e.getMessage()).addConstraintViolation();
            return false;
        }
        return true;
    }

}
