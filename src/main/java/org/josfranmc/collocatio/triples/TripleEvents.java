package org.josfranmc.collocatio.triples;

import java.util.HashSet;
import java.util.Set;

/**
 * Permite guardar el número de veces que se ha encontrado una tripleta y el cojunto de libros en los que esto ha sucedido.<p>
 * Los objetos de esta clase se usan dentro de la clase TriplesCollection, utilizándose para guardar las ocurrencias de cada tripleta. Para cada
 * tripleta encontrada se asociará un objeto TripleEvents que guardará un conjunto con los libros en los que ha aparecido la tripleta y las veces
 * que esto ha suceddio.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see TriplesCollection
 */
public class TripleEvents {

	/**
	 * Conjunto de libros en los que se ha encontrado una tripleta concreta
	 */
	private Set<String> books;
	
	/**
	 * Número de veces que se ha encontrado una tripleta determinada
	 */
	private long totalEvents;

	
	/**
	 * Constructor principal.  
	 * @param book
	 */
	public TripleEvents(String book) {
		this.books = new HashSet<String>();
		addEvent(book);
	}
	
	/**
	 * Añade un libro al conjunto e incrementa en uno el contador de apariciones.
	 * @param book
	 */
	public void addEvent(String book) {
		this.books.add(book);
		this.totalEvents++;
	}
	
	/**
	 * @return el conjunto de libros en los que se ha encontrado una tripleta concreta
	 */
	public Set<String> getBooks() {
		return books;
	}

	/**
	 * @return el número de veces que se ha encontrado una tripleta
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
