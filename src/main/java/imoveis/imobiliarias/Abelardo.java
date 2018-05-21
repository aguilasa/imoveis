package imoveis.imobiliarias;

import static imoveis.utils.Utils.buscarCondominio;
import static imoveis.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
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
    public Elements getElementos() {
        Document document = getDocument(getUrl());
        return document.select("div.imovel");
    }

    @Override
    public String getUrl() {
        return String.format(URLBASE, tipo, pagina);
    }

    @Override
    public int getPaginas() {
        Document document = getDocument();
        Elements paginas = document.select("ul.nav-paginas li a");
        if (!paginas.isEmpty()) {
            String valor = paginas.get(paginas.size() - 2).text();
            return Integer.valueOf(valor);
        }
        return 1;
    }

    @Override
    public Map<String, String> getPayload() {
        return new LinkedHashMap<>();
    }

    @Override
    public IImovel newImovel(Element elemento) {
        return new ImovelImpl(elemento, tipo);
    }

    private class ImovelImpl extends ImovelHtml {

        public ImovelImpl(Element elemento, String tipo) {
            super(elemento, tipo);
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
        Imobiliaria imobiliaria = new Abelardo("casa");
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
