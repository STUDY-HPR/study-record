// 인스턴화를 막으려거든 private 생성자를 사용하라
public class Item4 {
    private Item4() {
        // 상속을 불가능하게 하는 효과도 있다.
        // 모든 생성자는 명시적이든 묵시적이든 상위 클래스의 생성자를 호출하게 되는데, private 때문에 막힌다!
    }
}
