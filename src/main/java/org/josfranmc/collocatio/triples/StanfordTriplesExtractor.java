package org.josfranmc.collocatio.triples;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.josfranmc.collocatio.corpus.CorpusBuilder;
import org.josfranmc.collocatio.util.ThreadFactoryBuilder;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.TreebankLanguagePack;

/**
 * Realiza el proceso de extracción y almacenamiento de las tripletas detectadas en un conjunto de ficheros. Este proceso es el primer paso a realizar en
 * la ejecución del algoritmo para el cálculo del valor de la información mutua de tripletas, por lo que los objetos de esta clase deben instanciarse
 * en las clases que encapsulen este algoritmo. Es responsabilidad de estas clases realizar la configuración adecuada de una instancia StanfordTriplesExtractor.<p>
 * El proceso de extracción de tripletas se realiza de forma concurrente mediante la ejecución de hilos encargados de analizar las oraciones que se van
 * obteniendo de los textos analizados. Cada hilo creado se ejecuta mediante un objeto de tipo ParserThread, que encapsula todo el proceso de análisis y
 * almacenamiento de tripletas. Todos los hilos lanzados comparten un objeto de tipo TripleCollection que encapsula la estructura de datos en la que se
 * guardan las tripletas obtenidas. Este objeto es <i>thread-safe</i> por lo que se accede al mismo de forma concurrente manteniendo la sincronización de los
 * hilos y la consistencia de los datos.<p>
 * Para el análsis de los textos se utiliza el analizador para procesamiento del lenguaje de Stanford. Por defecto, este software está configurado
 * para el procesamiento de textos en inglés, si bien es posible especificar otros modelos para otros diomas. También se pueden pasar opciones de
 * configuración al parser.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see TriplesCollection
 * @see ParserThread
 */
public class StanfordTriplesExtractor {

	private static final Logger log = Logger.getLogger(StanfordTriplesExtractor.class);
	
	/**
	 * Tamaño máximo por defecto de las oraciones a analizar
	 */
	public static final int DEFAULT_SENTENCE_MAX_LENGTH = 35;
	
	/**
	 * Parser a utilizar para realizar los análisis
	 */
	private String model;
	
	/**
	 * Parámetros pasados al analizador de Stanford
	 */
	private HashMap<String, String> stanfordOptions;
	
	/**
	 * Ruta de los ficheros a procesar
	 */
	private String tetxsPathToProcess;	
	
	/**
	 * Número total de hilos a ejecutar
	 */
	private int totalThreads;

	/**
	 * Guarda las tripletas obtenidas
	 * @see TriplesCollection
	 */
	private TriplesCollection triplesCollection;
	
	/**
	 * Contador para saber el número de oraciones leidas
	 */
	private long totalSentences = 0; 
	
	
	/**
	 * Constructor principal. Establece la siguiente configuración básica:
	 * <ul>
	 * <li>carga el parser para el idioma inglés (lo carga del jar <i>stanford-models</i>, que se encuentra en el classpath)</li>
	 * <li>establece el máximo de hilos a ejecutar según el número de procesadores existentes en el sistema</li>
	 * <li>establece las opciones para el parser de Stanford</li>
	 * <li>inicializa un objeto del tipo TriplesCollection para almacenar las tripletas obtenidas</li>
	 * </ul>
	 * @see TriplesCollection
	 */
	public StanfordTriplesExtractor() {
		setModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		//setModel("edu/stanford/nlp/models/lexparser/spanishPCFG.ser.gz");
		setTotalThreads(Runtime.getRuntime().availableProcessors());
		setTriplesCollection(new TriplesCollection());
		initOptionsFlags();
	}
	
	/**
	 * Inicializa las opciones por defecto del parser.
	 */
	private void initOptionsFlags() {
		this.stanfordOptions = new HashMap<String, String>();
		setStanfordOption("-maxLength", Integer.toString(StanfordTriplesExtractor.DEFAULT_SENTENCE_MAX_LENGTH));
		setStanfordOption("-retainTmpSubcategories", null);
	}
	
	/**
	 * Devuelve una lista con las rutas de los ficheros a procesar.
	 * @return lista de cadenas de texto que representan las rutas de los ficheros
	 */
	private List<String> getFilesToProcess() {
		String path = getTextsPathToProcess();
		List<String> filter = new ArrayList<String>();
		if (path != null) {
			File folder = new File(path);
			if (!folder.exists()) {
				log.error("La ruta de los ficheros no es correcta");
				filter = null;
			} else {
				for (File file : folder.listFiles()) {
					if (file.isFile()) {
						filter.add(file.getPath());
					}
				}
			}
		}
		return filter;
	}	
	
	/**
	 * Prepara los textos a procesar.
	 */
	private void createCorpus() {
    	log.info("Preparando corpus de análisis...");
		new CorpusBuilder(getTextsPathToProcess()).build();
		log.info("Corpus de análisis preparado");
	}

	/**
	 * Obtiene un ExecutorService para el control y procesamiento de los hilos a lanzar y se personalizan algunas características de estos.
	 * @return ExecutorService
	 * @see ThreadFactoryBuilder
	 */
	private ExecutorService getExecutorService() {
		ThreadFactory threadFactoryBuilder = new ThreadFactoryBuilder()
				.setNameThread("ParserThread")
				.setDaemon(false)
				.setPriority(Thread.MAX_PRIORITY)
				.build();			
		return Executors.newFixedThreadPool(getTotalThreads(), threadFactoryBuilder);
	}
	
	/**
	 * Obtiene el nombre de un fichero dada una ruta, el cual es el identificador del libro que contiene dicho fichero.
	 * @param bookFile ruta y nombre del fichero del que extraer su identificador
	 * @return identificador del libro que contiene el fichero
	 */
	private String getBookId(String bookFile) {
		return bookFile.substring(bookFile.lastIndexOf(System.getProperty("file.separator"))+1, bookFile.lastIndexOf("."));
	}
	
	/**
	 * Obtiene una lista de las oraciones que componen un documento.<p>
	 * Primero se prepara el archivo con DocumentPreprocessor. Se usan las opciones por defecto
	 * de esta clase por lo que se utiliza el espacio en blanco para "tokenizar" el texto (mediante PTBTokenizer).
	 * @param bookFile ruta del fichero a procesar
	 * @return lista de oraciones
	 * @see DocumentPreprocessor
	 * @see HasWord
	 */
	private List<List<? extends HasWord>> getSentences(String bookFile) {
		log.debug("Inicio extracción oraciones de " + bookFile);
		long total = 0;
		DocumentPreprocessor dp = new DocumentPreprocessor(bookFile);
		List<List<? extends HasWord>> sentences = new ArrayList<>();
	    for (List<HasWord> sentence : dp) {
	    	sentences.add(sentence);
	    	totalSentences++;
	    	total++;
	    }
	    log.debug("Oraciones obtenidas de " + bookFile + ": " + total);
	    return sentences;
	}
	
	/**
	 * Ejecuta el proceso de extracción de tripletas de los ficheros existentes en la localización previamente especificada. Los pasos que se siguen son los siguiente:
	 * <ol>
	 * <li>Preparar el corpus de textos a analizar</li>
	 * <li>Crear y configurar los objetos necesarios del software de Stanford: LexicalizedParser,TreebankLanguagePack, GrammaticalStructureFactory</li>
	 * <li>Por cada fichero a procesar se obtiene una lista de las oraciones que lo componen</li>
	 * <li>Para cada oración se lanza un subproceso hijo que lleva a cabo el análisis de la misma y realiza la extracción y almacenamiento de las tripletas detectadas.
	 * Cada hilo creado se ejecuta mediante un objeto de tipo ParserThread, que encapsula todo el proceso de análisis y almacenamiento.<br>
	 * Todos los hilos lanzados comparten un objeto de tipo TripleCollection que encapsula la estructura de datos en la que se guardan las tripletas
	 * obtenidas. Este objeto es thread-safe por lo que se accede al mismo de forma concurrente manteniendo la sincronización de los hilos y la consistencia de los datos</li>
	 * <li>Finalmente se devuelve la colección de tripletas obtenidas</li>
	 * </ol>
	 * @return colección de tripletas obtenidas
	 * @see TriplesCollection
	 * @see ParserThread
	 * @see ParserThreadBuilder
	 * @see LexicalizedParser
	 * @see TreebankLanguagePack
	 * @see GrammaticalStructureFactory
	 */
	public TriplesCollection extractTriples() {
		int totalBooks = 0;
		writeHeadLog();
		if (isTextsPathToProcess()) {
			try {	
				final ExecutorService executorService = getExecutorService();
	
				// prepara los ficheros a analizar
				createCorpus();
				
				// creamos los objetos necesarios del software de Stanford
				LexicalizedParser lp = LexicalizedParser.loadModel(getModel());
				lp.setOptionFlags(getStanfordOptionsAsStrings());
				TreebankLanguagePack tlp = lp.getOp().langpack();  // new PennTreebankLanguagePack();
			    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			    
			    log.info("Leyendo textos...");
			    // recorremos los ficheros a analizar
			    for (String bookFile : getFilesToProcess()) {
				    log.debug("Procesando " + bookFile);
				    totalBooks++;
				    // recorremos las oraciones que componen un texto
				    for (List<? extends HasWord> sentence : getSentences(bookFile)) { 
				    	// saltamos las oraciones que superan el tamaño máximo establecido
				    	// si no lo hacemos, aunque estas oraciones no se analizan aparecen en el conjunto de resultados como tripletas con tipo de dependencia "dep"
				    	if (sentence.size() <= getMaxLength()) {
				    		// se construye y lanza un hilo para cada oración a analizar
					    	ParserThread parserThread = new ParserThreadBuilder()
					    			.setLexicalizedParser(lp)
					    	        .setGrammaticalStructureFactory(gsf)
					    	        .setSentence(sentence)
					    	        .setTriplesCollection(this.triplesCollection)
					    	        .setBook(getBookId(bookFile))
					    	        .build();
					    	executorService.submit(parserThread);
				    	}
				    }
			    }
			    log.info("Leidos " + totalBooks + " archivos, " + this.totalSentences + " oraciones");
			    log.info("Analizando textos...");
			    awaitTerminationAfterShutdown(executorService);
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();			
			}
		}
		log.info("Tripletas obtenidas: " + triplesCollection.getTotalTriples());
	    log.info("Fin extracción tripletas " + getCurrentTime());
	    return this.triplesCollection;
	}

	/**
	 * Inicia la para de los hilos lanzados, quedando a la espera de la finalización de los mismos.
	 * @param threadPool ExecutorService que gestiona los hilos a finalizar
	 */
	private void awaitTerminationAfterShutdown(ExecutorService threadPool) {
		threadPool.shutdown();
	    try {
	        if (!threadPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.MINUTES)) {
	        	threadPool.shutdownNow();
	        }
	    } catch (InterruptedException ex) {
	    	threadPool.shutdownNow();
	        log.error("Error esperando finalización de hilos. Interrumpiendo " + Thread.currentThread().toString());
	        Thread.currentThread().interrupt();
	    }
	}
	
	/**
	 * Añade un parámetro de configuración del parser y el valor que toma dicho valor
	 * @param key nombre del parámetro
	 * @param value valor del parámetro
	 */
	public void setStanfordOption(String key, String value) {
		this.stanfordOptions.put(key, value);
	}
	
	/**
	 * Asigna un conjunto de parámetros de configuración del parser mediante un map del tipo parámetro-valor.<br>Se comprueba si se pasa la opción
	 * -retainTmpSubcategories y en caso de no hacerlo se añade dicha opción (el uso de este parámetro está recomendado por Stanford).<br>
	 * Se comprueba si se pasa la opción -maxLength y en caso de no hacerlo se añade dicha opción con el valor definido por defecto.
	 * @param stanfordOptions map con los parámetros
	 */
	public void setStanfordOptions(HashMap<String, String> stanfordOptions) {
		if (stanfordOptions == null) {
			throw new IllegalArgumentException("No se permite opciones de configuración a null");
		}
		if (stanfordOptions.get("-retainTmpSubcategories") == null) {
			stanfordOptions.put("-retainTmpSubcategories", null);
		}
		if (stanfordOptions.get("-maxLength") == null) {
			stanfordOptions.put("-maxLength", Integer.toString(StanfordTriplesExtractor.DEFAULT_SENTENCE_MAX_LENGTH));
		}
		this.stanfordOptions = stanfordOptions;
	}

	/**
	 * Devuelve los parámetros de configuración del parser.
	 * @return mapa de los parámetros con sus valores
	 */
	public HashMap<String, String> getStanfordOptions() {
		return this.stanfordOptions;
	}
	
	/**
	 * Devuelve el valor de un parámetro de configuración del parser.
	 * @param key nombre del parámetro cuyo valor se quiere recuperar
	 * @return valor del parámetro
	 */
	public String getStanfordOption(String key) {
		return this.stanfordOptions.get(key);
	}
	
	/**
	 * Devuelve un array de strings con los parámetros del parse y los valores de estos si los hay.
	 * Se utiliza para cargar los parámetros de un objeto LexicalizedParser.
	 * @return array de strings con los parámetros del parse
	 */
	private String[] getStanfordOptionsAsStrings() {
		List<String> listOptions = new ArrayList<String>();
		for (Map.Entry<String, String> entry : this.stanfordOptions.entrySet()) {
			listOptions.add(entry.getKey());
			if (entry.getValue() != null) {
				listOptions.add(entry.getValue());
			}
		}
		return  listOptions.toArray(new String[] {});
	}
	
	/**
	 * @return total de hilos hijos a ejecutar
	 */
	public int getTotalThreads() {
		return totalThreads;
	}

	/**
	 * Estable el número máximo de hilos a ejecutar.
	 * @param totalThreads número total de hilos
	 */
	public void setTotalThreads(int totalThreads) {
		this.totalThreads = totalThreads;
	}
	
	/**
	 * @return la ruta del fichero que contiene el parser que se utiliza
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Asigna el parser a utilizar para realizar el análisis.
	 * @param model parser a utilizar
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return ruta de la carpeta que contiene los ficheros a analizar
	 */
	public String getTextsPathToProcess() {
		return tetxsPathToProcess;
	}

	/**
	 * Asigna la ruta de los ficheros a analizar
	 * @param pathTexts ruta de la carpeta que contiene los ficheros a analizar
	 */
	public void setTextsPathToProcess(String pathTexts) {
		this.tetxsPathToProcess = pathTexts;
	}
	
	/**
	 * Devuelve <i>true</i> si se ha indicado una ruta válida de archivos a procesar.
	 */
	private boolean isTextsPathToProcess() {
		if (getTextsPathToProcess() == null) {
			throw new IllegalArgumentException("No se ha especificado la ruta de los archivos a procesar.");
		}
		if (! new File(getTextsPathToProcess()).exists()) {
			throw new IllegalArgumentException("La ruta de los archivos a procesar no existe.");
		}
		return true;
	}

	/**
	 * @return the maxLength
	 */
	public int getMaxLength() {
		return Integer.parseInt(this.stanfordOptions.get("-maxLength"));
	}
	
	/**
	 * @return el objeto TriplesCollection que almacena las tripletas obtenidas
	 */
	public TriplesCollection getTriplesCollection() {
		return this.triplesCollection;
	}	
	
	/**
	 * Asigna el objeto que encapsula la colección de tripletas obtenidas.
	 * @param triplesCollection tripletas obtenidas
	 * @see TriplesCollection
	 */
	public void setTriplesCollection(TriplesCollection triplesCollection) {
		this.triplesCollection = triplesCollection;
	}

	private String getCurrentTime() {
		Date date = new Date();
		DateFormat hourFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return hourFormat.format(date);
	}
	
	private void writeHeadLog() {
		log.info("Inicio extracción tripletas " + getCurrentTime());
		log.info("  Leyendo archivos desde " + getTextsPathToProcess());
		log.info("  Parser seleccionado: " + getModel());
		log.info("  Opciones para LexicalizedParser: " + Arrays.toString(getStanfordOptionsAsStrings()));
		log.info("  Hilos a ejecutar: " + getTotalThreads() );
	}
}
