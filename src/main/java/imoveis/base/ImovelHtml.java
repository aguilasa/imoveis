package imoveis.base;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public abstract class ImovelHtml extends Imovel {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";

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
                documento = Jsoup.connect(getUrl()).timeout(0).userAgent(USER_AGENT).get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return documento;
    }

}
