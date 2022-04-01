## HashTable과 HashMap 그리고 ConcurrentHashMap

### 1. HashTable과 HashMap

1. Thread-Safe
   - HashTable은 thread-safe 하기때문에 멀티스레드 환경에서 사용할 수 있지만 HashMap은 그렇지 않다.
   - `Collections.synchornizedMap(HashMap)`을 사용해서 HashMap에 래핑 작업(?)을 거쳐 thread-safe하게 만들 수 있다.
2. null
   - HashTable은 key와 value에 null 허용하지 않지만, HashMap은 null을 허용한다.
3. Iterator
   - 저장된 요소를 순회할 때 HashTable은 `Enumeration`을 HashMap은 `Fail-Fast iteration`을 사용한다.

---

### 2. Enumeration과 Iterator

- `Enumeration과 Iterator` 모두 컬렉션 요소를 순회할 수 있는 기능을 제공한다는 공통점이 있다.

- Iterator는 `remove()` 메소드를 제공하지만 Enumeration은 그렇지 않다. Enumeration은 요소들을 탐색할 수만 있기 때문에 `읽기 전용`인 경우에만 사용할 수 있다.
- Iterator는 순회 도중에 컬렉션에 대해서 수정 작업이 발생할 경우 `ConcurrentModificationException`이 발생하지만 Enumeration은 그렇지 않다.



---

### 3. Fail-Fast와 Fail-Safe Iterator의 차이점

1. Concurrent Modification
   
- `Concurrent Modification` 은 다른 작업이 수행하고 있는 객체를 동시에 수정하는 것이다.
   
2. Fail-Fast Iterator

   - `Fail-Fast`는 가능한 빨리 예외가 노출시키고 동작을 중단시킨다.

   - 컬렉션은 요소가 추가되거나 삭제연산이 수행될 때 마다 증가하는 `modCount`라는 변수를 가지고 있다.

   - Fail-Fast Iterator가 생성될 때 해당 컬렉션에 대한 `modCount` 를 Iterator의 내부 변수에 복사한다.

   - Iterator의 next() 메소드를 호출할 때 복사된 modCount와 원본 modCount가 다르면 `ConcurrentModificationException`을 발생시킨다.

     ```java
     Map<String, String> map = new HashMap<>();
     Map.put("foo1", "bar1");
     Map.put("foo2", "bar2");
     
     Iterator iter = map.keySet().Iterator();
     while(iter.hasNext()){
         System.out.println(iter.next());
         map.put("foo3", "bar3"); // 요소를 추가하면 다음 next() 메소드 호출시 예외를 발생시킴
     }
     ```
     - Fast-Fail은 컬렉션을 순회하는 동안에 수정이 발생하면 예외를 발생시킨다.
     - Fast-Fail은 원본 컬렉션에 대해서 순회 연산을 수행한다.
     - 원본 컬렉션에 대해서 순회 연산을 수행하기 때문에 추가적인 컬렉션 복사가 없으므로 메모리를 절약할 수 있다.
     - 대표적으로 `ArrayList, HashMap, Vector` 등이 Fail-Fast Iterator를 사용한다.

   

3. Fail-Safe Iterator (non-Fail fast)

   - `Fail-Safe`는 예외 발생시 작업을 중단하지 않고 가능한한 예외를 회피하는데 초점을 둔다.

   - Fail-Safe Iterator는 원본 컬렉션의 복사본에 대해서 순회 연산을 하기때문에 순회 도중에 컬렉션이 수정되더라도 어떠한 예외도 발생시키지 않는다.

     ```java
     CopyOnWriteArrayList<Integer> list 
     		= new CopyOnWriteArrayList<Integer>(new Integer[] {1, 7, 9, 11});
     
     Iterator<Integer> itr = list.Iterator();
     while (itr.hasNext()) {
     	Integer i = itr.next();
     	System.out.println(i);
         
     	if (i == 7) {
     		list.add(15); // 추가한 요소는 print되지 않고 예외도 발생하지 않는다. 
     	}
     }
     ```

     - Fast-Safe는 원본 컬렉션에 대한 복사본에 대해서 순회 연산을 수행한다.
     - Fast-Safe는 순회하는 동안 원본 컬렉션에 수정이 발생해도 예외를 발생시키지 않는다.
     - 원본 컬렉션을 복사해야하기 때문에 메모리와 시간이 상대적으로 더 든다.
     - 대표적으로 `ConcurrentHashMap과 CopyOnWriteArrayList` 등이 Fast-Safe를 사용한다.

     

4. ConcurrentHashMap은 Fail-Safe이지만, 원본 컬렉션을 사용한다.

   - Fail-Safe Iterator가 반드시 복사된 컬렉션에 대해서 순회를 해야만 하는 것은 아니다.

   - `ConcurrentHashMap`의 경우, Fail-Fast는 아니지만 복사된 컬렉션을 사용하지는 않는다.

   ```java
   ConcurrentHashMap<String, Integer> m = new ConcurrentHashMap<String, Integer>();
   m.put("ONE", 1);
   m.put("SEVEN", 7);
   m.put("FIVE", 5);
   m.put("EIGHT", 8);
   		
   Iterator<String> it = m.keySet().Iterator();
   while (it.hasNext()) {
   	String key = it.next();
   	System.out.println(key + " : " + m.get(key));
   	m.put("NINE", 9);
   }
   ```

   ```java
   [출력 결과]
   EIGHT : 8
   FIVE : 5
   NINE : 9 // 순회할 때 추가한 원소가 출력됐다.
   ONE : 1
   SEVEN : 7
   ```

   - 순회하는 동안에 `m.put("NINE", 9)` 을 통해 요소를 추가했는데, 그 요소가 그대로 출력된 것을 볼 수 있다.
   - 즉, `ConcurrentHashMap`은 원본 컬렉션에 대해서 순회 연산을 한다고 볼 수 있다.

### 4. ConcurrentHashMap

- 멀티스레드 환경에서 동기화 문제를 해결하기 위해 사용되는 `ConcurrentHashMap`은 `HashTable`의 단점을 보완하는 자료구조이다.
- ConcurrentHashMap은 Map을 여러 개의 세그먼트로 나누는데 스레드는 자신이 연산을 수행하는 세그먼트영역에 대해서만 lock을 건다. 
- 세그먼트의 수는 concurreny level의 값으로 결정되는데 default 값은 16이다. 즉, 16개의 서로 다른 스레드가 동시에 다른 세그먼트에 연산을 수행할 수 있다는 뜻이다.

---

#### [참고]

[HashTable vs HashMap](https://javarevisited.blogspot.com/2010/10/difference-between-hashmap-and.html#axzz7P1jjk33X)

[Iterator vs enumeration](https://javarevisited.blogspot.com/2010/10/what-is-difference-between-enumeration.html)

[HashTable vs ConcurrentHashMap1](https://roynus.tistory.com/672)

[HashTable vs ConcurrentHashMap2](http://javarevisited.blogspot.sg/2011/04/difference-between-concurrenthashmap.html)

[Fail-Safe Iterator vs Fail-Fast Iterator](https://www.javatpoint.com/fail-fast-and-fail-safe-Iterator-in-java)

[How to use ConcurrentHashMap in Java](https://javarevisited.blogspot.com/2013/02/concurrenthashmap-in-java-example-tutorial-working.html#axzz7P1jjk33X)

[Top 5 Concurrent Collections Java Programmer Should Learn](https://javarevisited.blogspot.com/2013/02/concurrent-collections-from-jdk-56-java-example-tutorial.html#axzz7P1jjk33X)