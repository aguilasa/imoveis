package imoveis.base;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public abstract class ImovelHtml extends Imovel {

    protected Element elemento;

    private Document documento = null;

    public ImovelHtml(Element elemento) {
        this.elemento = elemento;
    }

    @Override
    public void carregar() {
        super.carregar();
        documento = null;
    }

    protected Document getDocumento() {
        if (documento == null) {
            try {
                documento = Jsoup.connect(getUrl()).get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return documento;
    }

}
