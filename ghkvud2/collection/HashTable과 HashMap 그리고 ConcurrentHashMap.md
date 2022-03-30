## HashTable vs HashMap

### 1. 차이점

1. Thread-Safe
   - HashTable은 thread-safe 하기때문에 멀티스레드 환경에서 사용할 수 있지만 HashMap은 그렇지 않다.
2. null
   - HashTable은 key와 value에 null 허용하지 않지만, HashMap은 null을 허용한다.
3. Iterator
   - HashTable은 `fast-fail



---

#### [참고]

[HashTable vs HashMap](https://javarevisited.blogspot.com/2010/10/difference-between-hashmap-and.html#axzz7P1jjk33X)

[iterator vs enumeration](https://javarevisited.blogspot.com/2010/10/what-is-difference-between-enumeration.html)

