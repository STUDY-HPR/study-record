## Multiple Data Source 적용하기

- 스프링 부트와 스프링 JPA를 활용해서 여러 DataSource의 환경을 설정하는 방법을 알아본다.



### 1. 구현 내용

- 스프링 부트 프로젝트를 생성하고, 구성에 필요한 디펜던시와 여러 DataSource를 사용하기 위해 필요한 환경 설정 파일을 생성한다. 학교와 관련된 정보를 저장하는 schooldb와 학생에 관련된 정보를 저장하는 studentdb를 생성하고 요청 URI에 따라서 각기 다른 datasource를 참조하도록 한다.

#### 1.1 school API

```json
[
    {
        "id" : 1,
        "name" : "서울대학교",
        "address" : "서울시 관악구 관악로 1" 
    }
]
```



#### 1.2 student API

```json
[
    {
        "id" : 1,
        "name" : "홍길동",
        "age" : 24
    },
    {
        "id" : 2,
        "name" : "성춘향",
        "age" : 26
    }
]
```



### 2. 데이터베이스 환경 설정 (application.yml)

```yaml
datasource:
  school:
    url: jdbc:mysql://localhost:3306/schooldb?createDatabaseIfNotExist=true
    username: root
    password: root
  student:
    url: jdbc:mysql://localhost:3306/studentdb?createDatabaseIfNotExist=true
    username: root
    password: root

spring:
  jpa:
    hibernate:
      ddl-auto: update
    generate-ddl: true
    show-sql: true
```



### 3. 엔티티

#### 3.1 School 엔티티

```java
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "school")
public class School {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String address;
}
```



#### 3.2 Student 엔티티

```java
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "student")
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private int age;
}
```

- 우리가 생성한 각각의 엔티티가 어느 datasource에 속하는지 명시해야하는데, 그 방법은 크게 두 가지가 있다.

  1. `@Table` 어노테이션에 **schema** 속성에 값을 설정한다.

     ```java
     @Entity
     @Table(name = "student", schema="student")
     public class Student {
         ...
     }
     ```

  2. `EntityManagerFactoryBuilder` 을 생성할 때 **packages** 속성에 값을 설정한다. (뒤에서 설명)



### 4. Data Source 환경 설정

- 우리 예제에서는 서로 다른 두 개의 데이터 베이스를 생성했기 때문에(school, student) datasource 설정 파일도 각각의 데이터 베이스에 맞게 두 개를 생성해야 한다. 둘 중 하나는 반드시 `@Primary` 어노테이션을 붙여서 primary 데이터 베이스를 지정해야 한다.

#### 4.1 Primary Data Source

```java
@Bean
@Primary
@ConfigurationProperties("datasource.student")
public DataSourceProperties studentDataSourceProperties(){
	return new DataSourceProperties();
}

@Bean
@Primary
public DataSource studentDataSource(){
	return studentDataSourceProperties()
		.initializeDataSourceBuilder()
		.type(HikariDataSource.class)
		.build();
}
```

- `application.yml` 파일에 있는 데이터 베이스 설정 값을 읽어서 `DataSourceProperties` 빈을 생성하고 그 설정 값을 토대로 `DataSource` 빈을 생성한다. 예제에서는 `Student` 데이터 베이스를 primary로 설정하기 위해 `@Primary` 어노테이션을 명시했다.

>적어도 하나의 DataSource 빈에 @Primary 어노테이션을 붙여야 한다. 그렇지 않을 경우, 어플리케이션이 실행되지 않는다.



#### 4.2 Secondary Data Source

```java
@Bean
@ConfigurationProperties(prefix = "datasource.school")
public DataSourceProperties schoolDataSourceProperties() {
	return new DataSourceProperties();
}

@Bean
public DataSource schoolDataSource() {
	return schoolDataSourceProperties()
		.initializeDataSourceBuilder()
		.type(HikariDataSource.class)
		.build();
}
```



### 5. EntityManagerFactory Bean

- 각각의 DataSource에 해당하는 `EntityManager` 객체를 얻기 위해 `EntityManagerFactoryBean` 을 사용한다.

  ```java
  @Bean(name = "studentEntityManager")
  @Primary
  public LocalContainerEntityManagerFactoryBean studentEntityManager(
  		EntityManagerFactoryBuilder builder) {
  	return builder.dataSource(studentDataSource()).packages(Student.class)
  			.build();
  }
  	
  @Bean(name = "schoolEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean schoolEntityManagerFactory(
  		EntityManagerFactoryBuilder builder) {
  	return builder.dataSource(schoolDataSource()).packages(School.class)
  			.build();
  }
  ```

  > 엔티티가 어떤 Datasource에 바인딩될지 정하는 방법은 `@Table` 어노테이션에 **schema** 값을 설정하는 방법과 지금 작성한 **packages** 메소드에 바인딩될 엔티티 타입을 전달하는 방법이 있다.



### 6. Transaction Management

- 각각의 DataSource에 해당하는 `TransactionManager`를 설정한다. 위에서 설정한 **EntityManagerFactory **를 참조할 수 있도록 `@Qualifier` 어노테이션을 사용한다.

  ```java
  @Bean(name = "studentTransactionManager")
  @Primary
  public PlatformTransactionManager studentTransactionManager(
  		@Qualifier("studentEntityManager") LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
  	return new JpaTransactionManager(entityManagerFactoryBean.getObject());
  }
  	
  @Bean(name = "schoolTransactionManager")
  public PlatformTransactionManager schoolTransactionManager(
  		@Qualifier("schoolEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
  	return new JpaTransactionManager(entityManagerFactoryBean.getObject());
  }
  ```

  

### 7. JPA Repository 환경 설정

- `@EnableJpaRepositories` 어노테이션을 사용해서 각각의 DataSource에 대한 정보를 제공해야 한다.

  ```java
  @EnableJpaRepositories(
  	basePackages = "com.example.multipledatasources.repository.student",
  	entityManagerFactoryRef = "studentEntityManager",
  	transactionManagerRef = "studentTransactionManager"
  )
  ```

  1. `basePackages` :  Repository의 기본 패키지를 설정한다. 
  2. `entityManagerFactoryRef` : 환경 설정 파일에 정의된 `EntityManagerFactory` 빈을 참조하도록 한다.
  3. `transactionManagerRef` : 환경 설정 파일에 정의된 `TransactionManager` 빈을 참조하도록 한다.

  

- 환경 설정을 끝마친 각각의 `DataSource` 파일은 아래와 같다.

  ```java
  @Configuration
  @EnableTransactionManagement
  @EnableJpaRepositories(
  	basePackages = "com.example.multipledatasources.repository.student",
  	entityManagerFactoryRef = "studentEntityManager",
  	transactionManagerRef = "studentTransactionManager"
  )
  public class StudentDataSourceConfig {
  
  	@Bean
  	@Primary
  	@ConfigurationProperties("datasource.student")
  	public DataSourceProperties studentDataSourceProperties() {
  		return new DataSourceProperties();
  	}
  
  	@Bean
  	@Primary
  	public DataSource studentDataSource() {
  		return studentDataSourceProperties()
  			.initializeDataSourceBuilder()
  			.type(HikariDataSource.class)
  			.build();
  	}
  
  	@Bean(name = "studentEntityManager")
  	@Primary
  	public LocalContainerEntityManagerFactoryBean studentEntityManager(
  		EntityManagerFactoryBuilder builder) {
  		return builder.dataSource(studentDataSource()).packages(Student.class)
  			.build();
  	}
  
  	@Bean(name = "studentTransactionManager")
  	@Primary
  	public PlatformTransactionManager studentTransactionManager(
  		@Qualifier("studentEntityManager") LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
  		return new JpaTransactionManager(entityManagerFactoryBean.getObject());
  	}
  }
  ```

  ```java
  @Configuration
  @EnableTransactionManagement
  @EnableJpaRepositories(
  	basePackages = "com.example.multipledatasources.repository.school",
  	entityManagerFactoryRef = "schoolEntityManagerFactory",
  	transactionManagerRef = "schoolTransactionManager"
  )
  public class SchoolDataSourceConfig {
  
  	@Bean
  	@ConfigurationProperties(prefix = "datasource.school")
  	public DataSourceProperties schoolDataSourceProperties() {
  		return new DataSourceProperties();
  	}
  
  	@Bean
  	public DataSource schoolDataSource() {
  		return schoolDataSourceProperties()
  			.initializeDataSourceBuilder()
  			.type(HikariDataSource.class)
  			.build();
  	}
  
  	@Bean(name = "schoolEntityManagerFactory")
  	public LocalContainerEntityManagerFactoryBean schoolEntityManagerFactory(
  		EntityManagerFactoryBuilder builder) {
  		return builder.dataSource(schoolDataSource()).packages(School.class)
  			.build();
  	}
  
  	@Bean(name = "schoolTransactionManager")
  	public PlatformTransactionManager schoolTransactionManager(
  		@Qualifier("schoolEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
  		return new JpaTransactionManager(entityManagerFactoryBean.getObject());
  	}
  
  }
  ```

  

### 8. Repository

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
}

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {
}
```



### 9. Controller

- Controller에서는 두 개의 endpoint를 제공한다. 하나는 `Student` 와 관련된 API이고, 다른 하나는 `School`과 관련된 API이다.
  1. http://localhost:8080/school
  2. http://localhost:8080/student

```java
@RestController
public class MainController {

	@Autowired
	private SchoolRepository schoolRepository;

	@Autowired
	private StudentRepository studentRepository;

	@GetMapping(value = "school")
	public ResponseEntity<List<School>> getSchool() {
		return ResponseEntity
            .status(HttpStatus.ACCEPTED)
			.body(schoolRepository.findAll());
	}

	@GetMapping(value = "student")
	public ResponseEntity<List<Student>> getStudent() {
		return ResponseEntity
            .status(HttpStatus.ACCEPTED)
			.body(studentRepository.findAll());
	}
}
```

