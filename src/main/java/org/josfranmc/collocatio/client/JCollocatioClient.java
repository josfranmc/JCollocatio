package org.josfranmc.collocatio.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import org.josfranmc.collocatio.JCollocatio;
import org.josfranmc.collocatio.algorithms.AlgorithmType;
import org.josfranmc.collocatio.algorithms.ParamsAlgorithm;
import org.josfranmc.collocatio.db.ConnectionFactory;
import org.josfranmc.collocatio.service.JCollocatioService;
import org.josfranmc.collocatio.service.domain.Collocatio;

/**
 * Clase que permite ejecutar un progrma cliente para realizar la extracción de colocaciones y la consulta de las mismas.
 * @author Jose Francisco Mena Ceca
 * @see ParamsAlgorithm
 * @see JCollocatio
 * @see JCollocatioService
 * @version 1.0
 */
public class JCollocatioClient {
	
	private static final Logger log = Logger.getLogger(JCollocatioClient.class);
	
	private static JCollocatio jc = null;
	
	private static JCollocatioService jcs = null;
	
	// Parámetros para extracción de colocaciones
	private static ParamsAlgorithm params = null;
	
	// Parámetros para consultas
	private static String queryType = null;
	private static String queryFilter = null;
	private static String queryDb = null;
	private static int offset = 0;
	private static int size = 0;
	
	private enum Tasks {
		EXTRACT,
		QUERY,
		HELP,
		EXIT,
		ERROR
	}
	
	/**
	 * Método principal de ejecución.
	 * @param args lista de argumentos pasados en la invocación del programa
	 */
	public static void main(String [] args){
		jc = new JCollocatio();
		jcs = new JCollocatioService();
		showHelp();
		String entradaTeclado = "";
		Scanner entradaEscaner = new Scanner(System.in);
		
		Tasks task = null;
		do {
			System.out.print("opción: ");
			entradaTeclado = entradaEscaner.nextLine();

			task = readParameters(entradaTeclado.split(" "));
			switch(task) {
			case EXTRACT:
				extractCollocations();
				break;
			case QUERY:
				queryCollocations();
				break;
			case HELP:
				showHelp();
				break;
			case ERROR:
				break;
			case EXIT:
				break;
			}
		} while (task != Tasks.EXIT);
		
		entradaEscaner.close();
		System.exit(0);
	}

	/**
	 * Lee los parámetros de configuración pasados como argumentos. 
	 * @param args lista de parámetros con los valores que toman
	 * @return <i>true</i> si no hay ningún error, <i>false</i> en caso contrario
	 */
	private static Tasks readParameters(String [] args) {
		log.debug("Total parámetros: " + args.length);
		Tasks task = null;
		resetParameters();
		if (args.length == 0 || (args[0].equals("-h") || args[0].equals("-help"))) {
			task = Tasks.HELP;
		} else if (args.length == 1 && args[0].equals("exit")) {
			task = Tasks.EXIT;		
		} else {
			if (args[0].equals("-q")) {
				task = getQueryParameters(args);
			} else {
				params = new ParamsAlgorithm();
				task = Tasks.EXTRACT;
				for (int i = 0; i < args.length; i+=2) {
					try {
						log.debug("argumento " + args[i] + " valor " + args[i+1]);
						if (args[i].equals("-a")) {
							params.setAlgorithmType(AlgorithmType.valueOf(args[i+1].toUpperCase()));
						} else if (args[i].equals("-p")) {
							params.setTextsPathToProcess(args[i+1]);
						} else if (args[i].equals("-j")) {
							params.setAdjustedFrequency(Double.parseDouble(args[i+1]));
						} else if (args[i].equals("-t")) {
							params.setTotalThreads(Integer.parseInt(args[i+1]));
						} else if (args[i].equals("-m")) {
							params.setModel(args[i+1]);
						} else if (args[i].equals("-b")) {	
							params.setSaveInDB(Boolean.parseBoolean(args[i+1]));
						} else if (args[i].equals("-n")) {
							params.setNewDataBase(args[i+1]);
						} else if (args[i].equals("-e")) {
							params.setNewDataBaseDescription(args[i+1]);
						} else if (args[i].equals("-f")) {
							params.setTriplesFilter(Arrays.asList(args[i+1].split(",")));
						} else if (args[i].equals("-o")) {
							//TODO
						} else {
							System.out.println("Parámetro: " + args[i] + " no reconocido. Ejecute JCollocatioClient -h para listar opciones.");
							task = Tasks.ERROR;
							break;
						}
					} catch (ArrayIndexOutOfBoundsException a) {
						task = Tasks.ERROR;
						System.out.println("Error. Número incorrecto de parámetros");
						break;
					} 
					catch (Exception e) {
						task = Tasks.ERROR;
						System.out.println("Error al leer parámetro " + i + ". Parámetro = " + args[i] + ", valor = " + args[i+1]);
						e.printStackTrace();
						break;
					}
				}
			}
		}
		return task;
	}

	/**
	 * Realiza la extracción de colocaciones.
	 */
	private static void extractCollocations() {
		jc.setAlgorithmConfig(params);
		jc.extractCollocations();
	}
	
	/**
	 * Ejecuta una consulta.
	 */
	private static void queryCollocations() {
		List<Collocatio> listCollocatio = new ArrayList<Collocatio>();
		jcs.setDataBase(getDataBaseName());
		
		switch (queryType)
		{
			case "by_words":
				if (queryFilter != null) {
					listCollocatio = jcs.findCollocationsByWords(Arrays.asList(queryFilter.split(",")), 1, 3);
				}
				break;
			case "start_with":
				if (queryFilter != null) {
					listCollocatio = jcs.findCollocationsStartWith(Arrays.asList(queryFilter.split(",")), 0, 0);
				}
				break;
			case "end_with":
				if (queryFilter != null) {
					listCollocatio = jcs.findCollocationsEndWith(Arrays.asList(queryFilter.split(",")), 0, 0);
				}
				break;
			case "all":
				if (offset == 0 && size == 0) {
					listCollocatio = jcs.findAllCollocations();
				} else {
					offset = (offset <= 0) ? 0 : offset;
					size = (size <= 0) ? 0 : size;
					listCollocatio = jcs.findAllCollocationsPaged(offset, size);
				}
				break;			
			default:
				break;
		}
		
		for (Collocatio col : listCollocatio) {
			System.out.println(col.toString() + " info_mutua = " + col.getInfomutua());
		}
	}
	
	/**
	 * Muestra el menú de ayuda.
	 */
	private static void showHelp() {
		System.out.println("Opciones:");
		System.out.println("");
		System.out.println("Para extracción de colocaciones:");
		System.out.println("   -a tipo de algoritmo (por defecto MUTUAL_INFORMATION)");
		System.out.println("   -p ruta archivos a procesar");
		System.out.println("   -t total de hilos a ejecutar");
		System.out.println("   -m model de Stanford a utilizar");
		System.out.println("   -o opciones para el parser de Stanford");
		System.out.println("   -f lista de dependencias por las que filtrar, separadas por comas");
		System.out.println("   -j ajuste de frecuencia para MUTUAL_INFORMATION");
		System.out.println("   -b guardar en base de datos (true/false, por defecto true)");
		System.out.println("   -n nombre nueva base de datos");
		System.out.println("   -e descripción nueva base de datos");
		System.out.println("");
		System.out.println("Para consultar:" + "");
		System.out.println("   -q [by_words | start_with | end_with] -f filtro [-b base_de_datos]");
		System.out.println("      (filtro = lista de dependencias separadas por coma)\"");	
		System.out.println("   -q all -p offset size [-b base_de_datos]");		
		System.out.println("      (offset = número de página, size = tamaño de página)\"");
		System.out.println("");
		System.out.println("(indicar solo -h para mostrar lista de opciones)");
		System.out.println("");
	}
	
	/**
	 * @return el nombre de la base de datos a utilizar
	 */
	private static String getDataBaseName() {
		return (queryDb == null || queryDb.isEmpty()) ? ConnectionFactory.DEFAULT_DB : queryDb;
	}
	
	private static void resetParameters() {
		params = null;
		queryType = null;
		queryFilter = null;
		queryDb = null;
	}
	
	private static Tasks getQueryParameters(String [] args) {
		Tasks task = Tasks.ERROR;
		offset = size = 0;
		try {
			if (args[1].equals("by_words") || args[1].equals("start_with") || args[1].equals("end_with")) {
				queryType = args[1];
				if (args[2].equals("-f")) {
					queryFilter = args[3];
					if (args.length > 4 ) {
						if (args[4].equals("-b")) {
							queryDb = args[5];
						} else {
							System.out.println("Error. Último parámetro inválido");
						}
					}
				}
			} else if (args[1].equals("all")) {
				queryType = args[1];
				task = Tasks.QUERY;
				if (args.length > 2 ) {
					if (args[2].equals("-p")) {
						offset = Integer.parseInt(args[3]);
						size = Integer.parseInt(args[4]);
						if (args.length > 6 && args[5].equals("-b")) {
							queryDb = args[6];
						}
					} else if (args[3].equals("-b")) {
						queryDb = args[4];
					} else {
						System.out.println("Error. Último parámetro inválido");
						task = Tasks.ERROR;
					}					
				}
			} else {
				System.out.println("Error. Tipo de consulta incorrecta.");
			}
		} catch (Exception e) {
			System.out.println("Error. Número de parámetros erróneos.");
		}
		/*if (args[0].equals("-q") && args[1].equals("all_page")) {
			if (args.length == 4) {
				task = Tasks.QUERY;
				queryType = args[1];
				offset = Integer.parseInt(args[2]);
				size = Integer.parseInt(args[3]);
			} else if (args.length == 5) {
				task = Tasks.QUERY;
				queryType = args[1];	
				offset = Integer.parseInt(args[2]);
				size = Integer.parseInt(args[3]);
				queryDb = args[4];
			} else {
				task = Tasks.ERROR;
				System.out.println("Error. Número incorrecto de parámetros");
			}
		} else if (args[0].equals("-q")) {
			if (args.length == 4) {
				task = Tasks.QUERY;
				queryType = args[1];
				queryFilter = args[2];
				queryDb = args[3];
			} else if (args.length == 3) {
				task = Tasks.QUERY;
				queryType = args[1];
				queryFilter = args[2];	
			} else if (args.length == 2) {
				task = Tasks.QUERY;
				queryType = args[1];					
			} else {
				task = Tasks.ERROR;
				System.out.println("Error. Número incorrecto de parámetros");
			}
		}*/
		return task;
	}
}
