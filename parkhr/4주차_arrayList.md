## ArrayList

- List인터페이스를 구현하고 크기 조정이 가능한 동적 배열이다.

- null을 포함한 모든 요소를 허락한다.

- 동기화를 제외하고 Vector와 거의 동일하다. (Vector는 thread safe)

- 배열처럼 접근 가능하다.

- add 할 시, 1개 넣으면 O(1)  n개 넣으면 O(n)이 소요된다.

- remove 시, 후속 요소들이 있다면 왼쪽으로 한칸씩 이동한다.

- arrayList는 capacity를 갖고 있음.

- capacity는 요소를 저장하기 위해 사용되는 list size임

- 요소가 추가되면 capacity는 자동으로 증가함
  1. 힙메모리에 더 큰 크기의 메모리 생성
  2. 현재 메모리 요소를 새 메모리에 복사
  3. 오래된 메모리 제거

- 동기화 되지않음 (thread safe 하지 않음)

- 만약 Thread들이 동시에 접근하여 변경하려면 외부에서 동기화 작업을 해줘야함.

- 다른방법 - `List list = Collections.synchronizedList(new ArrayList(...));` 요런식으로하면됨
