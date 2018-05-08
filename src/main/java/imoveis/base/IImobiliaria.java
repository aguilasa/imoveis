package imoveis.base;

import java.util.List;

public interface IImobiliaria {

    String getTipo();

    void setTipo(String tipo);

    int getPaginas();

    List<IImovel> getImoveis();
    
    void carregar();
    
    String getUrl(int pagina);
}
