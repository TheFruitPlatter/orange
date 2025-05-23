# Orange - Enterprise Java Development Foundation Components

<div align="right">
  <a href="./README.md">English</a> |
  <a href="./README.zh_CN.md">中文</a>
</div>

## Project Background
With over 10 years of professional experience, I've observed that many so-called development standards often become mere formalities with poor implementation. This project aims to standardize development practices, improve project quality, and enhance efficiency. If you're facing any of the following challenges, you should give it a try.

### Common Pain Points
#### Redis Management Challenges
1. Key naming collision risks relying on manual code review
2. Inconsistent expiration times for the same Key across different features developed by different team members
3. Difficulty ensuring cache consistency (even with "delete-before-query" pattern)
4. Lack of automatic renewal functionality (compatibility issues between Spring Boot native solution and Redission)

> Similar issues exist with other common components like Zookeeper, MQ, SQL, etc.

## Technology Roadmap
| Component     | Status     |
|--------------|-----------|
| Redis        | ✅ Completed |
| Logging      | 🚧 In Development |
| Zookeeper    | ⌛ Pending |
| MQ           | ⌛ Pending |
| JDBC         | ⌛ Pending |
| Thread Pool  | ⌛ Pending |

## Orange-Redis Core Features
### Standardized Management
- Interface-oriented Key management
- Automatic collision detection

### High Performance Guarantee
- Circuit breaker/rate limiting support
- Batch lock operations + automatic renewal

### Data Consistency
- Transaction support
- Final consistency guarantee with database transactions

## Quick Start
### Environment Requirements
- JDK 1.8+
- Maven 3.0+

### Installation Options
#### Option A: Source Compilation (Recommended)
```bash
git clone [project URL]
mvn clean install
```
#### Option B: Maven Dependency (Coming Soon)
```xml
<dependency>
  <groupId>com.langwuyue</groupId>
  <artifactId>orange-redis-spring-boot-starter</artifactId>
  <version>1.0.0-RELEASE</version>
</dependency>
```
### Basic Example
#### 1. Enable Component Scanning
```java
@OrangeRedisClientScan(basePackages = "com.your.package")
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```
#### 2. Define Operation Interface
```java
/**
 * Redis cache operation interface definition
 * Use @OrangeRedisValueClient to specify operation type
 * Use @OrangeRedisKey to define Key rules
 */
@OrangeRedisValueClient(valueType = RedisValueTypeEnum.STRING)
@OrangeRedisKey(
    expirationTime = @Timeout(value = 10, unit = TimeUnit.SECONDS),
    key = "orange:value:demo"
)
public interface DemoRedisApi {
    
    /**
     * Set cache value
     * @param value String value to cache
     */
    @SetValue
    void cacheValue(@RedisValue String value);
    
    /**
     * Get cache value
     * @return Cached string value
     */
    @GetValue
    String getValue();
}
```
#### 3. Business Implementation
```java
/**
 * Example Controller demonstrating Redis interface usage
 */
@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private DemoRedisApi redisApi;

    /**
     * Set cache value endpoint
     * @param data Data to cache
     * @return Operation result
     */
    @PostMapping("/set")
    public ResponseEntity<String> setCacheData(@RequestParam String data) {
        redisApi.cacheValue(data);
        return ResponseEntity.ok("Cache set successfully");
    }

    /**
     * Get cache value endpoint
     * @return Cached data
     */
    @GetMapping("/get")
    public ResponseEntity<String> getCacheData() {
        String value = redisApi.getValue();
        return ResponseEntity.ok(value);
    }
}
```
## Advanced Guide
Refer to the example module in the project:

- Covers 90%+ typical usage scenarios
- Includes complete test cases  
- Provides best practice examples

## Join Us
Contribution methods:

- Submit Pull Requests
- Report Issues  
- Contact email: `jianpanxia_liang@163.com`