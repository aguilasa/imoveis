package imoveis.utils;

import org.json.JSONObject;

import imoveis.base.IImovel;

public class Utils {

    public static JSONObject imovelToJson(IImovel imovel) {
        JSONObject json = new JSONObject();
        json.put("nome", imovel.getNome());
        json.put("url", imovel.getUrl());
        json.put("preco", imovel.getPreco());
        json.put("precoStr", imovel.getPrecoStr());
        json.put("bairro", imovel.getBairro());
        json.put("endereco", imovel.getEndereco());
        json.put("anunciante", imovel.getAnunciante());
        json.put("quartos", imovel.getQuartos());
        json.put("vagas", imovel.getVagas());
        json.put("area", imovel.getArea());
        json.put("suites", imovel.getSuites());
        json.put("condominio", imovel.getCondominio());
        return json;
    }

    public static double textoParaReal(String texto) {
        return Double.valueOf(texto.replace(".", "").replace(",", "."));
    }
}
