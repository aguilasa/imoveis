package imoveis.imobiliarias;

import static imoveis.utils.Utils.extrairValor;
import static imoveis.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import imoveis.base.IImovel;
import imoveis.base.Imobiliaria;
import imoveis.base.ImobiliariaHtml;
import imoveis.base.ImovelHtml;
import imoveis.excel.Excel;
import imoveis.utils.Utils;

public class DinamicaSul extends ImobiliariaHtml {

    private static final String URLBASE = "http://www.dinamicasul.com.br/imoveis/blumenau/quero-alugar/%s/pagina%d";

    public DinamicaSul(String tipo) {
        super(tipo);
    }

    @Override
    public int getPaginas() {
        Document document = getDocument();
        int p = 1;
        Elements paginas = document.select("ul.pagination.pagination-sm li");
        for (Element pagina : paginas) {
            String valor = pagina.text().trim();
            if (NumberUtils.isCreatable(valor)) {
                p = Integer.valueOf(valor);
            }
        }
        return p;
    }

    @Override
    public String getUrl() {
        return String.format(URLBASE, tipo, pagina);
    }

    @Override
    public Elements getElementos() {
        Document document = getDocument();
        return document.select("article.imovel");
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
        public void carregarUrl() {
            Element link = elemento.select("a").first();
            setUrl(link.attr("href"));
            setNome(link.attr("title"));
        }

        @Override
        public void carregarNome() {
        }

        @Override
        public void carregarBairro() {
            setBairro(elemento.select("p.bairro").first().text().replace("Bairro", "").trim());
        }

        @Override
        public void carregarPreco() {
            setPrecoStr(elemento.select("p.valor").last().text().trim());
            try {
                setPreco(extrairValor(getPrecoStr()));
            } catch (Exception e) {
                setPreco(0);
            }
        }

        @Override
        public void carregarQuartos() {
            Elements dados = getDocumento().select("div.extras.dormitorios span.un");
            if (!dados.isEmpty()) {
                String texto = dados.first().text().trim();
                setQuartos(Integer.valueOf(texto));
            }
        }

        @Override
        public void carregarVagas() {
            Elements dados = getDocumento().select("div.extras.garagens span.un");
            if (!dados.isEmpty()) {
                String texto = dados.first().text().trim();
                setVagas(Integer.valueOf(texto));
            }
        }

        @Override
        public void carregarSuites() {
            Elements dados = getDocumento().select("div.extras.suites span.un");
            if (!dados.isEmpty()) {
                String texto = dados.first().text().trim();
                setSuites(Integer.valueOf(texto));
            }
        }

        @Override
        public void carregarArea() {
            Elements dados = getDocumento().select("div.extras.areaprivativa span.un");
            if (!dados.isEmpty()) {
                String texto = dados.first().text().replace("m²", "").trim();
                setArea(textoParaReal(texto));
            }
        }

        @Override
        public void carregarAnunciante() {
            setAnunciante("Dinâmica Sul");
        }

        @Override
        public void carregarCondominio() {
            Element dado = getDocumento().select("div#conteudoImovel").first();
            if (dado != null) {
                List<Node> nodes = dado.childNodes();
                for (Node node : nodes) {
                    if (node instanceof TextNode) {
                        TextNode textNode = (TextNode) node;
                        String texto = textNode.text();
                        if (texto.toUpperCase().contains("CONDOM")) {
                            setCondominio(extrairValor(texto));
                            if (getCondominio() > 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void carregarEndereco() {
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new DinamicaSul("casa");
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
