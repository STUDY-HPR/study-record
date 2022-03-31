import java.util.Objects;

// 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라
// 사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다.
public class Item5 {
    // 동작에 따라 클래스를 변경할 수 없다..
    // private final Item5_1 item5_1 = new Item5_2();

    private final Item5_1 item;

    // 인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨준다.
    // 유연성과 테스트 용이성을 높여준다.
    public Item5(Item5_1 item) {
        this.item = Objects.requireNonNull(item);
    }

    public void run() {};

}

interface Item5_1 {

}

class Item5_2 implements Item5_1{

}

class Item5_3 implements Item5_1{

}

// 클래스가 내부적으로 하나 이상의 자원에 의존하고, 그 자원이 클래스 동작에 영향을 준다면 싱글턴과 정적 유틸리티 클래스는 사용하지 않는 것이 좋다.
