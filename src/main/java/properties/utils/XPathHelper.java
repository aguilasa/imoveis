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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

public class XPathHelper {

	private Document doc;

	public XPathHelper(Document doc) {
		this.doc = doc;
	}

	public XPathHelper(String url) throws IOException {
		this(JSoupUtils.getW3CDocument(url));
	}

	public List<String> list(String expression) {
		return list(expression, true);
	}

	public List<String> list(String expression, boolean addEmpty) {
		List<String> result = new ArrayList<>();
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		try {
			nodes = (NodeList) xPath.evaluate(expression, doc.getDocumentElement(), XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); ++i) {
				Node nodeItem = nodes.item(i);
//				if (nodeItem instanceof ElementNSImpl) {
//					ElementNSImpl el = (ElementNSImpl) nodeItem;
//
//					System.out.println(el);
//				}
				String value = nodeItem.getNodeValue() != null ? nodeItem.getNodeValue().trim()
						: nodeItem.getFirstChild().getNodeValue().trim();
				if (!addEmpty && StringUtil.isBlank(value)) {
					continue;
				}
				result.add(value);
			}
		} catch (XPathExpressionException e) {
			new Exception(e);
		}
		return result;
	}

	public String text(String expression) {
		String result = "";
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			result = ((String) xPath.evaluate(expression, doc.getDocumentElement(), XPathConstants.STRING)).trim();
		} catch (XPathExpressionException e) {
			new Exception(e);
		}
		return result;
	}

}
