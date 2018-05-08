package imoveis.base;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class Imobiliaria implements IImobiliaria {

    @Getter @Setter
    protected String tipo;
    private boolean carregou = false;
    protected List<IImovel> imoveis = new LinkedList<>();

    public Imobiliaria(String tipo) {
        this.tipo = tipo;
    }

    public List<IImovel> getImoveis() {
        if (!carregou) {
            carregar();
        }
        return imoveis;
    }


}
