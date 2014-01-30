

import java.io.File;

public class XmlVolumeValidator extends AbstractXMLVolumeValidator {
	
	private File iplantXmlSchemaFile;

	/**
	 * @param xmlSchemaFile
	 */
	public XmlVolumeValidator(File iplantXmlSchemaFile) {
		this.iplantXmlSchemaFile = iplantXmlSchemaFile;
	}
	
	@Override
	public boolean validate(File directory) {
		if(!directory.isDirectory())
			return false;
		
		File[] files =  directory.listFiles();
		int total = files.length;
		
		boolean result = true;
		for(int i = 0; i<total; i++) {
			File file = files[i];
			System.out.println(file.getName());
			result &= validateXMLFileWithSchema(file, iplantXmlSchemaFile);
			
		}
		return result;
	}

}
