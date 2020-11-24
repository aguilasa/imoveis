package properties.main;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import properties.excel.Excel;
import properties.runner.RunnerFactory;

public class PropertiesMain {

    public static void main(String[] args) {
        new PropertiesMain().run();
    }

    public void run() {
        Excel.getInstance().clear();
        List<Runnable> runners = RunnerFactory.getRunners();

        int nThreads = Runtime.getRuntime().availableProcessors() + 1;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        for (Runnable runner : runners) {
            executor.execute(runner);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(20, TimeUnit.MINUTES);
            Excel.getInstance().gerar();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
