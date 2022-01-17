package pp.spirit.io.pojo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import pp.spirit.base.base.BaseQuery;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author LiV
 * @Title: 查询工具类
 * @Package com.liv.sp.dao.datamodel
 * @Description:
 * @date 2020.12.11  17:41
 * @email 453826286@qq.com
 */
public class PPFileQuery extends PPFile implements BaseQuery<PPFile> {
    /**
     * 组装查询条件
     */
    @Override
    public QueryWrapper<PPFile> getQueryWrapper() {
        QueryWrapper qw = new QueryWrapper();
        if(StringUtils.isNotEmpty(this.getFileId())){
            qw.like(getMpColumnName("fileId"),this.getFileId());
        }
        if(StringUtils.isNotEmpty(this.getFileName())){
            qw.like(getMpColumnName("fileName"),this.getFileName());
        }
        if(StringUtils.isNotEmpty(this.getFileType())){
            qw.eq(getMpColumnName("fileType"),this.getFileType());
        }
        return qw;
    }

    /**
     * 组装查询条件
     */
    @Override
    public Specification<PPFile> getSpecification() {
        PPFile bean = this;
        return new Specification<PPFile>(){
            @Override
            public Predicate toPredicate(Root<PPFile> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = Lists.newArrayList();
                //实体类字段名称
                if(StringUtils.isNotEmpty(bean.getFileId())){
                    predicates.add(criteriaBuilder.like(root.get("fileId"), "%"+bean.getFileId()+"%"));
                }
                if(StringUtils.isNotEmpty(bean.getFileName())){
                    predicates.add(criteriaBuilder.like(root.get("fileName"), "%"+bean.getFileName()+"%"));
                }
                if(StringUtils.isNotEmpty(bean.getFileType())){
                    predicates.add(criteriaBuilder.equal(root.get("fileType"), bean.getFileType()));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
