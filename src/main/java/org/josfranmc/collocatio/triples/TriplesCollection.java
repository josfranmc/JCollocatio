package org.josfranmc.collocatio.triples;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

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
public class TriplesCollection implements Iterable<TripleData> {

	private static final Logger log = Logger.getLogger(TriplesCollection.class);
	
	/**
	 * Collection to store the triples according to the dependency type
	 */
	//private Map<Triple, TripleEvents> triples;
	
	private Map<String, Map<Triple, TripleEvents>> triples;

	/**
	 * Counter for triples obtained
	 */
	private long totalTriples;
	
	private Map<String, Long> totalTriplesByDependency;
	
	
	private Map<String, Map<String, Long>> headWords;
	
	private Map<String, Map<String, Long>> dependentWords;

	//private long positionDependencies;
	
	//private long positionTriples;
	
	//private Map.Entry<String, Map<Triple,TripleEvents>> triplesEntryL1 = null;
	//private Map.Entry<Triple,TripleEvents> triplesEntryL2 = null;
	//private Map<Triple,TripleEvents> triplesByDependency = null;
	
	private Iterator<Map.Entry<String, Map<Triple, TripleEvents>>> iteratorL1 = null;
	private Iterator<Map.Entry<Triple, TripleEvents>> iteratorL2 = null;
	
	
	/**
	 * Default constructor. 
	 */
	public TriplesCollection() {
		//triples = new HashMap<>();
		triples = new HashMap<>();
		totalTriplesByDependency = new HashMap<>();
		headWords = new HashMap<>();
		dependentWords = new HashMap<>();
		//dependencies = new HashSet<>();
		totalTriples = 0;
		//position = 0;
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
			addTriple(triples, triple, file);
			countWord(headWords, triple.getDependency(), triple.getHead());
			countWord(dependentWords, triple.getDependency(), triple.getDependent());
		}
	}

	/**
	 * Saves a list of triples along with the name of the file where are located.<p>
	 * For each triple, if it is the first time the triple is saved, then a <code>TripleEvents</code> object is associated with the triple. This object
	 * stores the number of times the triple is located and the names of the files where is located.<br>
	 * If the triple already exists in the collection, then the <code>TripleEvents</code> object is updated with the new event.
	 * This operation is <i>thread-safe</i>.
	 * @param triples list of triples to add
	 * @param file the name of the file where the triples are located
	 * @see Triple
	 * @see TripleEvents
	 */
	public synchronized void save(List<Triple> triples, String file) {
		triples.forEach((triple) -> save(triple, file));
	}

	private void addTriple(Map<String, Map<Triple, TripleEvents>> triples, Triple triple, String file) {
		Map<Triple, TripleEvents> triplesByDependency = triples.get(triple.getDependency());
		if (triplesByDependency == null) { // primera vez que se guarda una tripleta de un determinado tipo de dependencia
			triplesByDependency = new HashMap<>();
			triplesByDependency.put(triple, new TripleEvents(file));
			triples.put(triple.getDependency(), triplesByDependency);
		} else { // ya hay tripletas de esa dependencia
			TripleEvents events = triplesByDependency.get(triple);
			if (events == null) { // es la primera vez que se guarda esta tripleta
				
				// esto no va a pasar nunca, NO???
				
				events = new TripleEvents(file);
			} else { // ya existe la tripleta, actualizar contador
				events.addEvent(file);
			}
			triplesByDependency.put(triple, events);
		}
		totalTriples++;
		
		Long total = totalTriplesByDependency.putIfAbsent(triple.getDependency(), 1L);
		if (total != null) {
			totalTriplesByDependency.put(triple.getDependency(), ++total);
		}
	}
	
	private void countWord(Map<String, Map<String, Long>> words, String dependency, String word) {
		Map<String, Long> wordsByDependency = words.get(dependency);
		if (wordsByDependency == null) {
			wordsByDependency = new HashMap<>();
			wordsByDependency.put(word, 1L);
			words.put(dependency, wordsByDependency);
		} else { // ya hay palabras de esa dependencia
			Long total = wordsByDependency.get(word);
			total = (total == null) ? 1L :  ++total;
			wordsByDependency.put(word, total);
		}
	}
	
	/**
	 * Merge with a <code>TriplesCollection</code>.
	 * @param triplesToAdd object to merge with
	 */
//	public synchronized void add(TriplesCollection triplesToAdd) {		

//		for(Triple key : triplesToAdd.getTriples().keySet()) {
//			TripleEvents eventToAdd = triplesToAdd.getTriples().get(key);
//			TripleEvents actualEvent = this.triples.putIfAbsent(key, eventToAdd);
//			
//			if(actualEvent != null) {
//				actualEvent.addEvent(eventToAdd);
//				this.triples.put(key, actualEvent);
//			} 
//			totalTriples += eventToAdd.getTotalEvents();
//		}
//		this.dependencies.addAll(triplesToAdd.getDependencies());
//	}
	
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
	 * Returns the collection of triples. The collection is a map whose key is the dependency type and the value is the triples of that type. 
	 * @return the collection of triples
	 */
	public Map<String, Map<Triple, TripleEvents>> getTriples() {
		return this.triples;
	}
	
	public Map<Triple, TripleEvents> getTriplesByDependency(String dependency) {
		Map<Triple, TripleEvents> triplesByDependency = this.triples.get(dependency);
		if (triplesByDependency == null) {
			triplesByDependency = new HashMap<>();
		}
		return triplesByDependency;
	}

	/**
	 * Returns the set of dependency types in the collection.
	 * @return the set of dependency types in the collection
	 */
	public Set<String> getDependencies() {
		return this.triples.keySet();
	}

	/**
	 * Returns the total number of triples in the collection.
	 * @return the total number of triples in the collection
	 */
	public long getTotalTriples() {
		return this.totalTriples;
	}
	
	public long getTotalTriplesByDependency(String dependency) {
//		Map<Triple, TripleEvents> triplesByDependency = this.triples.get(dependency);
//		long total = 0L;
//		
//		//triplesByDependency.forEach((triple, events) -> total = events.getTotalEvents());
//		
//		for (Triple triple : triplesByDependency.keySet()) {
//			total += triplesByDependency.get(triple).getTotalEvents();
//		}
		
		Long total = this.totalTriplesByDependency.get(dependency);
		return (total == null) ? 0L : total;
	}

	public long getTotalTriple(Triple triple) {
		TripleEvents e = this.triples.get(triple.getDependency()).get(triple);
		return e.getTotalEvents();
	}
	
	public long getTotalHeadWord(String dependency, String word) {
		Map<String, Long> wordsByDependency = this.headWords.get(dependency);
		Long total = wordsByDependency.get(word);
		return (total == null) ? 0L : total;
	}
	
	public long getTotalDependentWord(String dependency, String word) {
		Map<String, Long> wordsByDependency = this.dependentWords.get(dependency);
		Long total = wordsByDependency.get(word);
		return (total == null) ? 0L : total;
	}
	
	/**
	 * For debugging. Shows the contain of the collection that store the triples.
	 */
	public void show() {
		for (String dependency : triples.keySet()) {
			log.info("******************************************************");
			log.info("dependency = " + dependency);
			log.info("******************************************************");
			Map<Triple, TripleEvents> triplesByDependency = triples.get(dependency);
			for (Triple triple : triplesByDependency.keySet()) {
				TripleEvents events = triplesByDependency.get(triple);
				log.info("   " + triple.getHead() + " " + triple.getDistance() + " " + triple.getDependent());
				log.info("   events = " + events.getTotalEvents());
			    for (String file : events.getFiles()) {
			    	log.info("   " + file.toString());
			    }
			    log.info("------------------------------------------------------");
			}
		}
	}
	
	public void printFile() {
		int offset = 0;
		final byte[] buffer = new byte[2048];
		int read = 0;
		try (BufferedOutputStream outputFileStream = new BufferedOutputStream (new FileOutputStream("triples.txt"))) {
			
			outputFileStream.write(buffer, offset, read);
			outputFileStream.flush();

			for (String dependency : triples.keySet()) {
				
				log.info("******************************************************");
				log.info("dependency = " + dependency);
				log.info("******************************************************");
				Map<Triple, TripleEvents> triplesByDependency = triples.get(dependency);
				for (Triple triple : triplesByDependency.keySet()) {
					TripleEvents events = triplesByDependency.get(triple);
					log.info("   " + triple.getHead() + " " + triple.getDistance() + " " + triple.getDependent());
					log.info("   events = " + events.getTotalEvents());
				    for (String file : events.getFiles()) {
				    	log.info("   " + file.toString());
				    }
				    log.info("------------------------------------------------------");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Iterator<TripleData> iterator() {

	    iteratorL1 = this.triples.entrySet().iterator();
	    iteratorL2 = iteratorL1.next().getValue().entrySet().iterator();
	     
	    Iterator<TripleData> it = new Iterator<TripleData>() {
	   
	    	@Override
	    	public boolean hasNext() {
	    		if (iteratorL1.hasNext() || iteratorL2.hasNext()) {
	    			return true;
	    		} else {
	    			return false;
	    		}
	    	}
	    	
	    	@Override
	    	public TripleData next() {
	    		Triple triple = null;
	    		TripleEvents events = null;
	    		long totalHeadWord = 0L;
	    		long totalDependentWord = 0L;
	    		Map.Entry<Triple,TripleEvents> triplesEntry = null;
	    		try {
	    			if (!iteratorL2.hasNext()) {
	    				iteratorL2 = iteratorL1.next().getValue().entrySet().iterator();
	    			}
	    			triplesEntry = iteratorL2.next();
	    			triple = triplesEntry.getKey();
	    			events = triplesEntry.getValue();
		    		totalHeadWord = getTotalHeadWord(triple.getDependency(), triple.getHead());
		    		totalDependentWord = getTotalDependentWord(triple.getDependency(), triple.getDependent());
	    		} catch (Exception e) {
	    			throw new NoSuchElementException();
	    		}
	    		return new TripleData(triple, events, totalHeadWord, totalDependentWord);
	    	}
	    };
	    return it;
	}
	
	public Iterator<TripleData> iterator(String dependency) {
		
		Entry<String, Map<Triple, TripleEvents>> entry = null;
		Iterator<Map.Entry<String, Map<Triple, TripleEvents>>> iteratorL1 = this.triples.entrySet().iterator();
		do {
			entry = iteratorL1.next();
		} while (entry.getKey() != dependency);

		Iterator<Map.Entry<Triple, TripleEvents>> iteratorL2 = entry.getValue().entrySet().iterator();
	     
	    Iterator<TripleData> it = new Iterator<TripleData>() {
	   
	    	@Override
	    	public boolean hasNext() {
	    		if (iteratorL2.hasNext()) {
	    			return true;
	    		} else {
	    			return false;
	    		}
	    	}
	    	
	    	@Override
	    	public TripleData next() {
	    		Triple triple = null;
	    		TripleEvents events = null;
	    		long totalHeadWord = 0L;
	    		long totalDependentWord = 0L;
	    		Map.Entry<Triple,TripleEvents> triplesEntry = null;
	    		try {
	    			triplesEntry = iteratorL2.next();
	    			triple = triplesEntry.getKey();
	    			events = triplesEntry.getValue();
		    		totalHeadWord = getTotalHeadWord(triple.getDependency(), triple.getHead());
		    		totalDependentWord = getTotalDependentWord(triple.getDependency(), triple.getDependent());
	    		} catch (Exception e) {
	    			throw new NoSuchElementException();
	    		}
	    		return new TripleData(triple, events, totalHeadWord, totalDependentWord);
	    	}
	    };
	    return it;
	}
}
