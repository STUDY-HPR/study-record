import java.util.Arrays;

public class Main {
	public static void main(String[] args) {
		DynamicArray d = new DynamicArray();
		d.append(1);
		d.append(2);
		d.append(3);
		d.append(4);
		d.append(5);
		System.out.println(d.toString());
	}
}

class DynamicArray{
	int size;
	int capacity;
	int[] array;
	
	DynamicArray() { // 초기화 작업
		this.size = 0; // 현재 배열 위치
		capacity = 5; // 배열 용량
		array = new int[capacity]; // 용량 크기만큼 배열 생성
	}
	
	public void append(int number) {
		if(size + 1 == capacity) { // 만약 현재 배열 위치+1 이 용량과 같다면
			newCreateArray(capacity); // newCreateArray 함수를 호출한다.
			array[++size] = number;
		}else {
			array[++size] = number;
		}
	}
	
	public void newCreateArray(int capacity) {
		this.capacity = capacity * 2; // 현재 용량의 *2 만큼
		int[] newArray = new int[this.capacity]; // 새로운 배열을 생성하고
		arrayCopy(array, newArray); // 새로운 배열에 기존 배열에 있는 값들을 복사후
		
		array = newArray; // 기존 배열의 주소를 새로운 배열 주소로 참조한다.
	}
	
	public void arrayCopy(int[] array, int[] copy) { // 배열 복사
		for(int i = 0; i < array.length; i++) {
			copy[i] = array[i];
		}
	}
	
	public String toString() { // 배열 출력
		return Arrays.toString(array);
	}
}
