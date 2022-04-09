## Annotation

자바 어노테이션은 자바 소스 코드에 추가하여 사용할 수 있는 메타데이터의 일종이다. 보통 @ 기호를 앞에 붙여서 사용한다.

비즈니스 로직에는 영향을 주지 않지만 해당 타겟의 연결 방법이나 소스코드의 구조를 변경할 수 있다.

어노테이션은 컴파일시기 또는 리플렉션을 거쳐 런타임 시기에 처리 된다.


@Target : 어노테이션의 타겟을 지정한다. field, class, enum 등 타입에 붙일 수 있다.

@Retention : 어노테이션의 지속기간을 설정한다. (단순 주석, 컴파일 시기, 런타임 시기)

<br/>

### custom annotation 만들기

어노테이션을 통해 필드에 값 주입하기

``` java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationTest {

    String value();
}
```

``` java 
import java.lang.reflect.Field;

public class AnnotationTest2 {
    public static void test(Object entity) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if(field.getAnnotation(AnnotationTest.class) != null){
                field.setAccessible(true);
                Object value = field.get(entity);
                if(value != null) {
                    field.set(entity, Integer.valueOf(field.getAnnotation(AnnotationTest.class).value()));
                }
            }
        }
    }
}
```

``` java
class Dish {
    @AnnotationTest(value = "10")
    private int calories;
    private Type type;

    public Dish() {

    }

    public int getCalories() {
        return this.calories;
    }

    public Type getType() {
        return this.type;
    }

    enum Type {
        FISH, OTHER, MEAT
    }

    @Override
    public String toString() {
        return "Dish{" +
                "calories=" + calories +
                ", type=" + type +
                '}';
    }
}
```

``` java
public class Test {
    public static void main(String[] args) throws throws IllegalAccessException {
        Dish dish = new Dish();
        AnnotationTest2.test(dish);
        System.out.println(dish.toString());
    }
}
```
![image](https://user-images.githubusercontent.com/46472772/162562755-08ab7add-af6f-47ee-bb96-d8cbb04cda4b.png)

calories 에 10이 주입된걸 볼 수 있다.

