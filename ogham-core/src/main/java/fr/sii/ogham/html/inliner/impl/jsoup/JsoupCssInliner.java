package fr.sii.ogham.html.inliner.impl.jsoup;

import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import fr.sii.ogham.html.inliner.CssInliner;
import fr.sii.ogham.html.inliner.ExternalCss;

public class JsoupCssInliner implements CssInliner {
	private static final String HREF_ATTR = "href";
	private static final String TRUE_VALUE = "true";
	private static final String SKIP_INLINE = "data-skip-inline";
	private static final String TEMP_STYLE_ATTR = "data-cssstyle";
	private static final String STYLE_ATTR = "style";
	private static final String STYLE_TAG = "style";
	private static final String CSS_LINKS_SELECTOR = "link[rel*=\"stylesheet\"], link[type=\"text/css\"], link[href$=\".css\"]";

	@Override
	public String inline(String htmlContent, List<ExternalCss> cssContents) {
		Document doc = Jsoup.parse(htmlContent);

		internStyles(doc, cssContents);
		String stylesheet = fetchStyles(doc);
		extractStyles(doc, stylesheet);
		applyStyles(doc);

		return doc.outerHtml();
	}

	/**
	 * Applies the styles to a <code>data-cssstyle</code> attribute. This is
	 * because the styles need to be applied sequentially, but before the
	 * <code>style</code> defined for the element inline.
	 *
	 * @param doc
	 *            the html document
	 */
	private static void extractStyles(Document doc, String stylesheet) {
		String trimmedStylesheet = stylesheet.replaceAll("\n", "").replaceAll("/\\*.*?\\*/", "").replaceAll(" +", " ");
		String styleRules = trimmedStylesheet.trim();
		String delims = "{}";
		StringTokenizer st = new StringTokenizer(styleRules, delims);
		while (st.countTokens() > 1) {
			String selector = st.nextToken();
			String properties = st.nextToken();
			Elements selectedElements = doc.select(selector.trim());
			for (Element selElem : selectedElements) {
				String oldProperties = selElem.attr(TEMP_STYLE_ATTR);
				selElem.attr(TEMP_STYLE_ATTR, oldProperties.length() > 0 ? concatenateProperties(oldProperties, properties) : properties);
			}
		}
	}

	/**
	 * Replace link tags with style tags in order to keep the same inclusion
	 * order
	 *
	 * @param doc
	 *            the html document
	 * @param cssContents
	 *            the list of external css files with their content
	 */
	private static void internStyles(Document doc, List<ExternalCss> cssContents) {
		Elements els = doc.select(CSS_LINKS_SELECTOR);
		for (Element e : els) {
			if (!TRUE_VALUE.equals(e.attr(SKIP_INLINE))) {
				String path = e.attr(HREF_ATTR);
				Element style = new Element(Tag.valueOf(STYLE_TAG), "");
				style.appendChild(new DataNode(getCss(cssContents, path)));
				e.replaceWith(style);
			}
		}
	}

	private static String getCss(List<ExternalCss> cssContents, String path) {
		for (ExternalCss css : cssContents) {
			if (css.getPath().getOriginalPath().contains(path)) {
				return css.getContent();
			}
		}
		throw new IllegalStateException("The css with path " + path + " doesn't exist in the list of css contents");
	}

	/**
	 * Generates a stylesheet from an html document
	 *
	 * @param doc
	 *            the html document
	 * @return a string representing the stylesheet.
	 */
	private static String fetchStyles(Document doc) {
		Elements els = doc.select(STYLE_TAG);
		StringBuilder styles = new StringBuilder();
		for (Element e : els) {
			if (!TRUE_VALUE.equals(e.attr(SKIP_INLINE))) {
				styles.append(e.data());
				e.remove();
			}
		}
		return styles.toString();
	}

	/**
	 * Transfers styles from the <code>data-cssstyle</code> attribute to the
	 * <code>style</code> attribute.
	 *
	 * @param doc
	 *            the html document
	 */
	private static void applyStyles(Document doc) {
		Elements allStyledElements = doc.getElementsByAttribute(TEMP_STYLE_ATTR);

		for (Element e : allStyledElements) {
			String newStyle = e.attr(TEMP_STYLE_ATTR);
			String oldStyle = e.attr(STYLE_ATTR);
			e.attr(STYLE_ATTR, (newStyle + "; " + oldStyle).replaceAll(";+", ";").trim());
			e.removeAttr(TEMP_STYLE_ATTR);
		}
	}

	private static String concatenateProperties(String oldProp, String newProp) {
		String prop = oldProp;
		if (!prop.endsWith(";")) {
			prop += ";";
		}
		return prop.trim() + " " + newProp.trim() + ";";
	}
}
