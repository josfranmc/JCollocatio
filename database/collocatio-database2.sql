SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema collocatio
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `collocatio` DEFAULT CHARACTER SET utf8 ;
USE `collocatio` ;
-- -----------------------------------------------------
-- Table `collocatio`.`col_catalog`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `collocatio`.`col_catalog` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NAME` VARCHAR(20) NOT NULL COMMENT 'Nombre de la base de datos',
  `DESCRIPTION` VARCHAR(100) NULL,
  `FECALT` DATETIME NULL COMMENT 'Fecha de creación de la base de datos',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
COMMENT = 'Guarda el registro de todas las bases de datos creadas para guardar colocaciones';

-- -----------------------------------------------------
-- Table `collocatio`.`col_collocatios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `collocatio`.`col_collocatios` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la colocación',
  `DEPENDENCY` VARCHAR(30) NOT NULL COMMENT 'Tipo de dependencia',
  `HEAD` VARCHAR(500) NOT NULL COMMENT 'Palabra 1 de la tripleta',
  `DEPENDENT` VARCHAR(500) NOT NULL COMMENT 'Palabra 1 de la tripleta',
  `DISTANCE` INTEGER NULL COMMENT 'Distancia entre las palabras',
  `MUTUALINFO` DOUBLE NULL COMMENT 'Valor información mutua',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
COMMENT = 'Collocatios';

-- -----------------------------------------------------
-- Table `collocatio`.`col_events`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `collocatio`.`col_events` (
  `IDCOL` INT NOT NULL COMMENT 'Identificador de la colocación',
  `IDFILE` VARCHAR(45) NOT NULL COMMENT 'Identificador del libro',
  CONSTRAINT `fk_collo_files`
    FOREIGN KEY (`IDCOL`)
    REFERENCES `collocatio`.`col_collocatios` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Ficheros en los que aparece cada colocación';

CREATE USER 'collocatio' IDENTIFIED BY 'colocolo9';
GRANT ALL PRIVILEGES ON `col_%`.* TO 'collocatio';
FLUSH PRIVILEGES;

-- INSERT INTO `collocatio`.`col_catalog` (NOMBRE, DESCRIPCION, FECALT) VALUES('collocatio', 'Base de datos por defecto', now());

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
