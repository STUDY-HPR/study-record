# Reentrant Lock

- 자바에서는 일반적으로(?)  `synchronized` 키워드를 사용하여 동기화 기법을 적용할 수 있다. synchronized 키워드를 사용했을 때에는 어떠한 추가적인 연산(?)을 지원하지 않는다. 예를 들어, 어느 한 스레드가 synchronized 블록을 벗어나게 되면 어떤 순서로 다음 스레드가 synchronized 블록에 접근할 수 있도록 할지, 스레드간의 경합으로 인해 발생하는 `Starvation` 현상은 어떻게 극복할 수 있는지와 같은 매커니즘을 제공하지 않는다. 이에 반해 ReentrantLock 클래스는 좀 더 유연한 매커니즘을 제공한다.



### 1. ReentrantLock 클래스

1. *ReentrantLock* 클래스는 `lock()`와 `unlock()` 메소드를 제공한다. 임계 구역에 진입할 때 *lock()*을 호출하여 다른 스레드들이 접근할 수 없도록 하고 *unlock()*을 호출하여 임계 구역에 대한 *lock*을 해제 할 수 있다.

2. ReentrantLock은 *hold count* 라는 값을 가지고 있는데, *lock()*을 호출할 때 마다 1씩 증가하고 *unlock()*을 호출할 때 마다 1씩 감소한다. ReentrantLock은 *unlock()*이 호출되기 전에 *lock()*을 호출하여 또 다시 *lock*을 획득할 수 있다. 즉, *hold count*의 값이 0이라면 공유 자원에 대한 *lock*이 해제됐다고 볼 수 있다.
3. *lock* 을 획득하기를 기다리는 스레드들이 공평하게 *lock*을 획득 할 수 있도록 하는 `fairness` 파라미터를 제공한다. 이 파라미터는 가장 오랫동안 기다린 스레드가 다음 *lock*을 획득할 수 있도록 한다.



### 2. ReentrantLock 클래스의 메소드

>- **lock() :** 공유 자원이 할당되지 않았다면, 스레드가 락을 획득하고 *hold count*의 값을 1씩 증가시킨다.
>
>- **unlock() :** *hold count*의 값을 1씩 감소시킨다. 공유 자원이 release되는 시점은 이 메소드를 호출할 때가 아니라 *hold count* 값이 0이 됐을 때이다.
>
>  > unlock() 메소드를 호출하지 않으면 *lock*이 반환되지 않아 데드락에 빠질 수 있다. 특히, 예외가 발생했을 때 ReentrantLock 클래스는 자동으로 *lock*을 반환하지 않기 때문에 명시적으로 *finally 블록*에서 unlock() 메소드를 호출해야만 한다.
>
>- **tryLock() : **공유 자원이 다른 스레드에게 할당되지 않았다면 *hold count*를 1 증가시키고 true를 반환한다. 공유 자원이 다른 스레드에게 할당된 상태라면 false를 반환하는데, 이때 해당 스레드는 *blocking*되지 않는다. 즉, 공유 자원을 획득하지 못했을 때 다른 작업을 할 수 있다.
>
>- **tryLock(long timeout, TimeUnit unit) :** 공유 자원을 획득하기 전에 인수로 전달된 시간만큼 기다린다. *timeout* 이후에는 **tryLock()**과 마찬가지로 다른 작업을 수행할 수 있다.
>
>- **lockInterruptibly() : ** 확인 필요
>
>- **getHoldCount() : ** 공유 자원에 대한 *hold count* 값을 리턴한다.
>
>- **isHeldByCurrentThread() : ** 현재 스레드가 공유 자원에 대해 *lock*을 획득했다면 true를 리턴한다.
>
>- **isLocked() : ** *lock 인스턴스*가 다른 스레드에 의해서 획득되었다면 treu를 리턴한다.



> ```java
> reentrantLock.lock();
> reentrantLock.lock();
> assertEquals(2, reentrantLock.getHoldCount());
> assertEquals(true, reentrantLock.isLocked());
> 
> reentrantLock.unlock();
> assertEquals(1, reentrantLock.getHoldCount());
> assertEquals(true, reentrantLock.isLocked());
> 
> reentrantLock.unlock();
> assertEquals(0, reentrantLock.getHoldCount());
> assertEquals(false, reentrantLock.isLocked());
> ```



### 3. ReentrantLock와 synchronized 키워드의 차이점

1. ReentrantLock의 생성자에 *fairness* 파라미터를 넘겨서 인스턴스를 생성하면 가장 오랫동안 기다린 스레드가 가장 먼저 임계 구역에 대한 *lock*을 획득할 수 있도록 한다. 반면에 synchronized는 임의의 스레드가 *lock*을 획득한다.
2. ReentrantLock은 *tryLock()* 메소드를 호출하여 *lock*을 획득하지 못했을 때 *blocking* 을 회피할 수 있다.
3. synchronized 키워드를 사용했을 때에는 스레드가 *lock*을 획득할 때까지 무한정으로 기다리게되지만 ReentrantLock는 *lockInterruptibly()* 메소드를 사용하면 다른 스레드에 의해서 인터럽트될 수 있다.
4. ReentrantLock은 *lock*을 획득하기 위해 대기 중인 스레드들이 리스트를 얻을 수 있다.



---

#### [출처]

[GeeksforGeeks ](https://www.geeksforgeeks.org/reentrant-lock-java/)

[ReentrantLock Example in Java, Difference between synchronized vs ReentrantLock](https://javarevisited.blogspot.com/2013/03/reentrantlock-example-in-java-synchronized-difference-vs-lock.html#axzz7QDk3Gbku)

[How to use Lock and ReentrantLock for Synchronization in Java - Example Tutorial](https://javarevisited.blogspot.com/2014/10/how-to-use-locks-in-multi-threaded-java-program-example.html#axzz7QDk3Gbku)

[Binary Semaphore vs Reentrant Lock](https://www.baeldung.com/java-binary-semaphore-vs-reentrant-lock)