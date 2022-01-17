package pp.spirit.base.incrementer;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;
import pp.spirit.base.utils.ContextUtils;

import java.io.Serializable;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.api.base.incrementer
 * @Description: 雪花算法生成器,jpa和mybatis-plus 使用
 * @date 2021.2.23  16:01
 * @email 453826286@qq.com
 */
@Component
public class IdGenerator implements IdentifierGenerator, com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator {
    public static final String GEN_NAME = "snowFlakeId";
    public static final String GEN_CLASS_NAME = "pp.spirit.base.incrementer.IdGenerator";
    /**
     * Generate a new identifier.
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return ContextUtils.getBean(SnowFlake.class).nextId();
    }

    @Override
    public Number nextId(Object entity) {
        return ContextUtils.getBean(SnowFlake.class).nextId();
    }
}