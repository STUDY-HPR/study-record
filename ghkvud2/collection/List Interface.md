## List Interface
- 리스트는 요소의 중복을 허용하며 순서를 유지하는 자료구조이다.

---

### 1. ArraList

- ArrayList는 0부터 시작하는 인덱스 값으로 구별되는 자료구조이다.

#### ArrayList에서 인덱스로 접근할 수 있는 이유는?

- ArrayList의 각각의 요소들은 다른 요소의 오른쪽에 **연속적으로** 저장된다.
- ArrayList의 첫 번째 주소 값과 인덱스 값을 통해 특정 위치에 있는 요소의 메모리 주소를 바로 계산하여 접근할 수 있다.

##### 1) ArrayList의 끝에 있는 요소를 조작할 때

- ArrayList의 끝에 요소를 조작할 때에는 `O(1)`만큼의 시간이 걸린다.

##### 2) ArrayList의 처음이나 중간에 있는 요소를 조작할 때

- ArrayList의 처음이나 중간에 있는 요소를 조작할 때에는 기존 요소들의 위치를 조정하는 연산이 추가로 수행되어야 하기 때문에 `O(n)` 만큼의 시간이 소요된다.



#### ArrayList에 요소가 가득찬 경우에는 어떻게 될까?

- 기존에 있던 ArrayList의 크기보다 더 큰(일반적으로 2배) 새로운 ArrayList을 생성하고 기존 ArrayList의 요소 값들을 하나씩 새로운 ArrayList로 복사한다.
- 기존 ArrayList에 있는 모든 요소에 이러한 연산을 진행하므로 `O(n)`의 시간만큼 소요된다.

- 즉, 작은 크기의 ArrayList을 사용할 경우 메모리를 절약할 수 있지만 `resize` 하는 과정에서 많은 시간이 걸리게되는 `trade-off`가 발생한다.



#### ArrayList을 사용할 때 고려해야할 사항

1. 마지막 요소에 대한 연산이 빈번할 때 사용하거나 인덱스 기반으로 요소를 접근할 때 사용하는 것이 좋다.
   - 바꿔말하면, 중간 요소에 있는 연산이 빈번할 경우에는 좋지 못한 성능을 갖는다.

2. ArrayList의 가득찼을 경우 `resize` 연산이 동반되기 때문에 성능 저하를 가져올 수 밖에 없다.

3. 같은 타입의 요소들만 저장할 수 있다.

---

### 2. LinkedList

- LinkedList의 가장 큰 장점은 리스트의 중간에 있는 요소에 연산에 대한 시간은 `O(1)`만큼 소요된다는 것이다.
- 각각의 요소는 다음 요소에 대한 참조 값을 가지고 있기 때문에 요소를 추가하거나 삭제할 때 모든 요소의 위치를 조정하는 것이 아닌 해당 위치의 양 옆의 `노드`에 대한 참조 값만을 갱신하면 된다.
- 하지만 특정 요소를 찾을 땐, ArrayList와 다르게 `Random Access`가 불가능하므로 LinkedList의 모든 요소를 탐색해야 하기 때문에 `O(n)`만큼의 시간이 소요된다.

##### 1.  LinkedList의 맨 앞이나 맨 끝에 있는 요소를 조작할 때

- LinkedList는 맨 앞과 끝에 있는 요소를 가르키는 `포인터`를 갖고 있으므로 해당 위치에서 연산을 수행할 땐 `O(1)`만큼의 시간이 걸린다.

##### 2. LinkedList의 중간에 있는 요소를 조작할 때

- LinkedList의 중간에 있는 요소를 찾아가기 위해 맨 앞이나 끝에서부터 탐색을 진행해야하므로 `O(n)`만큼의 시간이 걸린다.

#### LinkedList의 단점

- 다른 요소의 주소 값을 저장해야하는 참조 변수를 가져야 하기 때문에 약간의 오버헤드가 발생한다.
- `Random Access`가 불가능하기 때문에 요소를 탐색하는데에는 시간이 오래걸린다.

---

### 3. ArrayList와 LinkedList의 성능 비교

- 첫 번째 위치에 요소를 삽입할 때 ArrayList와 LinkedList의 성능 차이를 비교한다.

```java
public static void main(String[] args) {
	long start = System.currentTimeMillis();
    
	ArrayList<Integer> arrayList = new ArrayList<>();
	for (int i = 1; i <= 500000; i++) {
		arrayList.add(0, i);
	}
	System.out.println("ArrayList : " + (System.currentTimeMillis() - start));
    
    
	start = System.currentTimeMillis();
	LinkedList<Integer> linkedList = new LinkedList<>();
	for (int i = 1; i <= 500000; i++) {
		linkedList.addFirst(i);
	}
	System.out.println("LinkedList : " + (System.currentTimeMillis() - start));
}
```

```
[출력 결과]
ArrayList : 12226
LinkedList : 16
```

- 저장할 때마다 다른 요소들의 위치를 조정해야하는 `ArrayList`와 그렇지 않은 `LinkedList`간의 성능 차이가 많이 나는 것을 확인할 수 있다.
