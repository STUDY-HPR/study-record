### 정적 바인딩과 동적 바인딩

*바인딩(binding)이란 프로그램에 사용된 구성 요소의 실제 값 또는 프로퍼티를 결정짓는 행위를 의미합니다.*

*예를 들어 함수를 호출하는 부분에서 실제 함수가 위치한 메모리를 연결하는 것도 바로 바인딩입니다.*

[출처](http://www.tcpschool.com/php/php_oop_binding)

---

#### 1. 정적 바인딩

- `compile-time`에 발생하고, `Runtime` 시간 동안에는 변하지 않는 상태로 유지되는 바인딩이다.
- `static, private, final` 메소드는 정적 바인딩 대상이다.



아래 코드는 타입 정보를 기반으로 정적 바인딩이 발생하는 예시이다.

```java
class Parent{}
class Child extends Parent{}
``위와 같이 부모, 자식 클래스가 있을 때``

public class StaticBindingTest{
	
	public void method(Parent p){
		System.out.println("method(Parent)...");
	}
	
	public void method(Child c){
		System.out.println("method(Child)...");
	}	
	
	public static void main(String[] args) {

		Parent parent = new Child();
		Child child = new Child();

		StaticBindingTest test = new StaticBindingTest();

		test.method(parent);
		test.method(child);
	}	
}

```

위처럼 오버로딩된 메소드가 있을 때 `Parent` 타입의 객체를 파라미터로 전달했을 때와 `Child` 타입의 객체를 전달했을 때, 어떤 메소드가 호출될까?

```java
[실행결과]
method(Parent)...
method(Child)...
```

즉, `오버로딩`된 메소드는 타입정보만을 가지고 바인딩된다. `parent` 참조변수의 실제 구현체가 `Child` 객체라고 하더라도 `method(Parent p)` 메소드가 호출된다.



`Parent` 클래스와 `Child` 클래스에 같은 이름의 `static` 메소드를 추가해보자.

```java
class Parent{
	static void method(){
		System.out.println("parent....");
	}
}

class Child extends Parent{
	static void method(){
		System.out.println("Child...");
	}
}

public class StaticBindingTest {

	public static void main(String[] args) {
		Parent parent = new Child();
		parent.method();
	}
}
```

위와 같이 실행한 결과는 어떨까? `Child` 클래스가 `method`를 오버로딩했으니 `Child` 클래스가 재정의한 메소드가 호출될까?

```
[실행결과]
parent....
```

우선, `static 메소드`는 오버라이딩이 될 수 없다. **컴파일러 입장에서는 static 메소드를 보고, '아 이 메소드는 오버라이딩이 불가능하니까 실제 구현 클래스가 어떤 타입이건간에 이 메소드를 호출하면 되는구나'**라고 생각하고 정적 바인딩을 수행하는 것이다.

---

#### 2. 동적 바인딩

- `Runtime` 시간에 발생하거나 변경되는 바인딩이다.



아래와 같이 `Child` 클래스가 `Parent` 클래스의 메소드를 `오버라이딩`하도록 코드를 작성하자.

```java
class Parent{
	void method(){
		System.out.println("parent....");
	}
}

class Child extends Parent{
    @Override
	void method(){
		System.out.println("Child...");
	}
}

public class DynamicBindingTest {

	public static void main(String[] args) {
		Parent parent = new Child();
		parent.method();
	}
}
```

```
[실행결과]
Child...
```

`Child` 클래스는 `method()`를 오버라이딩했다. 참조 변수는 `Parent` 타입이지만 실제로는 `Child` 객체이므로 `Child`가 오버라이딩한 메소드를 호출한다. **컴파일러는 method() 를 수행하기 위해서는 실제 객체가 무엇인지 compile-time에는 판단할 수 없으므로 바인딩을 Runtime까지 미루게 된다.**

---

### 정리하면..

`정적 바인딩`은 타입 정보를 활용해서 `compile-time`에 바인딩을 한다. 그리고 `final, static, private` 메소드같이 `오버라이딩`이 불가능한 메소드들 또한 실제 객체가 무엇인지 `Runtime`까지 미룰 필요가 없으므로 `compile-time`에 바인딩한다.



반면, `동적 바인딩`은 실제 객체가 무엇인지 알아야하는 `오버라이딩`된 메소드를 호출할 때 발생한다.