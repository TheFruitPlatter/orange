# Orange - 企业级Java开发基础组件

<div align="right">
  <a href="./README.md">English</a> |
  <a href="./README.zh_CN.md">中文</a>
</div>


## 项目背景
在我过去的10多年工作经验中，发现所谓的研发规范，很多情况下都沦为形式主义，落实到位的很少。所以本项目的目的就是规范开发，提升项目质量，提升效率。如果你也面临一下几个问题，你一定要尝试一下。

### 常见痛点问题
#### Redis管理难题
1. Key命名冲突风险，依赖人工Code Review
2. 过期时间不统一，同一个Key，涉及到不同的功能，不同的功能不同的开发者完成。
3. 缓存一致性难以保证（先删后查模式仍存在不一致）
4. 缺乏自动续约功能（Spring Boot原生方案 vs Redission的兼容问题）

> 类似问题同样存在于Zookeeper、MQ、SQL等常用组件中

## 技术路线图
| 组件        | 状态     |
|-------------|----------|
| Redis       | ✅ 已完成 |
| 日志        | 🚧 开发中 |
| Zookeeper   | ⌛ 待开发 |
| MQ          | ⌛ 待开发 |
| JDBC        | ⌛ 待开发 |
| 线程池      | ⌛ 待开发 |

## Orange-Redis 核心特性
### 规范化管理
- 面向接口的Key管理
- 自动冲突检测

### 高性能保障
- 熔断/限流支持
- 批量锁操作+自动续约

### 数据一致性
- 事务支持
- 与数据库事务最终一致性保证

## 快速入门
### 环境准备
- JDK 1.8+
- Maven 3.0+

### 安装方式
#### 方案A：源码编译（当前推荐）
```bash
git clone [项目地址]
mvn clean install
```
#### 方案B：Maven依赖（即将发布）
```xml
<dependency>
  <groupId>com.langwuyue</groupId>
  <artifactId>orange-redis-spring-boot-starter</artifactId>
  <version>1.0.0-RELEASE</version>
</dependency>
```
### 基础示例

#### 1. 启用组件扫描
```java
@OrangeRedisClientScan(basePackages = "com.your.package")
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```
#### 2. 定义操作接口

```java
/**
 * Redis缓存操作接口定义
 * 使用@OrangeRedisValueClient注解指定操作类型
 * 使用@OrangeRedisKey注解定义Key规则
 */
@OrangeRedisValueClient(valueType = RedisValueTypeEnum.STRING)
@OrangeRedisKey(
    expirationTime = @Timeout(value = 10, unit = TimeUnit.SECONDS),
    key = "orange:value:demo"
)
public interface DemoRedisApi {
    
    /**
     * 设置缓存值
     * @param value 要缓存的字符串值
     */
    @SetValue
    void cacheValue(@RedisValue String value);
    
    /**
     * 获取缓存值
     * @return 返回缓存的字符串值
     */
    @GetValue
    String getValue();
}
```

#### 3. 业务调用
```java
/**
 * 示例Controller展示Redis接口调用
 */
@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private DemoRedisApi redisApi;

    /**
     * 设置缓存值接口
     * @param data 要缓存的数据
     * @return 操作结果
     */
    @PostMapping("/set")
    public ResponseEntity<String> setCacheData(@RequestParam String data) {
        redisApi.cacheValue(data);
        return ResponseEntity.ok("Cache set successfully");
    }

    /**
     * 获取缓存值接口
     * @return 缓存的数据
     */
    @GetMapping("/get")
    public ResponseEntity<String> getCacheData() {
        String value = redisApi.getValue();
        return ResponseEntity.ok(value);
    }
}
```
## 进阶指南
参考项目中的示例模块：
- 覆盖90%+的典型使用场景
- 包含完整测试用例
- 提供最佳实践示例

## 加入我们
参与贡献方式：
- 提交Pull Request
- 报告Issues
- 联系邮箱：jianpanxia_liang@163.com