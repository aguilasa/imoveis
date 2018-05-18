package imoveis.base;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class ImobiliariaJson extends Imobiliaria {

    public ImobiliariaJson(String tipo) {
        super(tipo);
    }

    public void carregar() {
        int paginas = getPaginas();

        for (int i = 1; i <= paginas; i++) {
            setPagina(i);
            String url = getUrl();
            JSONArray elementos = getElementos(url);
            int total = elementos.length();
            for (int j = 0; j < total; j++) {
                JSONObject elemento = elementos.getJSONObject(j);
                IImovel imovel = newImovel(elemento);
                imovel.carregar();
                imoveis.add(imovel);
            }
        }
    }

    public abstract JSONArray getElementos(String url);

    public abstract IImovel newImovel(JSONObject elemento);

}
