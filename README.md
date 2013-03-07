Lemon Lexicon Generator
=======================

The Lemon Lexicon Generator automatically generates a lexicon from an ontology. This project
consists of the following modules:

 * `stl`: The main generation code, using semantic, terminological and linguistic information
 * `stl.test`: Integration tests
 * `stl.web`: The web interface to the generator
 * `nlp.core`: Interfaces to basic NLP tools
 * `nlp.basic`: Basic NLP components, e.g., tokenizers
 * `nlp.stanford`: (Optional) Interface to [Stanford NLP Toolkit](http://nlp.stanford.edu/software/corenlp.shtml)

The generator can be executed as follows

    mvn exec:java -Dexec.mainClass=eu.monnetproject.lemon.generator.lela.Main \
        -Dexec.args="ontology.owl lemonLexicon.ttl"


