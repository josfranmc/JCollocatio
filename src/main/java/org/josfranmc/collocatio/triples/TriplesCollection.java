package org.josfranmc.collocatio.triples;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.josfranmc.collocatio.triples.TripleEvents;

import org.apache.log4j.Logger;

/**
 * Encapsulates the data type that is used to store the triples obtained in the extraction process.<p>
 * The triples are stored on a map whose key is a <code>Triple</code> object that represents a triple.
 * Each key is associated with a <code>TripleEvents</code> object as value, which acts as a counter that collects the key appearances.<p>
 * Objects of this class can be used concurrently, since the methods for saving data are thread-safe.
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see Triple
 * @see TripleEvents
 * @see StanfordTriplesExtractor
 */
public class TriplesCollection {

	private static final Logger log = Logger.getLogger(TriplesCollection.class);
	
	/**
	 * Collection to store the triples and where are found
	 */
	private Map<Triple, TripleEvents> triples;
	
	/**
	 * Set with the types of dependency stored in the collection
	 */
	private Set<String> dependencies;

	/**
	 * Counter for triples obtained
	 */
	private long totalTriples;


	/**
	 * Default constructor. 
	 */
	public TriplesCollection() {
		triples = new HashMap<>();
		dependencies = new HashSet<>();
		totalTriples = 0;
	}
	
	/**
	 * Saves a triple along with the name of the file where is located.<p>
	 * If It is the first time the triple is saved, then a <code>TripleEvents</code> object is associated with the triple. This object
	 * stores the number of times the triple is located and the names of the files where is located.<br>
	 * If the triple already exists in the collection, then the <code>TripleEvents</code> object is updated with the new event.
	 * This operation is <i>thread-safe</i>.
	 * @param triple triple to add
	 * @param file the name of the file where the triple is located
	 * @see Triple
	 * @see TripleEvents
	 */
	public synchronized void save(Triple triple, String file) {
		if (isValidDependency(triple.getDependency())) {
			TripleEvents events = triples.putIfAbsent(triple, new TripleEvents(file));
			if (events != null) {
				events = triples.get(triple);
				events.addEvent(file);
				triples.put(triple, events);
			} else {
				dependencies.add(triple.getDependency());
			}
			totalTriples++;
		}
	}

	/**
	 * Saves a list of triples along with the name of the file where are located.<p>
	 * For each triple, if it is the first time the triple is saved, then a <code>TripleEvents</code> object is associated with the triple. This object
	 * stores the number of times the triple is located and the names of the files where is located.<br>
	 * If the triple already exists in the collection, then the <code>TripleEvents</code> object is updated with the new event.
	 * This operation is <i>thread-safe</i>.
	 * @param triple triple to add
	 * @param file the name of the file where the triple is located
	 * @see Triple
	 * @see TripleEvents
	 */
	public synchronized void save(List<Triple> triples, String file) {
		for (Triple triple : triples) {
			save(triple, file);
		}
	}
	
	/**
	 * Merge with a <code>TriplesCollection</code>.
	 * @param triplesToAdd object to merge with
	 */
	public synchronized void add(TriplesCollection triplesToAdd) {		

		for(Triple key : triplesToAdd.getTriples().keySet()) {
			TripleEvents eventToAdd = triplesToAdd.getTriples().get(key);
			TripleEvents actualEvent = this.triples.putIfAbsent(key, eventToAdd);
			
			if(actualEvent != null) {
				actualEvent.addEvent(eventToAdd);
				this.triples.put(key, actualEvent);
			} 
			totalTriples += eventToAdd.getTotalEvents();
		}
		this.dependencies.addAll(triplesToAdd.getDependencies());
	}
	
	/**
	 * To erase dependencies that we don't want. They are garbage, like dependencies about punctuation. 
	 * @param dependency dependency type
	 * @return <i>true</i> if it is a dependency to process, <i>false</i> otherwise
	 */
	private boolean isValidDependency(String dependency) {
		boolean value = true;
		if (dependency.equals("punct") || dependency.toLowerCase().equals("root")) {
			value = false;
		}
		return value;
	}
	
	/**
	 * Returns the total number of triples in the collection.
	 * @return the total number of triples in the collection
	 */
	public long getTotalTriples() {
		return this.totalTriples;
	}

	/**
	 * Returns the collection of triples.
	 * @return the collection of triples
	 */
	public Map<Triple, TripleEvents> getTriples() {
		return this.triples;
	}

	/**
	 * Returns the set of dependency types in the collection.
	 * @return the set of dependency types in the collection
	 */
	public Set<String> getDependencies() {
		return this.dependencies;
	}

	/**
	 * For debugging. Shows the contain of the collection that store the triples.
	 */
	public void show() {
		for (Map.Entry<Triple, TripleEvents> entry : triples.entrySet()) {
			Triple key = entry.getKey();
			TripleEvents value = entry.getValue();
			log.info("------------------------------------------------------");
		    log.info("dependency = " + key.getDependency());
		    log.info("   " + key.getHead() + " " + key.getDistance() + " " + key.getDependent());
		    log.info("   total = " + value.getTotalEvents());
		    for (String file : value.getFiles()) {
		    	log.info(file.toString());
		    }
		    log.info("------------------------------------------------------");
		}
	}
}
