>spirit 项目介绍  
* 一个基于maven构建的，springboot\mybatis-plus\shiro 简单的后端单体项目框架，支持基本的增删改查、权限控制、日志管理、文档管理、流程管理功能。  
* 缓存采用redis,文件存储采用minio,日志管理使用logback，接口文档 swagger  ，数据校验 hibernate
* 部署方式支持 war 、jar、前后分离、整包
* 支持数据库多模式名全局配置
* 雪花ID主键规则
* 模块化组件，根据需要增加，模块之间弱依赖。`pp-base` 必须有， `pp-auth` `pp-util`,`pp-io`,`pp-monitor`按需引入  
  > * pp-base 支持项目运行的基础模块 ,引入：启动类添加 `@EnablePPSpirit`
  > * pp-security 权限控制模块,Bcrypt加密存储密码，用户登录 AES+RSA 密码加密传输，用户凭证 token+refreshToken 双重认证
  > * pp-io 文件管理模块  
  > * pp-cache 缓存管理模块
  > * pp-monitor 系统监控、日志模块  
  > * pp-util 工具类模块  
---

样例工程 ppweb 介绍: 
# POM.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <artifactId>pp</artifactId>
    <groupId>pp.spirit</groupId>
    <version>1.0-SNAPSHOT</version>
  </parent>

  <groupId>org.example</groupId>
  <artifactId>ppweb</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>war</packaging>

  <name>ppweb Maven Webapp</name>
  <!-- FIXME change it to the project's website -->
  <url>http://www.example.com</url>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
  </properties>

  <dependencies>
    <dependency>
      <artifactId>pp-base</artifactId>
      <groupId>pp.spirit</groupId>
      <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version>1.18.18</version>
      <scope>compile</scope>
    </dependency>
  </dependencies>
  <profiles>
    <profile>
      <!-- 声明这个profile的id身份 -->
      <id>dev</id>
      <!-- 默认激活：比如当mvn package命令是，没有传入参数，默认使用这个
                                  当使用mvn package -P dev 传入参数时，表示使用这个id的profile -->
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <!-- 该标签下配置对应的key  value -->
      <properties>
        <!-- 这里的标签名任意，在 项目的 properties、xml等配置文件中可以使用${profiles.active}取出dev这个值-->
        <profiles.active>dev</profiles.active>
      </properties>
    </profile>
    <profile>
      <id>test</id>
      <properties>
        <profiles.active>test</profiles.active>
      </properties>
    </profile>
    <profile>
      <id>pro</id>
      <properties>
        <profiles.active>pro</profiles.active>
      </properties>
    </profile>
  </profiles>
  <build>
    <finalName>${project.artifactId}-${project.version}</finalName>
    <resources>
      <resource>
        <directory>src/main/webapp</directory>
        <!--注意此次必须要放在此目录下才能被访问到 -->
        <targetPath>META-INF/resources</targetPath>
        <includes>
          <include>**/**</include>
        </includes>
      </resource>
      <resource>
        <directory>src/main/resources</directory>
        <filtering>true</filtering>
        <excludes>
          <exclude>profiles/**</exclude>
        </excludes>
        <includes>
          <include>**/*</include>
        </includes>
      </resource>
      <resource>
        <directory>src/main/resources/profiles/${profiles.active}</directory>
        <targetPath>${project.build.directory}/classes</targetPath>
      </resource>
    </resources>
    <pluginManagement><!-- lock down plugins versions to avoid using Maven defaults (may be moved to parent pom) -->
      <plugins>
        <plugin>
          <artifactId>maven-clean-plugin</artifactId>
          <version>3.1.0</version>
        </plugin>
        <!-- see http://maven.apache.org/ref/current/maven-core/default-bindings.html#Plugin_bindings_for_war_packaging -->
        <plugin>
          <artifactId>maven-resources-plugin</artifactId>
          <version>3.0.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-compiler-plugin</artifactId>
          <version>3.8.0</version>
        </plugin>
        <plugin>
          <artifactId>maven-surefire-plugin</artifactId>
          <version>2.22.1</version>
        </plugin>
        <plugin>
          <artifactId>maven-war-plugin</artifactId>
          <version>3.2.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-install-plugin</artifactId>
          <version>2.5.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-deploy-plugin</artifactId>
          <version>2.8.2</version>
        </plugin>
      </plugins>
    </pluginManagement>
  </build>
</project>

```
# 配置文件
```yaml
server:
  port: 81
  servlet:
    context-path: /pp
  tomcat:
    uri-encoding: UTF-8
##自定义白标签错误页面
  error:
    whitelabel:
      enabled: false
    path: /error

spring:
  server-dir: f:/springboot-tomcat
## 环境配置
  profiles:
    active: dev
## 数据库配置
  datasource:
    username: root
    password: root
    url: jdbc:mysql://localhost:3306/auth?useAffectedRows=true&useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
#    username: BJ_TEST
#    password: 123456789
#    url: jdbc:oracle:thin:@192.168.14.156:1521:orcl
#    type: com.alibaba.druid.pool.DruidDataSource
#    driver-class-name: oracle.jdbc.OracleDriver
  jpa:
    hibernate:
      #validate 项目启动表结构进行校验 如果不一致则报错
      #none
      ddl-auto: none
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
    show-sql: true
    format_sql: true
    properties:
      hibernate:
        event:
          merge:
            entity_copy_observer: allow
        session_factory:
          statement_inspector: pp.spirit.base.interceptor.JpaDynamicTableNameInterceptor
## 文件上传大小配置,不限制大小
  servlet:
    multipart:
      maxFileSize: -1
      maxRequestSize: -1
## 缓存配置
  redis:
    database: 0
    host: 127.0.0.1
    port: 6379
    password:
    timeout: 30000
#    cluster:
#      nodes:
#        - 127.0.0.1:6379
#        - 127.0.0.1:6380
#        - 127.0.0.1:6381
#        - 127.0.0.1:6382
    lettuce:
      # 关闭超时时间,在关闭客户端连接之前等待任务处理完成的最长时间，在这之后，无论任务是否执行完成，都会被执行器关闭。
      shutdown-timeout: 1000
      pool:
        # 连接池中的最大空闲连接 默认8
        max-idle: 8
        # 连接池中的最小空闲连接 默认0
        min-idle: 0
        # 连接池最大连接数 默认8 ，负数表示没有限制
        max-active: 8
        # 连接池最大阻塞等待时间（使用负值表示没有限制） 默认-1
        max-wait: -1

## 持久化配置
mybatis-plus:
  mapper-locations: classpath*:mybatis/mappers/**/*.xml
  global-config:
    # 控制台不打印logo
    banner: false

# 日志配置
logging:
  path: ${server.servlet.context-path}/logs

## 自定义配置
pp:
  io:
   
    # minio 
    # local 存储服务器的项目目录下
    type: minio  # 可选 minio 或者 local
    minio:
      end-point: http://127.0.0.1:9000/
      access-key: minioadmin
      secret-key: minioadmin
      default-bucket: miniobucket
    temp-dir: e:/temp  # 临时工作目录， 视频预览等。 指定绝对路径
  user:
    default-password: 123456
    login-fail-retry-times: 5   #用户登录失败重试次数
    login-fail-locked-time: 10   #用户登录失败锁定时常(分钟)
    login-timeouts: 60   #用户登录会话时常（分钟）
  token:
    auto-bind-cookie: false #登录成功后自动将token存储到cookie中（HttpOnly）。如果是前后分离项目，此项应设置为false，应交由前端处理token.否则的话前端无法读取和修改cookie值
    expire-time: 604800 # token有效时长，7天，这个jwt的有效期，默认设置不用修改，不得小于 会话有效期#  token过期时间，单位秒
    secret-key: storywebkey #  token加密密钥
  cache:
    expires: 604800  # 默认缓存有效时长，7天，使用CacheUtils 简单cache无永久缓存，默认设置不用修改;如需永久，使用 RedisUtils
  snowflake:
    worker-id: 1 #机器 ID 部分(影响雪花ID)
    datacenter-id: 18 #数据标识 ID 部分(影响雪花ID)(workerId 和 datacenterId 一起配置才能重新初始化 Sequence)
  database:
    default-table-prefix: auth #表前缀，这里自动追加 auth. 到sql的表名称前面； 包括jpa和mybatis（mybatis的默认schema配置失效）; 条件是表名称中不含点 .
    special-table-prefix:
      aaaa: USER,ORGAN,ROLE,APP,MENU,RESOURCES,USER_ROLE,OWNER_PERMISSION,LIV_GROUP,USER_GROUP,GROUP_ROLE,MENU_ACTIONS,DICT,DICT_TYPE,LIV_LOG,LIV_FILE  # 指定表的前缀，此类表追加 aaaa. 前缀  ; 条件是表名称中不含点 .,多个表明以英文逗号分隔
      other: ###### other 模式
```
# 增删改查
## POJO  
jpa和mybatis共用,代码要求jpa和mybatis 关于表的注解同时添加。如过只想用其一，在service层只继承官方其一即可，替换BaseService.
``` java
/**
 * 数据库实体,jpa和mybatis共用
 *
 * 1、表名和字段名称均使用驼峰标识，对应数据库使用下划线分割,指定 catalog（jpa）。
 * 2、主键采用自定义雪花ID算法,mybatis 默认设置，jpa需要GeneratedValue+GenericGenerator 配置
 * 3、Entity同时提供给 jpa 和 mybatis 使用 ，对二者注解的使用有一定功底
 * 4、采用 hibernateValidator 校验
 * 5、单表增删改查使用 mybatis
 * 6、联合查询使用 jpa
 * 7、not sql
 */
@Data
@Entity
@Table(name = "SP")
@TableName(value = "SP")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sp", description="java bean demo")
public class Sp  extends BaseBean<Sp> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @TableId
    @GeneratedValue(generator = IdGenerator.GEN_NAME,strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = IdGenerator.GEN_NAME, strategy = IdGenerator.GEN_CLASS_NAME)
    @ApiModelProperty(value = "主键")
    private Long id ;

    /*名称*/
    @ApiModelProperty(value = "名称")
    @Column(name="T_NAME")
    @TableField("T_NAME")
    private String name;

    /*说明*/
    @ApiModelProperty(value = "描述")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Override
    public void valid(Sp sp){
        //执行校验
        if (StringUtils.isEmpty(name)) {
            throw new ValidException("名称不能为空");
        }
    }
}
```
+ 校验，采用hibernate校验框架，除了字段上注解校验外，支持自定义逻辑校验  
`throw new ValidException("名称不能为空");`抛出异常， 配合controller 方法上添加 `@ValidResult`交给全局捕获。
``` java
//实体类中实现
@Override
public void valid(Sp sp){
    //执行校验
    if (StringUtils.isEmpty(name)) {
        throw new ValidException("名称不能为空");
    }
}
```
+ 查询，同时支持 jpa 和 mybatis 方式  ，配置相应的查询条件即可
建议：  
单表增删改查及复杂自定义sql查询使用 mybatis  
简单联合查询使用 jpa  
``` java
// 查询工具类继承
public class SpQuery extends Sp implements BaseQuery<Sp> {
    /**
     * 组装mybatis查询条件
     */
    @Override
    public QueryWrapper<Sp> getQueryWrapper() {
        QueryWrapper qw = new QueryWrapper();
        if(StringUtils.isNotEmpty(this.getName())){
            //实体类字段名称
            qw.like(getColumnName("name"),this.getName());
        }
        return qw;
    }

    /**
     * 组装jpa查询条件
     */
    @Override
    public Specification<Sp> getSpecification() {
        Sp bean = this;
        return new Specification<Sp>(){
            @Override
            public Predicate toPredicate(Root<Sp> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                //root.get("address")表示获取address这个字段名称
                List<Predicate> predicates = Lists.newArrayList();
                //实体类字段名称
                if(StringUtils.isNotEmpty(bean.getName())){
                    predicates.add(criteriaBuilder.like(root.get("name"), "%"+bean.getName()+"%"));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
```
# DAO  
同时创建 mybatis 和 jpa
```java
@Mapper
public interface SpMapper extends BaseMapper<Sp> {
}
```
```java
@Repository
public interface SpRepository extends BaseRepository<Sp,Long> {
}
```

#Service
简单接口
```java
public interface SpService{
}
```
实现类
```java
 @Service
 public class SpServiceImpl extends BaseService<Sp, SpMapper, SpRepository> implements SpService {
 }
```
#Controller
service直接调用mybatis方法，通过`service.getRepository()`调用JPA
```java
 @RestController
 @RequestMapping(value = "/test")
 @Api(tags = "测试控制器")
 public class SpController  extends BaseController<SpServiceImpl> {
 
     @ApiOperation(value = "根据ID查询", notes="根据给定的ID 查询")
     @ApiImplicitParam(name = "id", value = "当前登录ID", required = true, dataType = "String", paramType = "path",defaultValue = "1")
     @GetMapping(value="/getById/{id}")
     public ResultBody getById(@PathVariable("id") Long SpId) throws Exception {
         return ResultBody.success(service.getById(SpId));
     }
 
     @ApiOperation(value = "查询列表", notes="查询列表")
     @GetMapping(value="/list")
     @ValidResult
     public ResultBody list() throws Exception { 
        return ResultBody.success(service.list());
     }
 
     @ApiOperation(value = "jpa查询列表", notes="jpa查询列表")
     @PostMapping(value="/listjpa")
     public ResultBody listjpa() throws Exception {
         List<Sp> list = service.getRepository().findAll();
         return ResultBody.success(list);
     }
 
     @ApiOperation(value = "分页查询列表", notes="分页查询列表")
     @PostMapping(value="/pagelist")
     public ResultBody pagelist(@RequestBody SpQuery query) throws Exception {
         Page<Sp> pageList = service.page(query.getPage(),query.getQueryWrapper());
         return ResultBody.success(pageList);
     }
 
     @ApiOperation(value = "jpa分页查询列表", notes="jpa分页查询列表")
     @PostMapping(value="/pagelistjpa")
     public ResultBody pagelistjpa(@RequestBody SpQuery query) throws Exception {
         Page<Sp> list = toPlusPage(query,service.getRepository().findAll(query.getSpecification(),query.getJpaPage()));
         return ResultBody.success(list);
     }
 
     @ApiOperation(value = "新增", notes="新增")
     @PostMapping(value="/save")
     @ValidResult
     public ResultBody save(@RequestBody(required = true) @Valid Sp d, BindingResult result) {
         service.save(d);
         return ResultBody.success("保存成功");
     }
 
     @ApiOperation(value = "jpa新增", notes="jpa新增")
     @PostMapping(value="/save2")
     @ValidResult
     public ResultBody save2(@RequestBody(required = true) @Valid Sp d, BindingResult result) {
         service.getRepository().save(d);
         return ResultBody.success("保存成功");
     }
 
     @ApiOperation(value = "更新", notes="更新,根据主键id更新")
     @PutMapping(value="/update")
     @ValidResult
     public ResultBody update(@RequestBody(required = true) @Valid Sp d, BindingResult result) {
         service.updateById(d);
         return ResultBody.success("修改成功");
     }
 
     @ApiOperation(value = "删除", notes="删除,根据主键id删除")
     @DeleteMapping(value="/remove/{id}")
     public ResultBody delete(@PathVariable("id") Long id){
         boolean result = service.removeById(id);
         return ResultBody.success(result?"删除成功":"删除失败");
     }
 
     @ApiOperation(value = "批量删除", notes="删除,根据主键id删除")
     @PostMapping(value="/remove")
     public ResultBody delete(@RequestBody List<Long> ids){
         service.removeByIds(ids);
         return ResultBody.success("删除成功");
     }
 }
```