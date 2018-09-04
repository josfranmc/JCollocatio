package org.josfranmc.collocatio.triples;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

import org.josfranmc.collocatio.triples.TripleEvents;

import org.apache.log4j.Logger;

/**
 * Encapsula el tipo de dato que sirve para almacenar la colección de tripletas obtenida. Un objeto de este tipo es devuelto por el método <i>extractTriples()</i>
 * de la clase StanfordTriplesExtractor tras realizar el proceso de extracción de tripletas, el cual es utilizado posteriormente para realizar el cálculo del valor
 * de información mutua de las tripletas que guarda.<p>
 * Los objetos de esta clase se utilizarán concurrentemente por varios hilos de ejecución. Para garantizar la sincronización de los procesos y la
 * integridad de los datos sea un Map del tipo ConcurrentHashMap.
 * <p>El map guarda cada tripleta obtenida junto a un objeto de tipo TripleEvents, el cual guarda el número de veces que se ha encontrado la
 * tripleta y un conjunto con los libros en los que esto ha sucedido.<p>
 * También se guarda un conjunto con todos los tipos de dependencias que se han obtenido.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see Triple * 
 * @see TripleEvents
 */
public class TriplesCollection {

	private static final Logger log = Logger.getLogger(TriplesCollection.class);
	
	/**
	 * Colección que guarda las tripletas y donde se han encontrado
	 */
	private Map<Triple, TripleEvents> triplesCollection;
	
	/**
	 * Conjunto de todos los tipos de dependencias que se han obtenido
	 */
	private Set<String> dependenciesCollection;

	/**
	 * Contador del número de tripletas obtenidas
	 */
	private LongAdder totalTriples;

	/**
	 * Bloqueo para el control de concurrencia
	 */
	private ReentrantLock lock;
	
	
	/**
	 * Constructor principal. 
	 */
	public TriplesCollection() {
		triplesCollection = new ConcurrentHashMap<Triple, TripleEvents>();
		dependenciesCollection = ConcurrentHashMap.newKeySet();
		totalTriples = new LongAdder();
		lock = new ReentrantLock();
	}
	
	/**
	 * Guarda una tripleta en la estructura de datos definida para ello junto al libro en el cual ha sido encontrada.<p>
	 * Si es la primera vez que se guarda la tripleta se asocia a la misma un objeto TripleEvents en el que se recoge el número de veces que
	 * se ha encontrado la tripleta y en que libros. 
	 * Si ya existe la tripleta en la colección, se actualiza el objeto TripleEvents añadiendo el nuevo libro en el que ha aparecido la tripleta.
	 * Esta actualización se lleva a cabo un bloqueo de forma que la operación sea <i>thread-safe</i>
	 * @param triple tripleta a guardar
	 * @param book identificador del libro en el que se ha encontrado la tripleta
	 * @see TripleEvents
	 */
	public void save(Triple triple, String book) {
		TripleEvents events = addTripleToCollection(triple, book);
		if (events != null) {
			try {
				lock.lock();
				events = triplesCollection.get(triple);
				events.addEvent(book);
				triplesCollection.put(triple, events);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
		dependenciesCollection.add(triple.getDependency());
		totalTriples.increment();
	}

	/**
	 * Añade una nueva tripleta a la colección junto a su ocurrencia (en que libro se ha encontrado)
	 * @param triple tripleta a guardar
	 * @param book identificador del libro en el que se ha encontrado la tripleta
	 * @return null si es la primera vez que se guarda la tripleta, o un objeto TripleEvents que encapsula las veces que ha aparecido la tripleta y en que libros
	 * @see TripleEvents
	 */
	private TripleEvents addTripleToCollection(Triple triple, String book) {		
		TripleEvents te = new TripleEvents(book);
		TripleEvents value = triplesCollection.putIfAbsent(triple, te);
		return value;
	}
	
	/**
	 * Devuelve el número total de tripletas obtenidas
	 * @return Número de tripletas obtenidas
	 */
	public long getTotalTriples() {
		return this.totalTriples.sum();
	}

	/**
	 * @return la colección de todas las tripletas obtenidas
	 */
	public Map<Triple, TripleEvents> getTriplesCollection() {
		return this.triplesCollection;
	}

	/**
	 * Devuelve el conjunto de todos los tipos de dependencias obtenidas
	 * @return conjunto de dependencias
	 */
	public Set<String> getDependenciesCollection() {
		return this.dependenciesCollection;
	}

	/**
	 * Para labores de depuración y tests. Muestra en el dispositivo de log el contenido de la colección que almacena las tripletas.
	 */
	public void show() {
		for (Map.Entry<Triple, TripleEvents> entry2 : triplesCollection.entrySet()) {
			Triple key = entry2.getKey();
			TripleEvents value = entry2.getValue();
			log.info("------------------------------------------------------");
		    log.info("dependencia = " + key.getDependency());
		    log.info("   " + key.getWord1());
		    log.info("   " + key.getWord2());
		    log.info("   total = " + value.getTotalEvents());
		    for (String book : value.getBooks()) {
		    	log.info(book.toString());
		    }
		    log.info("------------------------------------------------------");
		}
	}
}
