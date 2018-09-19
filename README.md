# JCollocatio
Permite ejecutar algoritmos para realizar la extracción de colocaciones de un conjunto de textos.  
Por ahora solo dispone de la implementación de un algoritmo basado en el cálculo del valor de información mutua entre dos palabras.

## Contenido
+ database: script para creación de base de datos en MySQL
+ jar-flat: fichero jar con las clases de la aplicación junto a las dependencias necesarias, que se ubican en la carpeta lib
+ javadoc: documentación del código
+ src: código fuente

## Base de datos
Para el almacenamiento de las colocaciones extraidas se utiliza una base de datos MySQL 8.0.  
En la carpeta database se encuentra un script que permite configurar el entorno de base de datos. Debemos conectarnos como root a nuestra base de datos y ejecutar el script.  
En caso de que la base de datos se ubique en un equipo distinto al que ejecuta el programa y no pueda ser referenciado como localhost, se debe modificar el fichero de propiedades DBPool.properties ubicado en src/main/resources/db para indicar la dirección del host.

## Uso
Desde línea de comandos:

java -jar JCollocatio-1.0.jar

## Notas
El primer paso a seguir es la creación de la base de datos. Es necesaria para la ejecución del programa y para poder generar los ficheros jar de la aplicación.

Si se usa el jar flat hay que asegurarse que exista la carpeta lib dentro de la carpeta desde la que ejecutemos la aplicación. Dentro de esta carpeta lib debe existir el fichero stanford-parser-3.9.1-models.jar. Este fichero no se incluye por limitación de tamaño del repositorio. Este jar se puede obtener en el apartado Download de la página:  
https://nlp.stanford.edu/software/lex-parser.shtml

Si se empaqueta el proyecto con mvn package se puede obtener un fichero jar denominado JCollocatio-1.0-shaded.jar, el cual puede ejecutarse de forma autónoma.

El fichero de log generado se guarda en la carpeta log.
