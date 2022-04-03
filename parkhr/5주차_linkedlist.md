## LinkedList

- LinkedList 데이터 구조는 요소가 인접한 위치에 저장되지 않고 모든 요소가 데이터 부분과 주소 부분이 있는 개별 개체이다.

- 요소는 포인터와 주소를 사용하여 연결됩니다. 각 요소는 노드라고 불립니다. 

- 어레이보다 삽입 및 삭제가 용이하다. 

- 노드에 직접 액세스할 수 없다. 접근하고자 하는 노드에 도달하기 위해서는 처음부터 시작하여 링크를 따라 이동해야 한다.

``` java
public E get(int index) {
    checkElementIndex(index);
    return node(index).item;
}

Node<E> node(int index) {
    // assert isElementIndex(index);

    if (index < (size >> 1)) {
        Node<E> x = first;
        for (int i = 0; i < index; i++)
            x = x.next;
        return x;
    } else {
        Node<E> x = last;
        for (int i = size - 1; i > index; i--)
            x = x.prev;
        return x;
    }
}
```
