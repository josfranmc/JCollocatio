package org.josfranmc.collocatio.triples;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;
import org.josfranmc.collocatio.util.ThreadFactoryBuilder;

import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Performs the extraction process of triples from a set of files.<p>
 * The process can be set up through parameters in the form of a <code>Properties</code> object. Available properties are:
 * <ul>
 * <li><i>parserModel</i>: parser model to be used, default edu/stanford/nlp/models/parser/nndep/english_UD.gz</li>
 * <li><i>taggerModel</i>: tagger model to be used, default edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger</li>
 * <li><i>textFiles</i>: path to the folder that contains the file to process </li>
 * <li><i>maxLenSentence</i>: maximum length of sentence to parser, default 70</li>
 * <li><i>totalThreads</i>: number of threads to run, default available processors minus one</li>
 * </ul>
 * If any of them is not set the default value is used, except <i>textFiles</i> property that is mandatory to indicate a value.<p>
 * The extraction process is carried out concurrently. For each file to be process a new thread is thrown through a <code>ParserThread</code> object.
 * All threads share a <code>TripleCollection</code> object where to save the triples obtained. This object is <i>thread-safe</i>.<p>
 * For parsing the texts the Stanford dependency parser is used.
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see TriplesCollection
 * @see ParserThread
 */
public class StanfordTriplesExtractor {

	private static final Logger log = Logger.getLogger(StanfordTriplesExtractor.class);

	public static final String MODEL_PARSER_DEPENDENCIES = "edu/stanford/nlp/models/parser/nndep/english_UD.gz";
	public static final String MODEL_TAGGER_DEPENDENCIES = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
	public static final int MAX_LEN_SENTENCE = 70;

	/**
	 * Configuration parameters
	 */
	private Properties properties;
	
	/**
	 * Default constructor. Sets default parameters:
	 * <ul>
	 * <li>Parser model: edu/stanford/nlp/models/parser/nndep/english_UD.gz</li>
	 * <li>Tagger model: edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger</li>
	 * <li>Maximum length of sentence to parser: 70</li>
	 * <li>Number of threads to run: available processors minus one</li>
	 * </ul>
	 */
	public StanfordTriplesExtractor() {
		this(new Properties());
	}
	
	/**
	 * Constructor that initializes the object with parameters.<br>
	 * Parameters are set as a <code>Properties</code> object. Valid properties are:
	 * <ul>
	 * <li><b>parserModel</b>: parser model to be used</li>
	 * <li><b>taggerModel</b>: tagger model to be used</li>
	 * <li><b>textFiles</b>: path to the folder that contains the file to process</li>
	 * <li><b>maxLenSentence</b>: maximum length of sentence to parser</li>
	 * <li><b>totalThreads</b>: number of threads to run</li>
	 * </ul>
	 * If any of them is not set a default value is used, except <b>textFiles</b> property that is mandatory.
	 * @param properties parameters
	 */
	public StanfordTriplesExtractor(Properties properties) {
		//this.triplesCollection = new TriplesCollection();
		this.properties = (Properties) properties.clone();
	}
	
	/**
	 * Sets the configuration parameters for the extract process.
	 * @param properties parameters
	 */
	public void setConfiguration(Properties properties) {
		this.properties = properties;
	}
	
	/**
	 * Returns a list with the paths of the files to be process.
	 * @return a list with the paths of the files to be process
	 */
	private List<String> getFilesToParse() {
		List<String> filesPath = new ArrayList<>();
		for (File file : new File(getFilesFolderToParser()).listFiles()) {
			if (file.isFile()) {
				filesPath.add(file.getPath());
			}
		}
		return filesPath;
	}

	/**
	 * Obtains an <code>ExecutorService</code> object to control the threads to be launched. The threads are configured with some properties.
	 * @return an <code>ExecutorService</code> object
	 * @see ThreadFactoryBuilder
	 */
	private ExecutorService getExecutorService() {
		ThreadFactory threadFactory = new ThreadFactoryBuilder()
				.setNameThread("ParserThread")
				.setDaemon(false)
				.setPriority(Thread.MAX_PRIORITY)
				.build();			
		return Executors.newFixedThreadPool(getTotalThreads(), threadFactory);
	}
	
	/**
	 * Runs the process to extract triples according to the properties established.<p>
	 * For each file to be process a new thread is thrown through a <code>ParserThread</code> object.
	 * These objects perform the parsing and extraction of triples, saving them in a <code>TripleCollection</code> object that is shared by all.
	 * @return the triples obtained as a <code>TripleCollection</code> object
	 * @see TriplesCollection
	 * @see ParserThread
	 * @see ParserThreadBuilder
	 */
	public TriplesCollection extractTriples() {
		int totalBooks = 0;
		Integer totalSentences = 0;
		long timeStart = 0, timeEnd = 0;
		TriplesCollection triplesCollection = new TriplesCollection();
		
		writeHeadLog();
		if (isFilesFolderToParser()) {
			try {	
				final ExecutorService executorService = getExecutorService();

				final MaxentTagger tagger = new MaxentTagger(getTaggerModel());
				final DependencyParser parser = DependencyParser.loadFromModelFile(getParserModel());

				List<ParserThread> parsers = new ArrayList<>();
				timeStart = System.currentTimeMillis();
				
			    for (String file : getFilesToParse()) {

				    ParserThread parserThread = new ParserThreadBuilder()
				    		.setTagger(tagger)
				    		.setParser(parser)
				    		.setFile(file)
				    		.setMaxLenSentence(getMaxLenSentence())
			    	        .setTriplesCollection(triplesCollection)
			    	        .build();
				    
				    parsers.add(parserThread);
				    totalBooks++;
			    }
			    
			    log.info("Parsing files... ");
			    List<Future<Integer>> futures = executorService.invokeAll(parsers);
			    
				for(Future<Integer> future : futures) {
					totalSentences += future.get();
				}
				
				executorService.shutdown();
				timeEnd = System.currentTimeMillis();
			    log.info("Read " + totalBooks + " files, " + totalSentences + " sentences");
			} catch (Exception e) {
				log.error(e);			
			}
		}
		log.info("Total triples: " + triplesCollection.getTotalTriples());
	    log.info("End triples extraction [" + getElapsedTime(timeStart, timeEnd) + "]");
	    return triplesCollection;
	}
	
	/**
	 * Returns the path to the folder that contains the files to be parse.
	 * @return the path to the folder that constains the files to be parse
	 */
	public String getFilesFolderToParser() {
		return this.properties.getProperty("textFiles");
	}

	/**
	 * Returns if there is a right value for the property about the folder path with the files to parse.
	 * @return <i>true</i> if there is a right value for the path, otherwise throws <code>IllegalArgumentException</code>
	 */
	private boolean isFilesFolderToParser() {
		if (getFilesFolderToParser() == null) {
			throw new IllegalArgumentException("The folder path with the files to process is not specified.");
		}
		if (! new File(getFilesFolderToParser()).exists()) {
			throw new IllegalArgumentException("The path of the folder with the files to process is wrong.");
		}
		return true;
	}
	
	/**
	 * Returns the parser model to be used.
	 * @return the parser model to be used
	 */
	public String getParserModel() {
		String model = this.properties.getProperty("parserModel");
		if (model == null || model.isEmpty()) {
			model = MODEL_PARSER_DEPENDENCIES;
		}
		return model;
	}
	
	/**
	 * Returns the tagger model to be used.
	 * @return the tagger model to be used
	 */
	public String getTaggerModel() {
		String model = this.properties.getProperty("taggerModel");
		if (model == null || model.isEmpty()) {
			model = MODEL_TAGGER_DEPENDENCIES;
		}
		return model;
	}
	
	/**
	 * Returns the maximum length of the sentences to be processed.<br>
	 * Default length is 70 words per sentence.
	 * @return the maximum length of the sentences to be processed
	 */
	public int getMaxLenSentence() {
		int length = 0;
		try {
			length = Integer.parseInt(properties.getProperty("maxLenSentence"));
		} catch (Exception e) {
			length = StanfordTriplesExtractor.MAX_LEN_SENTENCE;
		}
		return length;
	}
	
	/**
	 * Returns the number of threads to be runned.<br>
	 * If the property totalThreads is not specified or is wrong, then the number of available processors minus one will be used.
	 * @return the number of threads to be runned
	 */
	public int getTotalThreads() {
		boolean error = false;
		int threads = 1;
		
		try {
			threads = Integer.parseInt(properties.getProperty("totalThreads"));
		} catch (Exception e) {
			error = true;
		}
		
		if (threads <= 0 || error) {
			int availableProcessors = Runtime.getRuntime().availableProcessors();
			if (availableProcessors > 1) {
				threads = availableProcessors - 1;
			}
		}
		
		return threads;
	}
	
	private String getElapsedTime(long init, long end) {
		long elapsed =  Math.round(((double)end - (double)init) / 1000);
		long hours = (elapsed / 3600);
		long minutes = ((elapsed - hours*3600) / 60);
		long seconds = elapsed - (hours*3600 + minutes*60);
		return (hours + ":" + minutes + ":" + seconds);
	}
	
	private void writeHeadLog() {
		log.info("Starting triples extraction");
		log.info("Processing files from: " + getFilesFolderToParser());
		log.info("Parse sentences up to: " + getMaxLenSentence() + " words");
		log.info("Threads to run: " + getTotalThreads() );
	}
}
