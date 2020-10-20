package properties.runner;

import java.util.List;

import properties.base.IImobiliaria;
import properties.base.IImovel;
import properties.excel.Excel;

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
