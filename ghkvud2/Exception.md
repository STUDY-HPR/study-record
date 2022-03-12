### 1. Exception이란?

- 자바에서 예상을 했건, 예상을 하지 않았건, 예외적인 일이 발생했을 때 발생시키는 것을 `Exception`이라고 한다.
- 시스템의 메모리가 부족한 경우와 같이 프로그램에서 처리할 수 없는 경우를 `Error`라고 한다.

### 2. Exception Handling

- `예외`를 처리하는 방법은 크게 두 가지로 나뉜다.

  #### 2.1 try-catch block

  - 예외가 발생할 수 있는 부분을 `try 블록`으로 감싸서 직접 예외를 처리하는 방식이다.
  - `try 블록`에서 예외가 발생할 경우, 그 이하의 문장은 실행되지 않고 `catch 블록`으로 넘어간다.
  - `try 블록`에서 `throw` 키워드를 사용해서 직접 예외를 발생시킬 경우, `catch 블록`에 해당 예외(혹은 부모) 클래스를 catch하지 않으면 컴파일 에러가 발생한다.

  #### 2.2 throws

  - 예외가 발생했을 때 직접 처리하지 않고 예외가 발생한 메소드를 호출한 `caller`에게 예외처리를 떠넘길 수 있다.

  #### 2.3 throw와 throws의 차이

  - `throws`는 위에서 설명한 것처럼, 예외가 발생한 메소드를 호출한 `caller`에게 예외처리 책임을 전가할 때 사용하는 키워드이다. 또한 메소드 선언부에서만 사용된다.
  - 반면에 `throw`는 예외가 필요한 상황에 `exception`을 생성할 때 사용하는 키워드이다. 이러한 상황에서도 `throws`를 통해 예외 처리 책임을 전가할 수 도 있고, `try-catch`를 통해 직접 예외를 처리할 수 있다.

### 3. Exception의 종류

- 자바에는 세 종류의 예외가 존재한다.
  1. error
     - `error`는 자바 프로그램 밖에서 발생한 예외를 말한다.
     - `error`가 발생할 경우, 프로세스에 영향을 주고 `exception`은 해당 스레드에만 영향을 준다.
  2. runtime (unchecked) exception
     - `runtime exception`은 예외가 발생할 것을 미리 감지하지 못했을 때 발생한다.
     - 컴파일 시점에 `runtime exception`을 핸들링했는지 확인하지 않기 때문에 `unchecked exception`이라고도 한다.
  3. checked exception
     - `error`와 `unchecked exception`을 제외한 모든 예외는 `checked exception`이다.
     - `try-catch`로 해당 예외를 직접 처리하거나 `throws`를 통해 예외를 던져야한다. 그렇지 않으면 컴파일 에러가 발생한다.

