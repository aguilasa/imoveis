package imoveis.base;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.Setter;

public abstract class ImobiliariaHtml extends Imobiliaria {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";

    @Setter
    private boolean post = false;

    public ImobiliariaHtml(String tipo) {
        super(tipo);
    }

    public void carregar() {
        int paginas = getPaginas();

        for (int i = 1; i <= paginas; i++) {
            setPagina(i);
            Elements elementos = getElementos();
            for (Element elemento : elementos) {
                IImovel imovel = newImovel(elemento);
                imovel.carregar();
                imoveis.add(imovel);
            }
        }
    }

    public Document getDocument() {
        return getDocument(getUrl());
    }

    public Document getDocument(String url) {
        try {
            Connection data = Jsoup.connect(url).timeout(0).userAgent(USER_AGENT).data(getPayload());
            return post ? data.post() : data.get();
        } catch (IOException e) {
            return new Document("");
        }
    }

    public abstract Elements getElementos();

    public abstract IImovel newImovel(Element elemento);

    public abstract Map<String, String> getPayload();

}
