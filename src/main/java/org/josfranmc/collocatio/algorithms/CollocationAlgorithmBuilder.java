package org.josfranmc.collocatio.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.josfranmc.collocatio.algorithms.OtherAlgorithm;
import org.josfranmc.collocatio.db.DataBaseBuilder;

/**
 * Crea y configura un algoritmo para la búsqueda de colocaciones. Los objetos creados de este tipo implementan la interfaz ICollocationAlgorithm
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see ICollocationAlgorithm
 */
public class CollocationAlgorithmBuilder {
	
	/**
	 * Parámetros de configuración pasados por el cliente
	 * @see paramsAlgorithm
	 */
	private ParamsAlgorithm paramsAlgorithm;
	
	
	/**
	 * Asigna los parámetros de configuración de un algoritmo, los cuales se encapsulan en un objeto de tipo ParamsAlgorithm
	 * @param paramsAlgorithm parámetros de configuración
	 * @return Referencia al objeto constructor (this)
	 * @see paramsAlgorithm
	 */
	public CollocationAlgorithmBuilder setAlgorithmConfig(ParamsAlgorithm paramsAlgorithm) {
		this.paramsAlgorithm = paramsAlgorithm;
		return this;
	}
	
	/**
	 * Construye y configura un objeto algoritmo del tipo ICollocationAlgorithm. La configuración se realiza en base a los parámetros
	 * que se han tenido que establecer previamente
	 * fijados previamente.
	 * @return Un objeto algoritmo del tipo ICollocationAlgorithm
	 * @see ICollocationAlgorithm
	 */
	public ICollocationAlgorithm build() {
		if (getParamsAlgorithm() == null) {
			throw new IllegalArgumentException("No se han establecido parámetros de configuración");
		}
		if (getAlgorithmType() == null) {
			throw new IllegalArgumentException("Debe especificarse tipo de algoritmo a utilizar");
		}
		if (getTotalThreads() < 1) {
			throw new IllegalArgumentException("Debe especificarse un número de hilos a utilizar mayor que cero");
		}
		
		ICollocationAlgorithm collocationAlgorithm = null;
		
		if (getAlgorithmType() == AlgorithmType.MUTUAL_INFORMATION) {
			collocationAlgorithm = getMutualInformationAlgorithm();			
		} else if (getAlgorithmType() == AlgorithmType.ANOTHER_ALGORITHM) {
			collocationAlgorithm = new OtherAlgorithm();
		}
		return collocationAlgorithm;
	}
	
	/**
	 * Construye y configura un objeto que encapsula el algoritmo MUTUAL_INFORMATION
	 * @return objeto de tipo MutualInformationAlgorithm
	 * @see MutualInformationAlgorithm
	 * @see AlgorithmType
	 */
	private MutualInformationAlgorithm getMutualInformationAlgorithm() {
		if (getTextsPathToProcess() == null || getTextsPathToProcess().isEmpty()) {
			throw new IllegalArgumentException("Debe especificarse la ruta de los ficheros a analizar");
		}
		
		MutualInformationAlgorithm mia = new MutualInformationAlgorithm();
		
		mia.setTextsPathToProcess(getTextsPathToProcess());
		mia.setTotalThreads(getTotalThreads());
		mia.setSaveInDB(getSaveInDB());
		mia.setAdjustedFrequency(getAdjustedFrequency());
		mia.setStanfordOptions(getStanfordOptions());
		
		//si no se indica nada se carga el parser para idioma inglés
		if (getModel() == null) {
			mia.setModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		} else {
			mia.setModel(getModel());
		}
		
		//si no se indica filtro de tripletas la lista de filtrado estará vacía
		if (getTriplesFilter() == null) {
			mia.setTriplesFilter(new ArrayList<String>());
		} else {
			mia.setTriplesFilter(getTriplesFilter());
		}

		// si se ha indicado una nueva base de datos se usará esta
		if (getNewDataBase() != null) {
			DataBaseBuilder dbb = new DataBaseBuilder(getNewDataBase(), getNewDataBaseDescription());
			dbb.createNewDB();	
			mia.setDataBaseName(getNewDataBase());
			// aunque se haya indicado no guardar en db se guardará
			mia.setSaveInDB(true);
		}
		return mia;
	}
	
	/**
	 * @return el conjunto de parámetros de configuración
	 * @see ParamsAlgorithm
	 */
	private ParamsAlgorithm getParamsAlgorithm() {
		return paramsAlgorithm;
	}

	/**
	 * @return tipo de algoritmo a instanciar
	 * @see AlgorithmType
	 */
	private AlgorithmType getAlgorithmType() {
		return this.paramsAlgorithm.getAlgorithmType();
	}
	
	/**
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @return tipo de tripleta a usar como filtro en el algoritmo de información mutua
	 */
	private List<String> getTriplesFilter() {
		return this.paramsAlgorithm.getTriplesFilter();
	}

	/**
	 * @return ruta a los ficheros a analizar para extraer tripletas
	 */
	private String getTextsPathToProcess() {
		return this.paramsAlgorithm.getTextsPathToProcess();
	}

	/**
	 * @return total de hilos a utilizar para parallelizar el proceso
	 */
	private int getTotalThreads() {
		return this.paramsAlgorithm.getTotalThreads();
	}

	/**
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @return opciones de configurtación para el analizador de Stanford
	 */
	private HashMap<String, String> getStanfordOptions() {
		return this.paramsAlgorithm.getStanfordOptions();
	}
	
	/**
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @return la ruta del fichero que contiene el parser a cargar
	 */
	private String getModel() {
		return this.paramsAlgorithm.getModel();
	}
	
	/**
	 * @return <i>true</i> si se debe guardar el proceso es base de datos, <i>false</i> en caso contrario
	 */
	private boolean getSaveInDB() {
		return this.paramsAlgorithm.isSaveInDB();
	}
	
	/**
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @return el valor de la constante para ajustar la frecuencia de la probabilidad conjunta de una tripleta
	 */
	private double getAdjustedFrequency() {
		return this.paramsAlgorithm.getAdjustedFrequency();
	}
	
	/**
	 * @return el nombre identificativo de la nueva base de datos a crear
	 */
	private String getNewDataBase() {
		return this.paramsAlgorithm.getNewDataBase();
	}
	
	/**
	 * @return la descripción de la base de datos a crear
	 */
	private String getNewDataBaseDescription() {
		return this.paramsAlgorithm.getNewDataBaseDescription();
	}
}
