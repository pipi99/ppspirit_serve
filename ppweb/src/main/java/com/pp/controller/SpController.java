package com.pp.controller;

import com.pp.pojo.Sp;
import com.pp.pojo.SpQuery;
import com.pp.service.SpService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pp.spirit.base.base.BaseController;

@Slf4j
@RestController
@RequestMapping(value = "/test")
@Api(value = "测试控制器",tags = {"测试控制器"})
public class SpController  extends BaseController<Sp,SpQuery,SpService> {

}
