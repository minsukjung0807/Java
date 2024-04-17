package master.chapter3;

public class OperatorEx30 {

    // 10진 정수를 2진수로 변환하는 메서드
    static String toBinaryString(int x) {
        String zero = "00000000000000000000000000000000";
        String tmp = zero + Integer.toBinaryString(x);
        return tmp.substring(tmp.length() - 32);
    }
    public static void main(String[] args) {
        int dec = 8;

        System.
    }
}
