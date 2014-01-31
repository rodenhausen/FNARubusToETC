import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;


public class Copy_2_of_DescriptionNodeCreator {

	private HashMap<String, String> distributionAbreviations = new HashMap<String, String>();
	private Pattern phenologyStartPattern = Pattern.compile("(Fl|fl|Fr|fr)\\s*\\.");
	private Pattern distributionStartPattern = Pattern.compile("(\\p{Alpha}+\\.)+");
	private HashSet<String> phenologyStartTokens;
	
	
	public Copy_2_of_DescriptionNodeCreator() {
		this.phenologyStartTokens = new HashSet<String>(Arrays.asList(new String[]{ "Fl", "fl", "Fr", "fr" }));
		distributionAbreviations = new HashMap<String, String>();
		distributionAbreviations.put("Fla.", "Florida");
		distributionAbreviations.put("Colo.", "Colorado");
		distributionAbreviations.put("B.C.", "British Columbia");
		distributionAbreviations.put("Calif.", "California");
		distributionAbreviations.put("Can.", "Canada");
		distributionAbreviations.put("C.B.", "Cape Breton I, Nova Scotia");
		distributionAbreviations.put("Centr.", "central");
		distributionAbreviations.put("Ci.", "Connecticut");
		distributionAbreviations.put("Ci. val.", "valley of the Connecticut River, New England");
		distributionAbreviations.put("D.C.", "District of Columbia");
		distributionAbreviations.put("Del.", "Delaware");
		distributionAbreviations.put("E.", "eastern");
		distributionAbreviations.put("Eastw.", "eastward");
		distributionAbreviations.put("Ga.", "Georgia");
		distributionAbreviations.put("Greenl.", "Greenland");
		//distributionAbreviations.put("(?<!("+Text2XML.ranks+"))\\s+I.", "island");
		distributionAbreviations.put("Id.", "island");
		distributionAbreviations.put("Ia.", "Iowa");
		distributionAbreviations.put("Ida.", "Ihado");
		distributionAbreviations.put("Ill.", "Illinois");
		distributionAbreviations.put("Ind.", "Indiana");
		distributionAbreviations.put("Kans.", "Kansas");
		distributionAbreviations.put("Ky.", "Kentucky");
		distributionAbreviations.put("L.", "lake");
		distributionAbreviations.put("L.I.", "Long Island, New York");
		distributionAbreviations.put("La.", "Louisiana");
		distributionAbreviations.put("Lab.", "Labrador");
		distributionAbreviations.put("L. Sup.", "Lake Superior");
		distributionAbreviations.put("Mackenz.", "Mackenzie District, Canada");
		distributionAbreviations.put("M.I.", "Magdalen Islands, Quebec, Canada");
		distributionAbreviations.put("Man.", "Manitoba");
		distributionAbreviations.put("Mass.", "Massachusetts");
		distributionAbreviations.put("Md.", "Maryland");
		distributionAbreviations.put("Me.", "Maine");
		distributionAbreviations.put("Mediterr. reg.", "Mediterranean region");
		distributionAbreviations.put("Mex.", "Mexico");
		distributionAbreviations.put("Mich.", "Michigan");
		distributionAbreviations.put("Minn.", "Minnesota");
		distributionAbreviations.put("Miss.", "Mississippi");
		distributionAbreviations.put("Mo.", "Missouri");
		distributionAbreviations.put("Mont.", "Montana");
		distributionAbreviations.put("Mt.", "mountain");
		distributionAbreviations.put("mts.", "mountains");
		distributionAbreviations.put("N.", "north");
		distributionAbreviations.put("N. Am.", "North America");
		distributionAbreviations.put("N.B.", "New Brunswick");
		distributionAbreviations.put("N.C.", "North Carolina");
		distributionAbreviations.put("N.D.", "North Dakota");
		distributionAbreviations.put("N.E.", "New England");
		distributionAbreviations.put("N.H.", "New Hampshire");
		distributionAbreviations.put("N.J.", "New Jersey");
		distributionAbreviations.put("N.M.", "New Mexico");
		distributionAbreviations.put("N.S.", "Nova Scotia");
		distributionAbreviations.put("N.Y.", "New York");
		distributionAbreviations.put("Ne.", "northeast");
		distributionAbreviations.put("Neb.", "Nebraska");
		distributionAbreviations.put("Nev.", "Nevada");
		distributionAbreviations.put("Nfld.", "Newfoundland");
		distributionAbreviations.put("Northw.", "northward");
		distributionAbreviations.put("Nw.", "northwest");
		distributionAbreviations.put("O.", "Ohio");
		distributionAbreviations.put("Okla.", "Oklahoma");
		distributionAbreviations.put("Ont.", "Ontario");
		distributionAbreviations.put("Oreg.", "Oregon");
		distributionAbreviations.put("Pa.", "Pennsylvania");
		distributionAbreviations.put("P.E.I.", "Prince Edward Island");
		distributionAbreviations.put("Pen.", "peninsula");
		distributionAbreviations.put("Que.", "province of Quebec");
		distributionAbreviations.put("R.", "river");
		distributionAbreviations.put("R.I.", "Rhode Island");
		distributionAbreviations.put("Reg.", "region");
		distributionAbreviations.put("S.", "south");
		distributionAbreviations.put("Se.", "south east");
		distributionAbreviations.put("S. Am.", "South America");
		distributionAbreviations.put("S.C.", "South Carolina");
		distributionAbreviations.put("S.D.", "South Dakota");
		distributionAbreviations.put("Sask.", "Saskatchewan");
		distributionAbreviations.put("Scotl.", "Scotland");
		distributionAbreviations.put("Tenn.", "Tennessee");
		distributionAbreviations.put("Tex.", "Texas");
		distributionAbreviations.put("Trop.", "tropical");
		distributionAbreviations.put("Ung.", "Ungava District, Canada");
		distributionAbreviations.put("Va.", "Virginia");
		distributionAbreviations.put("Val.", "valley");
		distributionAbreviations.put("Vt.", "Vermont");
		distributionAbreviations.put("W.", "western");
		distributionAbreviations.put("W.I.", "West Indies");
		distributionAbreviations.put("W. Va.", "West Virginia");
		distributionAbreviations.put("Wash.", "Washington");
		distributionAbreviations.put("Westw.", "westward");
		distributionAbreviations.put("Wisc.", "Wisconsin");
		distributionAbreviations.put("Wyo.", "Wyoming");
		distributionAbreviations.put("Yuk.", "Yukon Territory");
		distributionAbreviations.put("Afr.", "Africa");
		distributionAbreviations.put("Ala.", "Alabama");
		distributionAbreviations.put("Alta.", "Alberta");
		distributionAbreviations.put("Alt.", "Altitude");
		distributionAbreviations.put("Am.", "America");
		distributionAbreviations.put("Arct.", "Arctic");
		distributionAbreviations.put("Ariz.", "Arizona");
		distributionAbreviations.put("Ark.", "Arkansas");
		distributionAbreviations.put("Atl.", "Atlantic");
		distributionAbreviations.put("Austral.", "Australia");
		distributionAbreviations.put("Southw.", "southward");
		distributionAbreviations.put("Southwestw.", "southwestward");
		distributionAbreviations.put("St. P. et Miq.", "St. Pierre et Miquelon, south of Newfoundland");
		distributionAbreviations.put("Subtrop.", "subtropical");
		distributionAbreviations.put("Sw.", "southwest");
	}
	
	public List<Element> getNodes(List<Element> source) {
		List<Element> result = new LinkedList<Element>();
		for(Element element : source) {
			result.addAll(getDescriptions(element.getText()));
		}
		return result;
	}

	private List<Element> getDescriptions(String text) {
		List<Element> result = new LinkedList<Element>();
		String[] tokens = text.split("\\s+");
				
		StringBuilder habitatResult = new StringBuilder();
		StringBuilder distributionResult = new StringBuilder();
		StringBuilder phenologyResult = new StringBuilder();
		
		boolean habitat = true;
		boolean distri = false;
		boolean phenology = false;
		for(String token : tokens) {
			Matcher phenologyMatcher = phenologyStartPattern.matcher(token);
			
			Matcher distriMatcher = distributionStartPattern.matcher(token);
			if(!distri && habitat && (this.distributionAbreviations.containsKey(token))) { // || distriMatcher.matches())) {
				distri = true;
				habitat = false;
			}
			
			if(!phenology && distri && phenologyMatcher.matches()) {
				distri = false;
				phenology = true;
			}

			
			if(habitat)
				habitatResult.append(" " + token);
			if(distri)
				distributionResult.append(" " + token);
			if(phenology)
				phenologyResult.append(" " + token);
		}
		
		System.out.println("Habitat: " + habitatResult.toString().trim());
		System.out.println("Distribution: " + distributionResult.toString().trim());
		System.out.println("Phenology: " + phenologyResult.toString().trim());
		
		/*StringBuilder normalizedText = new StringBuilder();
		
		boolean isAbreviationPreviousToken = false;
		for(String token : tokens) {
			if(token.equals(".") && isAbreviationPreviousToken) {
				normalizedText.append(token);
			} else {
				normalizedText.append(" " + token);
			}
			isAbreviationPreviousToken = isAbreviation(token);
		}
		System.out.println(normalizedText.toString().trim());
		*/
		return result;
	}

	private boolean isAbreviation(String token) {
		return this.phenologyStartTokens.contains(token);
	}

	public static void main(String[] args) throws JDOMException, IOException {
		File input = new File("taxonomy_Rubus");
		for(File inputFile : input.listFiles()) {
			//System.out.println(inputFile.getName());
			SAXBuilder builder = new SAXBuilder();
			Document inputDoc = (Document) builder.build(inputFile);
			Element inputRoot = inputDoc.getRootElement();
			XPath xpath = XPath.newInstance("/treatment/habitat_elevation_distribution_or_ecology");
			List<Element> nodes = xpath.selectNodes(inputRoot);
			for(Element node : nodes) {
				System.out.println(node.getText());
			}
			
			Copy_2_of_DescriptionNodeCreator creator = new Copy_2_of_DescriptionNodeCreator();
			List<Element> result = creator.getNodes(nodes);
			
			System.out.println();
		}
	}
}
