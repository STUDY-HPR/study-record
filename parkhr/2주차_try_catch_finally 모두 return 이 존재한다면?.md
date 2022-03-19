## try_catch_finally 모두 return 이 존재한다면?

```java
public class ExceptionTest {
    public static void main(String[] args) {
        System.out.println(test());
    }

    public static int test() {
        try{
            test2(null);
            return 0;

        }catch (Exception e){
            return 1;
        }finally {
            System.out.println("finally");
        }
    }

    public static void test2(String a) throws Exception {
        if(a == null) throw new Exception();
        else {
            System.out.println("gogo");
        }
    }
}
```

보통 위 코드처럼 try 블럭안에서 로직을 처리하고 에러를 던지면 catch문에서 에러를 처리해준다. 그리고 마지막으로 finally는 무조건 처리 된다.

근데 만약 finally 블럭에서 return 으로 값을 넘겨주면 어떻게 될까?

<br/>

```java
public static int test() {
    try{
        test2(null);
        return 0;

    }catch (Exception e){
        return 1;
    }finally {
        System.out.println("finally");
        return 2; // 여기서도 반환한다면?
    }
}
```

<br/>

decompile 해보면 이와 같이 finally 블럭 안에서 정의한 return 이 try catch 문에 치환된 것을 볼 수 있다.

이는 try catch 문 또는 finally 둘 중 어떤 걸로 return 해야할지 모호하기 때문에 컴파일러에서 처리해주는 것 같다.

```java
// decompile 된 file
public class ExceptionTest {
  public static void main(String[] paramArrayOfString) {
    System.out.println(test());
  }
  
  public static int test() {
    try {
      test2(null);
      return 2;
    } catch (Exception exception) {
      return 2;
    } finally {
      Exception exception = null;
      System.out.println("finally");
    } 
  }
  
  public static void test2(String paramString) throws Exception {
    if (paramString == null)
      throw new Exception(); 
    System.out.println("gogo");
  }
}
```

궁금증 

1. 또한 finally 블럭에서 Exception exception = null 을 통해 메모리를 해제하는걸 볼 수 있다. 그렇다면 try catch 문은 finally를 쓰는게 성능상 더 이득인가 ?
2. catch ( Exception exception ) 이 어떻게 finally 로 넘어오는건가 ?
