package org.josfranmc.collocatio.service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.josfranmc.collocatio.db.ConnectionFactory;
import org.josfranmc.collocatio.service.QueryType;
import org.josfranmc.collocatio.service.domain.Collocatio;

/**
 * Implementa las operaciones que pueden realizarse sobre la tabla col_collocatio.<br>
 * Realiza la conexión a la base de datos de forma transparente al usuario.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see ICollocatioDao
 * @see Collocatio
 * @see QueryType
 */
public class CollocatioDao implements ICollocatioDao {
	
	private static final Logger log = Logger.getLogger(CollocatioDao.class);
	
	/**
	 * Nombre de la base de datos a la que conectarse
	 */
	private String dbName;
	
	
	/**
	 * Constructor por defecto
	 */
	public CollocatioDao() {
		setDataBase(ConnectionFactory.DEFAULT_DB);
	}
	
	/**
	 * @return una conexión a la base de datos
	 */
	private Connection getConnection() {
		Connection connection = ConnectionFactory.getInstance(this.dbName).getConnection();
		log.info("Accediendo a " + this.dbName);
		return connection;
	}
	
	/**
	 * @return un objeto de tipo Collocatio sin datos
	 * @see Collocatio
	 */
	@Override
	public Collocatio createElement() {
		return new Collocatio();
	}

	
	/**
	 * Elimina una base de datos.
	 * @param dbname identificador del registro a eliminar
	 */
	@Override
	public void deleteDataBase(String dbname) {
		Connection connection = null;
		PreparedStatement pstatement = null;
		try {
			connection = getConnection();
			String sqlInsert = "DROP DATABASE IF EXISTS ".concat(dbname);
			pstatement = connection.prepareStatement(sqlInsert);
			pstatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Guarda una coloación en la base de datos.
	 * @param entity objeto colocación a guardar
	 * @see Collocatio
	 */
	@Override
	public long save(Collocatio entity) {
		Connection connection = null;
		PreparedStatement pstatement = null;
		long generatedId = 0;
		try {
			connection = getConnection();
			String sqlInsert = "INSERT INTO col_collocatio (dependencia, palabra1, palabra2, infomutua) VALUES (?,?,?,?)";
			pstatement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
			pstatement.setString(1, entity.getDependencia());
			pstatement.setString(2, entity.getPalabra1());
			pstatement.setString(3, entity.getPalabra2());
			pstatement.setDouble(4, entity.getInfomutua());
			int affectedRows = pstatement.executeUpdate();
			if (affectedRows > 0) {
		        ResultSet generatedKeys = pstatement.getGeneratedKeys();
				if (generatedKeys.next()) {
					generatedId = generatedKeys.getLong(1);
					log.info("Guardado nuevo registro en " + getDbName() + " con id " + generatedId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstatement != null) {
				try {
					pstatement.close();
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return generatedId;
	}

	/**
	 * @param id identificador del registro a consultar
	 * @return la colocación correspondiente al identificador indicado
	 */
	@Override
	public Collocatio getElementById(Long id) {
		Collocatio col = null;
		Connection connection = null;
		PreparedStatement pstatement = null;
		try {
			connection = getConnection();
			pstatement = connection.prepareStatement("SELECT * FROM col_collocatio WHERE ID = ?");
			pstatement.setLong(1, id);
			ResultSet rs = pstatement.executeQuery();
			if (rs.next()) {
				col = getCollocatio(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstatement != null) {
				try {
					pstatement.close();
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return col; 
	}

	/**
	 * Elimina una colocación de la base de datos.
	 * @param id identificador del registro a eliminar
	 * @return número de registros afectados
	 */
	@Override
	public long deleteElement(Long id) {
		long result = 0;
		Connection connection = null;
		PreparedStatement pstatement = null;		
		try {
			connection = getConnection();
			pstatement = null;
			pstatement = connection.prepareStatement("DELETE FROM col_collocatio WHERE ID = ?");
			pstatement.setLong(1, id);
			result = pstatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstatement != null) {
				try {
					pstatement.close();
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * @return una lista con todas las colocaciones existentes en la base de datos
	 * @see Collocatio
	 */
	@Override
	public List<Collocatio> getAllElements() {
		List<Collocatio> colList = new ArrayList<Collocatio>();
		Collocatio col = null;
		Connection connection = null;
		PreparedStatement pstatement = null;
		long idcol = -1;
		try {
			connection = getConnection();
			pstatement = connection.prepareStatement("SELECT * FROM col_collocatio c, col_aparece a WHERE c.id=a.idcol");
			ResultSet rs = pstatement.executeQuery();
			while (rs.next()) {
				if (idcol != rs.getLong("ID")) {
					idcol = rs.getLong("ID");
					col = getCollocatio(rs);
			        colList.add(col);
			        col.setBook(rs.getString("IDLIB"));
				} else {
			        col.setBook(rs.getString("IDLIB"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstatement != null) {
				try {
					pstatement.close();
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return colList; 
	}
	
	/**
	 * Consulta todas las colocaciones existentes en la base de datos de forma paginada, devolviendo la página indicada.
	 * @param offset página que consultar
	 * @param size número de registros de la página
	 * @return una lista con las colocaciones recogidas en la página indicada
	 * @see Collocatio
	 */
	@Override
	public List<Collocatio> getAllElementsByPage(int offset, int size) {
		List<Collocatio> colList = new ArrayList<Collocatio>();
		Collocatio col = null;
		Connection connection = null;
		PreparedStatement pstatement = null;
		long idcol = -1;
		try {
			connection = getConnection();
			pstatement = connection.prepareStatement("SELECT * FROM col_collocatio c, col_aparece a WHERE c.id=a.idcol ORDER BY id LIMIT " + ((offset+size)-1) + "," + size);
			ResultSet rs = pstatement.executeQuery();
			while (rs.next()) {
				if (idcol != rs.getLong("ID")) {
					idcol = rs.getLong("ID");
					col = getCollocatio(rs);
			        colList.add(col);
			        col.setBook(rs.getString("IDLIB"));
				} else {
			        col.setBook(rs.getString("IDLIB"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstatement != null) {
				try {
					pstatement.close();
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return colList; 
	}
	
	@Override
	public List<Collocatio> findCollocationsByWords(List<String> words, int offset, int size) {
		return buildAndExecuteQuery(QueryType.BY_WORDS, words, offset, size);
	}

	@Override
	public List<Collocatio> findCollocationsStartWith(List<String> words, int offset, int size) {
		return buildAndExecuteQuery(QueryType.START_WITH, words, offset, size);
	}
	
	@Override
	public List<Collocatio> findCollocationsEndWith(List<String> words, int offset, int size) {
		return buildAndExecuteQuery(QueryType.END_WITH, words, offset, size);
	}

	@Override
	public List<Collocatio> findBestCollocationsByMutualInformation(int size) {
		List<Collocatio> colList = new ArrayList<Collocatio>();
		Collocatio col = null;
		Connection connection = null;
		PreparedStatement pstatement = null;
		long idcol = -1;
		try {
			connection = getConnection();
			String query = "SELECT * FROM (SELECT * FROM col_collocatio o order by infomutua DESC LIMIT " + size + ") c, col_aparece a WHERE c.id=a.idcol  order by c.infomutua DESC";
			pstatement = connection.prepareStatement(query);
			ResultSet rs = pstatement.executeQuery();
			while (rs.next()) {
				if (idcol != rs.getLong("ID")) {
					idcol = rs.getLong("ID");
					col = getCollocatio(rs);
			        colList.add(col);
			        col.setBook(rs.getString("IDLIB"));
				} else {
			        col.setBook(rs.getString("IDLIB"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstatement != null) {
				try {
					pstatement.close();
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return colList; 		
	}
	
	/**
	 * Construye una consulta SQL según el tipo indicado, devolviendo el resultado de su ejecución.<p>
	 * En funcuión del tipo de consulta indicado se utilizan unas columnas u otras. Todas las consultas reciben una lista de palabras como
	 * parámetros para filtrar los elementos a conseguir.  
	 * @param type tipo de consutla a realizar
	 * @param words lista de parámetros de la consulta
	 * @param offset página a obtener
	 * @param size  cantidad de registros de la página
	 * @return una lista con los elementos devueltos por la consulta
	 * @see QueryType
	 * @see Collocatio
	 */
	private List<Collocatio> buildAndExecuteQuery(QueryType type, List<String> words, int offset, int size) {
		List<Collocatio> colList = null;
		try {
			colList = new ArrayList<Collocatio>();
			if (words != null && words.size() > 0) {
				String query = getQuery(type, words.size(), offset, size); 
				try {
					PreparedStatement pstatement = getPreparedStatement(type, query, words, offset, size);
					colList = executeQuery(pstatement);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch(Exception e) {
			log.error("Error al construir consulta " + type.toString());
		}
		return colList;
	}
		
	/**
	 * Devuelve una cadena con la consulta SQL a ejecutar según el tipo indicado. La consulta es una SELECT en la que su parte WHERE contiene las columnas por las que
	 * consultar (según el tipo de consulta indicado) y el número de parámetros de sustitución necesarios.
	 * @param type tipo de consulta a obtener
	 * @param totalParams número de parámetros de sustitución de la consulta 
	 * @param offset página a obtener
	 * @param size  cantidad de registros de la página
	 * @return una sentencia SQL de tipo SELECT 
	 * @see QueryType
	 */
	private String getQuery(QueryType type, int totalParams, int offset, int size) {
		String query = null;
		switch (type) 
		{
			case BY_WORDS:
				query = getQueryForCollocationsByWords(totalParams, offset, size);
				break;
			case START_WITH:
				query = getQueryForCollocationsStartWith(totalParams, offset, size);
				break;
			case END_WITH:
				query = getQueryForCollocationsEndWith(totalParams, offset, size);
				break;			
			default:
				break;
		}
		return query;
	}
	
	/**
	 * Devuelve un objeto PreparedStatement configurado según el tipo indicado.
	 * @param type tipo de PreparedStatement a obtener
	 * @param query consulta SQL
	 * @param words lista de parámetros a asignar a los parámetros de sustitución de la consulta pasada
	 * @param offset página a obtener
	 * @param size  cantidad de registros de la página
	 * @return un objeto PreparedStatement
	 * @throws SQLException
	 * @see QueryType
	 */
	private PreparedStatement getPreparedStatement(QueryType type, String query, List<String> words, int offset, int size) throws SQLException {
		PreparedStatement pstatement = null;
		switch (type) 
		{
			case BY_WORDS:
				pstatement = getPreparedStatementForCollocationsByWords(query, words, offset, size);
				break;
			case START_WITH:
				pstatement = getPreparedStatementForCollocationsStartWith(query, words, offset, size);
				break;
			case END_WITH:
				pstatement = getPreparedStatementForCollocationsEndWith(query, words, offset, size);
				break;				
			default:
				break;
		}
		return pstatement;
	}
		
	/**
	 * Ejecuta la consulta preparada que se le pasa.
	 * @param pstatement consulta a ejecutar
	 * @return una lista con los elementos devueltos por la consulta
	 * @see Collocatio
	 */
	private List<Collocatio> executeQuery(PreparedStatement pstatement) {
		Collocatio col = null;
		List<Collocatio> colList = new ArrayList<Collocatio>();
		long idcol = -1;
		try {
			ResultSet rs = pstatement.executeQuery();
			while (rs.next()) {
				if (idcol != rs.getLong("ID")) {
					idcol = rs.getLong("ID");
					col = getCollocatio(rs);
			        colList.add(col);
			        col.setBook(rs.getString("IDLIB"));
				} else {
			        col.setBook(rs.getString("IDLIB"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pstatement != null) {
				try {
					Connection connection = pstatement.getConnection();
					pstatement.close();
					if (connection != null) {
						connection.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return colList;		
	}
	
	/**
	 * Devuelve una cadena de texto con la consulta SQL a utilizar en el método <i>findCollocationsByWords</i>.
	 * @param totalParams total de palabras a utilizar como parámetros
	 * @param offset página a obtener
	 * @param size  cantidad de registros de la página
	 * @return sentencia SQL
	 */
	private String getQueryForCollocationsByWords(int totalParams, int offset, int size) {
		StringBuilder query = new StringBuilder("SELECT * FROM col_collocatio c, col_aparece a WHERE c.id=a.idcol and (");
		StringBuilder word1Where = new StringBuilder("palabra1 IN (");
		StringBuilder word2Where = new StringBuilder("palabra2 IN (");
		for (int i = 0; i < totalParams; i++) {
			word1Where.append("?");
			word2Where.append("?");
			if (i < totalParams - 1) {
				word1Where.append(",");
				word2Where.append(",");
			}			
		}
		word1Where.append(")");
		word2Where.append(")");
		query.append(word1Where).append(" OR ").append(word2Where).append(") ORDER BY c.id");
		if (size > 0) {
			query.append(" LIMIT ").append(offset).append(",").append(size);
		}
		return query.toString();
	}
	
	/**
	 * Devuelve una cadena de texto con la consulta SQL a utilizar en el método <i>findCollocationsStartWith</i>.
	 * @param totalParams total de palabras a utilizar como parámetros
	 * @param offset página a obtener
	 * @param size  cantidad de registros de la página
	 * @return sentencia SQL
	 */
	private String getQueryForCollocationsStartWith(int totalParams, int offset, int size) {
		StringBuilder query = new StringBuilder("SELECT * FROM col_collocatio c, col_aparece a WHERE c.id=a.idcol and ");
		StringBuilder word1Where = new StringBuilder("palabra1 IN (");
		for (int i = 0; i < totalParams; i++) {
			word1Where.append("?");
			if (i < totalParams - 1) {
				word1Where.append(",");
			}				
		}
		word1Where.append(")");
		query.append(word1Where);
		if (size > 0) {
			query.append(" LIMIT ").append(offset).append(",").append(size);
		}
		return query.toString();
	}
	
	/**
	 * Devuelve una cadena de texto con la consulta SQL a utilizar en el método <i>findCollocationsEndWith</i>.
	 * @param totalParams total de palabras a utilizar como parámetros
	 * @param offset página a obtener
	 * @param size  cantidad de registros de la página
	 * @return sentencia SQL
	 */
	private String getQueryForCollocationsEndWith(int totalParams, int offset, int size) {
		StringBuilder query = new StringBuilder("SELECT * FROM col_collocatio c, col_aparece a WHERE c.id=a.idcol and ");
		StringBuilder word1Where = new StringBuilder("palabra2 IN (");
		for (int i = 0; i < totalParams; i++) {
			word1Where.append("?");
			if (i < totalParams - 1) {
				word1Where.append(",");
			}				
		}
		word1Where.append(")");
		query.append(word1Where);
		if (size > 0) {
			query.append(" LIMIT ").append(offset).append(",").append(size);
		}
		return query.toString();
	}
	
	/**
	 * Devuelve un objeto PreparedStatement para realizar una consulta en el método <i>findCollocationsByWords</i>.
	 * @param query sentencia SQL a ejecutar
	 * @param words lista de palabras a añadir como parámetros
	 * @param offset página a obtener
	 * @param size  cantidad de registros de la página
	 * @return un objeto PreparedStatement listo para ejecutar
	 * @throws SQLException
	 */
	private PreparedStatement getPreparedStatementForCollocationsByWords(String query, List<String> words, int offset, int size) throws SQLException {
		Connection connection = getConnection();
		PreparedStatement pstatement = connection.prepareStatement(query);
		if (words.size() == 1) {
			pstatement.setString(1, words.get(0));
			pstatement.setString(2, words.get(0));
		} else {
			for (int i = 0; i < words.size(); i++) {
				pstatement.setString(i+1, words.get(i));
				pstatement.setString(i+3, words.get(i));
			}
		}
		return pstatement;
	}
	
	/**
	 * Devuelve un objeto PreparedStatement para realizar una consulta en el método <i>findCollocationsStartWith</i>.
	 * @param query sentencia SQL a ejecutar
	 * @param words lista de palabras a añadir como parámetros
	 * @param offset página a obtener
	 * @param size  cantidad de registros de la página
	 * @return un objeto PreparedStatement listo para ejecutar
	 * @throws SQLException
	 */
	private PreparedStatement getPreparedStatementForCollocationsStartWith(String query, List<String> words, int offset, int size) throws SQLException {
		Connection connection = getConnection();
		PreparedStatement pstatement = connection.prepareStatement(query);
		for (int i = 0; i < words.size(); i++) {
			pstatement.setString(i+1, words.get(i));
		}
		return pstatement;
	}
	
	/**
	 * Devuelve un objeto PreparedStatement para realizar una consulta en el método <i>findCollocationsEndWith</i>.
	 * @param query sentencia SQL a ejecutar
	 * @param words lista de palabras a añadir como parámetros
	 * @param offset página a obtener
	 * @param size  cantidad de registros de la página
	 * @return un objeto PreparedStatement listo para ejecutar
	 * @throws SQLException
	 */
	private PreparedStatement getPreparedStatementForCollocationsEndWith(String query, List<String> words, int offset, int size) throws SQLException {
		Connection connection = getConnection();
		PreparedStatement pstatement = connection.prepareStatement(query);
		for (int i = 0; i < words.size(); i++) {
			pstatement.setString(i+1, words.get(i));
		}
		return pstatement;
	}
	
	/**
	 * Extrae los datos apuntados por el cursor y los devuelve como un objeto Collocatio
	 * @param rs 
	 * @return
	 * @throws SQLException
	 * @see Collocatio
	 */
	private Collocatio getCollocatio(ResultSet rs) throws SQLException {
		Collocatio col = createElement();
        col.setID(rs.getLong("ID"));
        col.setDependencia(rs.getString("DEPENDENCIA"));
        col.setPalabra1(rs.getString("PALABRA1"));
        col.setPalabra2(rs.getString("PALABRA2"));
        col.setInfomutua(rs.getDouble("INFOMUTUA"));
		return col;
	}

	/**
	 * Establece el nombre de la base de datos a la que conectarse.<p>
	 * Si previamente se ha establecido conexión con otra base de datos se elimina dicha conexión, en caso de seguir activa.
	 * @param dbName nombre de la base de datos
	 */
	@Override
	public void setDataBase(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return el nombre de la base de datos a la que se está conectado
	 */
	public String getDbName() {
		return dbName;
	}
}
