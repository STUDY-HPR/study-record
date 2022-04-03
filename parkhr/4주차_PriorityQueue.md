## PriorityQueue

- 동적배열이다.

- 우선 순위큐는 priority 힙을 기반으로 합니다. 

- priority 큐의 요소는 사용되는 컨스트럭터에 따라 자연순서 또는 큐 생성 시 제공되는 Comparator에 의해 정렬됩니다.

``` java
// 정렬 과정..
private void siftUpUsingComparator(int k, E x) {
    while (k > 0) {
        int parent = (k - 1) >>> 1;
        Object e = queue[parent];
        if (comparator.compare(x, (E) e) >= 0)
            break;
        queue[k] = e;
        k = parent;
    }
    queue[k] = x;
}
```



- null을 허용하지 않음

- 우선 순위 큐는 thread safe 하지 않습니다. 멀티스레딩 환경을 위해 BlockingQueue 인터페이스를 구현하는 Priotiry BlockingQueue를 제공한다.


<br/>

### heap 이란?
- 완전 이진 트리의 일종으로 우선순위 큐를 위하여 만들어진 자료구조이다.
- 부모 노드의 키 값이 자식 노드의 키 값보다 항상 큰(작은) 이진 트리를 말한다.

### 힙(heap)의 종류
1. 최대 힙(max heap) : 부모 노드의 키 값이 자식 노드의 키 값보다 크거나 같은 완전 이진 트리
key(부모 노드) >= key(자식 노드)
2. 최소 힙(min heap) : 부모 노드의 키 값이 자식 노드의 키 값보다 작거나 같은 완전 이진 트리
key(부모 노드) <= key(자식 노드)

### 힙의 삽입
![Untitled (8)](https://user-images.githubusercontent.com/46472772/161410340-0ab8b611-bb79-4665-8c65-717271b34992.png)
