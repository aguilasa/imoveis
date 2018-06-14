package imoveis.runner;

import java.util.List;

import imoveis.base.IImobiliaria;
import imoveis.base.IImovel;
import imoveis.excel.Excel;

public class Runner implements Runnable {

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
