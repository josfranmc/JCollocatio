package org.josfranmc.collocatio.algorithms;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.josfranmc.collocatio.triples.Triple;
import org.josfranmc.collocatio.triples.TripleEvents;

/**
 * Examina la colección de tripletas previamente obtenida y extrae la información referente a las tripletas de un tipo de dependencia concreto.<br>
 * Los objetos de esta clase se ejecutarán como hilos independientes desde el proceso principal. En concreto, se debe crear un objeto por cada tipo de dependencia existente en la
 * colección de tripletas a analizar. Los datos obtenidos del análisis realizado se encapsulan en una instancia de tipo TriplesData que es devuelta al proceso principal,
 * el cual debe encargarse de recibir los datos para su posterior procesamiento.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see Triple
 * @see TriplesData
 * @see TripleEvents
 */
public class ExtractTriplesDataThread implements Callable<TriplesData> {

	private static final Logger log = Logger.getLogger(ExtractTriplesDataThread.class);
	
	/**
	 * Nombre del tipo de dependencia de las tripletas a examinar 
	 */
	private String dependency;
	
	/**
	 * Colección de tripletas a examinar
	 */
	private Map<Triple, TripleEvents> triples;
	
	/**
	 * Colección de todas las palabras que ocupan la posición 1 en las tripletas del tipo de dependencia indicado por la propiedad dependency
	 * de la clase.<br>
	 * Cada palabra se acompaña del número de ocurrencias de la misma
	 */
	private Map<String, Long> headWordFrecuencyMap;
	
	/**
	 * Colección de todas las palabras que ocupan la posición 2 en las tripletas del tipo de dependencia indicado por la propiedad dependency 
	 * de la clase.<br>
	 * Cada palabra se acompaña del número de ocurrencias de la misma
	 */
	private Map<String, Long> dependentWordFrecuencyMap;
	
	/**
	 * Colección donde se guardan las tripletas analizadas correspondientes al tipo de dependencia indicado por la propiedad dependency de la clase
	 */
	private Map<Triple, TripleEvents> triplesCollectionByDependency;
	
	
	/**
	 * Constructor principal. Establece la colección de tripletas a analizar y el tipo de dependencia de las tripletas que deben buscarse en dicha colección
	 * @param map colección de tripletas a analizar
	 * @param dependency tipo de dependencia a buscar 
	 * @see Triple
	 * @see TripleEvents
	 */
	ExtractTriplesDataThread(Map<Triple, TripleEvents> map, String dependency) {
		if (map == null) {
			throw new IllegalArgumentException("La colección de tripletas a analizar no puede ser null.");
		}
		if (dependency == null) {
			throw new IllegalArgumentException("El tipo de dependencia no puede ser null.");
		}
		setTriplesCollection(map);
		setDependency(dependency);
		triplesCollectionByDependency = new HashMap<Triple, TripleEvents>();
		headWordFrecuencyMap = new HashMap<String, Long>();
		dependentWordFrecuencyMap = new HashMap<String, Long>();
	}
	
	/**
	 * Analiza la colección de tripletas buscando aquellas correspondientes a un tipo de dependencia determinado, las cuales guarda en una nueva colección.
	 * También se realiza:
	 * <ul>
     * <li>el cálculo del total de tripletas del tipo de dependencia buscado, sumando el número de ocurrencias de cada tripleta</li>
     * <li>se guardan las palabras que ocupan la posición 1 en las tripletas buscadas, junto con su frecuencia de repetición</li> 
     * <li>se guardan las palabras que ocupan la posición 2 en las tripletas buscadas, junto con su frecuencia de repetición</li>
     * </ul>
     * @see TriplesData
	 */
	@Override
	public TriplesData call() throws Exception {
		log.debug("DEPENDENCIA " + dependency);
		long totalTriples = 0;
		for (Entry<Triple, TripleEvents> mapEntry : triples.entrySet()) {
			// tripleta a analizar
			Triple triple = mapEntry.getKey();                
			if (triple.getDependency().equals(this.dependency)) {
				TripleEvents events = mapEntry.getValue();
				// total de ocurrencias de esta tripleta
				Long totalTriple = events.getTotalEvents();
				// actualizamos el total de tripletas del tipo de dependencia buscado
				totalTriples += totalTriple;
				// guardamos la tripleta 
				TripleEvents value = triplesCollectionByDependency.putIfAbsent(triple, events);
				if (value != null) {
					log.error("Tripleta duplicada. " + triple.toString() + " ya existe");
				}
				
				Long val = 0L;
				// guardamos la palabra head de la tripleta junto a su número de ocurrencias
				String headWord = triple.getHead();
				val = headWordFrecuencyMap.putIfAbsent(headWord, totalTriple);
				if (val != null) {
					// si ya estaba guardada actualizamos el número de ocurrencias de la misma
					val += totalTriple;
					headWordFrecuencyMap.put(headWord, val);
				}
				
				// guardamos la palabra dependent de la tripleta junto a su número de ocurrencias
				String dependentWord = triple.getDependent();
				val = dependentWordFrecuencyMap.putIfAbsent(dependentWord, totalTriple);
				if (val != null) {
					// si ya estaba guardada actualizamos el número de ocurrencias de la misma
					val += totalTriple;
					dependentWordFrecuencyMap.put(dependentWord, val);
				}
			}
		}
		// encapsulamos los datos obtenidos que van a ser devueltos
		TriplesData data = new TriplesData(dependency);
		data.setTriplesMap(triplesCollectionByDependency);
		data.setTotalTriplesByDependency(totalTriples);
		data.setWord1FrecuencyMap(headWordFrecuencyMap);
		data.setWord2FrecuencyMap(dependentWordFrecuencyMap);

		return data;
	}

	/**
	 * @param map Asigna la colección de tripletas a examinar
	 * @see Triple
	 * @see TripleEvents
	 */
	public void setTriplesCollection(Map<Triple, TripleEvents> map) {
		this.triples = map;
	}

	/**
	 * Asigna el nombre del tipo de dependencia de las tripletas a examinar
	 * @param dependency nombre de la dependencia que se asigna
	 */
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}
}
