Java8 이전에는 인터페이스가 추상 메소드만 가질 수 있었다. 

이런 방법의 구현은 별도의 클래스에서 오버라이딩 했어야 한다. 

따라서 인터페이스에 메소드를 추가하려면 해당 인터페이스를 implements 하고 있는 클래스에 코드를 새로 구현했어야 한다. 

이 문제를 해결하기 위해서 default method를 도입했다. (하위 호완성을 제공)

만약 두 인터페이스가 동일한 메서드 시그니처를 가진 default 메서드가 있다면, 구현 클래스에서는 사용할 기본 메서드를 명시적으로 지정하거나, 기본 메서드를 재정의 해야한다.

```java
interface TestInterface1
{
    // default method
    default void show()
    {
        System.out.println("Default TestInterface1");
    }
}
  
interface TestInterface2
{
    // Default method
    default void show()
    {
        System.out.println("Default TestInterface2");
    }
}
  
// Implementation class code
class TestClass implements TestInterface1, TestInterface2
{
    // Overriding default show method
    public void show()
    {
        // use super keyword to call the show
        // method of TestInterface1 interface
        TestInterface1.super.show();
  
        // use super keyword to call the show
        // method of TestInterface2 interface
        TestInterface2.super.show();
    }
  
    public static void main(String args[])
    {
        TestClass d = new TestClass();
        d.show();
    }
}
```

또한 인터페이스는 static 메소드를 가질 수 있다.
