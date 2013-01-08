package eu.monnetproject.tokenizer.latin;

import org.osgi.framework.*;
import eu.monnetproject.tokenizer.*;
import java.util.Hashtable;

public class LatinTokenizerActivator implements BundleActivator {
	
	
	public void start(BundleContext bc) {
		Tokenizer st = new LatinTokenizerImpl();
		bc.registerService(Tokenizer.class.getName(), st, new Hashtable());
	}
	
	public void stop(BundleContext bc) {
	}
}
