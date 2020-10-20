package realstate.runner;

import java.util.List;

import realstate.base.IImobiliaria;
import realstate.base.IImovel;
import realstate.excel.Excel;

public class Runner implements Runnable {

    private IImobiliaria imobiliaria;

    public Runner(IImobiliaria imobiliaria) {
        this.imobiliaria = imobiliaria;
    }

    @Override
    public void run() {
        List<IImovel> imoveis = imobiliaria.getProperties();
        Excel.getInstance().addTodosImovel(imoveis);
    }

}
