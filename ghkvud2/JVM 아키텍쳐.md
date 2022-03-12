## JVM의 구성요소

JVM은 아래 그림과 같이 이루어져있으며, 크게 3가지로 구분할 수 있다.

![JVM Architecture Diagram](https://www.javainterviewpoint.com/java-virtual-machine-architecture-in-java/jvm-architecture/)

---

### 1. Class Loader Subsystem

- `Class Loader Subsystem`은 `Runtime`시점에 클래스에 대한 참조가 발생할 때, `load, link, initialize` 단계를 수행한다.
- **즉, `.class` 파일을 메모리에 적재하고 검증 및 초기화하는 과정을 거쳐 어플리케이션에서 `.class`가 사용될 수 있도록하는 역할을 한다.**

#### 1.1 Loading

- `.java` 파일을 컴파일해서 생성된 `.class` 파일을 `Method 영역`에 적재하는 과정이다.

  ##### 1. BootStrap ClassLoader

  - `Extension ClassLoader`의 부모 클래스로서 `JAVA_HOME/jre/lib` 디렉토리에 있는 `rt.jar` 파일을 메모리로 적재하는 역할을 한다.
  - `rt.jar` 파일에는 `java.lang`과 같은 자바 표준 클래스들이 있다.

  ```java
  public class ClassLoaderExample {
  
  	public static void main(String[] args) {
  		System.out.println(String.class.getClassLoader());
  	}
  }
  ```

  ```
  [출력 결과]
  null
  ```

  - 출력 결과가 `null`인 이유는 `BootStrapClassLoder`는 `native code`로 작성되어 있기 때문에 Java 클래스로 표현이 되지 않기 때문이다.

  ##### 2. Extension (Platform) ClassLoader

  - `Application ClassLoder`의 부모 클래스로서 `JAVA_HOME/lib/ext` 에 있는 클래스들을 적재하는 역할을 한다.

  ```java
  public class ClassLoaderExample {
  
  	public static void main(String[] args) {
  		System.out.println(ZipInfo.class.getClassLoader());
  	}
  }
  ```

  ```
  [출력 결과]
  ClassLoader of ZipInfo.class: sun.misc.Launcher$ExtClassLoader@5a07e868
  ```

  - Java 버전에 따라 `Extension ClassLoader`가 로딩하는 클래스 위치(?)가 달라진 것 같다.
  - Java 11버전에서는 위 예제의 `ZipInfo`클래스를 찾을 수 없다는 에러가 발생한다.

  

  ##### 3. Application (System) ClassLoader

  - 어플리케이션에서 작성된 클래스들을 메모리에 적재하는 클래스 로더이다.
  - `classpath` 경로에 있는 클래스들을 메모리로 적재시키며 `-classpath, -cp` 옵션으로 해당 경로를 변경할 수 있다.

  ```java
  static class AppClass {}
  
  public class ClassLoaderExample {
  	public static void main(String[] args) {
  		System.out.println(AppClass.class.getClassLoader());
  	}
  }
  
  ```

  ```
  [출력 결과]
  jdk.internal.loader.ClassLoaders$AppClassLoader@2437c6dc
  ```

#### 1.2 Linking

##### 1) Verify

- `.class` 파일의 정합성(?)을 검증한다. 파일의 포맷팅과 유효한 컴파일러에 의해 컴파일되었는지 확인한다.
- 검증에 실패할 경우, `java.lang.VerifyError` 를 발생시킨다.

##### 2) Preparation

- `static` 변수들을 메모리에 적재하고 타입에 따라 `default` 값으로 초기화한다.
- 사용자가 정의한 값으로 초기화되는 과정은 다음의 `Initialization`에서 이뤄진다.

##### 3) Resoultion

- 심볼릭 참조가 실제 참조로 변경된다??? (무슨 소리인지 모르겠네)

#### 1.3 Initialization

- `static` 변수를 사용자가 정의한 값으로 초기화하고 `static block`이 실행된다.

---

### 2. Runtime Data Area

- `Runtime Data Area` 영역은 다섯가지 영역으로 구분된다.

#### 2.1 Method Area

- JVM마다 하나씩 생성되며 공유 메모리 공간이다. (non thread-safe)

- 모든 클래스 레벨의 데이터가 저장되는 영역이다.

- 클래스와 인터페이스에 대한 `Constant Pool`, 필드, `static 변수`, 생성자와 메소드를 저장하는 공간이다.

- `ClassLoader`가 `.class` 파일의 정보들을 파싱해서 `Method Area` 영역에 저장한다.

- ##### Constant Pool이란?

  - 클래스 파일의 구성 항목중 하나로 리터럴 상수 값을 저장하는 곳이다.
  - String, 숫자, 식별자 이름, Class 및 Method에 대한 참조 값이 저장된다.
  - 리터럴 상수 값들은 클래스 단위로 `Constant Pool`에서 관리되고 한 클래스에 여러 번 등장하는 리터럴 값은 메모리에 중복으로 저장되지 않는다.

#### 2.2 Heap Area

- 프로그램을 실행하면서 생성한 객체, 배열 등이 저장되는 곳이다.

#### 2.3 Stack Area

- 모든 스레드들이 동작할 수 있도록 독립적인 메모리를 할당하는 공간이다.
- 스레드별로 각각의 독립적인 메모리를 할당받으며 이 메모리는 공유되지 않는다.
- 메소드의 매개변수, 메소드 호출이 끝난 뒤 돌아갈 주소 값, 지역 변수 등이 저장된다.
- 이렇게 차례대로 저장되는 메소드의 호출 정보를 `스택 프레임`이라고 한다.

#### 2.4 PC Register

- 각각의 스레드들은 다음에 실행할 명령어 주소 값을 갖는 독립적인 `PC Register`를 갖는다.

#### 2.5 Native Method stack

- Java `ByteCode`가 아닌 다른 언어로 작성된 메소드를 실행할 때 스레드 별로 할당되는 메모리 공간이다.

---

### 3. Execution Engine

`Runtime Data Area` 영역에 할당된 바이트 코드는 `Execution Engine`에 의해서 읽혀지고 실행된다.

#### 3.1 Interpreter

#### 3.2 JIT-Compiler

#### 3.3 Garbage Collector



---

[출처]

- [DZone](https://dzone.com/articles/jvm-architecture-explained)

- [클래스로더 훑어보기](https://homoefficio.github.io/2018/10/13/Java-%ED%81%B4%EB%9E%98%EC%8A%A4%EB%A1%9C%EB%8D%94-%ED%9B%91%EC%96%B4%EB%B3%B4%EA%B8%B0/)
- [Baeldung](https://www.baeldung.com/java-classloaders)
- [Geeksforgeeks](https://www.geeksforgeeks.org/jvm-works-jvm-architecture/)

- [Permgen vs Metaspace in Java](https://www.baeldung.com/java-permgen-metaspace)

- [MetaSpace in Java 8 with Examples](https://www.geeksforgeeks.org/metaspace-in-java-8-with-examples/)
- [Understand JVM Loading, JVM Linking, and JVM Initialization](https://www.developer.com/design/understand-jvm-loading-jvm-linking-and-jvm-initialization/)
- [An Introduction to the Constant Pool in the JVM](https://www.baeldung.com/jvm-constant-pool)