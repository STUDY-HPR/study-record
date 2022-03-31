// private 생성자나 열거 타입으로 싱글턴임을 보증하라
// 싱글턴이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말한다.
public class Item3 {
    /**
     * public static final 필드 방식의 싱글턴
     * 리플렉션 API AccessibleObject.setAccessible 을 사용해 private 생성자를 호출할 수 있다.
     * 이러한 공격을 방어하려면 생성자를 수정하여 두 번째 객체가 생성되려 할 때 예외를 던지게 하면 된다.
     */
    public static final Item3 INSTANCE = new Item3();
    private Item3() {
        // 리플렉션으로 인한 호출 방어
        try {
            if(INSTANCE != null) throw new Exception();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

class Item3_1 {
    /**
     * 정적 팩터리 방식의 싱글턴
     */
    private static final Item3_1 INSTANCE = new Item3_1();
    private Item3_1() {
        // 리플렉션으로 인한 호출 방어
        try {
            if(INSTANCE != null) throw new Exception();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static Item3_1 getInstance() {return INSTANCE;}
}

enum Item3_2 {
    /**
     * 열거 타입 방식의 싱글턴
     * 가장 좋은 방법
     * 직렬화 상황이나 리플렉션 공격에서도 제2의 인스턴스가 생기는 일을 막아준다.
     * 단, 만들려는 싱글턴이 Enum 외의 클래스를 상속해야 한다면 사용할 수 없다.
     */
    INSTANCE;

    public void leaveTheBuilding() {}
}
