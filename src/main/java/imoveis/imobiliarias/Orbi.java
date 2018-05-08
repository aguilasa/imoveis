package imoveis.imobiliarias;

import org.json.JSONArray;
import org.json.JSONObject;

import imoveis.base.IImovel;
import imoveis.base.ImobiliariaJson;

public class Orbi extends ImobiliariaJson {

    private static final String URLBASE = "https://www.imoveis-sc.com.br/blumenau/alugar/%s?page=%d";

    public Orbi(String tipo) {
        super(tipo);
    }

    @Override
    public int getPaginas() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getUrl(int pagina) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONArray getElementos(String url) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IImovel newImovel(JSONObject elemento) {
        // TODO Auto-generated method stub
        return null;
    }

}
