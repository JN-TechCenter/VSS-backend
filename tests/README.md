# VSS 后端测试套件

## 📁 测试结构

```
VSS-backend/
├── src/
│   ├── main/java/com/vss/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── model/
│   │   └── config/
│   └── test/java/com/vss/           # 后端测试目录
│       ├── unit/                   # 单元测试
│       │   ├── controller/         # 控制器单元测试
│       │   │   ├── AuthControllerTest.java
│       │   │   ├── UserControllerTest.java
│       │   │   ├── InferenceControllerTest.java
│       │   │   └── ProfileControllerTest.java
│       │   ├── service/            # 服务层单元测试
│       │   │   ├── AuthServiceTest.java
│       │   │   ├── UserServiceTest.java
│       │   │   ├── InferenceServiceTest.java
│       │   │   └── ProfileServiceTest.java
│       │   ├── repository/         # 数据访问层单元测试
│       │   │   ├── UserRepositoryTest.java
│       │   │   ├── InferenceRepositoryTest.java
│       │   │   └── ProfileRepositoryTest.java
│       │   ├── model/              # 模型单元测试
│       │   │   ├── UserTest.java
│       │   │   ├── InferenceResultTest.java
│       │   │   └── ProfileTest.java
│       │   └── util/               # 工具类单元测试
│       │       ├── JwtUtilTest.java
│       │       ├── ValidationUtilTest.java
│       │       └── DateUtilTest.java
│       ├── integration/            # 集成测试
│       │   ├── api/                # API集成测试
│       │   │   ├── AuthApiIntegrationTest.java
│       │   │   ├── UserApiIntegrationTest.java
│       │   │   └── InferenceApiIntegrationTest.java
│       │   ├── database/           # 数据库集成测试
│       │   │   ├── UserDatabaseIntegrationTest.java
│       │   │   └── InferenceDatabaseIntegrationTest.java
│       │   └── external/           # 外部服务集成测试
│       │       ├── InferenceServiceIntegrationTest.java
│       │       └── RedisIntegrationTest.java
│       ├── e2e/                    # 端到端测试
│       │   ├── UserWorkflowE2ETest.java
│       │   ├── InferenceWorkflowE2ETest.java
│       │   └── AuthWorkflowE2ETest.java
│       ├── performance/            # 性能测试
│       │   ├── ApiPerformanceTest.java
│       │   ├── DatabasePerformanceTest.java
│       │   └── ConcurrencyTest.java
│       ├── security/               # 安全测试
│       │   ├── AuthSecurityTest.java
│       │   ├── SqlInjectionTest.java
│       │   └── XssProtectionTest.java
│       ├── fixtures/               # 测试数据
│       │   ├── UserFixtures.java
│       │   ├── InferenceFixtures.java
│       │   └── TestDataBuilder.java
│       ├── config/                 # 测试配置
│       │   ├── TestConfig.java
│       │   ├── TestDatabaseConfig.java
│       │   └── MockConfig.java
│       └── utils/                  # 测试工具
│           ├── TestUtils.java
│           ├── MockDataGenerator.java
│           └── AssertionHelpers.java
├── pom.xml
└── ...
```

## 🧪 测试类型说明

### 单元测试 (Unit Tests)
- **控制器测试**: REST API端点的独立测试
- **服务层测试**: 业务逻辑的单元测试
- **数据访问层测试**: Repository层的测试
- **模型测试**: 实体类和DTO的测试
- **工具类测试**: 工具方法的纯函数测试

### 集成测试 (Integration Tests)
- **API集成测试**: 完整的HTTP请求-响应测试
- **数据库集成测试**: 与真实数据库的交互测试
- **外部服务集成测试**: 与其他微服务的集成

### 端到端测试 (E2E Tests)
- **完整业务流程测试**: 从API到数据库的完整流程
- **用户场景测试**: 模拟真实用户操作

### 性能测试 (Performance Tests)
- **API性能测试**: 接口响应时间和吞吐量
- **数据库性能测试**: 查询性能和并发处理
- **并发测试**: 多线程和高并发场景

### 安全测试 (Security Tests)
- **认证授权测试**: JWT和权限验证
- **SQL注入防护测试**: 数据库安全
- **XSS防护测试**: 跨站脚本攻击防护

## 🚀 运行测试

### Maven命令运行测试
```bash
# 运行所有测试
mvn test

# 运行特定类型测试
mvn test -Dtest="**/*Test.java"           # 单元测试
mvn test -Dtest="**/*IntegrationTest.java" # 集成测试
mvn test -Dtest="**/*E2ETest.java"        # E2E测试
mvn test -Dtest="**/*PerformanceTest.java" # 性能测试

# 运行特定测试类
mvn test -Dtest="UserServiceTest"

# 运行特定测试方法
mvn test -Dtest="UserServiceTest#testCreateUser"
```

### 使用测试配置文件
```bash
# 使用测试环境配置
mvn test -Dspring.profiles.active=test

# 使用内存数据库
mvn test -Dspring.datasource.url=jdbc:h2:mem:testdb
```

### 生成测试报告
```bash
# 生成覆盖率报告
mvn jacoco:report

# 生成Surefire测试报告
mvn surefire-report:report
```

## 📊 测试覆盖率要求

- **单元测试覆盖率**: ≥ 85%
- **集成测试覆盖率**: ≥ 75%
- **整体代码覆盖率**: ≥ 80%
- **关键业务逻辑覆盖率**: ≥ 95%

## 🔧 测试配置

### Maven配置 (pom.xml)
```xml
<dependencies>
    <!-- Spring Boot Test Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Testcontainers for Integration Tests -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- MockWebServer for External API Testing -->
    <dependency>
        <groupId>com.squareup.okhttp3</groupId>
        <artifactId>mockwebserver</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- Surefire Plugin for Unit Tests -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <includes>
                    <include>**/*Test.java</include>
                </includes>
                <excludes>
                    <exclude>**/*IntegrationTest.java</exclude>
                    <exclude>**/*E2ETest.java</exclude>
                </excludes>
            </configuration>
        </plugin>
        
        <!-- Failsafe Plugin for Integration Tests -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-failsafe-plugin</artifactId>
            <configuration>
                <includes>
                    <include>**/*IntegrationTest.java</include>
                    <include>**/*E2ETest.java</include>
                </includes>
            </configuration>
        </plugin>
        
        <!-- JaCoCo Plugin for Coverage -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 测试配置类
```java
// src/test/java/com/vss/config/TestConfig.java
@TestConfiguration
@EnableJpaRepositories
@EntityScan("com.vss.model")
public class TestConfig {
    
    @Bean
    @Primary
    public DataSource testDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:schema.sql")
            .addScript("classpath:test-data.sql")
            .build();
    }
    
    @Bean
    @Primary
    public RedisTemplate<String, Object> testRedisTemplate() {
        // 使用嵌入式Redis或Mock
        return new RedisTemplate<>();
    }
}
```

## 📝 测试编写规范

### 单元测试示例
```java
// src/test/java/com/vss/unit/service/UserServiceTest.java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("应该成功创建新用户")
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .username("testuser")
            .email("test@example.com")
            .password("password123")
            .build();
            
        User savedUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .build();
            
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        UserResponse response = userService.createUser(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @DisplayName("当用户名已存在时应该抛出异常")
    void shouldThrowExceptionWhenUsernameExists() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .username("existinguser")
            .email("test@example.com")
            .password("password123")
            .build();
            
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessage("用户名已存在: existinguser");
    }
}
```

### 集成测试示例
```java
// src/test/java/com/vss/integration/api/UserApiIntegrationTest.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Transactional
class UserApiIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("vss_test")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    @DisplayName("应该成功注册新用户")
    void shouldRegisterUserSuccessfully() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .username("newuser")
            .email("newuser@example.com")
            .password("password123")
            .build();
        
        // When
        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
            "/api/users/register", 
            request, 
            UserResponse.class
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("newuser");
        
        // 验证数据库中确实创建了用户
        Optional<User> savedUser = userRepository.findByUsername("newuser");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("newuser@example.com");
    }
}
```

### E2E测试示例
```java
// src/test/java/com/vss/e2e/UserWorkflowE2ETest.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserWorkflowE2ETest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:6-alpine")
            .withExposedPorts(6379);
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("完整的用户注册-登录-更新资料流程")
    void shouldCompleteUserWorkflowSuccessfully() {
        // 1. 注册用户
        CreateUserRequest registerRequest = CreateUserRequest.builder()
            .username("e2euser")
            .email("e2euser@example.com")
            .password("password123")
            .build();
            
        ResponseEntity<UserResponse> registerResponse = restTemplate.postForEntity(
            "/api/users/register", 
            registerRequest, 
            UserResponse.class
        );
        
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        // 2. 用户登录
        LoginRequest loginRequest = LoginRequest.builder()
            .username("e2euser")
            .password("password123")
            .build();
            
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
            "/api/auth/login", 
            loginRequest, 
            LoginResponse.class
        );
        
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = loginResponse.getBody().getToken();
        
        // 3. 更新用户资料
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
            .displayName("E2E Test User")
            .bio("This is a test user")
            .build();
            
        HttpEntity<UpdateProfileRequest> requestEntity = new HttpEntity<>(updateRequest, headers);
        
        ResponseEntity<ProfileResponse> updateResponse = restTemplate.exchange(
            "/api/users/profile", 
            HttpMethod.PUT, 
            requestEntity, 
            ProfileResponse.class
        );
        
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().getDisplayName()).isEqualTo("E2E Test User");
    }
}
```

## 🐛 调试测试

### IDE调试
- 在IDE中右键测试类或方法选择"Debug"
- 设置断点进行逐步调试
- 查看变量值和调用栈

### 日志调试
```properties
# src/test/resources/application-test.properties
logging.level.com.vss=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### 测试数据调试
```java
@Test
void debugTest() {
    // 打印测试数据
    System.out.println("Test data: " + testData);
    
    // 使用日志
    log.debug("Processing user: {}", user);
    
    // 断言前检查
    assertThat(result).describedAs("Result should not be null").isNotNull();
}
```

## 📋 测试检查清单

### 开发前检查
- [ ] 确定测试策略和覆盖率目标
- [ ] 准备测试数据和Mock对象
- [ ] 配置测试环境和数据库

### 开发中检查
- [ ] 每个新方法都有对应的单元测试
- [ ] 关键业务流程有集成测试
- [ ] API端点有完整的测试覆盖

### 发布前检查
- [ ] 所有测试通过
- [ ] 代码覆盖率达到要求
- [ ] 性能测试满足标准
- [ ] 安全测试通过
- [ ] 集成测试在真实环境通过