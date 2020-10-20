package realstate.base;

public class Conversor {

    public static void main(String[] args) {
        String valor = "anunciante = elemento.select('a.imovel-anunciante')[0]['title'].strip()";
        valor = valor.replaceAll("'", "\"");
        valor = valor.replaceAll("\\[0\\]", "\\.first()");
        valor = valor.replaceAll("strip", "trim");
        valor = valor.replaceAll("get_text", "text");
        System.out.println(valor);
    }
}
