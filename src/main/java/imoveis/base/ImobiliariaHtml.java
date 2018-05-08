package imoveis.base;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class ImobiliariaHtml extends Imobiliaria {

    public ImobiliariaHtml(String tipo) {
        super(tipo);
    }

    public void carregar() {
        int paginas = getPaginas();

        for (int i = 1; i <= paginas; i++) {
            String url = getUrl(i);
            Elements elementos = getElementos(url);
            for (Element elemento : elementos) {
                IImovel imovel = newImovel(elemento);
                imovel.carregar();
                imoveis.add(imovel);
            }
        }
    }

    public abstract Elements getElementos(String url);

    public abstract IImovel newImovel(Element elemento);

}
