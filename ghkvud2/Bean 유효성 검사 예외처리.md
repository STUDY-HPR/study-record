Bean validation 공부하다가 질문드립니다.
객체 유효성 검사가 실패했을 경우, 예외 처리를 어떻게 하는 것이 가장 좋은 방법일지 궁금합니다.

### [요구사항]

OrderItem 객체의 유효성 검사
 1) price 필드는 **1000**이상의 값을 갖는다.
 2) quantity 필드는 **1~1000** 사이의 값을 갖는다.
 3) 주문 총액(price * quantity)는 **2만원** 이상이어야 한다.

```java
@Getter
@Setter
@ToString
public class OrderItem {

	private String name;

	@Min(1000)
	private int price;

	@Min(1) @Max(1000)
	private int quantity;

}
```



 스프링에서 유효성 검사를 자동으로 해주고 실패할 경우 **MethodArgumentNotValidException**을 발생시키기 때문에 해당 예외를 **@ExceptionHandler**로 처리하면 될 것 같다고 생각했습니다.

<u>객체 유효성 검사시 발생하는 예외는 하나의 Exception 클래스로 일관되게 처리하려고 합니다.</u>

---

#### 1. BindingResult 객체를 파라미터로 받지 않는 경우

```java
@Slf4j
@Validated
@RestController
public class OrderController {
    
	@ExceptionHandler
	public String handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {

		//bindingResult 객체에 담겨있는 오류 메시지를 담아서 return 하는 것이 바람직하나 
        //예제를 간단하게 하기 위해 string 반환
        
		return "MethodArgumentNotValidException 발생";
	}    
    
 	@PostMapping("/api/v1/orders")
	public String orderV1(@RequestBody @Validated OrderItem orderItem) {

		if (orderItem.getPrice() * orderItem.getQuantity() < 20000) {
            //MethodArgumentNotValidException 하더라도 bindingResult 객체가 없어
            //오류 메시지를 일관된게 처리할 수 없다.
		}
		return "order-v1-ok";
	}   
}
```



 - 요구사항 1~2)를 충족하지 못해서 발생하는 예외는 핸들링 할 수 있습니다.
 - 그러나, 요구사항 (3)은 사용자가 직접 예외 상황을 check하는 로직이 필요한데 bindingResult 객체없이
   @ExceptionHandler에서 일관되게 오류 메시지를 클라이언트에게 반환할 방법이 없으므로 바람직한 방법이 아니라고 판단했습니다.

---

#### 2. BindingResult 객체를 파라미터로 받아 MethodArgumentNotValidException를 throw하여 처리

```java
	@PostMapping("/api/v2/orders")
	public String orderV2(@RequestBody @Validated OrderItem orderItem, 
                          BindingResult bindingResult) throws
		NoSuchMethodException, MethodArgumentNotValidException {

        // 요구사항 3)을 check하는 로직
        if (orderItem.getPrice() * orderItem.getQuantity() < 20000) {
			bindingResult.reject(null, "수량 * 금액은 2만원을 초과해야합니다.");
		}

		if (bindingResult.hasErrors()) {
            //MethodArgumentNotValidException에 bindingResult를 담아서 throw
			throw new MethodArgumentNotValidException(
                new MethodParameter(
				this.getClass()
                    .getDeclaredMethod("orderV2",
								new Class[] {OrderItem.class, BindingResult.class}), 0),
				bindingResult);
		}

		return "order-v2-ok";
	}
```

 - 요구사항 3)을 check하는 로직을 작성하고 해당 내용을 bindingResult 객체에 담아 throw하여 처리하였습니다.
 - 다만, MethodArgumentNotValidException을 직접 생성하는 방식이 깔끔한 방법같지는 않습니다.

---

### 3. BindingResult 객체에 의존하는 사용자 정의 Exception 생성

```java
public class CustomInvalidException extends RuntimeException {

	BindingResult bindingResult;

	public CustomInvalidException(BindingResult bindingResult) {
		this.bindingResult = bindingResult;
	}

	public BindingResult getBindingResult() {
		return bindingResult;
	}
    
    ```생략```
}
```

```java
	
	@ExceptionHandler
	public String handleCustomInvalidException(CustomInvalidException e) {
		return "handleCustomInvalidException 발생";
	}

	@PostMapping("/api/v3/orders")
	public String orderV3(@RequestBody @Validated OrderItem orderItem,
                          BindingResult bindingResult) {
        
		if (orderItem.getPrice() * orderItem.getQuantity() < 20000) {
			bindingResult.reject(null, "수량 * 금액은 2만원을 초과해야합니다.");
		}

		if (bindingResult.hasErrors()) {
            // 사용자 정의 Exception throw
			throw new CustomInvalidException(bindingResult);
		}

		return "order-v3-ok";
	}
```

 - 요구사항 1~3)에서 발생하는 예외를 일괄적으로 처리할 수 있는 사용자 정의 클래스 CustomInvalidException를 생성하고 이 예외를 @ExceptionHandler에서 일관되게 처리하는 방법을 선택
 - <u>장점 : 모든 컨트롤러에서 객체의 유효성 검사 예외 처리를 CustomInvalidException을 통해 일관되게 할 수 있음</u>

---

### [요구사항 추가]

OrderItem 객체의 유효성 검사 외에도 API의 **@PathVariable 값의 유효성 검사 추가**

 - URI : /api/v4/{orderId} => **orderId의 길이는 2~50자 사이여야한다.**

```java
@Validated //추가
@RestController
public class OrderController {
    ```생략```
}
```

- Controller에 **@Validated 어노테이션 추가**

---

#### 4. @PathVariable 유효성 검사 예외처리는 Bean 유효성 검사와 일관된 방식으로 할 수 없음

```java
	@ExceptionHandler
	public String handleConstraintViolationException(ConstraintViolationException e){
		return "ConstraintViolationException 발생";
	}

	@PostMapping("/api/v4/orders/{orderId}")
	public String orderV4(
		@PathVariable @Validated @Length(min = 2, max = 50) String orderId,
		@RequestBody @Validated OrderItem orderItem, BindingResult bindingResult) {

		if (orderItem.getPrice() * orderItem.getQuantity() < 20000) {
			bindingResult.reject(null, "수량 * 금액은 2만원을 초과해야합니다.");
		}

		if (bindingResult.hasErrors()) {
			throw new CustomInvalidException(bindingResult);
		}
        
		return "order-v4-ok";
	}
```

 - @PathVariable의 경우 유효성 검사 실패시 **ConstraintViolationException을 발생**시키기 때문에
   사용자 정의 클래스 **CustomInvalidException로 처리할 수 없다.**

---

### 질문

1. **사용자 정의 Exception을 정의해서 처리하는 방법과 MethodArgumentNotValidException 을 이용해서 처리하는 방법중 어떤 방법이 더 나은 선택일지 궁금합니다.**

   - 사실 위와 같은 질문 내용을 정리하다보니 이게 크게 중요할까?라는 생각이 들게되었습니다.
   - 유효성 검사시 발생하는 예외를 일관되게 처리할 수 있다면 어떤 방식을 쓰던 상관없을 것 같다는 생각이 들었습니다.

   

2. **요구사항 3)과 같이 비즈니스 로직성 유효성 검사는 어디서 하는게 더 좋을까요?**

   - 위 예제와 같이 **"수량 x 금액은 2만원을 초과해야한다"** 라는 요구사항은 업무 로직이라고 생각합니다.

   - *<u>컨트롤러 계층</u>*이 아니라 *<u>서비스 계층</u>*에서 유효성 검사를 실행하고 실패하면 **Exception**을 컨트롤러 계층으로 throw하는게 더 좋은 선택일지 궁금합니다.

     - **"비즈니스 로직은 서비스 계층에서 처리한다"** 라는 관점에서 봤을 땐 이 방법이 좋아보이지만 유효성 검사 로직이 서로 다른 계층에 퍼져있는 것이 또 다른 단점이 될 수 있을 것 같습니다.

     

3. **@PathVariable 유효성 검사시** 클래스에 **@Validated** 어노테이션을 추가하는 이유는 무엇일까요?

   - ``Bean`` 유효성 검사와는 달리 **@PathVariable** 유효성 검사를 할 땐 클래스 레벨에 **@Validated** 어노테이션을 추가해야만 했습니다.
   - 동작 방식이 다른거 같은데 이 부분을 공부하기 위해서는 어떤 키워드로 찾아봐야 할까요..?



4. **@PathVariable** 과 **객체** 유효성 검사 실패시 발생하는 Exception이 다른데 이를 공통 예외로 처리해야할까요? (가능은 할까요?)
   - 먼저, **객체** 유효성 검사의 경우 발생하는 예외를 컨트롤 할 수 있지만 