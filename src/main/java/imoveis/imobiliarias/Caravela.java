package imoveis.imobiliarias;

import static imoveis.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import imoveis.base.IImovel;
import imoveis.base.Imobiliaria;
import imoveis.base.ImobiliariaHtml;
import imoveis.base.ImovelHtml;
import imoveis.excel.Excel;
import imoveis.utils.Utils;

public class Caravela extends ImobiliariaHtml {

    private static final String BASEIMOVEL = "http://caravela.imb.br/site/";
    private static final String URLBASE = "http://caravela.imb.br/site/busca.php?negocio=2&tipoimovel=%d&cidade=1039";

    public Caravela(String tipo) {
        super(tipo);
    }

    @Override
    public int getPaginas() {
        return 1;
    }

    @Override
    public String getUrl() {
        return String.format(URLBASE, tipo.equals("apartamento") ? 2 : 3);
    }

    @Override
    public Elements getElementos() {
        Document document = getDocument();
        return document.select("div.quadro_prod");
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
            setUrl(BASEIMOVEL.concat(link.attr("href")));
        }

        @Override
        public void carregarNome() {
            Element dados = elemento.select("span.dados_prod").first();
            if (dados != null) {
                List<TextNode> nodes = dados.childNodes().stream().filter(c -> c instanceof TextNode).map(c -> (TextNode) c).collect(Collectors.toList());
                setNome(String.format("%s - %s", nodes.get(0).text().trim(), nodes.get(1).text().trim()));
                setBairro(nodes.get(1).text().replace("Blumenau - ", "").trim());
                setPrecoStr(nodes.get(2).text().replace("R$", "").trim());
                try {
                    setPreco(textoParaReal(getPrecoStr()));
                } catch (Exception e) {
                    setPreco(0);
                }
            }
        }

        @Override
        public void carregarBairro() {
        }

        @Override
        public void carregarPreco() {
        }

        @Override
        public void carregarQuartos() {
        }

        @Override
        public void carregarVagas() {
        }

        @Override
        public void carregarSuites() {
        }

        @Override
        public void carregarArea() {
        }

        @Override
        public void carregarAnunciante() {
            setAnunciante("Caravela");
        }

        @Override
        public void carregarCondominio() {
        }

        @Override
        public void carregarEndereco() {
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new Caravela("apartamento");
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
