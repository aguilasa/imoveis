package imoveis.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import imoveis.base.IImovel;

public class Utils {

    private static final String valor_padrao_um = "R\\$\\s*([0-9]*\\.*[0-9]+,[0-9]{2})(\\s[\\w�-�]+)*(\\scondom�nio)";
    private static final String valor_padrao_dois = "(\\scondom�nio)(\\s[\\w�-�:\\.]*)*R\\$\\s*([0-9]*\\.*[0-9]+,[0-9]{2})";
    private static final Pattern padrao_um = Pattern.compile(valor_padrao_um, Pattern.CASE_INSENSITIVE);
    private static final Pattern padrao_dois = Pattern.compile(valor_padrao_dois, Pattern.CASE_INSENSITIVE);

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

    public static double buscarCondominio(String texto) {
        Matcher m = padrao_um.matcher(texto);
        if (m.find()) {
            return textoParaReal(m.group(1));
        } else {
            m = padrao_dois.matcher(texto);
            if (m.find()) {
                for (int i = 0; i < m.groupCount(); i++) {
                    System.out.println(i + " - " + m.group(i));
                }
            }
        }
        return 0;
    }
}
