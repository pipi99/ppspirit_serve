package com.pp.service;

import com.pp.dao.jpa.SpRepository;
import com.pp.dao.mapper.SpMapper;
import com.pp.pojo.Sp;
import org.springframework.stereotype.Service;
import pp.spirit.base.base.BaseService;

/**
 * @author LiV
 * @Title:
 * @Package com.liv.sp.service
 * @Description: 服务层
 * @date 2020.12.11  11:23
 * @email 453826286@qq.com
 */
@Service
public class SpService extends BaseService<Long, Sp, SpMapper, SpRepository> {
}
