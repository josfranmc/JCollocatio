package org.josfranmc.collocatio.corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Permite construir un corpus textual en base al conjunto de archivos existentes en una ruta especificada.<br>
 * Los archivos son procesados de forma que queden preparados para su posterior análisis.
 * @author Jose Francisco Mena Ceca
 * @version 1.0
 */
public class CorpusBuilder {
	
	private final static String FILE_SEPARATOR = System.getProperty("file.separator");
	
	/**
	 * Ruta en la que se encuentran los ficheros a procesar
	 */
	private String path;
	
	/**
	 * Ficheros procesados
	 */
	Map<String, String> processedFiles;
	
	
	/**
	 * Constructor principal.
	 * @param path ruta de los archivos a procesar
	 */
	public CorpusBuilder(String path) {
		if (path == null) {
			throw new IllegalArgumentException("No se ha especificado la ruta de los archivos a procesar.");
		}
		if (! new File(path).exists()) {
			throw new IllegalArgumentException("La ruta de los archivos a procesar no existe.");
		}
		this.path = path;
		this.processedFiles = new HashMap<String, String>();
	}
	
	/**
	 * Construye un corpus textual basado en los textos existentes en la ruta indicada, la cual se ha debido indicar al crear el objeto.<p>
	 * Realiza dos acciones principales:
	 * <ul>
	 * <li>Por un lado, dado que un mismo libro puede existir en varios ficheros, cada uno con diferente codificación, seleccionamos solamente
	 * uno de estos ficheros. Ej.: los archivos <i>45269.txt</i> y <i>45269-8.txt</i> son el mismo libro, pero lo que solo nos interesa procesar
	 * uno de ellos.</li>
	 * <li>Por otro lado, se elimina de los ficheros seleccionados la cabecera y la parte final de los mismos, ya que son elementos genéricos
	 * incorporados por el proyecto Gutenberg a modo de información.</li>
	 * </ul>
	 */
	public void build() {
		File folder = new File(getPath());
		if (folder.exists()) {
			for (File file : folder.listFiles()) {
				if (file.getAbsolutePath().endsWith(".txt")) {
					String pickedFile = pickFile(file.getAbsolutePath());
					if (pickedFile != null) {
						String tempFile = deleteHeadAndTail(file.getAbsolutePath());
						if (tempFile != null) {
							File temp = new File(tempFile);
							file.delete();
							// renombramos el fichero temporal creado con el nombre del fichero del que deriva
							temp.renameTo(file);
						}
					} else {
						file.delete();
					}
				}
			}
		}	
	}
	
	/**
	 * Comprueba si el fichero pasado como parámetro se debe procesar.<p>
	 * Un mismo libro puede estar recogido en varios ficheros con diferente codificación. Tienen el mismo código como nombre de fichero pero
	 * distinto sufijo. Ej.: los archivos <i>45269.txt</i> y <i>45269-8.txt</i> son el mismo libro, pero solo nos interesa procesar uno de
	 * ellos.<br>
	 * Primero obtenemos el código del fichero, sin sufijo, y se comprueba si es la primera vez que se trata. En ese caso, se añade a la lista
	 * de ficheros procesados.
	 * @param file ruta y nombre del fichero a comprobar
	 * @return el nombre del archivo si es la primera vez que se comprueba, <i>null</i> en caso contrario
	 */
	private String pickFile(String file) {
		String result = null;
		String name = file.substring(file.lastIndexOf(FILE_SEPARATOR)+1, file.lastIndexOf(".txt"));
		if (name.contains("-")) {
			name = name.substring(0, name.lastIndexOf("-"));
		} 
		String value = processedFiles.putIfAbsent(name, file);
		if (value == null) {
			result = name;
		}
		return result;
	}
	
	/**
	 * Elimina la cabecera y parte final de los archivos del proyecto Gutenebrg, que son elementos que se repiten en todos los archivos. No nos
	 * interesa procesar estas líneas.<p>
	 * Se crea un fichero temporal equivalente al fichero indicado pero sin los textos de cabecera y parte final. Si no se detecta ninguna
	 * cabecera no se crea el fichero temporal.
	 * @param pathFile ruta del fichero a tratar
	 * @return la ruta del fichero temporal creado, <i>null</i> en caso de no haberse eliminado nada
	 */
	private static String deleteHeadAndTail(String pathFile) {
		BufferedWriter  writer = null;
		BufferedReader reader = null;
		String line = null;
		String tempFile = null;
		try {
	        reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathFile), "ISO-8859-1"));

    		Pattern pattern1 = Pattern.compile(".*START.*GUTENBERG.*");
    		Pattern pattern2 = Pattern.compile(".*END.*PRINT.*");
    		Matcher matcher1 = null;
    		Matcher matcher2 = null;
    		
    		// leemos la cabecera hasta su final
            while ((line = reader.readLine()) != null) {
        		matcher1 = pattern1.matcher(line);
        		matcher2 = pattern2.matcher(line);
    			if (matcher1.matches() || matcher2.matches()) {
    				break;
    			}
            }
    		// si line == null no hemos encontrado cabecera y hemos llegado al final del texto
            if (line != null) {
            	tempFile = pathFile.concat("_tmp");
            	writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), "ISO-8859-1"));
	            pattern1 = Pattern.compile(".*END.*GUTENBERG.*");
	            pattern2 = Pattern.compile(".*End.*Gutenberg.*");
	            // escribimos en el fichero temporal hasta llegar a la parte final del libro
	            while ((line = reader.readLine()) != null) {
	            	matcher1 = pattern1.matcher(line);
	            	matcher2 = pattern2.matcher(line);
	            	if (matcher1.matches() || matcher2.matches()) {
	            		break;
	            	} else {
	            		writer.write(line);
	            		writer.newLine();
	            	}
	            }
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e2) { 
				e2.printStackTrace();
			}
		}
		return tempFile;
	}

	/**
	 * @return la ruta en la que se encuentran los ficheros a procesar
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Establece la ruta en la que se encuentran los ficheros a procesar
	 * @param path ruta de los ficheros
	 */
	public void setPath(String path) {
		this.path = path;
	}
}
