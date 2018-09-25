package org.josfranmc.collocatio.algorithms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.josfranmc.collocatio.triples.TriplesCollection;
import org.josfranmc.collocatio.util.ThreadFactoryBuilder;

/**
 * Implementa un algoritmo basado en el cálculo del valor denominado "información mutua". Los pasos de este algoritmo de definen en la clase
 * AbstractMutualInformationAlgorithm, de la cual se hereda, implementándose aquí lo dos pasos principales:
 * <ul>
 * <li>En el primero se obtiene una colección de tripletas mediante el uso de un objeto de tipo StanfordTriplesExtractor, el cual devuelve las
 * tripletas obtenidas encapsuladas en un objeto TriplesCollection</li>
 * <li>En el segundo se calcula el valor de información mutua de las tripletas obtenidas. El cálculo se hace de forma concurrente en dos fases. En la primera 
 * se obtienen los datos de las frecuencias necesarias para aplicar la fórmula y en el segundo se utilizan estos datos para realizar el cálculo.
 * </ul>
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see AbstractMutualInformationAlgorithm
 * @see StanfordTriplesExtractor
 * @see TriplesCollection
 */
public class MutualInformationAlgorithm extends AbstractMutualInformationAlgorithm {

	private static final Logger log = Logger.getLogger(MutualInformationAlgorithm.class);
	
	/**
	 * Ruta de los ficheros a procesar
	 */
	private String textsPathToProcess = null;
	
	/**
	 * Número total de hilos a ejecutar
	 */
	private int totalThreads;
	
	/**
	 * Parámetros para al analizador de Stanford
	 */
	private HashMap<String, String> stanfordOptions = null;
	
	/**
	 * Parser de Stanford a utilizar.
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 */
	private String model = null;
	
	/**
	 * Tipos de dependencia de las tripletas en los que basar la búsqueda de colocaciones
	 */
	private List<String> triplesFilter = null;
	
	/**
	 * Si se debe guardar en base de datos o no
	 */
	private boolean saveInDB = true;

	/**
	 * Constante para ajustar la frecuencia de la probabilidad conjunta de una tripleta  P(w1,rel,w2)
	 */
	private double adjustedFrequency = 0.0;
	
	/**
	 * Nombre de la base de datos a utilizar
	 */
	private String dataBaseName = null;

	
	/**
	 * Constructor por defecto.
	 */
	MutualInformationAlgorithm() {
		
	}
	
	/**
	 * Implementa el proceso de extracción de tripletas. Este proceso se lleva a acabo mediante un objeto StanfordTriplesExtractor.
	 * Se configura este objeto con los parámetros recibidos, se invoca al proceso de extracción y se obtiene un objeto TriplesCollection,
	 * el cual encapsula el conjunto de tripletas que se han obtenido
	 */
	@Override
	protected TriplesCollection extractTriples() {
		StanfordTriplesExtractor ste = new StanfordTriplesExtractor();
		ste.setModel(getModel());
		ste.setTextsPathToProcess(getTextsPathToProcess());
		ste.setTotalThreads(getTotalThreads());
		ste.setStanfordOptions(getStanfordOptions());
		return ste.extractTriples();
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
	@SuppressWarnings("null")
	@Override
	protected void calculateMutualInformation(TriplesCollection triplesCollection) {
		if (triplesCollection.getTotalTriples() > 0) {
			log.info("Inicio cálculo información mutua " + getCurrentTime());
			log.info("Guardar en base de datos: " + this.isSaveInDB());
			if (prepareDataBase()) {
				int totalThreads = 0;
				final long totalTriples = triplesCollection.getTotalTriples();
				ExecutorService executorServiceFreq = null;
				ExecutorService executorServiceCal = null;
				try {

					executorServiceFreq = Executors.newFixedThreadPool(getTotalThreads(), getThreadFactory("FregThread"));
					final ExecutorCompletionService<TriplesData> completionService = new ExecutorCompletionService<>(executorServiceFreq);
					log.info("Calculando datos de frecuencia...");
					for (String dependency : triplesCollection.getDependenciesCollection()) {
						if (isSelectedDependency(dependency)) {
							ExtractTriplesDataThread etdt = new ExtractTriplesDataThread(triplesCollection.getTriplesCollection(), dependency);
							completionService.submit(etdt);
							totalThreads++;
						}
					}
					executorServiceFreq.shutdown();
					//triplesCollection.getTriplesCollection().clear();
					triplesCollection = null;
					
					log.info("Calculando valor de información mutua para tripletas...");
					executorServiceCal = Executors.newFixedThreadPool(getTotalThreads(), getThreadFactory("CalThread"));
					for(int i = 0; i < totalThreads; i++) {
					    final Future<TriplesData> resultTask = completionService.take();
					    TriplesData data = null;
					    try {
					    	data = resultTask.get();
					    	data.setTotalTriples(totalTriples);
					    	data.setAdjustedFrequency(getAdjustedFrequency());
					    	
					    	log.info("Dependencia " + data.getDependency() + ": colocaciones " + data.getTotalElementsMap() + ", elementos a procesar " + data.getTotalTriplesByDependency());
					    	
					        CalculateMutualInformationThread cmit = new CalculateMutualInformationThread(data, getConnection(false));
					        executorServiceCal.execute(cmit);
					    } catch (RejectedExecutionException e) {     
					    	log.error("Tarea no aceptada para procesar datos de dependencia " + data.getDependency());
					    } catch (ExecutionException e) {
					        log.error("Incidencia " + e.getCause() + " al procesar datos dependencia " + data.getDependency());
					    } catch (InterruptedException ie) { 
					        log.error("Fin anómalo de hilo "  + " al procesar datos dependencia " + data.getDependency());
					        ie.printStackTrace();
					    }
					}
					
					executorServiceFreq.shutdown();
					executorServiceFreq = null;
				    awaitTerminationAfterShutdown(executorServiceCal);

				} catch (Exception e) {
					executorServiceFreq.shutdownNow();
					executorServiceCal.shutdownNow();
					log.error(e);
					e.printStackTrace();			
				}
			}
		} else {
			log.info("No hay datos que calcular");
		}
	}	

	/**
	 * @return un ThreadFactory para que personaliza los hilos a lanzar
	 */
	private ThreadFactory getThreadFactory(String name) {
		ThreadFactory threadFactoryBuilder = new ThreadFactoryBuilder()
				.setNameThread(name)
                .setDaemon(false)
                .setPriority(Thread.MAX_PRIORITY)
                .build();
		return threadFactoryBuilder;
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
		Connection connection = null;
		if (isSaveInDB()) {
			String dbName = this.getDataBaseName();
			if (dbName == null || dbName.equals(ConnectionFactory.DEFAULT_DB)) {
				connection = ConnectionFactory.getInstance().getConnection();
				if (connection != null) {
					deletePreviousContent(connection);
					result = true;
				} else {
					log.error("No se ha podido conectar a la base de datos por defecto.");
				}
			} else if (!dbName.equals(ConnectionFactory.DEFAULT_DB)) {
				connection = ConnectionFactory.getInstance(dbName).getConnection();
				if (connection != null) {
					result = true;
				} else {
					log.error("No se ha podido conectar a la base de datos " + dbName);
				}
			} else {
				throw new IllegalArgumentException("Error tratando nombre de base de datos");
			}
		}
		return result;
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

	/**
	 * @return los parámetros de configuración para el analizador de Stanford
	 */
	public HashMap<String, String> getStanfordOptions() {
		return stanfordOptions;
	}

	/**
	 * Establece los parámetros de configuración para el analizador de Stanford
	 * @param stanfordOptions colección de parámetros
	 */
	public void setStanfordOptions(HashMap<String, String> stanfordOptions) {
		this.stanfordOptions = stanfordOptions;
	}
	
	/**
	 * @return el parser de Stanford a utilizar
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Establece el parser a utilizar son el software de Stanford.
	 * @param model ruta del fichero que contiene el parser a cargar
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return la ruta de la carpeta que contiene los archivos a analizar
	 */
	public String getTextsPathToProcess() {
		return textsPathToProcess;
	}

	/**
	 * Establece la carpeta que contiene los archivos a analizar
	 * @param textsPathToProcess ruta de la carpeta que contiene los archivos a analizar
	 */
	public void setTextsPathToProcess(String textsPathToProcess) {
		this.textsPathToProcess = textsPathToProcess;
	}

	/**
	 * @return el número total de hilos a ejecutar
	 */
	public int getTotalThreads() {
		return totalThreads;
	}

	/**
	 * Establece el número total de hilos a ejecutar
	 * @param totalThreads número total de hilos a ejecutar
	 */
	public void setTotalThreads(int totalThreads) {
		this.totalThreads = totalThreads;
	}

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

	/**
	 * @return el valor de la constante para ajustar la frecuencia de la probabilidad conjunta de una tripleta
	 */
	public double getAdjustedFrequency() {
		return adjustedFrequency;
	}

	/**
	 * Establece la constante para ajustar la frecuencia de la probabilidad conjunta de una tripleta: <i>P(w1,rel,w2)</i>
	 * @param adjustedFrequency valor de la constante de ajuste
	 */
	public void setAdjustedFrequency(double adjustedFrequency) {
		this.adjustedFrequency = adjustedFrequency;
	}

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
		Date date = new Date();
		DateFormat hourFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return hourFormat.format(date);
	}
}
