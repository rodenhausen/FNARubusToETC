import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;


public class FileConverter {

	private List<Element> unusedElements = new LinkedList<Element>();
	
	public Document convert(File input, File output) throws JDOMException, IOException {
		System.out.println(input.getName());
		SAXBuilder builder = new SAXBuilder();
		Document inputDoc = (Document) builder.build(new InputStreamReader(new FileInputStream(input), "UTF8"));
		Element inputRoot = inputDoc.getRootElement();
		fillUnusedElements(inputRoot);
		
		Element outputRoot = new Element("treatment");
		Document outputDoc = new Document(outputRoot);
		outputDoc.setRootElement(outputRoot);

		createMeta(inputDoc, inputRoot, outputRoot);
		createTaxonIdentification(inputDoc, inputRoot, outputRoot);
		createNumber(inputDoc, inputRoot, outputRoot);
		createDescription(inputDoc, inputRoot, outputRoot);
		createDiscussion(inputDoc, inputRoot, outputRoot);
		createHabitatElevationDistriEcology(inputDoc, inputRoot, outputRoot);
		createPhenology(inputDoc, inputRoot, outputRoot);
		createKey(inputDoc, inputRoot, outputRoot);
		
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(outputDoc, new OutputStreamWriter(new FileOutputStream(output), "UTF8"));
		return outputDoc;
	}

	private void createProcessor(Document inputDoc, Element inputRoot,
			Element outputRoot) throws JDOMException {

	}

	private void createKey(Document inputDoc, Element inputRoot, Element outputRoot) throws JDOMException {
		XPath xpath = XPath.newInstance("/treatment/key");
		List<Element> nodes = xpath.selectNodes(inputDoc);
		addToOutput(outputRoot, nodes);
	}

	private void createPhenology(Document inputDoc, Element inputRoot, Element outputRoot) throws JDOMException {
		XPath xpath = XPath.newInstance("/treatment/phenology_info");
		List<Element >nodes = xpath.selectNodes(inputDoc);
		for(Element node : nodes) {
			Element textElement = node.getChild("text");
			if(textElement != null) {
				Element phenologyElement = new Element("description");
				phenologyElement.setText(textElement.getText());
				phenologyElement.setAttribute("type", "phenology");
				addToOutput(outputRoot, phenologyElement, node);
			}
		}
	}

	private void createDescription(Document inputDoc, Element inputRoot, Element outputRoot) throws JDOMException {
		XPath xpath = XPath.newInstance("/treatment/description");
		List<Element >nodes = xpath.selectNodes(inputDoc);
		if(nodes.isEmpty()) 
			System.out.println("empty ");
		for(Element node : nodes) {
			node.setAttribute("type", "morphology");
			addToOutput(outputRoot, nodes);
		}
	}

	private void createDiscussion(Document inputDoc, Element inputRoot,	Element outputRoot) throws JDOMException {
		XPath xpath = XPath.newInstance("/treatment/discussion");
		List<Element> nodes = xpath.selectNodes(inputDoc);
		addToOutput(outputRoot, nodes);
	}

	private void createHabitatElevationDistriEcology(Document inputDoc,	Element inputRoot, Element outputRoot) throws JDOMException {
		XPath xpath = XPath.newInstance("/treatment/habitat_elevation_distribution_or_ecology");
		List<Element> descriptionTypes = xpath.selectNodes(inputRoot);
		if(!descriptionTypes.isEmpty()) {
			DescriptionNodeCreator descriptionNodeCreator = new DescriptionNodeCreator();
			List<Element> descriptionNodes = descriptionNodeCreator.getNodes(descriptionTypes);
			addToOutput(outputRoot, descriptionNodes, descriptionTypes);
		}
		
		xpath = XPath.newInstance("/treatment/habitat");
		List<Element> nodes = xpath.selectNodes(inputDoc);
		for(Element node : nodes) {
			Element habitatElement = new Element("description");
			habitatElement.setText(node.getText());
			habitatElement.setAttribute("type", node.getName());
			addToOutput(outputRoot, habitatElement, node);
		}
		
		List<Element> children = inputRoot.getChildren();
		for(Element child : children) {
			Matcher matcher = Pattern.compile("(.*)_distribution").matcher(child.getName());
			if(matcher.matches()) {
				String distriType = matcher.group(1);
				Element distriElement = new Element("description");
				
				String text = getDistributionText(distriType, child.getText());
				
				distriElement.setText(text);
				distriElement.setAttribute("type", "distribution");
				addToOutput(outputRoot, distriElement, child);
			}
		}
		
		xpath = XPath.newInstance("/treatment/elevation");
		nodes = xpath.selectNodes(inputDoc);
		for(Element node : nodes) {
			Element elevationElement = new Element("description");
			elevationElement.setText(node.getText());
			elevationElement.setAttribute("type", node.getName());
			addToOutput(outputRoot, elevationElement, node);
		}
		
		xpath = XPath.newInstance("/treatment/ecology");
		nodes = xpath.selectNodes(inputDoc);
		for(Element node : nodes) {
			Element ecologyElement = new Element("description");
			ecologyElement.setText(node.getText());
			ecologyElement.setAttribute("type", node.getName());
			addToOutput(outputRoot, ecologyElement, node);
		}
	}

	private String getDistributionText(String distriType, String text) {
		String country =  getCountry(distriType);
		if(country.equals("United States") && text.matches(".*Mex.*")) {
			country = "Global";
		}
		Matcher mxMatcher = Pattern.compile("Mexico\\s*\\((.*)\\).*").matcher(text);
		Matcher caMatcher = Pattern.compile("Canada\\s*\\((.*)\\).*").matcher(text);
		Matcher usMatcher = Pattern.compile("United\\s*States\\s*\\((.*)\\).*").matcher(text);
		if(country.equals("Global")) {
			if(mxMatcher.matches()) {
				country = "Mexico";
				text = mxMatcher.group(1);
			}
			if(caMatcher.matches()) {
				country = "Canada";
				text = mxMatcher.group(1);
			}
			if(usMatcher.matches()) {
				country = "United States";
				text = mxMatcher.group(1);
			}
		}
		text = country + "(" + text + ")";
		return text;
	}

	private String getCountry(String abreviated) {
		abreviated = abreviated.toLowerCase();
		switch(abreviated) {
		case "global":
			return "Global";
		case "us": 
			return "United States";
		case "ca": 
			return "Canada";
		case "mx":
			return "Mexico";
		}
		return abreviated;
	}

	private void createNumber(Document inputDoc, Element inputRoot,	Element outputRoot) throws JDOMException {
		XPath xpath = XPath.newInstance("/treatment/number");
		Element node = (Element)xpath.selectSingleNode(inputDoc);
		if(node != null)
			addToOutput(outputRoot, node);
	}

	private void createTaxonIdentification(Document inputDoc, Element inputRoot, Element outputRoot) throws JDOMException {
		XPath xpath = XPath.newInstance("/treatment/TaxonIdentification/place_of_publication/other_info");
		List<Element> nodes = xpath.selectNodes(inputDoc);
		for(Element node : nodes) {
			node.setName("other_info_on_pub");
		}
		
		xpath = XPath.newInstance("/treatment/TaxonIdentification");
		nodes = xpath.selectNodes(inputDoc);
		for(Element node : nodes) {
			if(node.getAttribute("Status") != null) {
				node.setAttribute("status", node.getAttributeValue("Status"));
				node.removeAttribute("Status");
			}
			node.setName("taxon_identification");
		}
		addToOutput(outputRoot, nodes);
		
		xpath = XPath.newInstance("/treatment/taxon_identification");
		nodes = xpath.selectNodes(inputDoc);
		addToOutput(outputRoot, nodes);
	}

	private void createMeta(Document inputDoc, Element inputRoot, Element outputRoot) throws JDOMException {
		XPath xpath = XPath.newInstance("/treatment/meta");
		Element node = (Element)xpath.selectSingleNode(inputRoot);
		if(node != null) {
			Element source = node.getChild("source");
			source.setText("");
			Element author = new Element("author");
			author.setText("Gray");
			Element dateElement = new Element("date");
			dateElement.setText("1950");
			source.addContent(author);
			source.addContent(dateElement);

			xpath = XPath.newInstance("/treatment/meta/processed_by");
			Element processedByNode = (Element) xpath.selectSingleNode(inputRoot);
			if(processedByNode != null) {
				xpath = XPath.newInstance("/treatment/meta/processed_by/processor");
				List<Element> nodes = xpath.selectNodes(inputRoot);
				for(Element processorNode : nodes) {
					processorNode.setText("");
					processorNode.removeAttribute("process_type");
					dateElement = new Element("date");
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					dateElement.setText(dateFormat.format(date));
					processorNode.addContent(dateElement);
					Element software = new Element("software");
					software.setText("Text2XML");
					software.setAttribute("type", "format_conversion");
					software.setAttribute("version", "f0a80a8516a06e51224d01314403eb26d60f881d");
					processorNode.addContent(software);
					Element operator = new Element("operator");
					operator.setText("Hong, Cui");
					processorNode.addContent(operator);
				}
				this.addToOutput(processedByNode, nodes);
			}

			addToOutput(outputRoot, node);
			
		} else {
			Element meta = new Element("meta");
			Element source = new Element("source");
			
			xpath = XPath.newInstance("/treatment/author");
			List<Element> nodes = xpath.selectNodes(inputDoc);
			
			Element author = new Element("author");
			if(!nodes.isEmpty()) {
				String authorString = "";
				for(Element authorNode : nodes) {
					authorString += authorNode.getValue() + ", ";
				}
				author.setText(authorString.substring(0, authorString.length() - 2));
			} else {
				author.setText("N/A");
			}
			
			Element date = new Element("date");
			date.setText("N/A");
			meta.addContent(source);
			addToOutput(source, author, nodes);
			addToOutput(source, date);
			addToOutput(outputRoot, meta);
		}
	}

	private void addToOutput(Element parent, List<Element> nodes) {
		for(Element element : nodes) 
			addToOutput(parent, element);
	}

	private void addToOutput(Element parent, Element node) {
		this.addToOutput(parent, node, node);
	}
	
	private void addToOutput(Element parent, Element node, Element sourceNode) {
		this.addToOutput(parent, node, Arrays.asList(sourceNode));
	}
	
	private void addToOutput(Element parent, Element node, List<Element> sourceNodes) {
		node.detach();
		parent.addContent(node);
		for(Element sourceNode : sourceNodes) {
			this.removeFromUnusedElementsRecursively(sourceNode);
		}
	}
	
	private void addToOutput(Element parent, List<Element> nodes, List<Element> sourceNodes) {
		for(Element node : nodes) {
			node.detach();
			parent.addContent(node);
		}
		for(Element node : sourceNodes) 
			this.removeFromUnusedElementsRecursively(node);
	}
	
	private void removeFromUnusedElementsRecursively(Element node) {
		List<Element> children = node.getChildren();
		for(Element child : children) {
			this.removeFromUnusedElementsRecursively(child);
		}
		unusedElements.remove(node);
	}

	private void fillUnusedElements(Element node) {
		List<Element> children = node.getChildren();
		unusedElements.addAll(children);
		for(Element child : children) {
			this.fillUnusedElements(child);
		}
	}

	public List<Element> getUnusedElements() {
		return unusedElements;
	}
}
