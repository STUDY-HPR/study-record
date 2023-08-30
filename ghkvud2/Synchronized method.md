## 1.Synchronized Method
메소드에 synchronized 키워드를 선언하면 오직 하나의 스레드만 그 메소드를 실행할 수 있다. 어떤 스레드에 의해서 실행되고 있는 synchronized 메소드를 다른 스레드가 실행하려고 한다면 BLOCKED 상태에 놓이게 된다.

```java
class BoyFriend extends Thread {

	private final GirlFriend girlFriend;
	
    public BoyFriend(GirlFriend girlFriend) {
		this.girlFriend = girlFriend;
	}

	@Override
	public void run() {
		girlFriend.meet();
	}
}

class GirlFriend {

	public void meet() {
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + " meeting started!");
		System.out.println(threadName + " meeting ended!");
	}
}
```
```java
public class SynchronizedDemo {

	private GirlFriend girlfriend;

	@BeforeEach
	void setUp() {
		girlfriend = new GirlFriend();
	}

    @Test
    void test() {
        for (int i = 1; i <= 10; i++) {
            BoyFriend boyFriend = new BoyFriend(girlfriend);
            boyFriend.setName("BoyFriend-" + i);
            boyFriend.start();
        }
    }
}
```

위 테스트 코드에서는 10개의 BoyFriend 스레드 객체를 만들고 GirlFriend의 meet() 메소드를 호출한다.

```
BoyFriend-1 meeting started!
BoyFriend-2 meeting started!
BoyFriend-2 meeting ended!
BoyFriend-3 meeting started!
BoyFriend-3 meeting ended!
BoyFriend-1 meeting ended!
BoyFriend-5 meeting started!
BoyFriend-5 meeting ended!
BoyFriend-4 meeting started!
BoyFriend-4 meeting ended!
BoyFriend-7 meeting started!
BoyFriend-7 meeting ended!
BoyFriend-8 meeting started!
BoyFriend-6 meeting started!
BoyFriend-6 meeting ended!
BoyFriend-8 meeting ended!
BoyFriend-9 meeting started!
BoyFriend-9 meeting ended!
BoyFriend-10 meeting started!
BoyFriend-10 meeting ended!

```
테스트 실행 결과를 보면 BoyFriend-1의 meeting ended!가 출력되기 전에 BoyFriend-2 meeting started!가 출력된 것을 확인할 수 있다. BoyFriend-1와 BoyFriend-2 스레드가 동시에 GirlFriend의 meet() 메소드를 호출한 것을 확인할 수 있다.

```java
public synchronized void meet() {
	String threadName = Thread.currentThread().getName();
	System.out.println(threadName + " meeting started!");
	System.out.println(threadName + " meeting ended!");
}
```
위 처럼 GirlFriend 클래스의 meet() 메소드에 synchronized 키워드를 붙인뒤 테스트를 실행하면 현재 스레드가 호출하는 meet() 메소드가 끝난 뒤, 그 다음 스레드의 meet() 메소드가 호출되는 것을 확인할 수 있다.

```
BoyFriend-1 meeting started!
BoyFriend-1 meeting ended!
BoyFriend-2 meeting started!
BoyFriend-2 meeting ended!
BoyFriend-3 meeting started!
BoyFriend-3 meeting ended!
BoyFriend-4 meeting started!
BoyFriend-4 meeting ended!
BoyFriend-5 meeting started!
BoyFriend-5 meeting ended!
BoyFriend-6 meeting started!
BoyFriend-6 meeting ended!
BoyFriend-7 meeting started!
BoyFriend-7 meeting ended!
BoyFriend-8 meeting started!
BoyFriend-8 meeting ended!
BoyFriend-9 meeting started!
BoyFriend-9 meeting ended!
BoyFriend-10 meeting started!
BoyFriend-10 meeting ended!
```

method에 synchronized 키워드를 붙이면 특정 시점에 오직 하나의 스레드만 해당 메소드를 호출할 수 있다. 스레드는 synchronized 메소드에 진입하게 되면 해당 오브젝트의 lock을 얻는다. 이 스레드가 해당 오브젝트에 대한 lock을 release한 후에야 비로소 다른 스레드가 해당 메소드를 호출할 수 있다.


## 2. 동일한 객체의 서로 다른 synchronized 메소드를 호출할 경우는 어떻게 될까?

만약 한 객체가 두 개의 synchronized 메소드를 가지고 있고, 어느 한 스레드가 첫 번째 synchronized 메소드를 호출할 때, 다른 스레드가 두 번째 synchronized 메소드를 호출할 수 있을까?

```java
class GirlFriend {

	public void sing() {

		try {
			for (int i = 0; i < 10; i++) {
				System.out.println("Singing");
				Thread.sleep(100);
			}
		} catch (Exception e) {}
	}

	public void count() {
		try {
			for (int i = 0; i < 10; i++) {
				System.out.println(i);
				Thread.sleep(100);
			}
		} catch (Exception e) {}
	}
}

class BoyFriend1 extends Thread {

	private final GirlFriend girlFriend;

	public BoyFriend1(GirlFriend girlFriend) {
		this.girlFriend = girlFriend;
	}

	@Override
	public void run() {
		girlFriend.sing();
	}
}

class BoyFriend2 extends Thread {

	private final GirlFriend girlFriend;

	public BoyFriend2(GirlFriend girlFriend) {
		this.girlFriend = girlFriend;
	}

	@Override
	public void run() {
		girlFriend.count();
	}
}
```

```java
@Test
void test() throws InterruptedException {
	BoyFriend1 boyFriend1 = new BoyFriend1(girlfriend);
	boyFriend1.start();
	BoyFriend2 boyFriend2 = new BoyFriend2(girlfriend);
	boyFriend2.start();	
	Thread.sleep(2000);
}
```

```
Singing
0
Singing
1
2
Singing
Singing
3
4
Singing
Singing
5
6
Singing
7
Singing
8
Singing
9
Singing
```
테스트 실행 결과를 보면 sing() 메소드와 count() 메소드가 함께 호출되는 것을 확인할 수 있다. GirlFriend 메소드에 synchronized 키워드를 선언하고 다시 테스트를 실행한 결과는 아래와 같다.
```
Singing
Singing
Singing
Singing
Singing
Singing
Singing
Singing
Singing
Singing
0
1
2
3
4
5
6
7
8
9
```
스레드가 synchronized 메소드를 호출하면 해당 객체의 lock을 얻게 된다. BoyFriend1 스레드가 sing() 메소드를 호출하면서 GirlFriend 객체에 대한 lock을 획득했다. 그 다음 BoyFriend2 스레드가 count() 메소드를 호출하지만 GirlFriend 객체에 대한 lock이 BoyFriend1 스레드에 의해 선점되어 있는 상태이기 때문에 BLOCKED 상태에 놓이게되고 BoyFriend1 스레드가 lock을 release하면 그때서야 count() 메소드에 진입한다.


## 3. 서로 다른 객체의 synchronized 메소드를 호출할 경우는 어떻게 될까?

```java
class GirlFriend {

	public synchronized void meet() {
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + " meeting started!");
		System.out.println(threadName + " meeting ended!");
	}
}

@Test
void test() throws InterruptedException {
	
    BoyFriend boyFriend1 = new BoyFriend(new GirlFriend());
	boyFriend1.setName("BoyFriend1");
	boyFriend1.start();

	BoyFriend boyFriend2 = new BoyFriend(new GirlFriend());
	boyFriend2.setName("BoyFriend2");
	boyFriend2.start();

	Thread.sleep(1000);
}
```
```
BoyFriend1 meeting started!
BoyFriend1 meeting ended!
BoyFriend2 meeting started!
BoyFriend2 meeting ended!
```
각각의 객체는 자신만의 lock을 가지고 있다. 스레드가 synchronized 메소드에 진입할 때, 그 객체의 lock을 획득한다. BoyFriend1와 BoyFriend2 스레드가 획득하는 GirlFriend객체의 lock은 서로 다른 것이기 때문에 meet 메소드가 동시에 실행된다.


## 4. 같은 객체의 서로 다른 static synchronized 메소드를 호출할 경우는 어떻게 될까?

```java
	
class GirlFriend {

	public static synchronized void sing() {
		try {
			for (int i = 0; i < 10; i++) {
				System.out.println("Singing");
				Thread.sleep(100);
			}
		} catch (Exception e) {
		}
	}

	public static synchronized void count() {
		try {
			for (int i = 0; i < 10; i++) {
				System.out.println(i);
				Thread.sleep(100);
			}
		} catch (Exception e) {
		}
	}
}

class BoyFriend1 extends Thread {

	@Override
	public void run() {
		GirlFriend.sing();
	}
}

class BoyFriend2 extends Thread {
	
    @Override
	public void run() {
		GirlFriend.count();
	}
}
```

```java
@Test
void test() throws InterruptedException {

   	new BoyFriend1().start();
	new BoyFriend2().start();

	Thread.sleep(3000);
}
```
```
Singing
Singing
Singing
Singing
Singing
Singing
Singing
Singing
Singing
Singing
0
1
2
3
4
5
6
7
8
9
```
스레드가 static synchronized 메소드를 호출할 때 해당 객체의 java.lang.Class의 lock을 획득하게 된다. lock은 하나의 스레드에 의해서만 선점되므로 singing() 메소드 호출이 끝난 뒤에 count() 호출이 끝난 것을 확인할 수 있다. 스레드가 static synchronized 메소드를 호출할 때에는 GirlFriend의 java.lang.Class lock을 선점하지만 non-static synchronized 메소드를 호출할 때는 GirlFriend 객체의 lock을 획득한다. 이 두 lock은 별개의 lock이므로 만약 BoyFriend2스레드가 non-static 메소드를 호출했다면 BoyFriend1 스레드가 호출한 메소드와 동시에 실행됐을 것이다.
