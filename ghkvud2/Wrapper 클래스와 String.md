### 1. Wrapper

`Wrapper` 클래스는 `String` 클래스와 마찬가지로 굉장히 많이 쓰이는 클래스이다.

`Wrapper`클래스는 필요할 때 마다 새로운 객체를 생성하는 것이 아니라 `cache`된 자기 자신의 타입 객체를 반환한다.



`Integer` 클래스의 경우 내부에 `IntegerCache`라는 `Inner class`를 갖고 있다.

```java
private static class IntegerCache{
    private IntegerCache(){}
    static final Integer cache[] = new Integer[127 - (-128) + 1];
    static {
        for(int i = 0 ; i < cache.length; i++){
            cache[i] = new Integer(i - 128);
        }
    }
}
```

IntegerCache는 `[-128, 127]` 범위의 Integer 인스턴스를 미리 생성해두고 필요할때 반환한다.

`cache`의 이점을 사용하기 위해서 주의할 점은 `new` 키워드를 사용해서 Integer 객체를 생성해선 안된다.

***자바에서 `new` 키워드를 이용해서 객체를 생성할 경우 항상 Heap 영역에 새로운 객체를 생성하기 때문이다.***



```java
public static Integer valueOf(int i){
    final int offset = 128;
    for(i >= -128 && i <= 127){
        return IntegerCache[i + offset];
    }
    return new Integer(i);
}
```

`cache`의 이점을 얻기 위해서는 Integer 클래스의 `valueOf()` 메소드를 사용한다.



```java
public class IntegerCacheDemo{
    public static void main(String[] args){
        Integer a1 = 100;
        Integer a2 = Integer.valueOf(100);
        Integer a3 = new Integer(100);
        
        System.out.println(a1 == a2);	//true
        System.out.println(a1 == a3);	//false
    }
}
```

- `a1`과 `a2`를 비교하면 true가 나온다. 원시 값을 직접 할당하거나 `valueOf` 메소드를 사용했을 경우 `cache`를 사용하기 때문이다.
- `a1`과 `a3`를 비교하면  false가 나온다. `a3`는 `new` 연산자를 사용했기 때문에 `cache`가 아닌 새로운 객체를 생성한다.



## 그렇다면....

- 왜 캐싱을 사용할까? `new` 생성자를 통해 객체를 생성하는 것은 비용이 많이 들기 때문에 자주 쓰이는 객체들을 미리 생성해두고 필요할 때마다 이미 생성된 객체를 사용하면 비용을 줄일 수 있다.

- `IntegerCache`는 내부적으로 256개의 `Integer` 클래스를 미리 생성하고 저장해둔다. 고작 `Integer` 클래스 256개를 미리 생성하는 것이 정말 비용을 줄이는데 큰 역할을 할 것인지는 의문이다.



---

### 2. String

- `String`은 다른 객체들과 마찬가지로 `new` 연산자를 통해서 객체를 생성할 수 있을뿐만 아니라 `리터럴` 값으로도 객체를 생성할 수 있다.
- `리터럴`로 객체를 생성할 경우, `String Pool`에 저장되는데 같은 `리터럴`이 존재하는 경우 새로운 객체를 생성하는 것이 아니라 `String Pool`에 있는 객체를 반환한다.

```java
String s1 = new String("java");
String s2 = new String("java");
System.out.println(s1 == s2);
System.out.println(s1.equals(s2));
```

```
[출력 결과]
false
true
```

- `new` 연산자를 통해 생성한 객체는 서로 다른 레퍼런스 값을 갖는 객체이므로 `==` 비교는 false가 나오지만 값을 비교하도록 오버라이딩된 `equals`로 비교하면 `true`가 나온다.

```java
String s3 = "java";
String s4 = "java";
System.out.println(s3 == s4);
System.out.println(s3.equals(s4));
```

- `리터럴`로 생성한 객체는 `String pool`에 있는 같은 레퍼런스를 갖는 객체를 가리키므로 모두 `true`가 나온다.

#### intern() 메소드

- `intern` 메소드는 `String Pool`에 `equals()` 메소드로 비교하여 같은 문자열이 존재하면 해당 객체를 반환하고 그렇지 않으면 `String Pool`에 저장한다.

```java
String s1 = "java"
```

- 위와 같이 `리터럴`로 생성한 객체는 내부적으로 `intering`을 거치게 되는데 최초에는 `String Pool`에 `java`문자열이 존재하지 않아 해당 문자열을 `String Pool`에 저장해놓는다.
- 그 이후에 같은 `리터럴`로 객체를 생성할 경우 `String Pool`에 있는 값을 반환한다.



---