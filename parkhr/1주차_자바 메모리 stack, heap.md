### STACK

- Heap 영역에 생성된 Object 타입의 데이터의 참조값이 할당된다.
- primitive type의 데이터가 값과 함께 할당된다.
- 지역변수들은 scope 에 따른 visibility 를 가진다.
- 각 Thread 는 자신만의 stack 을 가진다.

<br/>

### HEAP

- object 타입의 데이터가 할당된다.
- JVM이 시작될 때 생성되며 애플리케이션이 실행되는 동안 크기가 증가하거나 감소
- 더 이상 사용되지 않는 가비지 수집 객체가 지워져 새로운 객체를 위한 공간이 생긴다.
- 몇개의 스레드가 존재하든 상관없이 단 하나의 heap 영역만 존재한다.

<br/><br/>
아래 코드를 실행하면서 메모리가 어떻게 적재되는지 보겠습니다.
```
public class Test {
    public static void main(String[] args) {
      List<Integer> list = new ArrayList<>();
      list.add(1);
      list.add(2);
      addTest(list);

      Integer a = list.get(3);
      a = 1;
    }

    public static addTest(List<Integer> parameter) {
      parameter.add(3);
      parameter.add(4);
    }
}
```
<br/><br/>
다음은 addTest()함수를 호출하기 전까지 실행한 결과이다.
<br/>
![Untitled](https://user-images.githubusercontent.com/46472772/158058561-218c9f68-60b3-4600-aece-698f1195a988.png)

<br/>
<br/>
<br/>

메서드 addTest()를 실행한 결과이다.<br/>
스택에 할당되는 지역변수들은 scope 에 따른 visibility 를 가지기 때문에, list를 사용할 수 없게 됩니다.
<br/>
![Untitled](https://user-images.githubusercontent.com/46472772/158058637-70c1d4fa-9ea2-43c7-922b-5950ff9129a5.png)

<br/>
<br/>

다시 main 함수로 돌아와서 Integer a 를 생성 및 초기화하고 있다.<br/>
addTest() 함수에서 빠져나왔으므로 parameter 변수가 스택에서 제거된 것을 볼 수 있습니다.
<br/>
![Untitled (1)](https://user-images.githubusercontent.com/46472772/158058703-806e2d07-1c27-4cc5-ada9-914a825ff016.png)

<br/>
<br/>
a의 값을 변경한 결과이다.<br/>

왜 new Integer(1) 를 통해 새로운 값을 생성하지 않고, 기존에 있는 Integer 객체를 참조하게 되는 걸까?
<br/>
![Untitled (2)](https://user-images.githubusercontent.com/46472772/158058757-45758d1a-f5ec-4634-a5c3-c7d2b74fd770.png)



