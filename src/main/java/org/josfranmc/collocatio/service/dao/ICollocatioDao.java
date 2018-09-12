package org.josfranmc.collocatio.service.dao;

import java.util.List;

import org.josfranmc.collocatio.service.domain.Collocatio;

/**
 * Interfaz que expone las operaciones que pueden realizarse sobre la tabla col_collocatio.<br>
 * Hereda de IGenericDao, interfaz que define las operaciones básicas para operar con la base de datos.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see IGenericDao
 * @see Collocatio
 */
public interface ICollocatioDao extends IGenericDao<Collocatio,Long> {
	
	/**
	 * Obtiene todas las colocaciones almacenadas.<br>Pagina la consulta y devuelve la página indicada.
	 * @param offset página a obtener
	 * @param size cantidad de registros de la página
	 * @return lista de colocaciones
	 */
	public List<Collocatio> getAllElementsByPage(int offset, int size);	
	
	/**
	 * Obtiene las colocaciones que contienen las palabras especificadas.<br>
	 * Permite obtener el resultado paginado.
	 * @param words lista de palabras de búsqueda
	 * @param offset página a obtener
	 * @param size cantidad de registros de la página
	 * @return lista de colocaciones recuperada
	 */
	public List<Collocatio> findCollocationsByWords(List<String> words, int offset, int size);
	
	/**
	 * Obtiene las colocaciones que empiezan por una palabra dada.<br>
	 * Permite obtener el resultado paginado.
	 * @param words palabras de búsqueda
	 * @param offset página a obtener
	 * @param size cantidad de registros de la página
	 * @return lista de colocaciones recuperada
	 * @see Collocatio
	 */	
	public List<Collocatio> findCollocationsStartWith(List<String> words, int offset, int size);
	
	/**
	 * Obtiene las colocaciones que terminan por una palabra dada.<br>
	 * Permite obtener el resultado paginado.
	 * @param words palabras de búsqueda
	 * @param offset página a obtener
	 * @param size cantidad de registros de la página
	 * @return lista de colocaciones recuperada
	 * @see Collocatio
	 */	
	public List<Collocatio> findCollocationsEndWith(List<String> words, int offset, int size);

	/**
	 * Obtiene las colocaciones con el valor más alto de información mutua.
	 * @param size cantidad de registros a obtener
	 * @return lista de colocaciones
	 */
	public List<Collocatio> findBestCollocationsByMutualInformation(int size);
	
	/**
	 * Elimina una base de datos.
	 * @param dbname identificador del registro a eliminar
	 */
	public void deleteDataBase(String dbname);	
	
	/**
	 * @return un nuevo objeto de tipo Collocatio
	 */
	public Collocatio createElement();
	
	/**
	 * Establece el nombre de la base de datos a la que conectarse.
	 * @param dbName nombre de la base de datos
	 */
	public void setDataBase(String dbName);
}
