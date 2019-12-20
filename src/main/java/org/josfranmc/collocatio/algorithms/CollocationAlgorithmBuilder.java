package org.josfranmc.collocatio.algorithms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.io.IOUtils;

/**
 * Creates and sets up an object that allows you to execute an algorithm in order to search collocations.<br>
 * This objects implements the <code>ICollocationAlgorithm</code> interface.
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see ICollocationAlgorithm
 */
public class CollocationAlgorithmBuilder {

	/**
	 * Setting parameters
	 */
	private Properties properties;
	
	
	/**
	 * Default constructor.
	 */
	public CollocationAlgorithmBuilder() {
		properties = new Properties();
	}
	
	/**
	 * Sets configuration properties for an algorithm.
	 * @param properties a <code>Properties</code> object
	 * @return a reference to the <code>CollocationAlgorithmBuilder</code> object that call this method
	 */
	public CollocationAlgorithmBuilder setConfiguration(Properties properties) {
		if (properties == null) {
			throw new IllegalArgumentException("No properties for setting algorithm");
		}
		this.properties.putAll(properties);
		return this;
	}
	
	/**
	 * Sets configuration for an algorithm from a properties file.
	 * @param file file name
	 * @return a reference to the <code>CollocationAlgorithmBuilder</code> object that call this method
	 */
	public CollocationAlgorithmBuilder setConfigurationFromFile(String file) {
		try {
			properties.load(IOUtils.readerFromString(file));
		} catch (IOException e) {
			throw new IllegalArgumentException("Error loading file " + file);
		}
		return this;
	}
	
	/**
	 * Returns the current configuration properties.
	 * @return the current configuration properties.
	 */
	public Properties getConfiguration() {
		return this.properties;
	}
	
	/**
	 * Builds and sets up an object that allows you to run an algorithm to get collocations.<br>
	 * This kind of objects implements the <code>ICollocationAlgorithm</code> interface.
	 * @return an <code>ICollocationAlgorithm</code> object	
	 * @see ICollocationAlgorithm
	 */
	public ICollocationAlgorithm build() {

		if (!isAlgorithmType(properties.getProperty("type"))) {
			throw new IllegalArgumentException("Algorithm type has not been established");
		}
		if (!isTextFilesProperty(properties.getProperty("textFiles"))) {
			throw new IllegalArgumentException("Path to files to be process has not been established");
		}

		checkTotalThreads();
		
		//properties.setProperty("saveInDb", Boolean.valueOf(properties.getProperty("saveInDb")));

		ICollocationAlgorithm collocationAlgorithm = null;

		if (getAlgorithmType() == AlgorithmType.MUTUAL_INFORMATION) {
			collocationAlgorithm = getMutualInformationAlgorithm();
		} else if (getAlgorithmType() == AlgorithmType.FRECUENCY) {
			//TODO - crear objeto para algoritmo basado en frecuencias
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
	
		MutualInformationAlgorithm mia = new MutualInformationAlgorithm();
		mia.setConfiguration(this.properties);

		// si se ha indicado una nueva base de datos se usará esta
//		if (getNewDataBase() != null) {
//			DataBaseBuilder dbb = new DataBaseBuilder(getNewDataBase(), getNewDataBaseDescription());
//			dbb.createNewDB();	
//			mia.setDataBaseName(getNewDataBase());
//			// aunque se haya indicado no guardar en db se guardará
//			mia.setSaveInDB(true);
//		}
		return mia;
	}

	/**
	 * Checks if the algorithm type property is right.
	 * @param type the value of the property
	 * @return <i>true</i> if the algorithm type property is right, <i>false</i> otherwise
	 */
	private boolean isAlgorithmType(String type) {
		boolean value = true;
		try {
			AlgorithmType.valueOf(type);
		} catch (Exception e) {
			value = false;
		}
		return value;
	}

	private AlgorithmType getAlgorithmType() {
		return AlgorithmType.valueOf(properties.getProperty("type"));
	}

	/**
	 * Checks if the <i>textFiles</i> property is right.
	 * @param path the value of the property
	 * @return <i>true</i> if the textFiles property is right, <i>false</i> otherwise
	 */
	private boolean isTextFilesProperty(String path) {
		boolean value = false;
		if (path != null && (new File(path).exists())) {
			value = true;
		}
		return value;
	}
	
	/**
	 * Checks if the value of the totalThreads property is right.
	 * By default, the number of available processors minus one is used.
	 */
	private void checkTotalThreads() {
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
			this.properties.setProperty("totalThreads", String.valueOf(threads));
		}
	}

	/**
	 * 
	 * @return <i>true</i> si se debe guardar el proceso es base de datos, <i>false</i> en caso contrario
	 */
	private boolean getSaveInDB() {
		return Boolean.valueOf(properties.getProperty("saveInDb"));
	}

	/**
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @return tipo de tripleta a usar como filtro en el algoritmo de información mutua
	 */
	private List<String> getTriplesFilter() {
		List<String> listFilter = null;
		String triplesFilter = properties.getProperty("triplesFilter");
		if (triplesFilter == null) {
			listFilter = new ArrayList<>();
		} else {
			String[] dep = triplesFilter.split(",");
			listFilter = Arrays.asList(dep);
		}
		return listFilter;
	}
}
