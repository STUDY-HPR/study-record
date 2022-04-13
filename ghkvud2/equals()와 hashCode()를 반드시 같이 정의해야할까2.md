## equals()와 hashCode()와 HashMap의 관계

### 1. equals 메소드

- 객체의 주소 값이 달라도 객체의 값이 같으면 같은 객체로 판단하는 것을 `동등성`이라고 한다.

- 즉, Person 타입의 인스턴스 p1, p2가 있을 때 객체가 가지고 있는 값이 같은 경우 `동등한 인스턴스`라고 한다.

  ```java
  class Person {
      private String name;
      private int age;
      
      public Person(String name, int age){ /*생략*/ }
      
      @Override
  	public boolean equals(Object o) {
          //IDE 도움을 받아서 override한 equals 메소드
          //equals 메소드에서 사용되는 필드는 hashCode()에서도 사용되어야 한다.
      }
  }
  ```

  ```java
  Person p1 = new Person("Person", 20);
  Person p2 = new Person("Person", 20);
  System.out.println(p1 == p2); 		// false
  System.out.println(p1.equals(p2)); 	// true
  ```

  - p1과 p2가 new 연산자를 통해 서로 다른 인스턴스를 생성했지만 재정의한 `equals()` 메소드를 통해 동등성을 비교하면 `true`를 출력한다.
  - 같은 타입의 객체들간의 동등성만을 비교한다고 장담할 수 있다면 `equals()` 메소드만 재정의하더라도 문제되지 않는다.

  ---

  ### 2. HashMap에서 Person 인스턴스 사용하기

  - `hashCode()` 메소드를 재정의하지 않은 `Person` 인스턴스를 HashMap의 key로 사용해보자.

    ```java
    HashMap<Person, String> map = new HashMap<>();
    
    Person p1 = new Person("David", 20); // Person 인스턴스 p1 생성
    Person p2 = new Person("David", 20); // p1과 동등한 p2 생성
    
    map.put(p1, "David"); 				 // p1을 HashMap의 key로 사용하여 저장
    
    String name = map.get(p2);			 // p2를 이용하여 get
    System.out.println(name);
    ```

    ```
    [출력 결과]
    null
    ```

    - p1과 p2가 동등 관계에 있지만 출력 결과는 null이 나왔다. 그 이유는 HashMap의 내부 동작 원리때문이다.

  ---

  ### 3. HashMap의 get(k)과 put(k, v) 메소드

  #### 1. put(k, v) 메소드를 호출하여 Entry를 저장할 때

  1. put 메소드로 전달된 key 객체의 `hashCode()` 메소드를 호출하여 hash 값을 얻는다.

  2. 만약, `hashCode()` 메소드가 재정의되어있지 않다면 Object 클래스의 hashCode를 호출할 것이고 이는 단순히 메모리상의 주소 값을 리턴한다.

  3. 반환된 해시 값에 해당하는 `버킷`에 `Entry(k, v)` 를 저장한다.

  

  #### 2. get(k) 메소드를 호출하여 값을 찾아올 때

  1. put 메소드와 마찬가지로 get 메소드로 전달된 key 객체의 `hashCode()` 메소드를 호출하여 해시 값을 얻는다.
  2. 반환된 해시 값에 해당하는 `버킷`에 찾아가 `Entry` 가 있는지 확인한다.

  #### 

  - Person 클래스는`hashCode()` 메소드를 재정의하지 않았기 때문에 서로 다른 인스턴스 p1과 p2의 hashCode 메소드는 서로 다른 값을 해시 값을 반환한다.
  - 즉, get() 메소드에 p2 인스턴스를 인자로 넘길 경우 HashMap은 p1이 저장된 버킷이 아닌 다른 버킷을 찾아가기때문에 null을 반환한 것이다.
  - HashMap은 key로 사용된 객체의 hashCode의 값을 먼저 비교하여 그 값이 같으면 equals() 메소드로 동등성을 비교하기 때문에 p1과 p2의 hashCode 값으로부터 계산된 버킷의 인덱스가 같다고 하더라도 null을 반환할 것이다.

  

  > HashMap이 버킷에 저장하는 요소는 value가 아닌 Entry(k, v) 객체이다. 하나의 버킷에 여러 Entry 객체들이 저장되어 있을 때 value 값에 대해서 equals() 메소드를 수행해서 동등성을 비교하는 것이 아니다.

---

### 4. hashCode() 메소드를 반드시 함께 정의해라

- 자바에서 HashMap과 HashSet은 아주 빈번하게 사용되는 자료구조이다.

- 지금 당장에는 인스턴스의 동등성만 비교할 것 같지만 요구사항의 변화에 따라 의도하지 않은 부작용을 야기할 수 있다.

  ```java
  @Override
  public int hashCode() {
  	final int prime = 31;
  	int result = 1;
  	result = prime * result + age;
  	result = prime * result + ((name == null) ? 0 : name.hashCode());
  	return result;
  }
  ```

  - hashCode 메소드를 재정의할 때에는 equals() 메소드를 재정의할 때 사용된 필드들을 반드시 모두 사용해야 한다.