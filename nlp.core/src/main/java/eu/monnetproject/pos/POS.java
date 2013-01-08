package eu.monnetproject.pos;

/**
 * Part-of-speech annotation
 */
 
 public interface POS {
 	 /**
 	  * Get the string value of this part-of-speech annotation
 	  */
 	 String getValue();
 	 
 	 /**
 	  * Get the containing POS tag set
 	  */
 	 POSSet getPOSSet();
 }
