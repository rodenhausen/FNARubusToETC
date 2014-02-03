import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Converter {

	/**
	 * @param args
	 * @throws JDOMException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws JDOMException, IOException {
		File input = new File("transformed");
		for(File file : input.listFiles()) {
			FileConverter fileConverter = new FileConverter();
			fileConverter.convert(file, new File("output" + File.separator + file.getName()));
			
			List<Element> unusedElements = fileConverter.getUnusedElements();
			
			//System.out.println("unused elements in file " + file.getName());
			for(Element element : unusedElements) {
				System.out.println(element.getName());
			}
			System.out.println("-------------------------------");
		}
	}

}
