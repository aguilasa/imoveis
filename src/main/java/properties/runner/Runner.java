package properties.runner;

import java.util.List;

import properties.base.IRealState;
import properties.base.IProperty;
import properties.excel.Excel;

public class Runner implements Runnable {

    private IRealState imobiliaria;

    public Runner(IRealState imobiliaria) {
        this.imobiliaria = imobiliaria;
    }

    @Override
    public void run() {
        List<IProperty> imoveis = imobiliaria.getProperties();
        Excel.getInstance().addTodosImovel(imoveis);
    }

}
