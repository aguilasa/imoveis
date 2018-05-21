package imoveis.imobiliarias;

import static imoveis.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

public class ACRC extends ImobiliariaHtml {

    private static final String IMOVELBASE = "https://www.acrcimoveis.com.br";
    private static final String URLBASE = "https://www.acrcimoveis.com.br/alugar/sc/sc/blumenau/%s/ordem-valor/resultado-crescente/quantidade-48/pagina-%d/";

    public ACRC(String tipo) {
        super(tipo);
    }

    @Override
    public Elements getElementos() {
        Document document = getDocument(getUrl());
        return document.select("div.resultado");
    }

    @Override
    public String getUrl() {
        return String.format(URLBASE, tipo, pagina);
    }

    @Override
    public int getPaginas() {
        Document document = getDocument();
        Elements paginas = document.select("ul.pagination li a");
        if (paginas.size() > 1) {
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
            Element link = elemento.select("div.foto a").first().select("img").first();
            setNome(link.attr("title"));
        }

        @Override
        public void carregarUrl() {
            Element link = elemento.select("div.foto a").first();
            setUrl(IMOVELBASE.concat(link.attr("href")));
        }

        @Override
        public void carregarPreco() {
            setPrecoStr(elemento.select("div.valor h5").first().text().trim());
            try {
                setPreco(textoParaReal(getPrecoStr().replace("R$", "")));
            } catch (Exception e) {
                setPreco(0);
            }
        }

        @Override
        public void carregarBairro() {
            setBairro(elemento.select("h4.bairro").first().text().trim());
        }

        @Override
        public void carregarQuartos() {
            Document documento = getDocumento();
            Elements dados = documento.select("div[title=\"Dormitórios\"]");
            if (!dados.isEmpty()) {
                dados = dados.first().select("span");
                if (!dados.isEmpty()) {
                    String valor = dados.first().text().trim();
                    if (StringUtils.isNumeric(valor)) {
                        setQuartos(Integer.valueOf(valor));
                    }
                }
            }
        }

        @Override
        public void carregarVagas() {
            Document documento = getDocumento();
            Elements dados = documento.select("div[title=\"Vagas\"]");
            if (!dados.isEmpty()) {
                dados = dados.first().select("span");
                if (!dados.isEmpty()) {
                    String valor = dados.first().text().trim();
                    if (StringUtils.isNumeric(valor)) {
                        setVagas(Integer.valueOf(valor));
                    }
                }
            }
        }

        @Override
        public void carregarSuites() {
        }

        @Override
        public void carregarArea() {
            Document documento = getDocumento();
            Elements dados = documento.select("div[title=\"Áreas\"]");
            if (!dados.isEmpty()) {
                dados = dados.first().select("span");
                if (!dados.isEmpty()) {
                    String valor = dados.first().text().trim().replaceAll("[^\\.0123456789]", "");
                    if (NumberUtils.isCreatable(valor)) {
                        setArea(Double.valueOf(valor));
                    }
                }
            }
        }

        @Override
        public void carregarAnunciante() {
            setAnunciante("ACRC");
        }

        @Override
        public void carregarCondominio() {
            Document documento = getDocumento();
            Elements dados = documento.select("div[title=\"Valores\"]");
            if (!dados.isEmpty()) {
                dados = dados.first().select("span span");
                if (!dados.isEmpty()) {
                    String valor = dados.get(1).text().replace("R$", "").replace(".", "").replace(",", ".").trim();
                    if (NumberUtils.isCreatable(valor)) {
                        setCondominio(Double.valueOf(valor));
                    }
                }
            }
        }

        @Override
        public void carregarEndereco() {
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new ACRC("casa");
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
