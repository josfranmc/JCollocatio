package org.josfranmc.collocatio.algorithms;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.josfranmc.collocatio.triples.Triple;
import org.josfranmc.collocatio.triples.TripleEvents;

import com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;

/**
 * Realiza el cálculo del valor de información mutua para un grupo de tripletas. El grupo de tripletas analizado y los datos necesarios para hacer
 * los cálculos se pasan mediante un objeto TriplesData.<p>
 * Los objetos de esta clase se ejecutarán como hilos independientes desde el proceso principal. En concreto, se debe crear un objeto por cada uno
 * de los objetos TriplesData que se han tenido que obtener previamente.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see TriplesData
 */
public class CalculateMutualInformationThread implements Runnable{

	private static final Logger log = Logger.getLogger(CalculateMutualInformationThread.class);
	
	private static final long INSERTS_LIMIT = 5000;

	/**
	 * Datos sobre los que realizar los cálculos para obtener el valor de información mutua
	 */
	private TriplesData data;
	
	/**
	 * Conexión a la base de datos en la que guardar los datos calculados
	 */
	private Connection connection;
	
	/**
	 * Si se debe guardar en base de datos o no
	 */
	private boolean saveDB;

	/**
	 * Contador para saber el número total de inserciones realizadas
	 */
	private long insertsTotal;
	
	/**
	 * Contador parcial para saber el número de inserciones que se llevan y saber cuando hacer commit
	 */
	private long insertsCount;


	/**
	 * Constructor principal. 
	 * @param data encapsula todos los datos necesarios para realizar los cálculos 
	 * @param totalTriples número total de tripletas que se han obtenido (todas las tripletas de todos los tipos de dependencia posibles)
	 * @see TriplesData
	 */
	CalculateMutualInformationThread(TriplesData data, Connection connection) {
		this.data = data;
		this.connection = connection;
		this.insertsCount = 0;
		this.insertsTotal = 0;
		this.saveDB = (connection == null) ? false : true;
	}

	/**
	 * Obtiene un PreparedStatement para realizar la inserción de las tripletas.<br>Permite obtener el ID del último registro insertado.
	 * @return el PreparedStatement listo para usarse
	 */
	private PreparedStatement getPreparedStatementToCollocations() {
		PreparedStatement psCollocatio = null;
		try {
			final String insertSql = "insert into col_collocatio(DEPENDENCIA, PALABRA1, PALABRA2, INFOMUTUA) VALUES(?,?,?,?)";
			psCollocatio = getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return psCollocatio;
	}
	
	/**
	 * Cierra un PreparedStatement.
	 * @param pstatement objeto de tipo PreparedStatement a cerrar
	 */
	private void closePreparedStatement(PreparedStatement pstatement) {
		try {
			pstatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Realiza el cálculo del valor de información mutua para cada tripleta de la colección pasada al crear el objeto, la cual está formada
	 * por tripletas pertenecientes a un tipo concreto de dependencia. 
	 */
	@Override
	public void run() {
		Triple triple = null;
		long totalTriples = data.getTotalTriples();                         // total de tripletas obtenidas (todas)
		long totalTriplesByDependency = data.getTotalTriplesByDependency(); // total de tripletas de un determinado tipo de dependencia
		long totalTriple = 0;                                               // total de ocurrencias de una tripleta concreta
		long totalTriplesByDependencyAndWord1 = 0;                          // total de ocurrencias de la palabra 1 de un tripleta de un tipo de dependencia concreto
		long totalTriplesByDependencyAndWord2 = 0;                          // total de ocurrencias de la palabra 2 de un tripleta de un tipo de dependencia concreto
		
		double P_A_B_C = 0;                           
		double P_B = 0;
		double P_A_given_B = 0;
		double P_C_given_A = 0;
		double mutualInformation = 0;

		PreparedStatement pstatement = getPreparedStatementToCollocations();

		try {
			log.info("Inicio hilo para dependencia " + data.getDependency());
			for (Entry<Triple, TripleEvents> entry : data.getTriplesMap().entrySet()) {
				P_A_B_C = P_B = P_A_given_B = P_C_given_A = mutualInformation = 0;
				
				// tripleta sobre la que calcular su valor de inforamción mutua
				triple = entry.getKey();    
				// ocurrencias de la tripleta
				TripleEvents events = entry.getValue();
				// total de ocurrencias de esta tripleta
				totalTriple = events.getTotalEvents();  
				
				// número de ocurrencias de la palabra 1 en el conjunto de las tripletas del tipo de dependencia que se está analizando 
				String word1 = triple.getWord1();
				totalTriplesByDependencyAndWord1 = data.getWord1FrecuencyMap().get(word1);
				// número de ocurrencias de la palabra 2 en el conjunto de las tripletas del tipo de dependencia que se está analizando 
				String word2 = triple.getWord2();
				totalTriplesByDependencyAndWord2 = data.getWord2FrecuencyMap().get(word2);

				P_A_B_C = (double) totalTriple / (double) totalTriples;	                                      
				P_A_B_C = P_A_B_C - data.getAdjustedFrequency();                                                     
				P_B =  (double) totalTriplesByDependency / (double) totalTriples;	                          
				P_A_given_B = (double) totalTriplesByDependencyAndWord1 / (double) totalTriplesByDependency;
				P_C_given_A = (double) totalTriplesByDependencyAndWord2 / (double) totalTriplesByDependency;
	
				mutualInformation = getLogBase2(P_A_B_C / (P_B * P_A_given_B * P_C_given_A));
				mutualInformation = new BigDecimal(mutualInformation).setScale(1, RoundingMode.HALF_EVEN).doubleValue();
				triple.setMutualInformation(mutualInformation);
				
				if (isSaveDB()) {
					long generatedId = saveCollocation(triple, pstatement);
					if (generatedId > 0) {
						try {
							saveBooks(events.getBooks(), generatedId);
						} catch (Exception e) {
							log.error("No se han podido guardar libros para tripleta " + triple.toString());
							log.error(e);
						}
					}
					incrementInsertsCount();
					if (getInsertsCount() == INSERTS_LIMIT) {
						doCommit();
						resetInsertsCount();
					}
				}	
			}
		} finally {
			closePreparedStatement(pstatement);
			closeConnection();
			log.info("Fin hilo dependencia " + data.getDependency() + ", inserciones " + this.insertsTotal);
			this.data = null;
		}
	}

	/**
	 * Calcula el logaritmo en base de dos de un número.<p>
	 * Si el número pasado es cero o negativo se devuelve el valor cero.
	 * @param num número del que se quiere calcular su logaritmo en base dos
	 * @return el logaritmo en base dos de un número
	 */
	private Double getLogBase2(double num) {
		Double result = (double) 0;
		try {
			result = Math.log10(num) / Math.log10(2);
		} catch (Exception e) {
			log.error("Error logaritmo ");
			result = (double) 0;
		}
		return result;
	}
	
	/**
	 * Guarda en la base de datos la información de una tripleta junto a su valor de información mutua.<p>
	 * Si se produce alguna excepción por bloqueos que impide realizar la inserción se hace commit de la realizado hasta ahora y se vuelve
	 * a intentar realizar la inserción otra vez.
	 * @param triple tripleta que guardar
	 * @param pstatement sentencia sql a ejecutar
	 * @return el ID con el que se ha guardado el registro en la base de datos
	 * @see Triple
	 */
	private long saveCollocation(Triple triple, PreparedStatement pstatement) {
		long generatedId = 0;
		boolean doIt = false;
		while(!doIt) {
			try {
				pstatement.setString(1, triple.getDependency());
				pstatement.setString(2, triple.getWord1());
				pstatement.setString(3, triple.getWord2());
				pstatement.setDouble(4, triple.getMutualInformation());
				int affectedRows = pstatement.executeUpdate();
				if (affectedRows > 0) {
			        ResultSet generatedKeys = pstatement.getGeneratedKeys();
					if (generatedKeys.next()) {
						generatedId = generatedKeys.getLong(1);
					}
				} else {
					log.error("No se pudo guardar " + triple.getDependency() + ":" + triple.getWord1() + ":" + triple.getWord2());
				}
				doIt = true;
				this.insertsTotal++;
			} catch (MySQLTransactionRollbackException e) {
				log.warn(e);
				doCommit();		
			} catch (MysqlDataTruncation e) {
				log.error(e);
				log.error("Length p1 = " + triple.getWord1().length() + ", Length p2 = " + triple.getWord2().length());
			} catch (Exception e) {
				log.error("saveCollocation " + triple.toString() + " mi " + triple.getMutualInformation());
				log.error(e);
			} 
		}
		return generatedId;
	}

	/**
	 * Guarda en la base de datos los libros en los que se ha encontrado una tripleta, la cual ya ha sido insertada en la base de datos
	 * @param books conjunto de libros a guardar
	 * @param idCol identificador asignado en la base de datos de la tripleta que se acaba de guardar, la cual ha sido encontrada en los libros a insertar.
	 * @throws Exception 
	 */
	private void saveBooks(Set<String> books, long idCol) throws Exception {
		PreparedStatement pstatement = null;
		StringBuilder insertSql = new StringBuilder("insert into col_aparece(IDCOL, IDLIB) VALUES ");
		
		final String[] booksArray = books.toArray(new String[books.size()]);
		final int totalBooks = booksArray.length;
		
		// construimos la sentencia insert, que insertará varias filas (tantas como libros haya)
		for (int i = 0; i < totalBooks; i++) {
			insertSql.append("(").append(idCol).append(", ?)");
			if (i < totalBooks-1) {
				insertSql.append(", ");
			}
		}
		insertSql.append(";");
		
		try {
			pstatement = getConnection().prepareStatement(insertSql.toString());
			// asignamos los parámetros
			for (int i = 0; i < totalBooks; i++) {
				pstatement.setString(i+1, booksArray[i]);
			}
			pstatement.executeUpdate();
		} finally {
			if (pstatement != null) {
				pstatement.close();
			}
		}
	}	
	
	/**
	 * @return conexión a la base de datos que se utiliza 
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Cierra la conexión establecida con la base de datos que se ha utiliado para guardar las colocaciones
	 */
	private void closeConnection() {
		if (connection != null) {
			doCommit();
			try {
				connection.close();
			} catch (SQLException e) {
				log.error(e);
				e.printStackTrace();
			}
		} else {
			log.error("La conexión es null, no se puede cerrar y validar los cambios.");
		}
	}
	
	/**
	 * Realiza commit en la base de datos, validando así las inserciones realizadas hasta el momento
	 */
	private void doCommit() {
		if (connection != null) {
	    	try {
				if (!connection.getAutoCommit()) {
					connection.commit();
				}
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}
		} else {
			log.error("La conexión es null, no se pueden validar los cambios.");
		}
	}
	
	/**
	 * @return el número de inserciones realizadas hasta el momento
	 */
	private long getInsertsCount() {
		return this.insertsCount;
	}	
	
	/**
	 * Incrementa el contador parcial de inserciones realizadas
	 */
	private void incrementInsertsCount() {
		this.insertsCount++;
	}	
	
	/**
	 * Resetea el contador parcial poniéndolo a cero
	 */
	private void resetInsertsCount() {
		this.insertsCount = 0;
	}	
	
	/**
	 * @return <i>true</i> si se deben guardar los resultados obtenidos en base de datos, <i>false</i> en caso contrario
	 */
	public boolean isSaveDB() {
		return saveDB;
	}
}
