package org.josfranmc.collocatio.triples;

import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Creates and sets up a <code>ParserThread</code> object. Five properties may be setting:
 * <ul>
 * <li>parser model</li>
 * <li>tagger model</li>
 * <li><code>TriplesCollection</code> object where to store the triples obtained</li>
 * <li>file to parser</li>
 * <li>maximun length of the sentences to parse</li>
 * </ul>
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see ParserThread
 * @see TriplesCollection
 */
public class ParserThreadBuilder {

	private DependencyParser parser; 
	
	private MaxentTagger tagger;
	
	/**
	 * Reference to the object that stores the triples obtained
	 */
	private TriplesCollection triplesCollecion = null;
	
	/**
	 * Name of the file to process
	 */
	private String file;
    
	/**
	 * Maximum length of sentences to be processed
	 */
	private int maxLenSentence;
	
	/**
	 * Default constructor.
	 */
	public ParserThreadBuilder() {
		
	}
	
	/**
	 * Sets the tagger model to be used.
	 * @param tagger the path of the tagger model
	 * @return a reference to the <code>ParserThreadBuilder</code> object that call this method
	 */
    public ParserThreadBuilder setTagger(MaxentTagger tagger) {
		this.tagger = tagger;
		return this;
	}

    /**
     * Sets the parser model to be used.
     * @param parser the path of the parser model
     * @return a reference to the <code>ParserThreadBuilder</code> object that call this method
     */
	public ParserThreadBuilder setParser(DependencyParser parser) {
		this.parser = parser;
		return this;
	}

	/**
	 * Sets the path of the file to parser.
	 * @param file file path
	 * @return a reference to the <code>ParserThreadBuilder</code> object that call this method
	 */
	public ParserThreadBuilder setFile(String file) {
        this.file = file;
        return this;
    }
    
	/**
	 * Sets the maximum length of the sentences to be processed.
	 * @param maxLenSentence length of the sentences
	 * @return a reference to the <code>ParserThreadBuilder</code> object that call this method
	 */
	public ParserThreadBuilder setMaxLenSentence(int maxLenSentence) {
		this.maxLenSentence = maxLenSentence;
		return this;
	}
	
    /**
     * Sets the <code>TriplesCollection</code> object that stores the triples obtained.
     * @param triplesCollection the <code>TriplesCollection</code> object
     * @return referencia al propio objeto builder (this)
     * @see TriplesCollection
     */
    public ParserThreadBuilder setTriplesCollection(TriplesCollection triplesCollection) {
        this.triplesCollecion = triplesCollection;
        return this;
    }

    /**
     * Creates and sets up a <code>ParserThread</code> object with the parameters previously set.<br>
     * If there is any wrong parameter an <code>IllegalArgumentException</code> exception is thrown.
     * @return  a <code>ParserThread</code> object correctly configured
     * @see ParserThread
     * @throws IllegalArgumentException
     */
    public ParserThread build() {
    	if (parser == null) {
            throw new IllegalArgumentException("Parser is required");
    	}
    	if (tagger == null) {
            throw new IllegalArgumentException("POS tagger is required");
    	}
    	if (file == null) {
            throw new IllegalArgumentException("File name is required");
    	}
    	if (triplesCollecion == null) {
            throw new IllegalArgumentException("TriplesCollecion is required");
    	}
    	ParserThread pt = new ParserThread();    	
    	pt.setParser(parser);
    	pt.setTagger(tagger);
    	pt.setFile(file);
    	pt.setMaxLenSentence(maxLenSentence);
    	pt.setTriplesCollection(triplesCollecion);
        return pt;
    }
}