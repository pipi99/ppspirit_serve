package pp.spirit.security.springsecurity.service;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import pp.spirit.base.properties.PPProperties;
import pp.spirit.base.utils.ContextUtils;
import pp.spirit.security.pojo.User;
import pp.spirit.security.service.UserService;
import pp.spirit.security.springsecurity.utils.SecurityUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User sysUser = userService.findByUserName(username);
        if (null == sysUser) {
            throw new UsernameNotFoundException("找不到该用户："+username);
        }else {
            //判断锁定状态
            int locked = sysUser.getLocked();
            if(locked ==1){
                int lockMinutes = ContextUtils.getBean(PPProperties.class).getUserLoginFailLockedMinutes();
                Date lockedDate = sysUser.getLockTime();
                //已经过了锁定时常
                if(DateUtils.addMinutes(lockedDate,lockMinutes).before(new Date())){
                    sysUser.setLocked(0);
                    userService.doLock(sysUser);
                }
            }
        }

        //权限
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(SecurityUtil.getUserPermissions(username).stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(Collectors.toList()));

        //是否锁定、是否可用交给框架去处理抛出异常
        return new org.springframework.security.core.userdetails.User(sysUser.getUserName(), sysUser.getPassword(),sysUser.getEnabled()!=0,true, true,sysUser.getLocked()!=1, authorities);
    }
}
