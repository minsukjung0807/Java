package master.chapter4;

public class FlowEx26 {
    public static void main(String[] args) {
        int sum = 0;
        int  i = 0;

        // i를 1씩 증가시키면서 sum에 계속 더해나간다.
        while((sum += ++i) <= 100) {
            System.out.printf("%d - %d%n", i, sum);
        }
    }
}
