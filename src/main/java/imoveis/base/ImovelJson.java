package imoveis.base;

import org.json.JSONObject;

public abstract class ImovelJson extends Imovel {

    protected JSONObject elemento;

    public ImovelJson(JSONObject elemento, String tipo) {
        this.elemento = elemento;
        this.setTipoImovel(TipoImovel.getEnum(tipo));
    }

}
