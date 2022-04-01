## LinkedHashMap으로 LRU 구현하기

### 1. Ordering Mode

- `LinkedHashMap`은 두 개의 `ordering mode`를 지원한다.

1. Insertion-Order

   - Map에 저장된 순서대로 엔트리의 순서를 유지하는 방식이다. 기본 생성자를 통해 LHM을 생성했을 경우에 해당한다.

     ```java
     LinkedHashMap<String, String> map = new LinkedHashMap<>();
     map.put("2", "2");
     map.put("1", "1");
     map.put("3", "3");
     
     for (Entry<String, String> string : map.entrySet()) {
     	System.out.println(string);
     }
     ```

     ```java
     [출력 결과]
     2=2
     1=1
     3=3
     ```

     - Map에 저장한 순서대로 엔트리가 출력되는 것을 볼 수 있다.
     - 위의 코드에서 아래와 같은 코드를 추가하고 Map을 다시 출력하면 엔트리의 순서가 유지되는 것을 확인할 수 있다.

     ```java
     // 위 코드와 동일
     map.put("2", "2222"); // 기존 key에 대한 value를 변경
     map.get("1"); // 저장된 key에 접근
     
     for (Entry<String, String> string : map.entrySet()) {
     	System.out.println(string);
     } 
     ```

     ```
     [출력 결과]
     2=2
     1=1
     3=3
     ```

2. Access-Order

   - Map 엔트리에 접근한 순서대로 순서를 유지하는 방식이다.

   - 가장 처음에 접근한 엔트리가 가장 마지막에 나타나는 방식이다.

     ```java
     @Test
     public void givenLinkedHashMap_whenAccessOrderWorks_thenCorrect() {
         LinkedHashMap<Integer, String> map 
           = new LinkedHashMap<>(16, .75f, true);
         map.put(1, null);
         map.put(2, null);
         map.put(3, null);
         map.put(4, null);
         map.put(5, null);
     
         Set<Integer> keys = map.keySet();
         assertEquals("[1, 2, 3, 4, 5]", keys.toString());
      
         map.get(4);
         assertEquals("[1, 2, 3, 5, 4]", keys.toString());
      
         map.get(1);
         assertEquals("[2, 3, 5, 4, 1]", keys.toString());
      
         map.get(3);
         assertEquals("[2, 5, 4, 1, 3]", keys.toString());
     }
     ```

     - 가장 최근에 접근한 요소가 가장 마지막 순서로 변경되는 것을 확인할 수 있다.

   

   ### 2. removeEldestEntry

   - LHM은 내부적으로 가장 오랫동안 참조되지 않은 노드를 참조하고 있는데 access-order 모드인 경우 `removeEldestEntry` 메소드가 true를 반환하면 해당 노드를 제거한다.
   - removeEldestEntry 메소드를 재정의하여 LRU 캐시를 구현할 수 있다.

   ```java
   public class MyLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
   
       private static final int MAX_ENTRIES = 5;
   
       public MyLinkedHashMap(
         int initialCapacity, float loadFactor, boolean accessOrder) {
           super(initialCapacity, loadFactor, accessOrder);
       }
   
       @Override
       protected boolean removeEldestEntry(Map.Entry eldest) {
           //가장 오랫동안 참조되지 않은 노드를 삭제할 조건을 명시한다.
           return size() > MAX_ENTRIES; 
       }
   
   }
   ```

   

   ```JAVA
   @Test
   public void givenLinkedHashMap_whenRemovesEldestEntry_thenCorrect() {
       LinkedHashMap<Integer, String> map
         = new MyLinkedHashMap<>(16, .75f, true);
       map.put(1, null);
       map.put(2, null);
       map.put(3, null);
       map.put(4, null);
       map.put(5, null);
       Set<Integer> keys = map.keySet();
       assertEquals("[1, 2, 3, 4, 5]", keys.toString());
    
       map.put(6, null);
       assertEquals("[2, 3, 4, 5, 6]", keys.toString());
    
       map.put(7, null);
       assertEquals("[3, 4, 5, 6, 7]", keys.toString());
    
       map.put(8, null);
       assertEquals("[4, 5, 6, 7, 8]", keys.toString());
   }
   ```



### 3. HashMap과 LinkedHashMap의 차이점

1. LinkedHashMap은 엔트리를 저장할 때 양방향 연결 리스트를 사용하므로 HashMap보다 메모리를 좀 더 많이 사용한다.
2. 모든 엔트리를 탐색하기 위한 시간 복잡도는 HashMap과 LinkedHashMap 모두 O(n) 만큼의 시간이 소요된다.
   - 하지만, LinkedHashMap이 조금 더 빠르다. LinkedHashMap은 양방향 연결 리스트 (즉, 엔트리의 수) 크기만큼만 탐색하면 되지만 HashMap은 버킷을 순차적으로 탐색하면서 해당 버킷에 연결된 엔트리까지 접근해야 한다.
   - 초기용량이 클 수록 HashMap은 접근할 버킷의 수가 많아지므로 탐색시간도 그만큼 늘어난다. (하지만 아주 작은 차이일듯)

---

#### [출처]

[LinkedHashMap-Baeldung](https://www.baeldung.com/java-linked-hashmap)

