// 불필요한 객체 생성을 피하라

public class Item6 {
    private static final Item6_1 item = new Item6_1();
    public static void main(String[] args) {
        String s = new String("bikini"); // XXXX

        String s2 = "bikini"; // OOOO

        Boolean b = new Boolean("true"); // XXXX

        Boolean b2 = Boolean.valueOf("true"); // OOOO

        Item6_1 item6_1 = new Item6_1(); // 객체 생성 비용이 많이 든다면? => 생성하고 실행하고 가비지컬렉션에 의해 지워지고 ... 다시 ..
        item6_1.run();

        // 위에 static 으로 초기화 하므로써 지워지고 다시 생성해야 하는일을 막는다.
        item.run();

        // 박싱된 기본 타입보다는 기본 타입을 사용하고, 의도치 않은 오토박싱이 숨어들지 않도록 주의
        Long sum = 0L;
        for (long i = 0; i < Integer.MAX_VALUE; i++){
            // 더하는 과정에서 Long 인스턴스가 만들어진다..
            sum += i;
        }
    }
}

class Item6_1 {
    public Item6_1() {
        for (int i = 0; i < 100000; i++){
            System.out.println("생성중 ...");
        }
    }
    void run() {}
}
