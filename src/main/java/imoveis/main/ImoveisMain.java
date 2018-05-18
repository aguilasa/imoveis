package imoveis.main;

import java.util.LinkedList;
import java.util.List;

import imoveis.base.IImobiliaria;
import imoveis.excel.Excel;
import imoveis.imobiliarias.Abelardo;
import imoveis.imobiliarias.Conexao;
import imoveis.imobiliarias.Orbi;

public class ImoveisMain {

    public static void main(String[] args) {
        Excel.getInstance().clear();
        List<IImobiliaria> imobiliarias = new LinkedList<>();
        imobiliarias.add(new Orbi("apartamento"));
        imobiliarias.add(new Conexao("apartamento"));
        imobiliarias.add(new Abelardo("apartamento"));

        for (IImobiliaria i : imobiliarias) {
            Excel.getInstance().addTodosImovel(i.getImoveis());
        }

        Excel.getInstance().gerar();
    }

}
