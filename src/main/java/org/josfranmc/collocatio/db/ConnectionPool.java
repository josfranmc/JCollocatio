package org.josfranmc.collocatio.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.josfranmc.collocatio.util.PropertiesFile;

/**
 * Permite crear un pool de conexiones a una base de datos.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class ConnectionPool implements IDBConnection {

	private static final Logger log = Logger.getLogger(ConnectionPool.class);
	
	/**
	 * Ruta del fichero de propiedades en el classpath
	 */
	private final static String DB_PROPERTIES_FILE = "db/DBPool.properties";
	/**
	 *  Nombre de la propiedad del fichero que indica el nombre del driver a cargar
	 */
	private final static String PROPERTY_DRIVER = "driver";
	/**
	 *  Nombre de la propiedad del fichero que indica el nombre del host en el que se aloja la base de datos
	 */
	private final static String PROPERTY_HOST = "host";
	/**
	 *  Nombre de la propiedad del fichero que indica el número de puerto de la base de datos
	 */
	private final static String PROPERTY_PORT = "port";
	/**
	 *  Nombre de la propiedad del fichero que indica el nombre de la base de datos
	 */
	private final static String PROPERTY_DBNAME = "dbname";
	/**
	 *  Nombre de la propiedad del fichero que indica la cadena con los parámetros de conexión
	 */
	private final static String PROPERTY_PARAMS = "params";
	/**
	 *  Nombre de la propiedad del fichero que indica el nombre de la cuenta de usuario
	 */
	private final static String PROPERTY_USER = "user";
	/**
	 *  Nombre de la propiedad del fichero que indica el password de la cuenta de usuario
	 */
	private final static String PROPERTY_PASSWORD = "password";
	
	/**
	 * Nombre del driver a utilizar para conectar con la base de datos
	 */
	private static String DB_DRIVER;
	
	/**
	 * URL de conexión a la base de datos
	 */
	private static String DB_URL;
	
	/**
	 * Nombre de la cuenta de usuario para conectar con la base de datos
	 */
	private static String DB_USER;
	
	/**
	 * Clave de la cuenta de usuario para conectar con la base de datos
	 */
	private static String DB_PASSWORD;
	
	/**
	 * Configura el acceso a la bas de datos y obtiene conexiones a la misma
	 */
	BasicDataSource basicDataSource;

	/**
	 * Constructor principal. Lee el fichero de configuración y crea el pool de conexiones con los datos leidos.
	 * Se se indica un nombre de base de datos la conexión se realizará sobre ella, en lugar de utilizar el valor por defecto del fichero de
	 * configuración.
	 * @param dataBase nombre de la base de datos con la que conectar
	 */
	ConnectionPool(String dataBase) {
		readPropertiesFile(dataBase);
		basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName(DB_DRIVER);
		basicDataSource.setUrl(DB_URL);
		basicDataSource.setUsername(DB_USER);
		basicDataSource.setPassword(DB_PASSWORD);
		basicDataSource.setMaxActive(-1);
		log.info("Creado pool de conexiones a " + DB_URL.substring(0, DB_URL.indexOf("?")));
	}
	
	/**
	 * Devuelve una conexión del pool de conexiones
	 */
	@Override
	public Connection getConnection() {
		Connection connection = null;
		try {
			connection = basicDataSource.getConnection();
		} catch (SQLException e) {
			log.error(e);
			//e.printStackTrace();
		}
		return connection;
	}
	
	/**
	 * Lee el fichero de propiedades que contiene los datos de configuración para la conexión a la base de datos.<br>
	 * Si se indica un nombre de base de datos se tratará de conectar a ella, si no se conectará con la base  de datos
	 * indicada en el fichero de propiedades.
	 * @param dbName nombre de la base de datos con la que conectar
	 * @see PropertiesFile
	 */
	private void readPropertiesFile(String dbName) {
		try {
			Properties prop = PropertiesFile.loadProperty(DB_PROPERTIES_FILE);
			
			DB_DRIVER = prop.getProperty(PROPERTY_DRIVER);
			String host = prop.getProperty(PROPERTY_HOST);
			String port = prop.getProperty(PROPERTY_PORT);
			String dbname = (dbName == null) ? prop.getProperty(PROPERTY_DBNAME) : dbName;
			String params = prop.getProperty(PROPERTY_PARAMS);
			DB_USER = prop.getProperty(PROPERTY_USER);
			DB_PASSWORD = prop.getProperty(PROPERTY_PASSWORD);
			DB_URL = "jdbc:mysql://" + host + ":" + port + "/" + dbname + "?" + params;

			log.info("Cargando datos de fichero " + DB_PROPERTIES_FILE);
			log.info("   driver = " + DB_DRIVER);
			log.info("   host = " + host);
			log.info("   port = " + port);
			log.info("   params = " + params);
			log.info("   dbname = " + dbname);
			log.info("   user = " + DB_USER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
