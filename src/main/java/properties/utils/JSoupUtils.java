package properties.utils;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;

public class JSoupUtils {

	public static Connection getConnection(String url, String proxy) {
		Connection connection = Jsoup.connect(url).timeout(300000);
		if (!StringUtil.isBlank(proxy)) {
			String[] split = proxy.split(":");
			String host = split.length == 3 ? split[0].concat(":").concat(split[1]) : split[0];
			int port = split.length == 3 ? Integer.parseInt(split[2]) : Integer.parseInt(split[1]);
			connection = connection.proxy(host, port);
		}
		return connection;
	}

	public static Connection getConnection(String url) {
		return getConnection(url, "");
	}

	public static Document get(String url, String proxy) throws IOException {
		return getConnection(url, proxy).get();
	}

	public static Document get(String url) throws IOException {
		return get(url, "");
	}

	public static org.w3c.dom.Document getW3CDocument(String url) throws IOException {
		return new W3CDom().fromJsoup(get(url));
	}
}
