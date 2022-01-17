package pp.spirit.security.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;
import pp.spirit.base.properties.PPProperties;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.security.dao.jpa.UserRepository;
import pp.spirit.security.dao.mapper.UserMapper;
import pp.spirit.security.pojo.User;
import pp.spirit.security.pojo.UserGroup;
import pp.spirit.security.pojo.UserQuery;
import pp.spirit.security.pojo.UserRole;
import pp.spirit.security.service.impl.UserGroupServiceImpl;
import pp.spirit.security.service.impl.UserRoleServiceImpl;
import pp.spirit.security.springsecurity.utils.JwtTokenUtil;
import pp.spirit.security.springsecurity.utils.SecurityAuthenticationCacheUtil;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends BaseService<Long, User, UserMapper, UserRepository>  {
    @Autowired
    private PPProperties ppProperties;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserGroupServiceImpl userGroupService;

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Autowired
    private OwnerPermissionService ownerPermissionService;
    /**
     * @Author: LiV
     * @Date: 2020.4.19 20:44
     * @Description: 根据用户名查询用户
     **/
    public User findByUserName(String username) {
        UserQuery query = new UserQuery();
        query.setUserName(username);
        List<User> users = this.getRepository().findAll(query.getSpecification());
        if(users != null&&users.size()>0) {
            return users.get(0);
        }
        return null;
    }

    /**
    * @Description: 用户锁定状态操作
    * @author: liv
    * @date  2022.1.11 15:34
    */
    public void doLock(User user) {
        User _user = this.getById(user.getUserId());
        _user.setLocked(user.getLocked());
        //需要锁定用户
        _user.setLockTime(null);
        if(user.getLocked() == 1){
            _user.setLockTime(new Date());
        }else{
            SecurityAuthenticationCacheUtil.deleteRetryTimes(_user.getUserName());
        }
        this.updateById(_user);
    }

    /**
     * @Description: 用户可用状态操作
     * @author: liv
     * @date  2022.1.11 15:34
     */
    public void doEnabled(User user) {
        User _user = this.getById(user.getUserId());
        _user.setEnabled(user.getEnabled());
        this.updateById(_user);
    }

    public User signedUser() {
        JwtTokenUtil jwtTokenUtil = ContextUtils.getBean(JwtTokenUtil.class);
        //用户携带的token
        String token = jwtTokenUtil.getRequestToken(ContextUtils.getRequest());
        String username = jwtTokenUtil.getUsernameFromToken(token);

        return this.findByUserName(username);
    }

    @Transactional
    @Override
    public boolean removeById(Serializable id) {
        //删除角色关系
        QueryWrapper qw = new QueryWrapper();
        qw.eq("USER_ID",id);
        userGroupService.remove(qw);
        userRoleService.remove(qw);

        QueryWrapper qw2 = new QueryWrapper();
        qw2.eq("OWNER_ID",id);
        ownerPermissionService.remove(qw2);

        return SqlHelper.retBool(this.getMapper().deleteById(id));
    }

    /**
     * @Author: LiV
     * @Date: 2020.4.19 17:05
     * @Description: 用户注册
     **/
    @Transactional
    public boolean reg(User user){
        user.setCreateDate(new Date());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.getMapper().insert(user);
        return true;
    }

    /**
     * @Author: LiV
     * @Date: 2020.4.19 17:05
     * @Description: 新增用户
     **/
    @Transactional
    @Override
    public boolean save(User user){
        user.setCreateDate(new Date());
        user.setPassword(passwordEncoder.encode(ppProperties.getUserDefaultPassword()));
        this.getMapper().insert(user);
        this.saveUserRoleGroup(user);
        return true;
    }

    /**
     * @Author: LiV
     * @Date: 2020.4.19 17:05
     * @Description: 修改密码
     **/
    @Transactional
    public boolean resetPwd(Long userId){
        User user = this.getById(userId);
        //修改密码
        user.setPassword(passwordEncoder.encode(ppProperties.getUserDefaultPassword()));
        return SqlHelper.retBool(this.getMapper().updateById(user));
    }

    @Transactional
    public boolean resetPwd(String oldPwd,String newPwd){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        QueryWrapper qw = new QueryWrapper();
        qw.eq("USER_NAME",username);
        User user = this.getOne(qw);
        if(passwordEncoder.matches(oldPwd,user.getPassword())){
            //修改密码
            user.setPassword(passwordEncoder.encode(newPwd));
            return SqlHelper.retBool(this.getMapper().updateById(user));
        }
        return false;
    }

    @Transactional
    @Override
    public boolean updateById(User user) {

        //删除角色关系
        QueryWrapper qw = new QueryWrapper();
        qw.eq("USER_ID",user.getUserId());
        userGroupService.remove(qw);
        userRoleService.remove(qw);

        QueryWrapper qw2 = new QueryWrapper();
        qw2.eq("OWNER_ID",user.getUserId());
        ownerPermissionService.remove(qw2);

        User databaseUser = this.getById(user.getUserId());
        user.setPassword(databaseUser.getPassword());
        this.getMapper().updateById(user);
        this.saveUserRoleGroup(user);
        return true;
    }

    //组合用户和用户组，用户和角色关系
    private void saveUserRoleGroup(User user){
        List<Long> roleIds = user.getRoleIds();
        if(roleIds!=null){
            List<UserRole> UserRoles = roleIds.stream().map(roleId->{
                return new UserRole(null,roleId,user.getUserId());
            }).collect(Collectors.toList());
            userRoleService.getRepository().saveAll(UserRoles);
            userRoleService.getRepository().flush();
        }

        List<Long> groupIds = user.getGroupIds();
        if(groupIds!=null){
            List<UserGroup> UserGroups = groupIds.stream().map(groupId->{
                return new UserGroup(null,groupId,user.getUserId());
            }).collect(Collectors.toList());
            userGroupService.getRepository().saveAll(UserGroups);
            userGroupService.getRepository().flush();
        }
    }
}

