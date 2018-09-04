package org.josfranmc.collocatio;

import org.josfranmc.collocatio.algorithms.CollocationAlgorithmBuilder;
import org.josfranmc.collocatio.algorithms.ICollocationAlgorithm;
import org.josfranmc.collocatio.algorithms.ParamsAlgorithm;
import org.josfranmc.collocatio.service.ICollocatioService;
import org.josfranmc.collocatio.service.JCollocatioService;

/**
 * Clase principal para la ejecución de algoritmos implementados para la obtención de colocaciones.<br>
 * Antes de ejecutar un algoritmo deben haberse especificado los parámetros de configuración del mismo. Esto se hace pasando un objeto ParamsAlgorithm.<p>
 * También permite obtener una instancia de un objeto ICollocatioService, el cual permite consultar la base de datos.
 * @author Jose Francisco Mena Ceca
 * @see ParamsAlgorithm
 * @see ICollocationAlgorithm
 * @see ICollocatioService
 * @version 1.0
 */
public class JCollocatio {

	/**
	 * Referencia al algoritmo a utilizar
	 * @see ICollocationAlgorithm
	 */
	private ICollocationAlgorithm algorithm;
	
	/**
	 * Parámetros de configuración del algoritmo a utilizar
	 * @see ParamsAlgorithm
	 */
	private ParamsAlgorithm algorithmConfig;
	
	
	/**
	 * Constructor por defecto
	 */
	public JCollocatio() {

	}

	/**
	 * Constructor que acepta un conjunto de parámetros de configuración
	 * @param params conjunto de parámetros
	 * @see ParamsAlgorithm
	 */
	public JCollocatio(ParamsAlgorithm params) {
		setAlgorithmConfig(params);
	}

	/**
	 * Ejecuta un algoritmo para buscar colocaciones.<br> El algoritmo ejecutado será el que se haya indicado
	 * entre los parámetros de configuración cargados. Si no se han especificado parámetros de configuración
	 * se lanza IllegalArgumentException
	 */
	public void extractCollocations() {
		if (this.algorithmConfig == null) {
			throw new IllegalArgumentException("No se han establecido parámetros de configuración");
		}
		this.algorithm.findCollocations();
	}

	/**
	 * Configura un algoritmo con una serie de parámetros
	 * @param params parámetros de configuración del algoritmo
	 * @see ParamsAlgorithm
	 */
	public void setAlgorithmConfig(ParamsAlgorithm params) {
		this.algorithmConfig = params;
		this.algorithm = new CollocationAlgorithmBuilder().setAlgorithmConfig(params).build();
	}

	/**
	 * @return los parámetros actualmente cargados
	 * @see ParamsAlgorithm
	 */
	public ParamsAlgorithm getAlgorithmConfig() {
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
