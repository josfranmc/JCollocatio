package org.josfranmc.collocatio.triples;

import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;

/**
 * Crea y configura un objeto ParserThread. 
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see ParserThread
 * @see TriplesCollection
 */
public class ParserThreadBuilder {

	private LexicalizedParser lp = null;
	private GrammaticalStructureFactory gsf = null;
	private List<? extends HasWord> sentence = null;
	private String book = null;

	private TriplesCollection triplesCollecion = null;
    
	
	/**
	 * Establece el objeto LexicalizedParser a utiizar.
	 * @param lp objeto LexicalizedParser
	 * @return referencia al propio objeto builder (this)
	 * @see LexicalizedParser
     * @see ParserThreadBuilder
	 */
    public ParserThreadBuilder setLexicalizedParser(LexicalizedParser lp) {
        this.lp = lp;
        return this;
    }    
    
    /**
     * Establece el objeto TriplesCollection que almacena las tripletas obtenidas.
     * @param triplesCollection objeto TriplesCollection
     * @return referencia al propio objeto builder (this)
     * @see TriplesCollection
     * @see ParserThreadBuilder
     */
    public ParserThreadBuilder setTriplesCollection(TriplesCollection triplesCollection) {
        this.triplesCollecion = triplesCollection;
        return this;
    }
    
    /**
     * Establece el objeto GrammaticalStructureFactory a utilizar.
     * @param gsf objeto GrammaticalStructureFactory
     * @return referencia al propio objeto builder (this)
     * @see GrammaticalStructureFactory
     * @see ParserThreadBuilder
     */
    public ParserThreadBuilder setGrammaticalStructureFactory(GrammaticalStructureFactory gsf) {
        this.gsf = gsf;
        return this;
    } 
    
    /**
     * Establece la oración a analizar
     * @param sentence oración a analizar
     * @return referencia al propio objeto builder (this)
     * @see ParserThreadBuilder
     * @see HasWord
     */
    public ParserThreadBuilder setSentence(List<? extends HasWord> sentence) {
        this.sentence = sentence;
        return this;
    }
    
    /**
     * Establece el identificador del libro al que pertenece la oración a analizar.
     * @param book identificador del libro
     * @return referencia al propio objeto builder (this)
     * @see ParserThreadBuilder
     */
    public ParserThreadBuilder setBook(String book) {
        this.book = book;
        return this;
    }

    /**
     * Crea un objeto ParserThread y lo configura con las opciones que se han tenido que establecer previamente. Todas las opciones de
     * configuración son obligatorias, por lo que si alguna no ha sido establecida se genera una excepción del tipo IllegalArgumentException.
     * @return objeto ParserThread
     * @see ParserThread
     * @throws IllegalArgumentException
     */
    public ParserThread build() {
    	if (this.lp == null) {
            throw new IllegalArgumentException("LexicalizedParser es requerido");
    	}
    	if (this.gsf == null) {
            throw new IllegalArgumentException("GrammaticalStructureFactory es requerido");
    	}
    	if (this.sentence == null) {
            throw new IllegalArgumentException("sentence es requerido");
    	}
    	if (this.book == null) {
            throw new IllegalArgumentException("Book es requerido");
    	}
    	if (this.triplesCollecion == null) {
            throw new IllegalArgumentException("TriplesCollecion es requerido");
    	}
    	ParserThread pt = new ParserThread();
    	pt.setLp(this.lp);
    	pt.setGsf(this.gsf);
    	pt.setSentence(this.sentence);
    	pt.setTriplesCollection(this.triplesCollecion);
    	pt.setBook(this.book);
        return pt;
    }
}