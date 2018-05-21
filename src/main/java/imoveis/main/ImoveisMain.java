package imoveis.main;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import imoveis.base.IImobiliaria;
import imoveis.base.IImovel;
import imoveis.excel.Excel;
import imoveis.imobiliarias.ACRC;
import imoveis.imobiliarias.Abelardo;
import imoveis.imobiliarias.Conexao;
import imoveis.imobiliarias.LFernando;
import imoveis.imobiliarias.Orbi;
import imoveis.imobiliarias.Portal;

public class ImoveisMain {

    public static void main(String[] args) {
        new ImoveisMain().run();
    }

    public void run() {
        Excel.getInstance().clear();
        List<Runnable> runners = new LinkedList<>();
        runners.add(new Runner(new Orbi("apartamento")));
        runners.add(new Runner(new Orbi("casa")));
        runners.add(new Runner(new Conexao("apartamento")));
        runners.add(new Runner(new Conexao("casa")));
        runners.add(new Runner(new Abelardo("apartamento")));
        runners.add(new Runner(new Abelardo("casa")));
        runners.add(new Runner(new ACRC("apartamento")));
        runners.add(new Runner(new ACRC("casa")));
        runners.add(new Runner(new Portal("apartamento")));
        runners.add(new Runner(new Portal("casa")));
        runners.add(new Runner(new LFernando("apartamento")));
        runners.add(new Runner(new LFernando("casa")));

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

    class Runner implements Runnable {

        private IImobiliaria imobiliaria;

        public Runner(IImobiliaria imobiliaria) {
            this.imobiliaria = imobiliaria;
        }

        @Override
        public void run() {
            List<IImovel> imoveis = imobiliaria.getImoveis();
            Excel.getInstance().addTodosImovel(imoveis);
        }

    }

}
