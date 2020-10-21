package properties.base;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import properties.utils.XPathHelper;

public abstract class PropertyHtml extends Property {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";

	protected Element elemento;

	private Document documento = null;
	private XPathHelper xpath = null;

	public PropertyHtml(Element elemento, PropertyType type) {
		this.elemento = elemento;
		this.setPropertyType(type);
	}

	@Override
	public void load() {
		super.load();
		documento = null;
	}

	protected Document getDocumento() {
		if (documento == null) {
			try {
				documento = Jsoup.connect(getUrl()).timeout(0).userAgent(USER_AGENT).get();
			} catch (IOException e) {
				documento = new Document("");
			}
		}
		return documento;
	}

	protected XPathHelper xpath() {
		if (xpath == null) {
			try {
				xpath = new XPathHelper(getUrl());
			} catch (IOException e) {
				new Exception(e);
			}
		}
		return xpath;
	}

}
