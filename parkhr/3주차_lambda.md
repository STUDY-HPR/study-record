## lambda

`동작 파라미터화`란  아직은 어떻게 실행할 것인지 결정하지 않은 `코드 블록`을 의미한다.

예를 들어 나중에 실행될 메서드의 인수로 코드 블록을 전달할 수 있다.

동작 파라미터화를 추가하려면 쓸데없는 코드가 늘어난다. 자바 8은 `람다 표현식`으로 이 문제를 해결한다.

<br/>

계속해서 늘어나는 요구사항 ..

아래 코드는 파라미터를 추가하는 방법을 이용하여 요구사항을 맞추었지만, 좀 더 유연하게 대응하고 싶다.

```java
// 리스트에서 녹색 사과만 필터링
public static List<Apple> filterGreenApples(List<Apple> inventory) {
	List<Apple> result = new ArrayList<>();
	for (Apple apple: inventory) {
		if(GREEN.equals(apple.getColor())) {
			result.add(apple);
		}
	}
}

// 빨간 사과도 필터링 추가하고 싶어! => 색을 파라미터화 한다.
public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
	List<Apple> result = new ArrayList<>();
	for (Apple apple: inventory) {
		if(apple.getColor().equals(color)) {
			result.add(apple);
		}
	}
}

// 색 이외에도 150그램을 기준으로 가벼운 사과 무거운 사과 구분하고 싶어!
// 필터링 조건을 적용하는 부분의 코드가 색 필터링 코드와 대부분 중복!!
public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
	List<Apple> result = new ArrayList<>();
	for (Apple apple: inventory) {
		if(apple.getWeight() > weight) {
			result.add(apple);
		}
	}
}
```
<br/>
<br/>

사과의 어떤 속성에 기초하여 불리언값을 반환하는 방법이 있다.

```java
public interface ApplePredicate {
	boolean test (Apple apple);
}
--
public class AppleHeavyWeightPredicate implements ApplePredicate {
	public boolean test(Apple apple) {
		return apple.getWeight() > 150;
	}
}
--
public class AppleGreenColorPredicate implements ApplePredicate {
	public boolean test(Apple apple) {
		return GREEN.equlas(apple.getColor());
	}
}
--
// 메서드가 다양한 동작을 받아서 내부적으로 다양한 동작을 수행
public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
	List<Apple> result = new ArrayList<>();
	for (Apple apple: inventory) {
		if(p.test(apple)) {
			result.add(apple);
		}
	}
}

// ApplePredicate 전달 => 코드를 전달한것과 같다!!
List<Apple> greenApples = filterApples(inventory, new AppleGreenColorPredicate());
```

하지만 위 방법은 복잡하다.. filterApples 메서드로 새로운 동작을 전달하려면 ApplePredicate 인터페이스를 구현하는 여러 클래스를 정의한 다음에 인스턴스화해야 한다. ⇒ 코드의 양이 많아진다.

이를 개선하기 위해 익명 클래스를 이용하여 코드의 양을 줄일 수 있다.

```java

List<Apple> redApples = filterApples(inventory, new ApplePredicate(){
	public boolean test(Apple apple){
		return RED.equlas(apple.getColor());
	}
})
```

더 줄이고 싶은데... ⇒ 람다 표현식을 사용하여 재구현 할 수 있다!

```java
List<Apple> result = filterApples(inventory, (Apple apple) -> RED.equals(apple.getColor()));
```
