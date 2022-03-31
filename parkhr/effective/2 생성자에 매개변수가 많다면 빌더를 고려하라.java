// item2 생성자에 매개변수가 많다면 빌더를 고려하라
public class Item2 {
    public static void main(String[] args) {
        // 점층적 생성자
        Student student = new Student(10000, "name");

        // 자바빈즈
        Student2 student2 = new Student2();
        student2.setId(10000);
        student2.setName("name");

        // 빌더
        Student3 student3 = new Student3.Builder().id(0).name("name").build();
    }
}

class Student {
    private int id;
    private String name;

    /**
     * 점층적 생성자 패턴을 사용할 수 있지만, 매개변수 개수가 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다.
     * 각 값의 의미가 무엇인지 헷갈릴 것이고, 매개변수가 몇 개인지도 주의해서 세어보아야 한다.
     */
    public Student (int id){
         this(id, null);
    }

    public Student (String name){
        this(0, name);
    }

    public Student (int id, String name){
        this.id = id;
        this.name = name;
    }
}

class Student2 {
    private int id;
    private String name;

    /**
     * 자바빈즈 패턴에서는 객체 하나를 만들려면 메서드 여러 개 호출해야하고,
     * 객체가 완전히 생성되기 전까지는 일관성이 무너진 상태에 놓인다.
     * 클래스를 불변으로 만들 수 없다.
     */
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class Student3 {
    private int id;
    private String name;

    /**
     * 빌더 패턴은 명명된 선택적 매개변수를 흉내 낸 것이다.
     *
     */
    public static class Builder {
        private int id = 0;
        private String name = "";

        public Builder id(int id){
            //유효성 검사
            if(id < 1) throw new IllegalArgumentException();
            
            this.id = id;
            return this;
        }

        public Builder name(String name){
            // 유효성 검사
            if(name.length() == 0) throw new IllegalArgumentException();

            this.name = name;
            return this;
        }

        public Student3 build(){
            // 유효성 검사
            return new Student3(this);
        }
    }

    private Student3(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }
}

// 생성자나 정적 패터리가 처리해야 할 매개변수가 많다면 빌더 패턴을 선택하는 게 더 낫다.
