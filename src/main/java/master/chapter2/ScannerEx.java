package master.chapter2;
import java.util.*;

public class ScannerEx {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("두자리 정수를 하나 입력하세요.>");
        String input = scanner.nextLine();
        int num = Integer.parseInt(input);  // 입력 받은 문자열을 숫자로 변환

        System.out.println("입력 내용: " + input);
        System.out.printf("num=%d%n", num);

        scanner.close();
    }
}
