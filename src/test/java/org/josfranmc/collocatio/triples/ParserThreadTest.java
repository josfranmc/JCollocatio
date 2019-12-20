package org.josfranmc.collocatio.triples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.TreebankLanguagePack;

/**
 * Clase que implementa los test para probar los métodos de la clase ParserThread
 * @author Jose Francisco Mena Ceca
 * @version 2.0
 */
public class ParserThreadTest {

	private static LexicalizedParser lp;
	private static TreebankLanguagePack tlp;
	private static GrammaticalStructureFactory gsf;
	
	private static File fileTemp;
	private static String filePath;
	private static String fileName;
	

	/**
	 * Método ejecutado una vez antes de la ejecución de todos los tests.<br>
	 * Inicializa variables y crea un fichero temporal de prueba para analizar. Este fichero contiene dos oraciones y de él se pueden obtener las siguientes tripletas:
	 * <ul>
	 * <li>cop(red, is)</li>
     * <li>nsubj(blue, car)</li>
     * <li>root(root, red)</li>
     * <li>nsubj(red, car)</li>
     * <li>root(root, blue)</li>
     * <li>cop(blue, is)</li>
     * <li>det(car, the)</li>
	 * </ul>
	 */
	@BeforeClass 
	public static void setUp() {
		lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		tlp = lp.getOp().langpack();
	    gsf = tlp.grammaticalStructureFactory();
	    
	    filePath = System.getProperty("user.dir").concat(System.getProperty("file.separator"));
	    fileName = "11111.txt";
	    createTestFile();
	}

	/**
	 * Comprueba que se obtienen las tripletas correctas. Para ello se usa el fichero temporal creado.<p>
	 * Se comprueba:
	 * <ul>
	 * <li>que el objeto TriplesCollection devuelto no sea null,</li>
	 * <li>que se obtienen todas las tripletas, que deben ser ocho</li>
	 * <li>que se obtiene la tripleta det(car, the)</li>
	 * <li>que el total de tripletas det(car, the) es 2</li>
	 * <li>que el total de tripletas nsubj(blue, car) es 1</li>
	 * </ul>
	 */
//	@Test
//	public void testParserThreadRun() {
//		TriplesCollection c = new TriplesCollection();
//	    for (List<? extends HasWord> sentence : getSentences()) { 
//    		ParserThread parserThread = new ParserThreadBuilder()
//
//        	        .setTriplesCollection(c)
//        	        .setFile("11111")
//        	        .build();
//	    	parserThread.call();
//	    }
//		TripleEvents te1 = c.getTriples().get(getTriple1());
//		TripleEvents te2 = c.getTriples().get(getTriple2());
//		
//	    assertNotNull("TriplesCollection es null", c);
//	    assertEquals("Número incorrecto de tripletas devueltas", 8, c.getTotalTriples());
//	    assertNotNull("Tripleta errónea", c.getTriples().get(getTriple2()));
//	    assertEquals("Número total de tripleta erróneo (2)", 2, te2.getTotalEvents());
//	    assertEquals("Número total de tripleta erróneo (1)", 1, te1.getTotalEvents());
//	}
	
	/**
	 * Método ejecutado una vez después de la ejecución de todos los tests
	 */
	@AfterClass
	public static void done() {
		if (fileTemp.exists()) {
			fileTemp.delete();
		}
	}
	
	/**
	 * Crea un fichero temporal con dos oraciones
	 */
	private static void createTestFile() {
	    fileTemp = new File(filePath, fileName);
	    try {
			if (fileTemp.createNewFile()) {
		        FileWriter fichero = null;
		        PrintWriter pw = null;
		        try {
		            fichero = new FileWriter(filePath.concat(fileName));
		            pw = new PrintWriter(fichero);
		            pw.println("The car is blue. The car is red");
		        } catch (Exception e) {
		            e.printStackTrace();
		        } finally {
		           try {
		           if (null != fichero)
		              fichero.close();
		           } catch (Exception e2) {
		              e2.printStackTrace();
		           }
		        }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Devuelve las oraciones del fichero de prueba preparadas para su análisis
	 */
	private List<List<? extends HasWord>> getSentences() {
		DocumentPreprocessor dp = new DocumentPreprocessor(filePath.concat(fileName));
		List<List<? extends HasWord>> sentences = new ArrayList<>();
	    for (List<HasWord> sentence : dp) {
	    	sentences.add(sentence);
	    }
	    return sentences;
	}
	
	/**
	 * La tripleta devuelta aparece una vez en el fichero de prueba
	 */
	private Triple getTriple1() {
    	Triple triple = new Triple();
    	triple.setDependency("nsubj");
    	triple.setHead("blue");
    	triple.setDependent("car");
    	return triple;
	}	
	
	/**
	 * La tripleta devuelta aparece dos veces en el fichero de prueba
	 */
	private Triple getTriple2() {
    	Triple triple = new Triple();
    	triple.setDependency("det");
    	triple.setHead("car");
    	triple.setDependent("the");
    	return triple;
	}
}
