package imoveis.imobiliarias;

import static imoveis.utils.Utils.*;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import imoveis.base.IImovel;
import imoveis.base.Imobiliaria;
import imoveis.base.ImobiliariaHtml;
import imoveis.base.ImovelHtml;
import imoveis.excel.Excel;
import imoveis.utils.Utils;

public class Abelardo extends ImobiliariaHtml {

    private static final String IMOVELBASE = "http://www.abelardoimoveis.com.br";
    private static final String URLBASE = "http://www.abelardoimoveis.com.br/imoveis-tipo-%s-para-locacao-em-blumenau-pg-%s";

    public Abelardo(String tipo) {
        super(tipo);
    }

    @Override
    public Elements getElementos(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.select("div.imovel");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getUrl(int pagina) {
        return String.format(URLBASE, tipo, pagina);
    }

    @Override
    public int getPaginas() {
        try {
            Document document = Jsoup.connect(getUrl(1)).get();
            Elements paginas = document.select("ul.nav-paginas li a");
            String valor = paginas.get(paginas.size() - 2).text();
            return Integer.valueOf(valor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IImovel newImovel(Element elemento) {
        return new ImovelImpl(elemento);
    }

    private class ImovelImpl extends ImovelHtml {

        public ImovelImpl(Element elemento) {
            super(elemento);
        }

        @Override
        public void carregarNome() {
            Element link = elemento.select("a.visualizar-imovel").first();
            setNome(link.attr("title"));
        }

        @Override
        public void carregarUrl() {
            Element link = elemento.select("a.visualizar-imovel").first();
            setUrl(IMOVELBASE.concat(link.attr("href")));
        }

        @Override
        public void carregarPreco() {
            setPrecoStr(elemento.select("strong.preco-imovel").first().text().trim());
            try {
                setPreco(textoParaReal(getPrecoStr()));
            } catch (Exception e) {
                setPreco(0);
            }
        }

        @Override
        public void carregarBairro() {
            Elements valores = elemento.select("div.endereco-imovel");
            if (!valores.isEmpty()) {
                Element valor = valores.first();
                String texto = valor.text().trim();
                Elements span = valor.select("span");
                if (!span.isEmpty()) {
                    texto = texto.replace(span.first().text().trim(), "");
                }
                texto = texto.replaceAll("Blumenau -", "").trim();
                setBairro(texto);
            }
        }

        @Override
        public void carregarQuartos() {
            Document documento = getDocumento();
            Elements dados = documento.select("div.dormitorios div.quantidade");
            if (!dados.isEmpty()) {
                setQuartos(Integer.valueOf(dados.first().text().trim()));
            }
        }

        @Override
        public void carregarVagas() {
            Document documento = getDocumento();
            Elements dados = documento.select("div.garagens div.quantidade");
            if (!dados.isEmpty()) {
                setVagas(Integer.valueOf(dados.first().text().trim()));
            }
        }

        @Override
        public void carregarSuites() {
            Document documento = getDocumento();
            Elements dados = documento.select("div.suites div.quantidade");
            if (!dados.isEmpty()) {
                setSuites(Integer.valueOf(dados.first().text().trim()));
            }
        }

        @Override
        public void carregarArea() {
            Document documento = getDocumento();
            Elements dados = documento.select("div.areaprivada div.quantidade");
            if (!dados.isEmpty()) {
                setArea(textoParaReal(dados.first().text().trim()));
            }
        }

        @Override
        public void carregarAnunciante() {
            setAnunciante("Abelardo");
        }

        @Override
        public void carregarCondominio() {
            Document documento = getDocumento();
            Elements resumo = documento.select("div.resumo-imovel");
            if (!resumo.isEmpty()) {
                String texto = resumo.first().text();
                setCondominio(buscarCondominio(texto));
            }
        }

        @Override
        public void carregarEndereco() {
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new Abelardo("apartamento");
        List<IImovel> imos = imobiliaria.getImoveis();
        Excel.getInstance().clear();
        for (IImovel imo : imos) {
            Excel.getInstance().addImovel(imo);
            JSONObject json = Utils.imovelToJson(imo);
            System.out.println(json.toString());
        }
        Excel.getInstance().gerar();
    }

}
