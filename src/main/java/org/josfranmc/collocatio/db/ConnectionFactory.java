package org.josfranmc.collocatio.db;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.josfranmc.collocatio.db.IDBConnection;
import org.josfranmc.collocatio.util.PropertiesFile;

/**
 * Factoría para crear y gestionar el acceso a las base de datos.<p>
 * Para crear un objeto de esta clase hay que llamar al método de clase <i>getInstance().</i> La primera vez que se llama se crea una instancia de la 
 * factoría para gestionar las conexiones con la base de datos indicada. Si no se indica ninguna base de datos se asume el uso de la base de datos
 * por defecto. En llamadas sucesivas se obtiene la instancia ya creada.<p>
 * Internamente se instancia el objeto apropiado para el acceso a la base de datos. El tipo de este objeto a instanciar se obtiene leyendo
 * el fichero de propiedades.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see IDBConnection
 */
public class ConnectionFactory {

	private static final Logger log = Logger.getLogger(ConnectionFactory.class);
	
	/**
	 *  Nombre de la base de datos por defecto
	 */
	public final static String DEFAULT_DB = "col_default";
	
	/**
	 *  Ruta del fichero de propiedades en el classpath
	 */
	private final static String DB_PROPERTY_FACTORY_FILE = "db/DBConnFactory.properties";
	
	/**
	 *  Nombre de la propiedad del fichero que indica el nombre de la clase a instanciar
	 */
	private final static String PROPERTY_DEFAULT_DB_CLASS = "defaultDBClass";
	
	/**
	 *  Nombre de la clase a instanciar
	 */
	private static String DEFAULT_DB_CLASS;
	
	/**
	 * Factorías creadas. Una por cada base de datos a utilizar.
	 */
	private static Map<String, ConnectionFactory> instances = new HashMap<String, ConnectionFactory>();
	
	/**
	 * Referencias a los objetos que gestionan el acceso a la base de datos. Una por cada base de datos a utilizar.
	 */
	private static Map<String, IDBConnection> dbConnections = new HashMap<String, IDBConnection>();
	

	/**
	 * Constructor. Crea una instancia de la clase a utilizar para conectarse a la base de datos indicada. El tipo de la clase que se instancia
	 * se obtiene del fichero de propiedades que se carga.<p>
	 * Las instancias creadas van guardándose en un mapa. Por cada base de datos indicada se crea la correspondiente instancia de conexión.
	 * @param dbName nombre de la base de datos con la que conectar
	 */
	private ConnectionFactory(String dbName) {
		log.info("CONFIGURANDO ACCESO A BASE DE DATOS");
		readPropertiesFile();
		try {
			Class<?> c = Class.forName(DEFAULT_DB_CLASS);
			Constructor<?> constructor = c.getDeclaredConstructor(String.class);
			constructor.setAccessible(true);
			IDBConnection dbConnection = (IDBConnection) constructor.newInstance(dbName);
			dbConnections.put(dbName, dbConnection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return una conexión a la base de datos por defecto
	 */
	public Connection getConnection() {
		return getConnection(ConnectionFactory.DEFAULT_DB);
	}

	/**
	 * Devuelve una conexión a la base de datos indicada.
	 * @param dbName nombre de la base de datos de la que obtener una conexión
	 * @return conexión a la base de datos
	 */
	public Connection getConnection(String dbName) {
		return dbConnections.get(dbName).getConnection();
	}
	
	/**
	 * Obtiene una instancia de la factoría para conectarse a la base de datos por defecto (col_default).<p>
	 * Sólo se permite la creación de una instancia, por lo que una vez creada se devuelve la misma instancia en llamadas sucesivas.
	 * @return la instancia de la factoría para la base de datos por defecto
	 */
	public static ConnectionFactory getInstance() {
		return ConnectionFactory.getInstance(ConnectionFactory.DEFAULT_DB);
	}
	
	/**
	 * Obtiene una instancia de la factoría y establece la conexión con la base de datos cuyo nombre se pasa por parámetro.<p>
	 * Sólo se permite la creación de una instancia por cada base de datos indicada, por lo que una vez creada se devuelve la misma instancia en llamadas sucesivas.
	 * @param dbName nombre de la base de datos con la que conectar
	 * @return la instancia de la factoría para la base de datos indicada
	 */	
	public static ConnectionFactory getInstance(String dbName) {
		if (!instances.containsKey(dbName)) {
			synchronized (ConnectionFactory.class) {
				if (!instances.containsKey(dbName)) {
					instances.put(dbName, new ConnectionFactory(dbName));
				}
			}
		}
		return instances.get(dbName);
	}

	/**
	 * Elimina la instancia de una factoría, si esta existe.
	 * @param dbName nombre de la instancia a eliminar
	 */
	public static void deleteInstance(String dbName) {
		if (instances.containsKey(dbName)) {
			synchronized (ConnectionFactory.class) {
				if (instances.containsKey(dbName)) {
					instances.remove(dbName);
				}
			}
		}
	}
	
	/**
	 * Elimina todas las instancias creadas de la factoría.
	 */
	public static void deleteAllInstances() {
		if (!instances.isEmpty()) {
			synchronized (ConnectionFactory.class) {
				if (!instances.isEmpty()) {
					instances.clear();;
				}
			}
		}
	}
	
	/**
	 * Comprueba si existe una instancia de la factoría para la base de datos indicada.
	 * @param dbname nombre de la base de datos a comprobar
	 * @return <i>true</i> si existe una instancia de ConnectionFactory para la base de datos indicada, <i>false</i> en caso contrario
	 */
	public static boolean isInstance(String dbname) {
		return (instances.get(dbname) != null);
	}
	
	/**
	 * Lee el fichero de propiedades que contiene los datos de configuración para la factoría. Principalmente contiene
	 * el nombre de la clase a utilizar para acceder a la base de datos.
	 * @see PropertiesFile
	 */
	private void readPropertiesFile() {
		try {
			Properties prop = PropertiesFile.loadProperty(DB_PROPERTY_FACTORY_FILE);
			DEFAULT_DB_CLASS = prop.getProperty(PROPERTY_DEFAULT_DB_CLASS);
			log.info("Cargando datos de fichero " + DB_PROPERTY_FACTORY_FILE);
			log.info("   DefaultDBClass = " + DEFAULT_DB_CLASS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
