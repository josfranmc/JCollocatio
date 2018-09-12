package org.josfranmc.collocatio.service;

import java.util.List;

import org.josfranmc.collocatio.service.domain.Collocatio;

/**
 * Define el interfaz que debe implementarse para la realización de consultas y otras operaciones sobre las bases de datos que almacenan las
 * colocaciones extraidas.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 * @see Collocatio
 */
public interface ICollocatioService {
	
	/**
	 * @param id identificador 
	 * @return una colocación según su identificador en la base de datos
	 */
	public Collocatio findCollocationsById(long id);
	
	/**
	 * Obtiene las colocaciones que contienen las palabras especificadas.
	 * @param words lista de palabras de búsqueda
	 * @param offset página a obtener
	 * @param size cantidad de registros de la página
	 * @return lista de colocaciones recuperada
	 * @see Collocatio
	 */
	public List<Collocatio> findCollocationsByWords(List<String> words, int offset, int size);
	
	/**
	 * Obtiene las colocaciones que empiezan por una palabra dada.
	 * @param words palabras de búsqueda
	 * @param offset página a obtener
	 * @param size cantidad de registros de la página
	 * @return lista de colocaciones recuperada
	 * @see Collocatio
	 */
	public List<Collocatio> findCollocationsStartWith(List<String> words, int offset, int size);
	
	/**
	 * Obtiene las colocaciones que terminan por una palabra dada.
	 * @param words palabras de búsqueda
	 * @param offset página a obtener
	 * @param size cantidad de registros de la página
	 * @return lista de colocaciones recuperada
	 * @see Collocatio
	 */
	public List<Collocatio> findCollocationsEndWith(List<String> words, int offset, int size);
	
	/**
	 * Obtiene todas las colocaciones existentes en la base de datos.
	 * @return lista de colocaciones recuperada
	 * @see Collocatio
	 */
	public List<Collocatio> findAllCollocations();
	
	/**
	 * Obtiene todas las colocaciones almacenadas paginando el resultado.
	 * @param offset página a obtener
	 * @param size cantidad de registros de la página
	 * @return lista de colocaciones
	 */
	public List<Collocatio> findAllCollocationsPaged(int offset, int size);
	
	/**
	 * Obtiene las colocaciones con el valor más alto de información mutua.
	 * @param size cantidad de registros a obtener
	 * @return lista de colocaciones
	 */
	public List<Collocatio> findBestCollocationsByMutualInformation(int size);
	
	/**
	 * Añade una nueva colocación a la base de datos.
	 * @param collocatio colocación a insertar
	 * @return el identificador asiganado al nuevo registro guardado
	 * @see Collocatio
	 */
	public long addCollocation(Collocatio collocatio);

	/**
	 * Elimina una colocación de una base de datos.
	 * @param id identificador del registro a eliminar
	 * @return el número de registros afectados
	 */
	public long deleteCollocation(Long id);
	
	/**
	 * Elimina una base de datos.
	 * @param dbname identificador del registro a eliminar
	 */
	public void deleteDataBase(String dbname);
	
	/**
	 * @return un nuevo objeto de tipo Collocatio
	 * @see Collocatio
	 */
	public Collocatio createElement();
	
	/**
	 * Establece la base de datos con la que trabajar.
	 * @param dbName nombre de la base de datos
	 */
	public void setDataBase(String dbName);
}
