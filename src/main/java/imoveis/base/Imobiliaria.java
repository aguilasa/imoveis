package imoveis.base;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.Getter;

public abstract class Imobiliaria {

    @Getter
    protected String tipo;
    @Getter
    protected int paginas;
    private boolean carregou = false;
    private List<Imovel> imoveis = new LinkedList<Imovel>();

    public Imobiliaria(String tipo) {
        this.tipo = tipo;
    }

    public List<Imovel> getImoveis() {
        if (!carregou) {
            carregar();
        }
        return imoveis;
    }

    public void carregar() {
        paginas = numeroPaginas();

        for (int i = 1; i <= paginas; i++) {
            String url = getUrl(i);
            Elements elementos = getElementos(url);
            for (Element elemento : elementos) {
                Imovel imovel = newImovel(elemento);
                imovel.carregar();
                imoveis.add(imovel);
            }
        }
    }

    public abstract Elements getElementos(String url);

    public abstract String getUrl(int pagina);

    public abstract int numeroPaginas();

    public abstract Imovel newImovel(Element elemento);

}
