package eu.monnetproject.doc;

public class FormatNotSupportedException extends Exception {

	String filePath = "";
	String fileExtension = "";
	
	public FormatNotSupportedException(String filePath, String fileExtension) {
		super();
		this.filePath = filePath;
		this.fileExtension = fileExtension;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void printStackTrace() {
		System.out.println("The file format \'"+fileExtension+"\' of the file"+filePath+" is not supported");
		//this.printStackTrace();
	}
	
}
