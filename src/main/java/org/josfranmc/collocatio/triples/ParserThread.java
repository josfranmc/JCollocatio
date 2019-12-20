package org.josfranmc.collocatio.triples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * Performs the parse of a file in order to extract the dependencies as triples.<p>
 * To create an object of this class the <code>ParserThreadBuilder</code> object must be used.<p>
 * These objects implements the <code>Callable</code> interface so they can be executed concurrently. In particular, a <code>ParserThread</code> object
 * is created and runned as independent thread from a <code>StanfordTriplesExtractor</code> object.
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see ParserThreadBuilder
 * @see StanfordTriplesExtractor
 * @see TriplesCollection
 */
class ParserThread implements Callable<Integer> {

	//private static final Logger log = Logger.getLogger(ParserThread.class);

	private MaxentTagger tagger;
	
	private DependencyParser parser; 

	/**
	 * The path of the file to be process
	 */
	private String filePath;
	
	/**
	 * Maximum length of sentences to be processed
	 */
	private int maxLenSentence;
	
	/**
	 * Reference to the object that stores the triples obtained
	 * @see TriplesCollection
	 */
	private TriplesCollection triplesCollection;

	
	/**
	 * Default constructor.
	 */
	ParserThread() {
		
	}
	
	/**
	 * Parsing a file and performs the extraction and storage of the triples.
	 * @return the number of sentences parsed
	 * @see Triple
	 * @see TripleCollection
	 */
	@Override
	public Integer call() {
		Integer totalSentences = 0;
		List<Triple> triples = new ArrayList<>();
	    DocumentPreprocessor sentences = new DocumentPreprocessor(this.filePath);
	    
	    for (List<HasWord> sentence : sentences) {
	    	if (sentence.size() <= getMaxLenSentence()) {
	        	List<TaggedWord> tagged = tagger.tagSentence(sentence);
	        	GrammaticalStructure gs = parser.predict(tagged);
	           
	        	for (TypedDependency td : gs.typedDependenciesCCprocessed()) { 
	            	Triple triple = getTriple(td.toString());
	            	if (triple != null) {
	            		triples.add(triple);
	            	}
	            }
	        	totalSentences++;
	    	}
        }
	    triplesCollection.save(triples, getFileName());
		//log.debug("End thread " + Thread.currentThread().getName());
		//log.debug("  triples obtained " + triplesCollection.getTotalTriples());
		//log.debug("  map size " + triplesCollection.getTriples().size());
		return totalSentences;
	}
	
	/**
	 * Returns a <code>Triple</code> object according to the passed parameter-<p>
	 * The parameter represents a triple with the shape <i>dependency(head_word-position1, dependant_word-position2)</i>.<br>
	 * e.g: <i>det(car-2, the-1)</i><p>
	 * There may be dependencies with the shape <i>dependency:subtype</i>. In these cases, the main part only is taken (from the left to the colon).<br>
	 * e.g: <i>nmod:in(happy-3, town-6)</i>, -> <i>nmod</i> is taken<p>
	 * All words obtained are returned in lowercase.
	 * @param dependency dependency that represents the triple to obtain
	 * @return a <code>Triple</code> object
	 * @see Triple
	 */
	private Triple getTriple(String dependency) {
    	Triple triple = null;
    	// regular expression
    	// ((.*):.*|(.*))\\(  extract dependency
    	// (.*)-(\\d*),\\s    extract head word and position
    	// (.*)-(\\d*)\\)     extract dependent word and position
    	final String PATTERN = "((.*):.*|(.*))\\((.*)-(\\d*),\\s(.*)-(\\d*)\\)";
    
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(dependency);
		if (matcher.matches()) {
			triple = new Triple();
	    	// si la dependencia es del tipo dependencia:subtipo se obtiene en el grupo 2, si no en el grupo 3
	    	if (matcher.group(3) != null) {
	    		triple.setDependency(matcher.group(3));
	    	} else {
	    		triple.setDependency(matcher.group(2));
	    	}
	    	triple.setHead(matcher.group(4).toLowerCase());
	    	triple.setDependent(matcher.group(6).toLowerCase());
	    	int distance = Integer.valueOf(matcher.group(7)) - Integer.valueOf(matcher.group(5)) - 1;
	    	triple.setDistance(distance);
		}
		return triple;
	}

	/**
	 * Returns the object that stores the triples obtained.
	 * @return the object that stores the triples obtained
	 */
	public TriplesCollection getTriplesCollection() {
		return this.triplesCollection;
	}
	
	/**
	 * Sets the object that stores the triples obtained.
	 * @param triplesCollecion where the triples are stored
	 * @see TriplesCollection
	 */
	public void setTriplesCollection(TriplesCollection triplesCollecion) {
		this.triplesCollection = triplesCollecion;
	}
	
	/**
	 * Returns the path of the file to parser..
	 * @return the path of the file to parser.
	 */
	String getFile() {
		return filePath;
	}

	/**
	 * Sets the path of the file to parser.
	 * @param file file path
	 */
	void setFile(String file) {
		this.filePath = file;
	}
	
	/**
	 * Returns the name of the file to parser.
	 * @return the name of the file to parser.
	 */
	private String getFileName() {
		return filePath.substring(filePath.lastIndexOf(System.getProperty("file.separator"))+1, filePath.lastIndexOf("."));
	}

	/**
	 * Returns the path of the tagger model.
	 * @return the path of the tagger model
	 */
	public MaxentTagger getTagger() {
		return tagger;
	}

	/**
	 * Sets the tagger model to be used.
	 * @param tagger the path of the tagger model
	 */
	public void setTagger(MaxentTagger tagger) {
		this.tagger = tagger;
	}

	/**
	 * Returns the parser model to be used.
	 * @return the parser model to be used.
	 */
	public DependencyParser getParser() {
		return parser;
	}

	/**
	 * Sets the parser model to be used.
	 * @param parser the path of the parser model
	 */
	public void setParser(DependencyParser parser) {
		this.parser = parser;
	}
	
	/**
	 * Returns the maximum length of the sentences to be processed.
	 * @return the maximum length of the sentences to be processed
	 */
	public int getMaxLenSentence() {
		return this.maxLenSentence;
	}

	/**
	 * Sets the maximum length of the sentences to be processed
	 * @param maxLenSentence length of the sentences
	 */
	public void setMaxLenSentence(int maxLenSentence) {
		this.maxLenSentence = maxLenSentence;
	}
}
