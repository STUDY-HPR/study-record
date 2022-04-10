## HashSet

- Hashset의 내부를 보니 HashMap을 구성하고 있다.

``` java
...
private transient HashMap<E,Object> map;
...
public HashSet() {
    map = new HashMap<>();
}
...
```

- 순서에 대해서는 보장되지 않는다.
- null요소를 허용한다.
- Set Interface를 구현하기 때문에 중복된 값은 허용되지 않는다.
- initial capacity(초기 용량) : 해시 테이블이 생성되었을 때의 버킷 수를 의미한다. 현재 크기가 꽉 차면 버킷 수가 자동으로 증가한다.
- load factor : 로드 팩터는 용량이 자동으로 증가하기 전에 HashSet이 얼마나 가득 찰 수 있는지를 나타내는 척도이다.

![Untitled (9)](https://user-images.githubusercontent.com/46472772/161411738-2b0b6254-ca95-4dab-a005-b7f078b4464d.png)

- 퍼포먼스에 대한 영향: 
부하율과 초기 용량은 HashSet 조작의 퍼포먼스에 영향을 주는2가지 주요 요인입니다.
0.75의 부하율은 시간과 공간의 복잡성에 대해 매우 효과적인 성능을 제공합니다. 
로드 팩터 값을 그 이상으로 높이면 메모리 오버헤드는 감소하지만(내부 재구축 조작이 감소하기 때문에), 
해시 테이블의 추가 및 검색 조작에 영향을 줍니다. 재탕 작업을 줄이려면 초기 용량을 현명하게 선택해야 합니다. 
초기 용량이 최대 엔트리 수를 로드 팩터로 나눈 값보다 클 경우 재해시 조작은 발생하지 않습니다.

- thread safe 하지 않음
