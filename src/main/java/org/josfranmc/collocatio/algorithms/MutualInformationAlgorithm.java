package org.josfranmc.collocatio.algorithms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.josfranmc.collocatio.db.ConnectionFactory;
import org.josfranmc.collocatio.triples.StanfordTriplesExtractor;
import org.josfranmc.collocatio.triples.Triple;
import org.josfranmc.collocatio.triples.TripleData;
import org.josfranmc.collocatio.triples.TripleEvents;
import org.josfranmc.collocatio.triples.TriplesCollection;
import org.josfranmc.collocatio.util.ThreadFactoryBuilder;

/**
 * Implements an algorithm to obtain collocations by calculating the value of the mutual information.<p>
 * This algorithm consists of two main steps:
 * <ul>
 * <li>First, get a collection of dependencies in the form of triples using a <code>StanfordTriplesExtractor</code> object. This object
 * returns the triples obtained as a <code>TriplesCollection</code> object.
 * </li>
 * <li>Second, calculate the mutual information value for each triple in the <code>TriplesCollection</code> object.
 * </ul>
 * You must use the <code>CollocationAlgorithmBuilder</code> class in order to create a <code>MutualInformationAlgorithm</code> object.
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see CollocationAlgorithmBuilder
 * @see StanfordTriplesExtractor
 * @see TriplesCollection
 */
public class MutualInformationAlgorithm implements ICollocationAlgorithm {

	private static final Logger log = Logger.getLogger(MutualInformationAlgorithm.class);
	
	/**
	 * Types of dependencies to consider in the calculation (if null, all the dependencies are considered)
	 */
	private List<String> triplesFilter = null;
	
	/**
	 * Si se debe guardar en base de datos o no
	 */
	private boolean saveInDB = true;

	/**
	 * Nombre de la base de datos a utilizar
	 */
	private String dataBaseName = null;

	
	
	
	/**
	 * Setting parameters
	 */
	//private Properties properties;
	
	private int totalThreads;
	private double adjustedFrequency;
	
	/**
	 * To extract dependencies
	 */
	private StanfordTriplesExtractor triplesExtractor;
	
	/**
	 * Default constructor.
	 */
	MutualInformationAlgorithm() {
		
	}
	
	/**
	 * Sets parameters to configure the algorithm.
	 * @param properties parameters 
	 */
	public void setConfiguration(Properties properties) {
		this.triplesExtractor = new StanfordTriplesExtractor();
		this.triplesExtractor.setConfiguration(properties);
		
		setAdjustedFrequency(properties.getProperty("adjustedFrequency"));
	}
	
	/**
	 * Runs the algorithm
	 */
	@Override
	public void findCollocations() {
		if (this.triplesExtractor != null) {
			log.info("USING MUTUAL INFORMATION ALGORITHM " + getCurrentTime());
			TriplesCollection triples = triplesExtractor.extractTriples();
			calculateMutualInformation(triples);
			log.info("END MUTUAL INFORMATION ALGORITHM " + getCurrentTime());
		} else {
			log.info("MUTUAL INFORMATION ALGORITHM NO SETTING" + getCurrentTime());
		}
	}

	// ¿SON NECESARIOS ESTOS GETS?
	
	// hacer un get que devuelva el objeto Properties y ya está
	
	/**
	 * Returns the parser model to be used.
	 * @return the parser model to be used
	 */
	public String getParserModel() {
		return triplesExtractor.getParserModel();
	}
	
	/**
	 * Returns the tagger model to be used.
	 * @return the tagger model to be used
	 */
	public String getTaggerModel() {
		return triplesExtractor.getTaggerModel();
	}
	
	/**
	 * Returns the path to the folder that contains the files to be parse.
	 * @return the path to the folder that contains the files to be parse
	 */
	public String getFilesFolderToParser() {
		return triplesExtractor.getFilesFolderToParser();
	}
	
	/**
	 * Returns  the number of threads to be runned.
	 * @return  the number of threads to be runned
	 */
	public int getTotalThreads() {
		return this.totalThreads;
	}

	/**
	 *  Implementa el proceso de cálculo del valor "información mutua" de las tripletas obtenidas previamente. El cálculo se hace de forma
	 *  concurrente en dos fases.<p> 
	 *  <ul>
	 *  <li>En primer lugar, para cada tipo de dependencia se ejecuta un hilo encargado de obtener los datos de frecuencias asociados a ese tipo de
	 *  dependencia, los cuales son necesarios para la aplicaión de la fórmula. La ejecución de cada hilo se realiza mediante un objeto de tipo ExtractTriplesInfoThread.</li>
	 *  <li>Una vez lanzados todos los hilos necesarios se van recogiendo los resultados devueltos por los mismos según var terminando las tareas. Los
	 *  resultados son devueltos en objetos de tipo TriplesData. Conforme se van obteniendo son pasados a un objeto CalculateMutualInformationThread 
	 *  encargado de realizar los cálculos del valor de información mutua, siendo ejecutados de forma paralela</li>
	 *  </ul>
	 */
	//@SuppressWarnings("null")
	//@Override
	protected void calculateMutualInformation(TriplesCollection triplesCollection) {
		if (triplesCollection.getTotalTriples() > 0) {
			log.info("Calculate mutual information value " + getCurrentTime());
			log.info("Guardar en base de datos: " + this.isSaveInDB());
			if (prepareDataBase()) {
				int totalThreads = 0;
				//final long totalTriples = triplesCollection.getTotalTriples();
				//ExecutorService executorServiceFreq = null;
				//ExecutorService executorService = null;
				try {
//					final ExecutorService executorServiceFreq = getExecutorService("FreqThread");
//					//final ExecutorCompletionService<TriplesData> completionService = new ExecutorCompletionService<>(executorServiceFreq);
//					
//					log.info("Calculating frequency data...");
//					List<ExtractTriplesDataThread> extracter = new ArrayList<>();
//					
//					for (String dependency : triplesCollection.getDependencies()) {
//						if (isSelectedDependency(dependency)) {
//							ExtractTriplesDataThread etdt = new ExtractTriplesDataThread(triplesCollection.getTriples(), dependency);
//							//completionService.submit(etdt);
//							extracter.add(etdt);
//							totalThreads++;
//						}
//					}
//					
//					List<Future<TriplesData>> futures = executorServiceFreq.invokeAll(extracter);
//					
//					executorServiceFreq.shutdown();
//					triplesCollection = null;
					
					log.info("Calculating mutual information value for triples...");
					final ExecutorService executorService = getExecutorService("CalculateThread");
					List<Future<List<Collocation>>> collocationsFuture = new ArrayList<>();
					
					Set<String> dependencies = triplesCollection.getDependencies();
					
					for (String dependency : dependencies) {
					
						//Iterator<TripleData> it = triplesCollection.iterator(dependency);
						//while (it.hasNext()) {
							//TripleData tripleData = it.next();
							CalculateMutualInformationThread cmit = new CalculateMutualInformationThreadBuilder()
									.setTriplesCollection(triplesCollection)
									.setDependency(dependency)
									//.setTripleData(tripleData)
									//.setTotalDependencies(triplesCollection.getTotalTriples())
									.setAdjustedFrequency(getAdjustedFrequency())
									.build();
							//CalculateMutualInformationThread cmit = new CalculateMutualInformationThread(tripleData, getAdjustedFrequency(), triplesCollection.getTotalTriples(), getConnection(false));
							Future<List<Collocation>> future = executorService.submit(cmit);
							collocationsFuture.add(future);
						//}
					
					}
					
					triplesCollection = null; 
					
					for (Future<List<Collocation>> future : collocationsFuture) {
					//for(int i = 0; i < totalThreads; i++) {
						List<Collocation> collocation = future.get();
//					    TriplesData data = null;
//					    try {
//					    	data = resultTask.get();
//					    	data.setTotalTriples(totalTriples);
//					    	data.setAdjustedFrequency(getAdjustedFrequency());
//					    	
//					    	log.info("Dependencia " + data.getDependency() + ": colocaciones " + data.getTotalElementsMap() + ", elementos a procesar " + data.getTotalTriplesByDependency());
//					    	
//					        CalculateMutualInformationThread cmit = new CalculateMutualInformationThread(data, getConnection(false));
//					        executorServiceCal.execute(cmit);
//					    } catch (RejectedExecutionException e) {     
//					    	log.error("Tarea no aceptada para procesar datos de dependencia " + data.getDependency());
//					    } catch (ExecutionException e) {
//					        log.error("Incidencia " + e.getCause() + " al procesar datos dependencia " + data.getDependency());
//					    } catch (InterruptedException ie) { 
//					        log.error("Fin anómalo de hilo "  + " al procesar datos dependencia " + data.getDependency());
//					        ie.printStackTrace();
//					    }
					}
					
//					executorServiceFreq.shutdown();
//					//executorServiceFreq = null;
//				    awaitTerminationAfterShutdown(executorServiceCal);

				} catch (Exception e) {
					//executorServiceFreq.shutdownNow();
					//executorService.shutdownNow();
					log.error(e);
					e.printStackTrace();			
				}
			}
		} else {
			log.info("No hay datos que calcular");
		}
	}	

	/**
	 * @return el valor de la constante para ajustar la frecuencia de la probabilidad conjunta de una tripleta
	 */
	public double getAdjustedFrequency() {		
		return this.adjustedFrequency;
	}
	
	private void setTotalThreads(String totalThreads) {
		boolean error = false;
		int threads = 1;
		
		try {
			threads = Integer.parseInt(totalThreads);
		} catch (Exception e) {
			error = true;
		}
		
		if (threads <= 0 || error) {
			int availableProcessors = Runtime.getRuntime().availableProcessors();
			if (availableProcessors > 1) {
				threads = availableProcessors - 1;
			}
		}
		this.totalThreads = threads;
	}
	
	private void setAdjustedFrequency(String adjustedFrequency) {
		try {
			this.adjustedFrequency = Double.parseDouble(adjustedFrequency);
			if (this.adjustedFrequency > 1 || this.adjustedFrequency < 0) {
				this.adjustedFrequency = 1;
			}
		} catch (Exception e) {	
			this.adjustedFrequency = 1;
		}		
	}
	
	/**
	 * Obtains an <code>ExecutorService</code> object to control the threads to be launched. The threads are configured with some properties.
	 * @return an <code>ExecutorService</code> object
	 * @see ThreadFactoryBuilder
	 */
	private ExecutorService getExecutorService(String name) {
		ThreadFactory threadFactory = new ThreadFactoryBuilder()
				.setNameThread(name)
                .setDaemon(false)
                .setPriority(Thread.MAX_PRIORITY)
                .build();
		return Executors.newFixedThreadPool(getTotalThreads(), threadFactory);
	}
	
	/**
	 * Termina de forma controlada la ejecución de los hilos lanzados mediante el ExecutorService que controla
     * los hilos de tipo CalculateMutualInformationThread.<p>
     * Inicia la parada de los hilos lanzados y queda a la espera de que terminen de ejecutarse.
	 * @param threadPoolCal ExecutorService que controla hilos de tipo CalculateMutualInformationThread
	 */
	private void awaitTerminationAfterShutdown(ExecutorService threadPoolCal) {
		threadPoolCal.shutdown();
	    try {
	        if (!threadPoolCal.awaitTermination(Integer.MAX_VALUE, TimeUnit.MINUTES)) {
	        	threadPoolCal.shutdownNow();
	        }
	    } catch (InterruptedException ex) {
	    	threadPoolCal.shutdownNow();
	        log.error("Error esperando finalización de hilos. Interrumpiendo " + Thread.currentThread().toString());
	        Thread.currentThread().interrupt();
	    }
	}
	
	/**
	 * Indica si un tipo de dependencia debe ser procesado o no. Se comprueba si el tipo de dependencia pasado está en la lista de dependencias
	 * en la que basar la búsqueda.<br>
	 * Si la lista de consulta no contiene elementos se entiende que todas las dependencias son válidas
	 * @param dependency tipo de dependencia a consultar
	 * @return <i>true</i> si la dependencia debe ser procesada, <i>false</i> en caso contrario
	 */
	private boolean isSelectedDependency(String dependency) {
		boolean value = false;
		List<String> tripleFilter = getTriplesFilter();
		if (tripleFilter == null || tripleFilter.size() == 0) {
			value = true;
		} else {
			for (String dep : tripleFilter) {
				if (dep.equals(dependency)) {
					value = true;
					break;
				}
			}
		}
		return value;
	}
	
	/**
	 * Prepara la base de datos a utilizar. Si se usa la base de datos por defecto se borra su contenido.
	 * @return <i>true</i> si se ha podido conectar a la base de datos y prepararla, <i>false</i> en caso contrario
	 */
	private boolean prepareDataBase() {
		boolean result = false;
		return true;
//		Connection connection = null;
//		if (isSaveInDB()) {
//			String dbName = this.getDataBaseName();
//			if (dbName == null || dbName.equals(ConnectionFactory.DEFAULT_DB)) {
//				connection = ConnectionFactory.getInstance().getConnection();
//				if (connection != null) {
//					deletePreviousContent(connection);
//					result = true;
//				} else {
//					log.error("No se ha podido conectar a la base de datos por defecto.");
//				}
//			} else if (!dbName.equals(ConnectionFactory.DEFAULT_DB)) {
//				connection = ConnectionFactory.getInstance(dbName).getConnection();
//				if (connection != null) {
//					result = true;
//				} else {
//					log.error("No se ha podido conectar a la base de datos " + dbName);
//				}
//			} else {
//				throw new IllegalArgumentException("Error tratando nombre de base de datos");
//			}
//		}
//		return result;
	}
	
	/**
	 * Borra el contenido de las tablas de la base de datos por defecto (col_default)
	 * @param connection conexión a la base de datos
	 */
	private void deletePreviousContent(Connection connection) {
		PreparedStatement pstatement = null;
		try {
			log.info("Borrando contenido previo...");
			pstatement = connection.prepareStatement("DELETE FROM col_aparece");
			pstatement.executeUpdate();
			pstatement.close();
			pstatement = connection.prepareStatement("DELETE FROM col_collocatio");
			pstatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstatement != null) {
				try {
					pstatement.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
		}
	}
	
	/**
	 * Obtiene una conexión a la base de datos que se esté usando.<p>
	 * Permite especificar si se quiere un comportamiento transaccional desactivando la opción de autocommit.
	 * @param autocommit <i>true</i> si se quiere hacer commit tras cada inserción en la base de datos, <i>false</i> en caso contrario
	 * @return conexión a la base de datos, null si no se ha podido obtener
	 */
	private Connection getConnection(boolean autocommit) {
		Connection connection = null;
		if (this.isSaveInDB()) { 
			connection = ConnectionFactory.getInstance(getDataBaseName()).getConnection();
			if (connection != null) {
				try {
					connection.setAutoCommit(autocommit);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				log.error("No se ha podido obtener conexión a la base de datos");
			}
		}
		return connection;
	}
	
	/**
	 * @return los tipos de dependencia que se quieren analizar
	 */
	public List<String> getTriplesFilter() {
		return triplesFilter;
	}

	/**
	 * Establece los tipos de dependencia que se quieren analizar. De esta forma se filtran las tripletas a analizar en función de su tipo de dependencia
	 * @param list lista de tipo de dependencias
	 */
	public void setTriplesFilter(List<String> list) {
		this.triplesFilter = list;
	}
	

//	
//	public void setParserModel(String model) {
//		this.parserModel = model;
//	}
//	

//	
//	public void setTaggerModel(String model) {
//		this.taggerModel = model;
//	}
//

//
//	/**
//	 * Establece la carpeta que contiene los archivos a analizar
//	 * @param filesFolderToProcess ruta de la carpeta que contiene los archivos a analizar
//	 */
//	public void setFilesFolderToProcess(String filesFolderToProcess) {
//		this.filesFolderToProcess = filesFolderToProcess;
//	}

//	/**
//	 * Sets the number of threads to be runned.
//	 * @param totalThreads the number of threads
//	 */
//	public void setTotalThreads(int totalThreads) {
//		this.totalThreads = totalThreads;
//	}

	/**
	 * @return <i>true</i> si se deben guardar los resultados obtenidos en base de datos, <i>false</i> en caso contrario
	 */
	public boolean isSaveInDB() {
		return saveInDB;
	}

	/**
	 * Indica si se deben guardar los resultados obtenidos en base de datos o no
	 * @param saveInDB <i>true</i> si se deben guardar los resultados obtenidos en base de datos, <i>false</i> si no se quiere
	 */
	public void setSaveInDB(boolean saveInDB) {
		this.saveInDB = saveInDB;
	}

//	/**
//	 * Establece la constante para ajustar la frecuencia de la probabilidad conjunta de una tripleta: <i>P(w1,rel,w2)</i>
//	 * @param adjustedFrequency valor de la constante de ajuste
//	 */
//	public void setAdjustedFrequency(double adjustedFrequency) {
//		this.adjustedFrequency = adjustedFrequency;
//	}
	
//	/**
//	 * Returns the maximum length of the sentences to be processed.
//	 * @return the maximum length of the sentences to be processed
//	 */
//	public int getMaxLenSentence() {
//		return maxLenSentence;
//	}
//
//	/**
//	 * Sets the maximum length of the sentences to be processed.
//	 * @param maxLenSentence length of the sentences
//	 */
//	public void setMaxLenSentence(int maxLenSentence) {
//		this.maxLenSentence = maxLenSentence;
//	}

	/**
	 * @return el nombre de la base de datos a utilizar
	 */
	public String getDataBaseName() {
		return (dataBaseName == null || dataBaseName.isEmpty()) ? ConnectionFactory.DEFAULT_DB : dataBaseName;
	}

	/**
	 * Establece el nombre de la base de datos a utilizar.
	 * @param dataBaseName nombre de la base de datos
	 */
	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}

	private String getCurrentTime() {
		return (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(new Date());
	}
}
