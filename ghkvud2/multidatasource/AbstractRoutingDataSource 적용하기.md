# AbstractRoutingDataSource 적용하기

- 스프링 부트와 JPA를 활용해서 `AbstractRoutingDataSource`를 적용해보도록 하자. AbstractRoutingDataSource은 DataSource를 구현한 클래스로써 `lookup key` 기반으로 동적으로 타겟 DataSource를 변경할 수 있도록하는 클래스이다. 현재 `context`에 따라 동적으로 타겟 DataSource를 변경할 수 있다.



### 1. 구현 내용

- 예제에서는 요청의 헤더 값에 따라 DataSource를 달리하는 예제를 작성한다. 우리가 글로벌한 서비스를 운영하고 있고 HTTP 요청의 `branch 헤더` 값에 따라 접속해야하는 데이터베이스가 다르다고 가정한다. 아래는 예제에서 제공하는 endpoint와 `branch 헤더`값에 따라 응답 형태이다.



#### 1.1 endpoint

- http://localhost:8080/employee

#### 1.2 `branch` = korea일 때, koreadb에 저장된 직원 정보를 조회하여 응답으로 반환한다.

```json
[
    {
        "id" : 1,
        "name" : "Hong Kildong",
        "branch" : "korea"
    },
    {
        "id" : 2,
        "name" : "Kim Sungjun",
        "branch" : "korea"
    }
]
```



#### 1.3 `branch` = japan일 때, japandb에 저장된 직원 정보를 조회하여 응답으로 반환한다.

```json
[
    {
        "id" : 1,
        "name" : "Yamamoto",
        "branch" : "Japan"
    }
]
```





### 2. 데이터베이스 환경 설정 (application.yml)

```yaml
datasource:
  korea:
    url: jdbc:mysql://localhost:3306/koreadb?userSSL=false&serverTimezone=UTC
    username: root
    password: root
  japan:
    url: jdbc:mysql://localhost:3306/japandb?userSSL=false&serverTimezone=UTC
    username: root
    password: root
spring:
  jpa:
    database: mysql
    hibernate:
      ddl-auto: update
    generate-ddl: true
    show-sql: true
```



### 3. 엔티티

```java
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "employee")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String branch;
}
```



### 4. DataSource Context

- `AbstractRoutingDataSource` 가 동적으로 타겟 DataSource를 변경하기 위해서 타겟이 될 수 있는 DataSource를 Map 형태로 가지고 있다. 이를 위해 `BranchEnum` 을 정의하여 Map의 key 값으로 사용한다.

  ```java
  @Getter
  @RequiredArgsConstructor
  public enum BranchEnum {
  
  	KOREA("korea"), JAPAN("japan");
  	
  	private final String name;
  
  }
  ```



### 5. Context Holder

- `AbstractRoutingDataSource` 에서 사용될 DataSource의 `lookup key`를 저장하는 용도로 사용된다. 현재 컨텍스트에서 사용될 DataSource의 key(BranchEnum 타입)을 `ThreadLocal`에 저장하고 `AbstractRoutingDataSource`  에서 ThreadLocal에 저장된 값을 참조하여 타겟 DataSource를 결정한다.

  ```java
  public class BranchContextHolder {
  
  	private static ThreadLocal<BranchEnum> threadLocal = new ThreadLocal<>();
  
  	public static void setBranchContext(BranchEnum branchEnum) {
  		threadLocal.set(branchEnum);
  	}
  
  	public static BranchEnum getBranchContext(){
  		return threadLocal.get();
  	}
  
  	public static void clearBranchContext(){
  		threadLocal.remove();
  	}
  }
  ```

  

### 6. DataSource Routing

- `DataSourceRouting` 클래스는 `AbstractRoutingDatasource` 를 상속받은 클래스다. `AbstractRoutingDatasource` 클래스의 `determineCurrentLookupKey` 메소드를 오버라이딩 했는데, 위에서 지정한 ContextHolder에서 현재 Context에서 사용될 타겟 DataSource를 꺼내온다.

  ```java
  public class DataSourceRouting extends AbstractRoutingDataSource {
  
  	@Override
  	protected Object determineCurrentLookupKey() {
  		return BranchContextHolder.getBranchContext();
  	}
  }
  ```



### 7. DataSource Configuration

```java
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
	basePackages = "com.example.routingdatasource.repository",
	entityManagerFactoryRef = "entityManagerFactory",
	transactionManagerRef = "transactionManager"
)
public class DataSourceConfig {

	@Bean
	@Primary
	public DataSource dataSource() {
		DataSourceRouting dataSourceRouting = new DataSourceRouting();
		dataSourceRouting.setTargetDataSources(targetDataSources());
		dataSourceRouting.setDefaultTargetDataSource(koreaDataSource());
		return dataSourceRouting;
	}

	private Map<Object, Object> targetDataSources() {
		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put(BranchEnum.KOREA, koreaDataSource());
		targetDataSources.put(BranchEnum.JAPAN, japanDataSource());
		return targetDataSources;
	}

	@Bean
	@ConfigurationProperties("datasource.korea")
	public DataSourceProperties koreaDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource koreaDataSource() {
		return koreaDataSourceProperties()
			.initializeDataSourceBuilder()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean
	@ConfigurationProperties("datasource.japan")
	public DataSourceProperties japanDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource japanDataSource() {
		return japanDataSourceProperties()
			.initializeDataSourceBuilder()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean(name = "entityManager")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
		EntityManagerFactoryBuilder builder) {
		return builder.dataSource(dataSource()).packages(Employee.class)
			.build();
	}

	@Bean(name = "transcationManager")
	public JpaTransactionManager transactionManager(
		@Autowired @Qualifier("entityManager") LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
		return new JpaTransactionManager(entityManagerFactoryBean.getObject());
	}

}
```



### 8. DataSource Interceptor

- 어플리케이션에 들어오는 요청의 `branch 헤더` 값을 확인하여 어떤 DataSource를 사용할지 결정한다.

  ```java
  @Component
  public class DataSourceInterceptor implements HandlerInterceptor {
  
  	@Override
  	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
  		Exception {
  
  		String branch = request.getHeader("branch");
  		if (BranchEnum.KOREA.toString().equalsIgnoreCase(branch)) {
  			BranchContextHolder.setBranchContext(BranchEnum.KOREA);
  		} else {
  			BranchContextHolder.setBranchContext(BranchEnum.JAPAN);
  		}
  		return true;
  	}
  }
  ```

  ```java
  @Configuration
  @RequiredArgsConstructor
  public class WebConfig implements WebMvcConfigurer {
  
  	private final DataSourceInterceptor dataSourceInterceptor;
  
  	@Override
  	public void addInterceptors(InterceptorRegistry registry) {
  		registry.addInterceptor(dataSourceInterceptor).addPathPatterns("/**");
  	}
  }
  ```



### 9. Repository & Service

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
}

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	public List<Employee> getEmployees() {
		return employeeRepository.findAll();
	}
}
```



### 10. Controller

```java
@RestController
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@GetMapping(value = "employee")
	public ResponseEntity<List<Employee>> getEmployees() {
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(employeeService.getEmployees());
	}
}
```

