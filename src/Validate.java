import java.io.File;


public class Validate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		XmlVolumeValidator validator = new XmlVolumeValidator(new File("semanticMarkupInput.xsd"));
		validator.validate(new File("output"));
	}

}
