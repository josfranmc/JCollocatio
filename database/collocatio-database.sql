SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
-- -----------------------------------------------------
-- Schema col_default
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `col_default` DEFAULT CHARACTER SET utf8 ;
USE `col_default` ;
-- -----------------------------------------------------
-- Table `col_default`.`col_registro`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `col_default`.`col_registro` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `NOMBRE` VARCHAR(20) NOT NULL COMMENT 'Nombre de la base de datos',
  `DESCRIPCION` VARCHAR(100) NULL,
  `FECALT` DATETIME NULL COMMENT 'Fecha de creación de la base de datos',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
COMMENT = 'Guarda el registro de todas las bases de datos creadas para guardar colocaciones';

-- -----------------------------------------------------
-- Table `col_default`.`col_collocatio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `col_default`.`col_collocatio` (
  `ID` INT NOT NULL AUTO_INCREMENT COMMENT 'Identificador de la colocación',
  `DEPENDENCIA` VARCHAR(30) NOT NULL COMMENT 'Tipo de dependencia',
  `PALABRA1` VARCHAR(500) NOT NULL COMMENT 'Palabra 1 de la tripleta',
  `PALABRA2` VARCHAR(500) NOT NULL COMMENT 'Palabra 1 de la tripleta',
  `INFOMUTUA` DOUBLE NULL COMMENT 'Valor información mutua',
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
COMMENT = 'Colocaciones';

-- -----------------------------------------------------
-- Table `col_default`.`col_aparece`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `col_default`.`col_aparece` (
  `IDCOL` INT NOT NULL COMMENT 'Identificador de la colocación',
  `IDLIB` VARCHAR(45) NOT NULL COMMENT 'Identificador del libro',
  CONSTRAINT `fk_collo_libros`
    FOREIGN KEY (`IDCOL`)
    REFERENCES `col_default`.`col_collocatio` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Libros en los que aparece cada colocación';

CREATE USER 'collocatio' IDENTIFIED BY 'colocolo9';
GRANT ALL PRIVILEGES ON `col_%`.* TO 'collocatio';
FLUSH PRIVILEGES;
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
