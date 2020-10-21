package properties.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jsoup.helper.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XPathHelper {

	private Document doc;

	public XPathHelper(Document doc) {
		this.doc = doc;
	}

	public XPathHelper(String url) throws IOException {
		this(JSoupUtils.getW3CDocument(url));
	}

	public List<String> list(String expression) throws XPathExpressionException {
		return list(expression, true);
	}

	public List<String> list(String expression, boolean addEmpty) throws XPathExpressionException {
		List<String> result = new ArrayList<>();
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList) xPath.evaluate(expression, doc.getDocumentElement(), XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); ++i) {
			String value = nodes.item(i).getNodeValue().trim();
			if (!addEmpty && StringUtil.isBlank(value)) {
				continue;
			}
			result.add(value);
		}
		return result;
	}

	public String text(String expression) throws XPathExpressionException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		return ((String) xPath.evaluate(expression, doc.getDocumentElement(), XPathConstants.STRING)).trim();
	}

}
