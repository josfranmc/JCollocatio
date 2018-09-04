package org.josfranmc.collocatio.triples;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * Encapsula el proceso de análisis de una oración para extraer las tripletas que la componen y lleva a cabo el almacenamiento de las mismas.
 * Los objetos ParserThread implementan la interfaz Runnable por lo que pueden ser ejecutados de forma concurrente. De esta forma se puede realizar
 * el análisis de varias oriaciones de forma paralela.<p>
 * La creación de estos objetos debe hacerse utilizando ParserThreadBuilder, de forma que se realice una correcta configuración.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class ParserThread implements Runnable {

	private static final Logger log = Logger.getLogger(ParserThread.class);
	
	/**
	 * Referencia al objeto LexicalizedParser del software de Stanford utilizado para el análisis
	 */
	private LexicalizedParser lp;
	
	/**
	 * Referencia al objeto GrammaticalStructureFactory del software de Stanford utilizado para el análisis
	 */
	private GrammaticalStructureFactory gsf;

	/**
	 * Oración a analizar de la que obtener sus tripletas
	 */
	private List<? extends HasWord> sentence;
	
	/**
	 * Identificador del libro en el que se encuantra la oración a analizar
	 */
	private String book;
	
	/**
	 * Colección donde almacenar las tripletas obtenidas
	 * @see TriplesCollection
	 */
	private TriplesCollection triplesCollection;
	

	/**
	 * Obtiene una lista de las tripletas contenidas en una oración
	 * @return lista de tripletas
	 */
	private List<TypedDependency> getTriplesList() {
        Tree parse = lp.parse(sentence);
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
        return tdl;
	}
	
	/**
	 * Realiza la extracción y almacenamiento de las tripletas contenidas en una oración
	 * @see Triple
	 */
	@Override
	public void run() {
		log.debug(" +++ Analizando " + sentence.toString());
        for (TypedDependency td : getTriplesList()) { 
        	log.debug(" triple = " + td.toString());
        	Triple triple = getTriplet(td.toString(), getBook());
        	if (triple != null) {
        		triplesCollection.save(triple, getBook());
        	} else {
        		log.warn(Thread.currentThread().getName() + " - Imposible descomponer " + td.toString());
        	}
        }
		log.debug("Fin hilo " + Thread.currentThread().getName());
	}
	
	/**
	 * Devuelve un objeto de tipo Triple basado en los datos pasados por parámetros.<br>
	 * La cadena de texto pasada representa una tripleta de la forma <i>dependencia(palabra1-posicion1, palabra2-posicion2)</i>, donde:
	 * <ul>
	 * <li><i>dependencia</i>: tipo de dependencia de la tripleta</li>
	 * <li><i>palabra1</i>: palabra 1 de la tripleta</li>
	 * <li><i>posicion1</i>: posicion que ocupa la palabra 1 en la oración de donde se ha extraido la tripleta</li>
	 * <li><i>palabra2</i>: palabra 2 de la tripleta</li>
	 * <li><i>posicion2</i>: posicion que ocupa la palabra 2 en la oración de donde se ha extraido la tripleta</li>
	 * </ul>
	 * Ejemplo: <i>nsubj(blue-4, cars-2)</i><p>
	 * Puede darse el caso de tripletas cuya dependencia sea de la forma <i>dependencia:subtipo</i>, como <i>nmod:in(happy-3, town-6)</i>.
	 * En estos casos se toma como dependencia sólo la parte principal, la correspondiente desde la izquierda hasta los dos puntos.<p>
	 * Todas las palabras obtenidas se devuelven en minúsculas.
	 * @param td cadena de texto que representa la tripleta a obtener; contiene el tipo de dependencia y las dos palabras que relaciona
	 * @param idBook identificador del libro en el que se encuentra la tripleta
	 * @return objeto de tipo Triple
	 * @see Triple
	 */
	private Triple getTriplet(String td, String idBook) {
    	Triple tripleta = null;
    	// expresión regular para extraer la dependencia y las dos palabras que relaciona
    	// ((.*):.*|(.*))\\(  extrae la dependencia
    	// (.*)-.*,\\s        extrae la palabra 1
    	// (.*)-.*            extrae la palabra 2
    	final String PATTERN = "((.*):.*|(.*))\\((.*)-.*,\\s(.*)-.*";
    
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(td);
		if (matcher.matches()) {
			tripleta = new Triple();
	    	// si la dependencia es del tipo dependencia:subtipo se obtiene en el grupo 2, si no en el grupo 3
	    	if (matcher.group(3) != null) {
	    		tripleta.setDependency(matcher.group(3));
	    	} else {
	    		tripleta.setDependency(matcher.group(2));
	    	}
	    	tripleta.setWord1(matcher.group(4).toLowerCase());
	    	tripleta.setWord2(matcher.group(5).toLowerCase());
		}
		return tripleta;
	}

	/**
	 * @return objeto LexicalizedParser
	 * @see LexicalizedParser
	 */
	LexicalizedParser getLp() {
		return lp;
	}

	/**
	 * Establece el LexicalizedParser utilizado para parsear la oracióna a analizar
	 * @param lp objeto LexicalizedParser
	 * @see LexicalizedParser
	 */
	void setLp(LexicalizedParser lp) {
		this.lp = lp;
	}

	/**
	 * @return objeto GrammaticalStructureFactory
	 * @see GrammaticalStructureFactory
	 */
	GrammaticalStructureFactory getGsf() {
		return gsf;
	}

	/**
	 * Establece la factoría para obtener un GrammaticalStructure
	 * @param gsf objeto GrammaticalStructureFactory
	 * @see GrammaticalStructureFactory
	 */
	void setGsf(GrammaticalStructureFactory gsf) {
		this.gsf = gsf;
	}

	/**
	 * @return oración a analizar
	 * @see HasWord
	 */
	List<? extends HasWord> getSentence() {
		return sentence;
	}

	/**
	 * Establece la oración a analizar
	 * @param sentence oración a analizar
	 * @see HasWord
	 */
	void setSentence(List<? extends HasWord> sentence) {
		this.sentence = sentence;
	}

	/**
	 * @return el objeto que sirve para almacenar las tripletas obtenidas
	 */
	public TriplesCollection getTriplesCollection() {
		return this.triplesCollection;
	}
	
	/**
	 * Establece el objeto que sirve para almacenar las tripletas obtenidas
	 * @param triplesCollecion donde guardar las tripletas
	 * @see TriplesCollection
	 */
	public void setTriplesCollection(TriplesCollection triplesCollecion) {
		this.triplesCollection = triplesCollecion;
	}
	
	/**
	 * @return el identificador del libro al que pertenece la oracióna analizar
	 */
	String getBook() {
		return book;
	}

	/**
	 * Establece el identificador del libro al que pertenece la oracióna analizar
	 * @param book identifiador del libro
	 */
	void setBook(String book) {
		this.book = book;
	}
}
