package imoveis.imobiliarias;

import static imoveis.utils.Utils.textoParaReal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

public class Alianca extends ImobiliariaHtml {

    private static final String URLBASE = "http://www.alianca.imb.br";
    private static final String PESQUISA = URLBASE + "/pesquisa-de-imoveis";

    public Alianca(String tipo) {
        super(tipo);
        setPost(true);
    }

    @Override
    public Elements getElementos() {
        Document document = getDocument(getUrl());
        return document.select("div#imoveis_grid div.imovel_bloco_voosuave");
    }

    @Override
    public String getUrl() {
        return PESQUISA;
    }

    @Override
    public int getPaginas() {
        Document document = getDocument();
        Elements elements = document.select("div#paginationTopContent div.pagination a");
        if (!elements.isEmpty()) {
            int p = 0;
            for (Element e : elements) {
                String valor = e.text().trim();
                if (NumberUtils.isCreatable(valor)) {
                    p = Integer.valueOf(valor);
                }
            }
            return p;
        }
        return 1;
    }

    @Override
    public Map<String, String> getPayload() {
        LinkedHashMap<String, String> payload = new LinkedHashMap<>();
        payload.put("codTB", "1");
        payload.put("codUF", "0");
        payload.put("codCid", "11");
        payload.put("codBai", "0");
        payload.put("codTP", tipo.equals("apartamento") ? "2" : "1");
        payload.put("qtdQuartos", "0");
        payload.put("qtdSuites", "0");
        payload.put("qtdSalas", "0");
        payload.put("qtdGaragens", "0");
        payload.put("codVal", "Min");
        payload.put("codValBK", "0");
        payload.put("codVal2", "Max");
        payload.put("codValbk2", "0");
        payload.put("searchtype", "2");
        payload.put("codOrd", "1");
        payload.put("pageIndex", String.valueOf(pagina - 1));
        return payload;
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
            Element link = elemento.select("a.localizacao").first();
            setNome(link.text().replace("Bairro:", "").trim());
            setUrl(URLBASE.concat(link.attr("href")));
            setBairro(getNome());
        }

        @Override
        public void carregarUrl() {
        }

        @Override
        public void carregarPreco() {
            setPrecoStr(elemento.select("span.imovel_valor").first().text().replace("R$", "").trim());
            try {
                setPreco(textoParaReal(getPrecoStr()));
            } catch (Exception e) {
                setPreco(0);
            }
        }

        @Override
        public void carregarBairro() {
        }

        @Override
        public void carregarQuartos() {
            Element dado = elemento.select("a.quartos").first();
            if (dado != null) {
                String valor = dado.text().trim();
                if (NumberUtils.isCreatable(valor)) {
                    setQuartos(Integer.valueOf(valor));
                }
            }
        }

        @Override
        public void carregarVagas() {
            Element dado = elemento.select("a.garagens").first();
            if (dado != null) {
                String valor = dado.text().trim();
                if (NumberUtils.isCreatable(valor)) {
                    setVagas(Integer.valueOf(valor));
                }
            }
        }

        @Override
        public void carregarSuites() {
            Element dado = elemento.select("a.suites").first();
            if (dado != null) {
                String valor = dado.text().trim();
                if (NumberUtils.isCreatable(valor)) {
                    setSuites(Integer.valueOf(valor));
                }
            }
        }

        @Override
        public void carregarArea() {
        }

        @Override
        public void carregarAnunciante() {
            setAnunciante("Alian�a");
        }

        @Override
        public void carregarCondominio() {
            Document documento = getDocumento();
            Elements dados = documento.select("div.imovel-detalhe-preco");
            for (Element dado : dados) {
                String valor = dado.text().toLowerCase().trim();
                if (valor.contains("condom�nio")) {
                    valor = valor.replace("condom�nio:", "").replace("r$", "").replace(".", "").replace(",", ".").trim();
                    setCondominio(Double.valueOf(valor));
                }
            }
        }

        @Override
        public void carregarEndereco() {
        }

    }

    public static void main(String[] args) {
        Imobiliaria imobiliaria = new Alianca("apartamento");
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
