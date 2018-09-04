# JCollocatio
Permite ejecutar algoritmos para realizar la extracción de colocaciones de un conjunto de textos.  
Por ahora solo dispone de la implementación de un algoritmo basado en el cálculo del valor de información mutua entre dos palabras.


## Base de datos
Para el almacenamiento de las colocaciones extraidas se utiliza una base de datos MySQL 8.0.  
En la carpeta database se encuentra un script que permite configurar el entorno de base de datos. Debemos conectarnos como root a nuestra base de datos y ejecutar el script.  
En caso de que la base de datos se ubique en un equipo distinto al que ejecuta el programa y no pueda ser referenciado como localhost, se debe modificar el fichero de propiedades DBPool.properties ubicado en src/main/resources/db para indicar la dirección del host. 
