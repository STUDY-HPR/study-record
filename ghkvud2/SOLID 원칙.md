# SOLID 원칙



## 1. SRP (단일 책임 원칙)

- 클래스는 하나의 책임만 가져야 하고 클래스의 모든 기능은 그 책임을 수행하는데 집중되어야 한다는 원칙이다. 어떤 변화에 의해 클래스를 변경해야 하는 이유는 오직 하나뿐이어야함을 의미한다. **하나의 클래스가 여러 원인에 의해서 변경이 잦다면 각 책임을 별도의 클래스로 분할하여 클래스 당 하나의 책임만을 맡도록 리팩토링할 수 있다.** 만약 클래스들이 유사하고 비슷한 책임을 중복해서 갖고 있다면 슈퍼 클래스를 사용하여 유사한 책임을 슈퍼 클래스에 위임할 수 있다. 그 반대로 책임이 분산되어 있는 경우, 변경 사항을 반영하기 위해 여러 클래스를 수정해야하는 위험이 있기 때문에 산발적으로 퍼져있는 책임을 한곳으로 모아서 응집성을 높여야 한다.

  ```java
  public class AreaCalculator {
  
  	public double sum(List<Object> shapes) {
          double sum = 0;
          for (int i = 0; i < shapes.size(); i++) {
              
          	Object shape = shapes.get(i);
          	
          	if(shape instanceof Square) {
          		sum += Math.pow(((Square)shape).getLength(), 2);
          	}
          	
          	if(shape instanceof Circle) {
          		sum += Math.PI * Math.pow(((Circle)shape).getRadius(), 2);
          	}
          }
          return sum;
      }
  }
  ```

  - *AreaCalculator* 클래스의 *sum* 메소드는 주어진 도형의 넓이를 구하는 기능을 제공한다. 매개변수로 주어진 List의 인스턴스를 확인하여 그에 맞는 넓이를 구해 반환한다. *AreaCalculator*  클래스에 도형의 넓이를 *json* 문자열 형태로 반환해야할 필요가 있어 아래와 같이 메소드를 추가했다.

  ```java
  public class AreaCalculator {
   
      public double sum(List<Object> shapes) {
          ...
      }
          
      public String json(List<Object> shapes){
          return "{sum : " + sum(shapes) + "}";
      }
  }
  ```

  - *json* 문자열 형태뿐만 아니라 *csv* 형태의 문자열로 반환해야할 필요가 있어 메소드를 또 다시 추가했다.

  ```java
  class AreaCalculator {
   
      public int sum(List<Object> shapes) {
          ...
      }
          
      public String json(List<Object> shapes){
          ...
      }
      
      public String csv(List<Object> shapes){
          return "sum," + sum(shapes);
      }
  }
  ```

  - 현재 *AreaCalculator* 클래스는 도형의 넓이를 구하는 책임과 넓이를 다양한 문자열 형태로 반환하는 책임을 갖고 있다. 즉 하나의 클래스가 두 개의 책임을 갖고 있기 때문에 클래스가 변경되는 이유도 두 가지가 생긴 것이다. 위에서 설명한 **하나의 클래스가 여러 원인에 의해서 변경된다면 각 책임을 별도의 클래스로 분할 할 수 있다.** 즉, 도형의 넓이를 구하는 책임과 넓이를 문자열로 반환하는 책임을 분리하는 것이다. 서로 다른 두 책임을  *AreaCalculator* 클래스와 *ShapePrinter* 클래스로 분할하여 단일 책임 원칙을 지킬 수 있다.

  ```java
  class ShapePrinter {
  
  	public String json(int area) {
  		return "{sum : " + area + "}";
  	}
  
  	public String csv(int area) {
  		return "sum," + area;
  	}
  }
  ```



## 2.  OCP (개방 폐쇄 원칙)

- 소프트웨어의 구성 요소는 확장에는 열려있고, 변경에는 닫혀있어야 한다는 원리이다. 요구사항의 변경이나 추가사항이 발생하더라도, 기존 구성요소는 수정이 일어나지 말아야하며, 기존 구성요소를 쉽게 확장해서 재사용할 수 있어야 한다는 뜻이다. **변경될 것과 변하지 않는 것을 명확히 구분하고, 이 두 모듈이 만나는 지점에 인터페이스를 정의하여 변하지 않는 모듈이 이 인터페이스에 의존하도록 코드를 작성하는 것이다.** 변하지 않는 것은 인터페이스에만 의존하기 때문에 인터페이스를 구현하고 있는 구현체를 새로 작성하면 수정없이 기능을 확장하거나 변경할 수 있다.

-  *AreaCalculator* 클래스의 *sum* 메소드는 리스트의 각 요소의 타입(Sqaure, Circle)을 확인하여 각각의 도형에 맞는 넓이를 구하고 있다. 즉, 변하지 않는 부분(넓이를 구하는 기능)과 변하는 부분(각 도형에 따라 적용되는 넓이를 구하는 공식)이 혼재되어 있다. **도형의 넓이를 구한다는 변하지 않는 sum 메소드와 각각의 도형이 무엇이냐에 따라 넓이를 구하는 공식이 변경되는 부분에 인터페이스를 정의하여 OCP 원칙을 지킬 수 있다.**  *AreaCalculator* 클래스의 sum 메소드는 *Shape* 인터페이스에만 의존하고 있다. 넓이를 구하기 위해서 *Shape* 인터페이스의 구현체의 *area()* 메소드를 호출하면 그만이다. 기존에 Rectangle, Circle 클래스 외에 다른 도형이 추가된다고 하더라도 *Shape* 인터페이스만 구현한다면 *AreaCalculator* 클래스의 *sum* 메소드를 변경하지 않고도 기능을 확장할 수 있게 되는 것이다.

  ```JAVA
  interface Shape {
  	double area();
  }
  
  class Rectangle implements Shape{
      double area(){ ... }
  }
  
  class AreaCalculator {
  
      @Override
      public double sum(List<Shape> shapes) {
          double sum = 0;
          for (int i = 0; i < shapes.size(); i++) {
              sum += shapes.get(i).area();
          }
          return sum;
      }
  }
  ```



## 3. LSP (리스코프 치환 원칙)

- 서브 타입은 언제나 슈퍼 타입으로 교체할 수 있어야 한다는 원칙이다. **즉, 서브 타입은 언제나 슈퍼 타입과 호환될 수 있어야하며 슈퍼 타입이 약속한 규약을 지켜야 한다.** 이 원칙은 단순히 컴파일이 성공하는 것 뿐만 아니라 규약을 지켜 의도한대로 기능이 동작하도록 해야한다는 것이다. *AreaCalculator* 클래스는 *Shape* 인터페이스에 의존하고 *Shape* 인터페이스의 *area* 메소드를 호출함으로써 도형의 넓이를 반환받을 것이라고 기대한다. 만약 *Shape* 인터페이스의 구현체가 전혀 다른 기능을 수행한다면 *AreaCalculator* 클래스는 더 이상 *Shape* 인터페이스에 의존할 수도 없고 그렇게 해서도 안된다. LCP를 위반하는 *Shape* 인터페이스 구현체를 생성해보자. (*Shape* 인터페이스는 도형의 넓이를 반환한다는 규약을 어긴 구현체이다.)

  ```java
  class NoShape implements Shape {
      @Override
      public double area() {
          throw new IllegalStateException("Cannot calculate area");
      }
  }
  ```

- **서브 클래스에서는 슈퍼 클래스의 사전 조건과 같거나 더 약한 수준에서 사전 조건을 대체할 수 있고, 슈퍼 클래스의 사후 조건과 같거나 더 강한 수준에서 사후 조건을 대체할 수 있다.** 만약 서브 클래스의 사전 조건의 제약 사항이 슈퍼 클래스보다 더 강하다면 슈퍼 클래스에서는 실행되던 것이 서브 클래스의 더 강한 조건으로 인해 실행되지 않을 수 있다. 반면 서브 클래스의 사후 조건이 더 약하다면 슈퍼 클래스에서는 통과시키지 않는 상태를 통과시킬 수 있다.



## 4. ISP (인터페이스 분리 원칙)

- 한 클래스는 자신이 사용하지 않는 인터페이스는 구현하지 말아햐 한다는 원리이다. **하나의 범용적인 인터페이스보다는, 여러 개의 구체적인 인터페이스가 낫다**라고 정의할 수 있다. 이전까지의 *Shape* 인터페이스는 도형의 넓이만 구하는 *area* 메소드만 제공했다. 추가적으로 도형의 부피를 구하는 *volume* 메소드를 추가해보자. *Shape* 인터페이스에 메소드가 추가됐으니 *Shape* 인터페이스 구현체인 Rectangle 클래스도 *volumn* 메소드를 구현해야 한다. 그리고 추가로 정육면체를 나타내는 *Cube* 클래스도 새로 정의하자.

  ```java
  interface Shape {
  	double area();
      double volumn();
  }
  
  class Rectangle implements Shape{
      double area(){ ... }
      double volumn(){
          //직사각형은 2차원이기 때문에 부피를 구할 수 없음.
          return 0;
      }
  }
  
  class Cube implements Shape{
      double area(){ ... }
      double volumn(){ ... }
  }
  ```

  - 3차원인 정육면체는 부피를 구할 수 있지만 직사각형은 2차원 평면이기 때문에 부피를 구할 수 없다. *Shape* 인터페이스에 *volumn* 메소드가 있으니 부피를 구할 수 없는 *Rectangle* 클래스도 의미없이 *volumn* 메소드를 구현할 수 밖에 없다.  만약 *Shape* 인터페이스에 3차원 도형에서만 구현할 수 있는 메소드를 추가한다면 2차원 도형인 *Rectangle* 클래스에도 해당 메소드를 의미없이 구현할 수 밖에 없다.

  

- 이러한 문제를 해결하기 위해 *ISP*를 적용할 수 있다. 2차원 도형 전용 인터페이스, 3차원 도형 전용 인터페이스를 따로 선언하여 구현 클래스가 선택적으로 기능을 구현할 수 있도록 한다. 기존 *Shape* 인터페이스에서 부피를 구하는 메소드를 따로 분리하여 *ThreeDimensionalShape* 인터페이스를 생성하자. *Rectangle* 클래스는 *Shape* 인터페이스만 구현하도록 하고, *Cube* 클래스는 *Shape와 ThreeDimensionalShape* 인터페이스 모두를 구현하도록 한다.

  ```java
  interface ThreeDimensionalShape {
      double volume();
  }
  
  class Rectangle implements Shape{
      double area(){ ... }
  }
  
  class Cube implements Shape, ThreeDimensionalShape{
      double area(){ ... }
      double volumn(){ ... }
  }
  ```

  

## 5. DIP (의존성 역전의 원칙)

- 하위 모듈의 변경이 상위 모듈의 변경을 요구하는 관계를 끊도록 해야한다는 원칙이다.  **즉, 자신보다 변하기 쉬운 것에 의존하지 말고 변하지 않는 것에 의존하도록 하는 것이다.** 상위 클래스일수록, 인터페이스일수록, 추상 클래스일수록 변하지 않을 가능성이 높기에 하위 클래스나 구체 클래스가 아닌 상위 클래스, 인터페이스, 추상 클래스에 의존하라는 원칙이다. 기존 *ShapePrinter* 클래스를 다음과 같이 변경해보자.

  ```java
  class ShapePrinter {
      
      AreaCalculator calcualtor = new AreaCalculator();
  
  	public String json(List<Object> shapes) {
  		return "{sum : " + calcualtor.area(shapes) + "}";
  	}
  
  	public String csv(List<Object> shapes) {
  		return "sum," + calcualtor.area(shapes);
  	}
  }
  ```

  - *ShapePrinter* 클래스는 *AreaCalculator* 클래스에 직접 의존하고 있다. 만약 *AreaCalculator*가 아닌 다른 클래스에 의존할 경우가 발생하면 *ShapePrinter* 클래스를 직접 변경해야만 한다. **변하지 않는 것과 변하는 것의 접점에 인터페이스를 둔다는 OCP 원칙**을 적용한다면 *AreaCalculator* 구체 클래스가 아닌 다른 구체 클래스로 변경하더라도 *ShapePrinter* 클래스는 변경할 필요가 없다.

    ```JAVA
    interface IAreaCalculator { ... }
    class AreaCalculator implements IAreaCalculator { ... }
    
    class ShapePrinter {
        
    	IAreaCalculator calcualtor;
        
        public ShapePrinter(IAreaCalculator calcualtor){
            this.calcualtor = calcualtor;
        }
        
        ...
    }
    ```

  - 기존의 *ShapePrinter* 클래스에서의 *AreaCalculator* 클래스는 그 무엇에도 의존하지 않는 클래스였는데 *IAreaCalculator* 인터페이스에 의존하게 되었다. **즉, 의존의 방향이 역전된 것이다.**

---

### [참고]

[넥스트리 SOLID](https://www.nextree.co.kr/p6960/)

[AMIGOS CODE](https://www.youtube.com/watch?v=_jDNAf3CzeY&t=1237s)

