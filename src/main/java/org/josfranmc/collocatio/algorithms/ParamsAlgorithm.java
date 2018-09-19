package org.josfranmc.collocatio.algorithms;

import java.util.HashMap;
import java.util.List;

/**
 * Encapsula todos los posibles parámetros que se pueden utiizar para configurar los distintos algoritmos
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class ParamsAlgorithm {

	/**
	 * Tipo de algoritmo a utilizar
	 * @see AlgorithmType
	 */
	private AlgorithmType algorithmType = null;
	
	/**
	 * Tipos de dependencia de las tripletas en los que basar la búsqueda de colocaciones.<br>
	 * Si es null se analizan todas las trilpetas que se obtengan.<p>
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 */
	private List<String> triplesFilter = null;
	
	/**
	 * Ruta de los ficheros a procesar
	 */
	private String textsPathToProcess = null; 
	
	/**
	 * Número total de hilos a ejecutar
	 */
	private int totalThreads = Runtime.getRuntime().availableProcessors();
	
	/**
	 * Parámetros para al analizador de Stanford.<p>
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 */
	private HashMap<String, String> stanfordOptions = null;
	
	/**
	 * Parser de Stanford a utilizar.
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 */
	private String model = null;
	
	/**
	 * Si se debe guardar en base de datos o no
	 */
	private boolean saveInDB = true;
	
	/**
	 * Constante para ajustar la frecuencia de la probabilidad conjunta de una tripleta: <i>P(w1,rel,w2)</i>.<p>
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 */
	private double adjustedFrequency = 0.0;
	
	/**
	 * Nombre de la nueva base de datos a crear
	 */
	private String newDataBase = null;
	
	/**
	 * Descripcion de la nueva base de datos a crear
	 */
	private String newDataBaseDescription = null;
	
	
	/**
	 * Constructor por defecto.
	 */
	public ParamsAlgorithm() {
		this.stanfordOptions = new HashMap<String, String>();
	}

	/**
	 * @return el tipo de algoritmo utilizado
	 * @see AlgorithmType
	 */
	public AlgorithmType getAlgorithmType() {
		return algorithmType;
	}

	/**
	 * @param algorithmType el tipo de algoritmo ha utilizar
	 * @see AlgorithmType
	 */
	public void setAlgorithmType(AlgorithmType algorithmType) {
		this.algorithmType = algorithmType;
	}

	/**
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @return lista de tipos de dependencia de tripletas en los que basar la búsqueda de colocaciones
	 */
	public List<String> getTriplesFilter() {
		return triplesFilter;
	}

	/**
	 * Establece los tipos de dependencia de las tripletas en los que basar la búsqueda de colocaciones.<p>
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @param triplesFilter lista de tipos de dependencia
	 */
	public void setTriplesFilter(List<String> triplesFilter) {
		this.triplesFilter = triplesFilter;
	}
	
	/**
	 * Establece un tipo de dependencia en el que basar la búsqueda de colocaciones.<p>
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @param tripleFilter tipo de dependencia
	 */
	public void setTripleFilter(String tripleFilter) {
		this.triplesFilter.add(tripleFilter);
	}

	/**
	 * @return la ruta de la carpeta que contiene los ficheros a analizar
	 */
	public String getTextsPathToProcess() {
		return textsPathToProcess;
	}

	/**
	 * Establece la ruta de la carpeta que contiene los ficheros a analizar
	 * @param tetxsPathToProcess ruta de la carpeta que contiene los ficheros a analizar
	 */
	public void setTextsPathToProcess(String tetxsPathToProcess) {
		this.textsPathToProcess = tetxsPathToProcess;
	}

	/**
	 * @return el número total de hilos a ejecutar
	 */
	public int getTotalThreads() {
		return totalThreads;
	}

	/**
	 * @param totalThreads número total de hilos a ejecutar
	 */
	public void setTotalThreads(int totalThreads) {
		this.totalThreads = totalThreads;
	}

	/**
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @return las opciones de configuración del analizador de Stanford
	 */
	public HashMap<String, String> getStanfordOptions() {
		return stanfordOptions;
	}

	/**
	 * Establece las opciones de configuración del analizador de Stanford. Se agrupan en un hashmap en el que la clave de cada 
	 * elemento es el nombre del parámetro y su valor asociado el valor que se le asigana al parámetro.<br>
	 * Para parámetros que no necesitan un valor asociado se debe pasar null como valor.<p>
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @param stanfordOptions HashMap con las opciones de configuración y el valor que toman
	 */
	public void setStanfordOptions(HashMap<String, String> stanfordOptions) {
		this.stanfordOptions = stanfordOptions;
	}
	
	/**
	 * Añade un parámetro de configuración al analizador de Stanford.<p>
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @param key nombre del parámetro
	 * @param value valor que toma el parámetro, o null en caso de que el parámetro no necesite ningún valor
	 */
	public void setStanfordOption(String key, String value) {
		this.stanfordOptions.put(key, value);
	}

	/**
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @return la ruta del fichero que contiene el parser a cargar
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Establece el parser a utilizar con el software de Stanford.<p>
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @param model ruta del fichero que contiene el parser a cargar
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return <i>true</i> si se deben guardar los resultados obtenidos en base de datos, <i>false</i> en caso contrario
	 */
	public boolean isSaveInDB() {
		return saveInDB;
	}

	/**
	 * Indica si se deben guardar los resultados obtenidos en base de datos o no.
	 * @param saveInDB <i>true</i> si se deben guardar los resultados obtenidos en base de datos, <i>false</i> si no se quiere
	 */
	public void setSaveInDB(boolean saveInDB) {
		this.saveInDB = saveInDB;
	}

	/**
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @return el valor de la constante para ajustar la frecuencia de la probabilidad conjunta de una tripleta
	 */
	public double getAdjustedFrequency() {
		return adjustedFrequency;
	}

	/**
	 * Establece la constante para ajustar la frecuencia de la probabilidad conjunta de una tripleta: <i>P(w1,rel,w2)</i><p>
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION).
	 * @param adjustedFrequency valor de la constante de ajuste
	 */
	public void setAdjustedFrequency(double adjustedFrequency) {
		this.adjustedFrequency = adjustedFrequency;
	}

	/**
	 * @return el nombre para la nueva base de datos a crear
	 */
	public String getNewDataBase() {
		return newDataBase;
	}

	/**
	 * Establece un nombre identificativo para la nueva base de datos a crear.<br>El tamaño máximo del identifiador es de 14 caracteres.<br>
	 * Al nombre indicado se le añade el prefijo col_
	 * @param newDataBase nombre de la nueva base de datos a crear
	 */
	public void setNewDataBase(String newDataBase) {
		this.newDataBase = (newDataBase == null || newDataBase.isEmpty()) ? null : "col_" + newDataBase;
	}

	/**
	 * @return la descripcion de la nueva base de datos a crear
	 */
	public String getNewDataBaseDescription() {
		return newDataBaseDescription;
	}

	/**
	 * Establece una descripción para la nueva base de datos a crear.<p>Si no se establece ninguna se establecerá el texto:
	 * "Proceso lanzado el <i>fecha_lanzamiento</i>"
	 * @param newDataBaseDescription descripción de la nueva base de datos
	 */
	public void setNewDataBaseDescription(String newDataBaseDescription) {
		this.newDataBaseDescription = newDataBaseDescription;
	}
}
