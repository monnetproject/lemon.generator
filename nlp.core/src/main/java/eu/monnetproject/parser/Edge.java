package eu.monnetproject.parser;

public interface Edge {
	String getName();

	public static final Edge UNNAMED = new Edge() {
		public String getName() { return "unnamed"; }
	};
}
