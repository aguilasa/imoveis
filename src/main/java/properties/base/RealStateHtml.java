package properties.base;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.Setter;

public abstract class RealStateHtml extends RealState {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";

	@Setter
	private boolean post = false;

	public RealStateHtml(PropertyType type, ActionType action) {
		super(type, action);
	}

	public void load() {
		int pages = getPages();

		for (int i = 1; i <= pages; i++) {
			setPage(i);
			Elements elementos = getElements();
			for (Element elemento : elementos) {
				IProperty imovel = newProperty(elemento);
				imovel.load();
				properties.add(imovel);
			}
		}
	}

	public Document getDocument() {
		return getDocument(getUrl());
	}

	public Document getDocument(String url) {
		try {
			Connection data = Jsoup.connect(url).timeout(0).userAgent(USER_AGENT).data(getPayload())
					.validateTLSCertificates(false);
			return post ? data.post() : data.get();
		} catch (IOException e) {
			e.printStackTrace();
			return new Document("");
		}
	}

	public abstract Elements getElements();

	public abstract Map<String, String> getPayload();

	public abstract IProperty newProperty(Element element);

}
