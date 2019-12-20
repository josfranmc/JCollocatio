package org.josfranmc.collocatio.triples;

import java.util.HashSet;
import java.util.Set;

/**
 * The objects of this class act as a counter to the triples found. They allow you to save the number of times a triple is found and where.<p>
 * These objects are used inside of <code>TriplesCollection</code> class to save in a map the occurrences of each triple.
 * For each triple found a <code>TripleEvents</code> is assigned.
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 * @see TriplesCollection
 */
public class TripleEvents {

	/**
	 * Set of files where triples are found
	 */
	private Set<String> files;
	
	/**
	 *  The number of times a triplet has been found
	 */
	private long totalEvents;

	
	/**
	 * Default constructor.  
	 * @param file name of the file to add
	 */
	TripleEvents(String file) {
		this.files = new HashSet<>();
		addEvent(file);
	}
	
	/**
	 * Adds the name of the file where a triple is founded and increase the event counter by one.
	 * @param file name of the file to add
	 */
	public void addEvent(String file) {
		this.files.add(file);
		this.totalEvents++;
	}
	
	/**
	 * Updates the object with the data of a <code>TripleEvents</code> object. Adds the name of the files and sums the counter.
	 * @param event data to add
	 */
	public void addEvent(TripleEvents event) {
		this.files.addAll(event.getFiles());
		this.totalEvents += event.getTotalEvents();
	}
	
	/**
	 * Returns the set of files where a triple was found.
	 * @return the set of files where a triple was found
	 */
	public Set<String> getFiles() {
		return files;
	}

	/**
	 * Returns the number of times a triple has been found.
	 * @return the number of times a triple has been found
	 */
	public long getTotalEvents() {
		return totalEvents;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TripleEvents [total=" + getTotalEvents() + "]";
	}
}
