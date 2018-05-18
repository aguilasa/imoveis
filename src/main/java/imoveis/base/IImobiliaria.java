package imoveis.base;

import java.util.List;

public interface IImobiliaria {

    String getTipo();

    void setTipo(String tipo);
    
    int getPagina();
    
    void setPagina(int pagina);

    int getPaginas();

    List<IImovel> getImoveis();
    
    void carregar();
    
    String getUrl();
}
