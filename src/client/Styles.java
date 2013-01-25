package client;

import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class Styles {
	
	public static HTMLEditorKit kit = new HTMLEditorKit();
	
	public static Document doc() {
		style();
		return kit.createDefaultDocument();
	}
	
	public static void style() {
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet.addRule("body {color: #222222; font-family: Helvetica; margin: 4px; }");
	}
	
}
