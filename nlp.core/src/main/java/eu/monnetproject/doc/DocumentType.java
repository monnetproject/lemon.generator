package eu.monnetproject.doc;

public class DocumentType {

	//TXT,PDF,HTML;
        
        private String documentType;

        private DocumentType(String documentType) {
            this.documentType = documentType;
        }
        
        public static final DocumentType TXT = new DocumentType("txt");
        public static final DocumentType TXT_UTF8 = new DocumentType("txt-utf8");
        public static final DocumentType PDF = new DocumentType("pdf");
        public static final DocumentType HTML = new DocumentType("html");
        
        @Override
        public String toString() {
            return documentType;
        }
	
}
