package org.josfranmc.collocatio.algorithms;

import java.util.HashMap;
import java.util.Map;

import org.josfranmc.collocatio.triples.Triple;
import org.josfranmc.collocatio.triples.TripleEvents;

/**
 * Encapsula información referentes a las frecuencias obtenidas de las tripletas de un determinado tipo de dependencia, así como otros datos necesarios
 * para el cálculo del valor de información mutua de estas tripletas. Los datos encapsulados son:
 * <ul>
 * <li>Todas las tripletas de un tipo concreto de dependencia, junto con su frecuencia de repetición</li>
 * <li>El número total de estas tripletas </li>
 * <li>Todas las palabras que ocupan la posición 1 en esas tripletas, junto con su frecuencia de repetición</li>
 * <li>Todas las palabras que ocupan la posición 2 en esas tripletas, junto con su frecuencia de repetición</li>
 * <li>El número total de tripletas obtenidas (todas las tripletas de todos los tipos de dependencia posibles)</li>
 * </ul>
 * Los objetos de esta clase son creados y menejados por los diferentes procesos ejecutados por el algoritmo MutualInformationAlgorithm, siendo
 * utiizados para pasar la información relevante del cálculo entre ellos.<p>
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see Triple
 * @see TripleEvents
 */
public class TriplesData {

	/**
	 * Nombre de la dependencia.
	 */
	private String dependency;
	
	/**
	 * Colección de tripletas del tipo de dependencia indicado por la propiedad dependency de la clase.
	 * Cada tripleta se acompaña de las ocurrencias de la misma.
	 */
	private Map<Triple, TripleEvents> triplesMap;
	
	/**
	 * Total de tripletas del tipo de dependencia indicado por la propiedad dependency de la clase
	 */
	private long totalTriplesByDependency;
	
	/**
	 * Colección de todas las palabras que ocupan la posición 1 en las tripletas del tipo de dependencia indicado por la propiedad dependency de la clase
	 * Cada palabra se acompaña del número de ocurrencias de la misma
	 */
	private Map<String, Long> word1FrecuencyMap;
	
	/**
	 * Colección de todas las palabras que ocupan la posición 2 en las tripletas del tipo de dependencia indicado por la propiedad dependency de la clase
	 * Cada palabra se acompaña del número de ocurrencias de la misma
	 */
	private Map<String, Long> word2FrecuencyMap;
	
	/**
	 * Número total de tripletas que se han obtenido (todas las tripletas de todos los tipos de dependencia posibles)
	 */
	private long totalTriples;
	
	/**
	 * Constante para ajustar la frecuencia de la probabilidad conjunta de una tripleta: <i>P(w1,rel,w2)</i>.<p>
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 */
	private double adjustedFrequency = 0.0;
	
	
	/**
	 * Constructor principal. Establece el nombre de la dependencia cuyos datos relacionados se van a encapsular. 
	 * @param dependency nombre de la dependencia
	 */
	TriplesData(String dependency) {
		setDependency(dependency);
		word1FrecuencyMap = new HashMap<String, Long>();
		word2FrecuencyMap = new HashMap<String, Long>();
	}

	/**
	 * @return tipo de dependencia que encapsula el objeto
	 */
	public String getDependency() {
		return dependency;
	}

	/**
	 * Asigna el nombre de la dependencia cuyos datos encapsula el objeto
	 * @param dependency nombre de la dependencia que se asigna
	 */
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}

	/**
	 * @return la colección de tripletas del tipo de dependencia indicado por la propiedad dependency de la clase.
	 */
	public Map<Triple, TripleEvents> getTriplesMap() {
		return triplesMap;
	}

	/**
	 * Establece la colección de tripletas.
	 * @param triplesCollectionByDependency the triplesCollection to set
	 */
	public void setTriplesMap(Map<Triple, TripleEvents> triplesCollectionByDependency) {
		this.triplesMap = triplesCollectionByDependency;
	}

	/**
	 * @return la colección de todas las palabras que ocupan la posición 1 en las tripletas del tipo de dependencia que encapsula el objeto
	 */
	public Map<String, Long> getWord1FrecuencyMap() {
		return word1FrecuencyMap;
	}

	/**
	 * Asigna la colección de todas las palabras que ocupan la posición 1 en las tripletas del tipo de dependencia que encapsula el objeto
	 * @param word1FrecuencyMap2 the word1FrecuencyMap to set
	 */
	public void setWord1FrecuencyMap(Map<String, Long> word1FrecuencyMap2) {
		this.word1FrecuencyMap = word1FrecuencyMap2;
	}

	/**
	 * @return la colección de todas las palabras que ocupan la posición 2 en las tripletas del tipo de dependencia que encapsula el objeto
	 */
	public Map<String, Long> getWord2FrecuencyMap() {
		return word2FrecuencyMap;
	}

	/**
	 * Asigna la colección de todas las palabras que ocupan la posición 2 en las tripletas del tipo de dependencia que encapsula el objeto
	 * @param word2FrecuencyMap the word2FrecuencyMap to set
	 */
	public void setWord2FrecuencyMap(Map<String, Long> word2FrecuencyMap) {
		this.word2FrecuencyMap = word2FrecuencyMap;
	}

	/**
	 * @return total de tripletas del tipo de dependencia que encapsula el objeto 
	 */
	public long getTotalTriplesByDependency() {
		return totalTriplesByDependency;
	}

	/**
	 * Asigna el total de tripletas del tipo de dependencia que encapsula el objeto 
	 * @param totalTriples total de tripletas
	 */
	public void setTotalTriplesByDependency(long totalTriples) {
		this.totalTriplesByDependency = totalTriples;
	}

	/**
	 * @return el número total de tripletas que se han obtenido (todas las tripletas de todos los tipos de dependencia posibles)
	 */
	public long getTotalTriples() {
		return totalTriples;
	}

	/**
	 * Establece el número total de tripletas obtenidas (todas las tripletas de todos los tipos de dependencia posibles)
	 * @param totalTriples número total de tripletas que se han obtenido 
	 */
	public void setTotalTriples(long totalTriples) {
		this.totalTriples = totalTriples;
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
	 * Se utiliza en el algoritmo de cálculo de información mutua (AlgorithmType.MUTUAL_INFORMATION)
	 * @param adjustFrecuency valor de la constante de ajuste
	 */
	public void setAdjustedFrequency(double frequencyAdjusted) {
		this.adjustedFrequency = frequencyAdjusted;
	}
	
	/**
	 * @return el total de elementos de la colección de tripletas del tipo de dependencia indicado por la propiedad dependency de la clase. 
	 */
	public long getTotalElementsMap() {
		return this.triplesMap.size();
	}
}
