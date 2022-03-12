### equals()와 hashCode()를 꼭 같이 정의해야할까?

`equals` 메소드에 대해서 공부하다보면 항상 `hashCode`를 같이 정의해야한다고 한다. 

꼭 그래야하는 이유는 무엇일까?



아래와 같은 `Member` 클래스를 정의하고 동등성 비교를 위해 `equals`메소드를 다음과 같이 재정의 한다.

- `동등성`이란 객체가 주소 값이 다르더라도 값이 같으면 같은 객체라고 취급하는 것을 말한다.

```java
public class Member {

	private String name;
	private int age;
	private String address;
    
	```생성자 생략```

	@Override
	public boolean equals(Object o) {
        
		if (this == o)
			return true;
        
		if (o == null || getClass() != o.getClass())
			return false;
        
		Member member = (Member)o;
        
		return age == member.age && 
            Objects.equals(name, member.name) && 
            Objects.equals(address,	member.address);
	}
    
}
```



아래와 같이 서로 다른 `Member` 객체의 주소 값을 비교하면 `false`가 나오고 `equals`메소드를 통해 비교하면  `true`가 나온다.

```java
public static void main(String[] args) {
	Member member1 = new Member("David", 24, "Seoul");
	Member member2 = new Member("David", 24, "Seoul");
    
	System.out.println(member1 == member2);			// false
	System.out.println(member1.equals(member2));	// true
}
```



그렇다면, `Collection` 에서 `Member`객체를 사용할 땐 어떨까?

아래 코드의 실행 결과는 `David`가 나올 것 같지만 `null`이 나온다. 

```java
public static void main(String[] args) {
	HashMap<Member, String> map = new HashMap<>();
    
	Member member1 = new Member("David", 24, "Seoul");
	map.put(member1, "David");

	Member member2 = new Member("David", 24, "Seoul");
	String memberName = map.get(member2);

	System.out.println("Member name : " + memberName); // ????
}
```

- `Collection`에서 `hashCode` 를 재정의하지 않은 객체를 사용할 때 문제가 되는데 `Collection`이 객체를 비교하는 과정은 다음과 같다.

1. `hashCode` 의 리턴 값이 다르면 다른 객체라고 판단한다.
2. `hashCode`의 리턴 값이 같으면 `equals` 메소드를 이용해서 두 객체를 비교하고 그 결과가 `true`이면 같은 객체로 판단한다.
   - 즉,  `equals` 메소드를 이용해서 두 객체를 비교하기 전에 `hashCode`의 값이 다르기 때문에 다른 객체로 판단한 것이다.



다음과 같이 `hashCode` 메소드를 재정의하고 코드를 다시 실행하면 `David` 를 결과로 얻을 수 있다.

```java 
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((address == null) ? 0 : address.hashCode());
	result = prime * result + age;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
}
```



### 그래서....?

`equals` 메소드와 `hashCode`를 같이 정의하는 것이 바람직하다. 지금 당장은 `equals` 메소드만을 사용할 것 같아도 프로그램의 요구사항 변화에 따라 위와 같이 의도치 않은 부작용을 낳을 수 있다.

---



### HashMap의 get(key)과 put(key, value) 메소드



#### 1. get(key) 메소드를 호출할 때

`get(key)` 메소드를 호출하면 파라미터로 전달된 key 객체의 `hashCode()`메소드를 호출해서 해시 테이블의 bucket을 찾아간다. bucket에 하나의 `Entry (key 와 value를 저장해놓는)` 가 있다면 해당 `Entry`를 반환한다.

- 만약, 서로 다른 key 객체가 같은 `hashCode`를 반환하면 어떻게 될까?

- `put(key, value)` 메소드를 이용해서 해시 테이블에 저장할 때 서로 다른 객체의 `Entry`가 같은 bucket에 `linked list` 형태로 저장된다. (separate chaining)

  - 단, `threshold` 값을 넘으면 효율적인 탐색을 위해서 `balanced binary tree` 형태로 저장된다.

  

 해당 bucket에 여러 `Entry`가 있을 경우 `linked list`를 탐색하면서 `Entry`가 가진 `key.equals()`를 호출하여 `true` 반환하는 Entry를 찾는다.

- **여기서 중요한 것은 `value.equals()`를 사용해서 탐색하는 것이 아니라는 것이다.**



#### 2. put(key, value) 메소드를 호출할 때

`get(key)` 메소드와 마찬가지로 `key.hashCode()`를 이용해서 bucket을 찾아간다. 해당 bucket이 비어있다면 `Entry`를 저장하고 이미 저장된 `Entry`가 있다면 `linked list`에 저장한다.



**HashMap의 get(), put() 메소드와 관련해서 기억할 것은 key에 대해서 equals() 메소드와 hashCode() 메소드가 호출된다는 것이다. value는 단순히 Entry에 저장될 뿐이다.**



---

#### 참고

https://www.java67.com/2013/06/how-get-method-of-hashmap-or-hashtable-works-internally.html

https://tecoble.techcourse.co.kr/post/2020-07-29-equals-and-hashCode/