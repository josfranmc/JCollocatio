package org.josfranmc.collocatio.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Permite construir una nueva base de datos para almacenar colocaciones y su información asociada.<p>
 * Se puede asignar un nombre para la nueva base de datos y una descrpción de la misma. Si no se hace se utilizarán un nombre y un texto 
 * descriptivo por defecto.<p>
 * Si la nueva base de datos a crear ya existe no se hace nada. No se borra la existente.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class DataBaseBuilder {

	private static final Logger log = Logger.getLogger(DataBaseBuilder.class);
	
	/**
	 * Conexión a la base de datos
	 */
	private Connection connection;
	
	/**
	 * Nombre de la nueva base de datos a crear
	 */
	private String dataBaseName;
	
	/**
	 * Descripción de la nueva base de datos a crear
	 */
	private String dataBaseDescription;
	
	
	/**
	 * Constructor por defecto.
	 */
	public DataBaseBuilder() {
		this(null, null);
	}

	/**
	 * Constructor. Fija el nombre de la nueva base de datos a utilizar, junto a su descripción.
	 * @param name nombre de la nueva base de datos a crear
	 * @param description descripción de la nueva base de datos a crear
	 */
	public DataBaseBuilder(String name, String description) {
		setDataBaseName(name);
		setDataBaseDescription(description);
	}
	
	/**
	 * Comprueba si existe la base de datos a crear comprobando si ya está registrado el nombre en la tabla de registro de la base de datos
	 * por defecto (col_default).
	 * @return <i>true</i> si no existe la base de datos que se quiere crear, <i>false</i> en caso contrario
	 */
	private boolean isNewDB() {
		boolean value = false;
		Connection connection = getConnection();
		PreparedStatement pstatement = null;
		final String query = "SELECT ID FROM col_registro WHERE NOMBRE = ?";
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, getDataBaseName());
			ResultSet rs = pstatement.executeQuery();
			if (!rs.next()) {
				value = true;
			}
		} catch (NullPointerException e) {	
			throw new NullPointerException("No se ha podido comprobar existencia de base de datos.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstatement != null) {
				try {
					pstatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return value; 
	}	
	
	/**
	 * Crea una nueva base de datos.<p>
	 * Primero se registra el nombre de la nueva base de datos en el registro de la base de datos principal del sistema (col_default). El
	 * nombre registrado será col_<i>nombre</i>, siendo <i>nombre</i> la cadena de texto establecida previamente. Si no se ha indicado ningún
	 * nombre se utilizará col_<i>fecha_hora</i>. Ej.: <i>col_190618_234510</i><br>
	 * Si se ha establecido algún texto descriptivon se utilizará este; en caso constrario se utilizará por defecto el texto:
	 * "DB creada el <i>fecha_lanzamiento</i>"
	 * @return un array en el que cada elemento contiene el resultado del proceso batch de creación ejecutado
	 */
	public int[] createNewDB() {
		int result[] = null;
		setConnection(connectionToDefaultDb());
		checkName();
		if (isNewDB()) {
			checkDescription();
			registerNewDB();
			result = createDB();
			closeConnection();
			log.info("CREADA NUEVA BASE DE DATOS: " + getDataBaseName());
		} else {
			log.error("Ya existe una base de datos con el nombre indicado.");
			throw new IllegalArgumentException("Ya existe una base de datos con el nombre indicado.");
		}
		return result;
	}
	
	/**
	 * @return una conexión a la base de datos por defecto
	 */
	private Connection connectionToDefaultDb() {
		return ConnectionFactory.getInstance().getConnection();
	}
	
	/**
	 * Da de alta en la base de datos principal del sistema (col_default) el nombre y descripción de la nueva base de datos a crear.
	 */
	private void registerNewDB() {
		final String queryString = "insert into col_registro(NOMBRE, DESCRIPCION, FECALT) VALUES(?,?,NOW())";
		PreparedStatement pstatement = null;
		try {
			pstatement = getConnection().prepareStatement(queryString);
			pstatement.setString(1, getDataBaseName());
			pstatement.setString(2, getDataBaseDescription());
			pstatement.executeUpdate();
		} catch (Exception e) {
			log.error(e);
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e) {
				log.error(e);
			}
		}		
	}
	
	/**
	 * Crea una nueva base de datos.<br>
	 * Si ya existe una base de datos con el nombre que se ha establecido previamente no se crea la nueva base de datos.
	 * @return un array en el que cada elemento contiene el resultado del proceso batch de creación ejecutado
	 */
	private int[] createDB() {
		int result[] = null;
		Statement s = null;
		try {
			s = getConnection().createStatement();
			s.addBatch("CREATE SCHEMA IF NOT EXISTS `" + getDataBaseName() + "` DEFAULT CHARACTER SET utf8;");
			s.addBatch("USE `" + getDataBaseName() + "`;");

			s.addBatch("CREATE TABLE IF NOT EXISTS `" + getDataBaseName() + "`.`col_collocatio` ("
					+  "`ID` INT NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la colocación',"
					+  "`DEPENDENCIA` VARCHAR(30) NOT NULL COMMENT 'Tipo de dependencia',"
					+  "`PALABRA1` VARCHAR(50) NOT NULL COMMENT 'Palabra 1 de la tripleta',"
					+  "`PALABRA2` VARCHAR(50) NOT NULL COMMENT 'Palabra 1 de la tripleta',"
					+  "`INFOMUTUA` DOUBLE NULL COMMENT 'Valor información mutua',"
					+  "PRIMARY KEY (`ID`))"
					+  "COMMENT = 'Colocaciones';");

			s.addBatch("CREATE TABLE IF NOT EXISTS `" + getDataBaseName() + "`.`col_aparece` ("
					+  "`IDCOL` INT NOT NULL COMMENT 'Identificador de la colocación',"
					+  "`IDLIB` VARCHAR(45) NOT NULL COMMENT 'Identificador del libro',"
					+  "CONSTRAINT `fk_collo_libros`"
					+  " FOREIGN KEY (`IDCOL`)"
					+  "  REFERENCES `" + getDataBaseName() + "`.`col_collocatio` (`ID`)"
					+  "  ON DELETE NO ACTION"
					+  "  ON UPDATE NO ACTION)"
					+  "COMMENT = 'Libros en los que aparece cada colocación';");
			
			s.addBatch("USE `col_default`;");

			result = s.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Comprueba si se ha establecido un nombre para la nueva base de datos a crear y que no excede el tamaño máximo permitido.
	 * Si no es así se levanta IllegalArgumentException.
	 */
	private void checkName() throws IllegalArgumentException, StringIndexOutOfBoundsException {
		if (getDataBaseName() == null) {
			throw new IllegalArgumentException("No se ha especificado un nombre para la nueva base de datos.");
		}
		if (getDataBaseName().length() > 14) {
			throw new StringIndexOutOfBoundsException("Nombre para la nueva base de datos mayor de 14 caracteres.");
		}
	}
	
	/**
	 * Comprueba si se ha establecido una descripción para la nueva base de datos a crear. Si no es así se asigna una por defecto.
	 * @throws StringIndexOutOfBoundsException
	 */
	private void checkDescription() throws StringIndexOutOfBoundsException {
		if (getDataBaseDescription() != null && getDataBaseDescription().length() > 50) {
			throw new StringIndexOutOfBoundsException("Descripción para la nueva base de datos mayor de 50 caracteres.");
		}		
		if (getDataBaseDescription() == null) {
			Date date = new Date();
			DateFormat hourFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			setDataBaseDescription("DB creada el " + hourFormat.format(date));
		}
	}
	
	/**
	 * Cierra la conexión con la base de datos.
	 */
	private void closeConnection() {
		if (getConnection() != null) {
			try {
				getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return el nombre de la base de datos a crear
	 */
	public String getDataBaseName() {
		return dataBaseName;
	}

	/**
	 * Establece el nombre de la base de datos a crear.
	 * @param dataBaseName nombre a asignar
	 */
	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}

	/**
	 * @return la descripción de la base de datos a crear
	 */
	public String getDataBaseDescription() {
		return dataBaseDescription;
	}

	/**
	 * Establece la descripción de la base de datos a crear.
	 * @param dataBaseDescription descripción a asignar
	 */
	public void setDataBaseDescription(String dataBaseDescription) {
		this.dataBaseDescription = dataBaseDescription;
	}

	/**
	 * @return el objeto Connection utilizado para conectar con la base de datos
	 */
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * Establece el objeto Connecion a utilizarse para conectar con la base de datos.
	 * @param connection objeto Connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
