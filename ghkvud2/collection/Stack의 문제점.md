## Stack의 문제점과 ArrayDeque

### 1. Stack의 단점

1. Vector를 상속받고 있다.

   - Stack은 `Last In First Out` 자료구조인데, Vector를 상속받았기 때문에 인덱스 기반으로 요소에 접근하거나 추가할 수 있다.

     ```java
     Stack<Integer> stack = new Stack<>();
     stack.push(1);
     stack.push(2);
     stack.add(1,3); // 인덱스에 요소 추가
     
     stack.get(0); // 인덱스 기반으로 접근
     ```

     - 위처럼 Vector 클래스를 상속했기 때문에 인덱스 기반의 연산이 가능해지므로 `LIFO` 자료구조의 특성을 깨뜨릴 수 있다.

     

   - Iterator를 사용할 때, `LIFO` 구조로 출력되지 않는다.

     ```java
     Stack<Integer> stack = new Stack<>();
     stack.push(1);
     stack.push(2);
     
     Iterator<Integer> iter = stack.iterator();
     while (iter.hasNext()) {
     	System.out.println(iter.next());
     }
     ```

     ```
     [출력 결과]
     1 // 먼저 입력한 요소가 먼저 나옴
     2
     ```

     - Stack을 사용하는 이유는 `LIFO` 구조를 필요로 할 때인데, Stack의 Iterator를 사용하면 `FIFO` 구조처럼 출력된다.

   

2. 초기 용량을 설정할 수 없다.

   - Stack은 초기 용량을 설정할 수 있는 생성자가 없기 때문에 Vector 클래스의 초기 용량인 10으로 고정된다.
   - 즉, Stack에 많은 요소를 삽입할 경우 새로운 배열을 생성하고 기존 원소들을 복사하는 작업이 추가로 발생한다.

   

3. 모든 메소드에 동기화를 적용하여 성능이 떨어진다.

---

### 2. Stack대신 ArrayDeque을 사용하라

1. Stack과 달리 `LIFO` 구조를 유지한다.

   ```JAVA
   ArrayDeque<Integer> stack = new ArrayDeque<>();
   stack.push(1);
   stack.push(2);
   stack.push(3);
   
   Iterator<Integer> iter = stack.iterator();
   while (iter.hasNext()) {
   	System.out.println(iter.next());
   }
   ```

   ```
   [출력 결과]
   3
   2
   1
   ```

   

2.  초기값을 설정할 수 있다.

3. 모든 메소드에 동기화를 적용하지 않는다.

   



---

#### [출처]

[deque vs stack](https://www.baeldung.com/java-deque-vs-stack)