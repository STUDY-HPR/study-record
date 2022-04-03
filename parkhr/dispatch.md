## 디스패치
디스패치는 어떤 메소드를 호출할 것인가를 결정하여 그것을 실행하는 과정이다. 

## 정적 디스패치
정적 디스패치는 그와 다르게 컴파일 시점에 어떤 메소드를 실행할지 결정된다.
``` java
public class Parent {
    public static void main(String[] args) {
        Child1 child = new Child1();
        child.test(); // Child1의 메소드가 실행된다!
    }

    public void test() {

    }
}

class Child1 extends Parent{
    @Override
    public void test() {
        System.out.println("child1");
    }
}

class Child2 extends Parent{
    @Override
    public void test() {
        System.out.println("child2");
    }
}
```


## 동적 디스패치
동적 디스패치(dynamic dispatch)는 실행시점에(런타임) 어떤 메소드를 실행할 지 결정되는 것이다.
``` java
public class Parent {
    public static void main(String[] args) {
        Parent child = new Child1();
        child.test(); // 컴파일 시점이는 누가 실행될 지 모른다.
    }

    public void test() {

    }
}

class Child1 extends Parent{
    @Override
    public void test() {
        System.out.println("child1");
    }
}

class Child2 extends Parent{
    @Override
    public void test() {
        System.out.println("child2");
    }
}
```


## 더블 디스패치
자바는 싱글 디스패치만을 지원하고 있다.

이를 해결하기 위한 ..
...

