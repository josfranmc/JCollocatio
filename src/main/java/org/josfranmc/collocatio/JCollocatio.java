package org.josfranmc.collocatio;

import java.util.Properties;

import org.josfranmc.collocatio.algorithms.CollocationAlgorithmBuilder;
import org.josfranmc.collocatio.algorithms.ICollocationAlgorithm;
import org.josfranmc.collocatio.service.ICollocatioService;
import org.josfranmc.collocatio.service.JCollocatioService;

/**
 * Main class to run algorithms in order to obtain collocations.<p>
 * Before running an algorithms you must specify a series of configuration parameters, either when you create the object or calling the <code>setAlgorithmConfig()</code> method after create it. 
 * For that, a <code>Properties</code> object is necessary.<p>
 * También permite obtener una instancia de un objeto ICollocatioService, el cual permite consultar la base de datos.
 * @author Jose Francisco Mena Ceca
 * @see ICollocatioService
 * @version 2.0
 */
public class JCollocatio {

	/**
	 * Reference to the algorithm to run
	 * @see ICollocationAlgorithm
	 */
	private ICollocationAlgorithm algorithm;
	
	/**
	 * Configuration parameters of the algorithm to use
	 */
	private Properties algorithmConfig;
	
	/**
	 * Default constructor.
	 */
	public JCollocatio() {

	}

	/**
	 * Constructor that accept a group of setting parameters.
	 * @param config group of parameters as a <code>Properties</code> object
	 */
	public JCollocatio(Properties config) {
		setAlgorithmConfig(config);
	}

	/**
	 * Runs an algorithm to search collocations.<p>
	 * Before, the <code>setAlgorithmConfig</code> method must be called for a correct configuration. The algorithm to be used is one of the parameters to specify.
	 * If no parameters are set an <code>IllegalArgumentException</code> is raised.
	 */
	public void extractCollocations() {
		if (this.algorithmConfig == null) {
			throw new IllegalArgumentException("No setting parameters");
		}
		this.algorithm.findCollocations();
	}

	/**
	 * Sets the needed parameters to setting an algorithm.
	 * @param algorithmConfig configuration parameters as a <code>Properties</code> object
	 */
	public void setAlgorithmConfig(Properties algorithmConfig) {
		if (this.algorithmConfig == null) {
			throw new IllegalArgumentException("No setting parameters");
		}		
		this.algorithmConfig = algorithmConfig;
		this.algorithm = new CollocationAlgorithmBuilder().setConfiguration(algorithmConfig).build();
	}

	/**
	 * Returns the currently loaded parameters.
	 * @return the currently loaded parameters
	 */
	public Properties getAlgorithmConfig() {
		return this.algorithmConfig;
	}
	
	/**
	 * @return una instancia de un objeto tipo ICollocatioService, el cual permite realizar consultas a la base de datos
	 * @see ICollocatioService
	 */
	public ICollocatioService getCollocatioService() {
		return new JCollocatioService();
	}
}
