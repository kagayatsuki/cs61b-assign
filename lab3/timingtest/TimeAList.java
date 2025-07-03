package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();

        // 测试列表大小：1000, 2000, 4000, ..., 128000
        for (int N = 1000; N <= 128000; N *= 2) {
            // 创建新的 AList
            AList<Integer> list = new AList<>();

            // 开始计时
            Stopwatch timer = new Stopwatch();

            // 执行 N 次 addLast 操作
            for (int i = 0; i < N; i++) {
                list.addLast(i);
            }

            // 记录耗时
            double time = timer.elapsedTime();

            // 存储结果
            Ns.addLast(N);
            times.addLast(time);
            opCounts.addLast(N);
        }

        // 打印计时表
        printTimingTable(Ns, times, opCounts);
    }
}
