package properties.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import properties.base.IProperty;

public class Utils {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";
    private static final String valor_padrao_um = "R\\$\\s*([0-9]*\\.*[0-9]+,[0-9]{2})(\\s[\\wÀ-ú]+)*(\\scondomínio)";
    private static final String valor_padrao_dois = "(\\scondomínio:*)(\\s[\\wÀ-ú:\\.]*)*R\\$:*\\s*([0-9]*\\.*[0-9]+,[0-9]{2})";
    private static final String valor_padrao = "R\\$\\s*([0-9]*\\.*[0-9]+,[0-9]{2})";
    private static final Pattern padrao_um = Pattern.compile(valor_padrao_um, Pattern.CASE_INSENSITIVE);
    private static final Pattern padrao_dois = Pattern.compile(valor_padrao_dois, Pattern.CASE_INSENSITIVE);
    private static final Pattern padrao = Pattern.compile(valor_padrao, Pattern.CASE_INSENSITIVE);

    public static JSONObject imovelToJson(IProperty imovel) {
        JSONObject json = new JSONObject();
        json.put("nome", imovel.getName());
        json.put("url", imovel.getUrl());
        json.put("preco", imovel.getPrice());
        json.put("precoStr", imovel.getPriceStr());
        json.put("bairro", imovel.getDistrict());
        json.put("endereco", imovel.getAddress());
        json.put("anunciante", imovel.getAdvertiser());
        json.put("quartos", imovel.getRooms());
        json.put("vagas", imovel.getParkingSpaces());
        json.put("area", imovel.getArea());
        json.put("suites", imovel.getSuites());
        json.put("condominio", imovel.getCondominium());
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
                return textoParaReal(m.group(3));
            }
        }
        return 0;
    }

    public static double extrairValor(String texto) {
        Matcher m = padrao.matcher(texto);
        if (m.find()) {
            return textoParaReal(m.group(1));
        }
        return 0;
    }

    public static String slug(String str) {
        str = str.trim().toLowerCase();

        String from = "ãàáäâẽèéëêìíïîõòóöôùúüûñç·/_,:;";
        String to = "aaaaaeeeeeiiiiooooouuuunc------";

        for (int i = 0, l = from.length(); i < l; i++) {
            str = str.replaceAll("" + from.charAt(i), "" + to.charAt(i));
        }

        str = str.replaceAll("[^a-z0-9 -]", "").replaceAll(" ", "-").replaceAll("\\-+", "-");

        return str;
    }

    public static CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setUserAgent(USER_AGENT).build();
    }
    
    public static void main(String[] args) {
        System.out.println(extrairValor("Condomínio aproximadamente R$ 295"));
    }

}
