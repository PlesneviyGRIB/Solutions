import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Task_7 {
    private static int cntOfThreads;

    // Count for iterations for one thread! Total number of threads = cntOfIterations * cntOfThreads
    private static int cntOfIterations = 10000;

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        checkParams(args);

        long time = System.currentTimeMillis();

        List<Future<Double>> futures = new ArrayList<>(cntOfThreads);

        ExecutorService executorService = Executors.newFixedThreadPool(cntOfThreads);

        boolean singh = false;
        for(int i = 0; i<cntOfThreads; i++) {
            futures.add(executorService.submit(new Calculator(singh,  3 + i*2, cntOfThreads * 2, cntOfIterations)));
            singh = !singh;
        }

        executorService.shutdown();

        result(time, futures);
    }

    static void checkParams(String[] args){
        if(args.length<1) {
            System.out.println("Provide count of threads as param!");
            System.exit(0);
        }
        try{
            cntOfThreads = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Wrong param!");
            System.exit(0);
        }
    }

    static void result(long startTime, List<Future<Double>> futures) throws ExecutionException, InterruptedException {
        double res = 1;
        for(Future<Double> future : futures)
            res += future.get();

        System.out.println("RES: " + res);

        startTime -= System.currentTimeMillis();
        System.out.println("Time: " + -startTime);
    }

    static class Calculator implements Callable<Double>{
        private final boolean singh;
        private final long startNum;
        private final long pace;
        private final long iterationCnt;

        public Calculator(boolean singh, long startNum, long pace, int iterationCnt ) {
            this.singh = singh;
            this.startNum = startNum;
            this.pace = pace;
            this.iterationCnt = iterationCnt;
        }
        @Override
        public Double call() {
            double res = 0;
            long del = startNum;

            for (int i = 0; i<iterationCnt; i++) {
                res += 1.0 / del;
                del += pace;
            }
            return singh ? res : -res;
        }
    }
}
