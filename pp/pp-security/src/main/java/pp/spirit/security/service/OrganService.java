package pp.spirit.security.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.security.dao.jpa.OrganRepository;
import pp.spirit.security.dao.mapper.OrganMapper;
import pp.spirit.security.pojo.Organ;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@Service
public class OrganService  extends BaseService<Long, Organ, OrganMapper, OrganRepository> {
    @Transactional
    @Override
    public boolean removeById(Serializable id) {
        Organ organ = this.getRepository().findById((Long)id).get();
        //有用户
        if(organ.getUsers()!=null && organ.getUsers().size()>0){
            throw new RuntimeException("组织下有用户，无法删除，请先删除用户！");
        }

        //删除下级组织
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("PARENT_ID",id);
        List<Organ> organList = this.list(queryWrapper);
        if(organList!=null&&organList.size()>0){
            throw new RuntimeException("有下级组织，无法删除，请先删除下级组织！");
        }

        //删除
        return SqlHelper.retBool(this.getBaseMapper().deleteById(id));
    }
}
