/*
 * Created on 10 de mai de 2018.
 *
 * Copyright 2018 Senior Ltda. All rights reserved.
 */
package imoveis.base;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Teste {

    public static void main(String[] args) throws Exception {
        new Teste().run();
    }

    public void run() {
        List<Runnable> runners = new LinkedList<>();
        for (int i = 0; i < 100; i++) {
            runners.add(new Runner());
        }
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        for (Runnable runner : runners) {
            executor.execute(runner);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(20, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    class Runner implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getId());
            Random rand = new Random();
            int millis = rand.nextInt(1000) + 1;
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
