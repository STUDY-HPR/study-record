### 1. static이란? [참고](https://javarevisited.blogspot.com/2011/11/static-keyword-method-variable-java.html#axzz7NnOYXbPY)

- `static` 변수는 개별적인 객체들에 속해있는 것이 아니라 클래스 자체에 속해있는 변수이다.
- 즉, 모든 객체들이 공유하는 변수라고 할 수 있고 이러한 특성 때문에 멀티 스레드 환경에서 반드시 `동기화`되어야 한다.
- 반면에, `non-static` 변수는 객체에 속해있기 때문에 객체마다 각기 다른 값을 갖는다.



#### 1) static context에서 non-static 변수를 참조할 수 없는 이유는? [참고](https://javarevisited.blogspot.com/2012/02/why-non-static-variable-cannot-be.html#axzz7NnOYXbPY)

- `static`변수는 클래스에 속하고 `non-static` 변수는 객체에 속한다고 했다.
- `static` 변수는 클래스 로더에 의해 JVM으로 로딩될 때 초기화되고, `non-static` 변수는 객체가 생성될 때 초기화된다.
- 객체를 생성하지 않고도 접근할 수 있는 `static context`에서 아직 생성되지 않은 `non-static` 변수를 참조할 경우 컴파일 에러가 발생한다.
- `static context`에서 `non-static` 변수에 접근할 수 있는 유일한 방법은 `static context`내에서 객체를 생성하고 그 객체를 통해 접근하는 방법밖에는 없다.



#### 2) main 메소드가 static인 이유는 무엇일까? [참고](http://javarevisited.blogspot.sg/2011/12/main-public-static-java-void-method-why.html)

- JVM이 자바 프로그램을 실행하기 위해서 첫 번째로 찾는 것이 바로 `main` 메소드이다.

- `main` 를 시작으로 자바 프로그램을 실행하는데 만약 `main` 메소드가 존재하지 않으면 `NoSuchMethodError:main` 에러를 발생시킨다.

- `main` 메소드가 static으로 선언되어있지 않으면 JVM은 프로그램을 실행시키기 위해서 메인 클래스의 객체를 생성해야 하는데, 만약 생성자가 `오버로딩`되어 있다면 어떤 생성자를 통해 객체를 생성해야하는지 알 수 없다.

- 그러나 static 메소드는 JVM으로 로딩될 때 사용할 수 있으므로 객체를 생성하지 않고도 JVM에 의해서 실행될 수 있다.

  - 추가로,  JVM이 `main` 메소드에 접근하기 위해서 `public` 접근 제어자를 쓴다.

  

#### 3) static block이라는 것도 있다.

- 클래스가 JVM으로 로딩될 때 실행되는 `static block`이 있다.

```java
static {
    String staticValue = "JVM으로 로드될 때 수행되는 영역이다."
}
```

- 주의할 점은 `static block`에서 Exception이 발생했을 때 JVM으로 클래스 로딩자체가 안되기 때문에 프로그램에서 해당 클래스를 참조할 경우 `java.lang.NoClassDefFoundError` 를 발생시킨다.



#### 4) static 메소드는 오버라이딩 할 수 없다.

- static 메소드는 객체가 아닌 클래스에 속하는 메소드이기 때문에 오버라이딩 할 수 없다.
- 부모 클래스와 자식 클래스에서 똑같은 시그니쳐를 갖는 메소드를 정의하고 호출했을 때, 호출하는 `타입` 정보에 기반하여 메소드를 호출한다.



#### 5) 언제 메소드를 static으로 선언해야할까?

- 메소드가 객체의 인스턴스 변수에 의존적할 필요가 없고 매개변수에만 의존하여 로직을 수행할 때 static으로 선언하면 좋다.(?)
- 하지만, static 메소드의 단점은 오버라이딩을 할 수 없기 때문에 mock 객체로 대체할 수 없어 테스트하기 까다롭다는 단점이 있다.

---

#### [더 알아보기]

[static변수와 non static 변수를 혼용하지 마라](https://javarevisited.blogspot.com/2011/11/static-keyword-method-variable-java.html#axzz7NnOYXbPY)

