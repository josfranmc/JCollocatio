# JCollocatio
Permite ejecutar algoritmos para realizar la extracción de colocaciones de un conjunto de textos.  
Por ahora solo dispone de la implementación de un algoritmo basado en el cálculo del valor de información mutua entre dos palabras.


## Stanford Parser
El programa hace uso del analizador de Stanford (The Stanford Parser) para la extracción de tripletas. El paquete principal se incluyen en el proyecto como una dependencia más que Maven gestiona. Sin embargo, el jar con los models de los idiomas sopartados no se encuentra en los repositorios de Maven. Para poder manejarlo desde Maven es necesario darlo de alta en el repositorio local que utilicemos. Para ello, situándonos en la carpeta donde esté el jar, solo hay que ejecutar el siguiente comando:

mvn install:install-file –Dfile=stanford-parser-3.9.1-models.jar -DgroupId=edu.stanford.nlp -DartifactId=stanford-parser-3.9.1-models -Dversion=3.9.1 -Dpackaging=jar

El jar stanford-parser-3.9.1-models.jar puede obtenerse en el apartado Download de la página:  
https://nlp.stanford.edu/software/lex-parser.shtml


## Base de datos
Para el almacenamiento de las colocaciones extraidas se utiliza una base de datos MySQL 8.0.  
En la carpeta database se encuentra un script que permite configurar el entorno de base de datos. Debemos conectarnos como root a nuestra base de datos y ejecutar el script.  
En caso de que la base de datos se ubique en un equipo distinto al que ejecuta el programa y no pueda ser referenciado como localhost, se debe modificar el fichero de propiedades DBPool.properties ubicado en src/main/resources/db para indicar la dirección del host. 
