CREATE DATABASE  IF NOT EXISTS `forkilia` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `forkilia`;
-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: forkilia
-- ------------------------------------------------------
-- Server version	8.4.0
-- NOTA: los datos de usuarios y clientes son de ejemplo.
-- Usuario de prueba: chef_lux | contraseña: demo1234

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categorias`
--

DROP TABLE IF EXISTS `categorias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias` (
  `ID_Categoria` varchar(10) NOT NULL,
  `ID_Usuario` varchar(10) NOT NULL COMMENT 'Emprendedor dueño de la categoría',
  `Nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`ID_Categoria`),
  UNIQUE KEY `ID_Usuario` (`ID_Usuario`,`Nombre`),
  KEY `ID_Usuario_2` (`ID_Usuario`),
  CONSTRAINT `categorias_ibfk_1` FOREIGN KEY (`ID_Usuario`) REFERENCES `usuarios` (`ID_Usuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias`
--

LOCK TABLES `categorias` WRITE;
/*!40000 ALTER TABLE `categorias` DISABLE KEYS */;
INSERT INTO `categorias` VALUES ('C003','U001','Bebidas'),('C005','U001','Combos'),('C001','U001','Plato Fuerte'),('C004','U001','Postres'),('C002','U001','Sopas');
/*!40000 ALTER TABLE `categorias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `ID_Cliente` varchar(10) NOT NULL,
  `ID_Usuario` varchar(10) NOT NULL COMMENT 'Emprendedor dueño del cliente',
  `Nombre` varchar(100) NOT NULL,
  `Telefono` varchar(15) DEFAULT NULL COMMENT 'Opcional, no siempre se captura',
  `Direccion` varchar(150) DEFAULT NULL COMMENT 'Solo aplica si el pedido es a domicilio',
  `Empresa` varchar(100) DEFAULT NULL,
  `NumPersonas` int DEFAULT NULL,
  PRIMARY KEY (`ID_Cliente`),
  KEY `ID_Usuario` (`ID_Usuario`),
  CONSTRAINT `clientes_ibfk_1` FOREIGN KEY (`ID_Usuario`) REFERENCES `usuarios` (`ID_Usuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES ('CL001','U001','Cliente de Ejemplo','6690000000','',NULL,NULL),('CL002','U001','Empresa Demo','6690000001','Calle Ejemplo 123','Empresa Demo',14);
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingredients`
--

DROP TABLE IF EXISTS `ingredients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ingredients` (
  `ID_Ingrediente` varchar(10) NOT NULL,
  `ID_Usuario` varchar(10) NOT NULL COMMENT 'Emprendedor dueño del ingrediente',
  `Nombre` varchar(80) NOT NULL,
  `Unidad_Base` varchar(15) NOT NULL COMMENT 'g, kg, ml, L, pzas, dientes, rebanadas',
  PRIMARY KEY (`ID_Ingrediente`),
  UNIQUE KEY `ID_Usuario` (`ID_Usuario`,`Nombre`),
  KEY `ID_Usuario_2` (`ID_Usuario`),
  CONSTRAINT `ingredients_ibfk_1` FOREIGN KEY (`ID_Usuario`) REFERENCES `usuarios` (`ID_Usuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ingredients`
--

LOCK TABLES `ingredients` WRITE;
/*!40000 ALTER TABLE `ingredients` DISABLE KEYS */;
INSERT INTO `ingredients` VALUES ('I001','U001','Carne de res','kg'),('I002','U001','Chile guajillo','pzas'),('I003','U001','Tortillas de maíz','pzas');
/*!40000 ALTER TABLE `ingredients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventario`
--

DROP TABLE IF EXISTS `inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario` (
  `ID_Inventario` varchar(10) NOT NULL,
  `ID_Ingrediente` varchar(10) NOT NULL,
  `Stock_Actual` decimal(10,2) NOT NULL DEFAULT '0.00',
  `Stock_Minimo` decimal(10,2) NOT NULL DEFAULT '0.00',
  `Costo_Unitario` decimal(10,2) NOT NULL,
  PRIMARY KEY (`ID_Inventario`),
  UNIQUE KEY `ID_Ingrediente` (`ID_Ingrediente`),
  CONSTRAINT `inventario_ibfk_1` FOREIGN KEY (`ID_Ingrediente`) REFERENCES `ingredients` (`ID_Ingrediente`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventario`
--

LOCK TABLES `inventario` WRITE;
/*!40000 ALTER TABLE `inventario` DISABLE KEYS */;
/*!40000 ALTER TABLE `inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mermas`
--

DROP TABLE IF EXISTS `mermas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mermas` (
  `ID_Merma` varchar(10) NOT NULL,
  `ID_Usuario` varchar(10) NOT NULL COMMENT 'Emprendedor dueño de la merma',
  `ID_Ingrediente` varchar(10) NOT NULL,
  `ID_Receta` varchar(10) DEFAULT NULL COMMENT 'Puede registrarse sin venir de una receta cocinada',
  `Cantidad_Perdida` decimal(10,2) NOT NULL,
  `Costo_Unitario` decimal(10,2) NOT NULL,
  `Costo_Total` decimal(10,2) NOT NULL,
  `Fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID_Merma`),
  KEY `ID_Ingrediente` (`ID_Ingrediente`),
  KEY `ID_Receta` (`ID_Receta`),
  KEY `ID_Usuario` (`ID_Usuario`),
  CONSTRAINT `mermas_ibfk_1` FOREIGN KEY (`ID_Ingrediente`) REFERENCES `ingredients` (`ID_Ingrediente`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `mermas_ibfk_2` FOREIGN KEY (`ID_Receta`) REFERENCES `recetas` (`ID_Receta`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `mermas_ibfk_3` FOREIGN KEY (`ID_Usuario`) REFERENCES `usuarios` (`ID_Usuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mermas`
--

LOCK TABLES `mermas` WRITE;
/*!40000 ALTER TABLE `mermas` DISABLE KEYS */;
/*!40000 ALTER TABLE `mermas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificaciones`
--

DROP TABLE IF EXISTS `notificaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notificaciones` (
  `ID_Notificacion` varchar(10) NOT NULL,
  `ID_Usuario` varchar(10) DEFAULT NULL COMMENT 'NULL = notificación global del sistema',
  `Tipo` varchar(30) NOT NULL COMMENT 'stock_bajo, pedido_urgente, pedido_completado, etc.',
  `Mensaje` varchar(255) NOT NULL,
  `Leida` tinyint(1) NOT NULL DEFAULT '0',
  `Fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID_Notificacion`),
  KEY `ID_Usuario` (`ID_Usuario`),
  CONSTRAINT `notificaciones_ibfk_1` FOREIGN KEY (`ID_Usuario`) REFERENCES `usuarios` (`ID_Usuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificaciones`
--

LOCK TABLES `notificaciones` WRITE;
/*!40000 ALTER TABLE `notificaciones` DISABLE KEYS */;
INSERT INTO `notificaciones` VALUES ('N001','U001','pedido_nuevo','Nuevo pedido F0001 por $165.00',0,'2026-08-07 08:28:49');
/*!40000 ALTER TABLE `notificaciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido_detalle`
--

DROP TABLE IF EXISTS `pedido_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedido_detalle` (
  `ID_Pedido` varchar(10) NOT NULL,
  `ID_Receta` varchar(10) NOT NULL,
  `Cantidad` int NOT NULL DEFAULT '1',
  `Precio_Unitario` decimal(10,2) NOT NULL,
  PRIMARY KEY (`ID_Pedido`,`ID_Receta`),
  KEY `ID_Receta` (`ID_Receta`),
  CONSTRAINT `pedido_detalle_ibfk_1` FOREIGN KEY (`ID_Pedido`) REFERENCES `pedidos` (`ID_Pedido`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `pedido_detalle_ibfk_2` FOREIGN KEY (`ID_Receta`) REFERENCES `recetas` (`ID_Receta`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido_detalle`
--

LOCK TABLES `pedido_detalle` WRITE;
/*!40000 ALTER TABLE `pedido_detalle` DISABLE KEYS */;
INSERT INTO `pedido_detalle` VALUES ('PD001','R001',1,165.00);
/*!40000 ALTER TABLE `pedido_detalle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos` (
  `ID_Pedido` varchar(10) NOT NULL,
  `Folio` varchar(20) NOT NULL,
  `ID_Cliente` varchar(10) NOT NULL,
  `ID_Usuario` varchar(10) NOT NULL COMMENT 'Emprendedor dueño del pedido',
  `Tipo` varchar(15) NOT NULL COMMENT 'local, recoleccion o domicilio',
  `Mesa` varchar(10) DEFAULT NULL COMMENT 'Solo se usa si Tipo = local',
  `Total` decimal(10,2) NOT NULL DEFAULT '0.00',
  `Anticipo` decimal(10,2) NOT NULL DEFAULT '0.00',
  `Status` varchar(15) NOT NULL DEFAULT 'pendiente' COMMENT 'pendiente, preparando, listo o entregado',
  `Notas` varchar(255) DEFAULT NULL,
  `Fecha` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Descuento_Aplicado` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID_Pedido`),
  UNIQUE KEY `Folio` (`Folio`),
  KEY `ID_Cliente` (`ID_Cliente`),
  KEY `ID_Usuario` (`ID_Usuario`),
  CONSTRAINT `pedidos_ibfk_1` FOREIGN KEY (`ID_Cliente`) REFERENCES `clientes` (`ID_Cliente`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `pedidos_ibfk_2` FOREIGN KEY (`ID_Usuario`) REFERENCES `usuarios` (`ID_Usuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos`
--

LOCK TABLES `pedidos` WRITE;
/*!40000 ALTER TABLE `pedidos` DISABLE KEYS */;
INSERT INTO `pedidos` VALUES ('PD001','F0001','CL001','U001','recoleccion',NULL,165.00,89.64,'listo','Pedido de prueba','2026-08-07 08:28:49',0);
/*!40000 ALTER TABLE `pedidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receta_ingrediente`
--

DROP TABLE IF EXISTS `receta_ingrediente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receta_ingrediente` (
  `ID_Receta` varchar(10) NOT NULL,
  `ID_Ingrediente` varchar(10) NOT NULL,
  `Cantidad` decimal(10,2) NOT NULL,
  `Unidad` varchar(15) NOT NULL,
  PRIMARY KEY (`ID_Receta`,`ID_Ingrediente`),
  KEY `ID_Ingrediente` (`ID_Ingrediente`),
  CONSTRAINT `receta_ingrediente_ibfk_1` FOREIGN KEY (`ID_Receta`) REFERENCES `recetas` (`ID_Receta`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `receta_ingrediente_ibfk_2` FOREIGN KEY (`ID_Ingrediente`) REFERENCES `ingredients` (`ID_Ingrediente`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receta_ingrediente`
--

LOCK TABLES `receta_ingrediente` WRITE;
/*!40000 ALTER TABLE `receta_ingrediente` DISABLE KEYS */;
INSERT INTO `receta_ingrediente` VALUES ('R001','I001',500.00,'g'),('R001','I002',4.00,'pzas'),('R001','I003',8.00,'pzas');
/*!40000 ALTER TABLE `receta_ingrediente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recetas`
--

DROP TABLE IF EXISTS `recetas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recetas` (
  `ID_Receta` varchar(10) NOT NULL,
  `ID_Usuario` varchar(10) NOT NULL COMMENT 'Emprendedor dueño de la receta',
  `ID_Categoria` varchar(10) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `Rendimiento` int NOT NULL DEFAULT '1',
  `Tiempo_Prep_Min` int NOT NULL,
  `Costo` decimal(10,2) NOT NULL,
  `Precio` decimal(10,2) NOT NULL,
  `Margen` decimal(5,2) DEFAULT NULL,
  `Dificultad` varchar(10) NOT NULL DEFAULT 'Media' COMMENT 'Baja, Media o Alta',
  `Tip` varchar(255) DEFAULT NULL COMMENT 'Consejo opcional, no todas las recetas lo tienen',
  `Imagen` varchar(255) DEFAULT NULL COMMENT 'URL o ID de Unsplash de la imagen del platillo',
  `Procedimiento` text COMMENT 'Pasos de preparación, separados por comas o saltos de línea',
  PRIMARY KEY (`ID_Receta`),
  KEY `ID_Categoria` (`ID_Categoria`),
  KEY `ID_Usuario` (`ID_Usuario`),
  CONSTRAINT `recetas_ibfk_1` FOREIGN KEY (`ID_Categoria`) REFERENCES `categorias` (`ID_Categoria`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `recetas_ibfk_2` FOREIGN KEY (`ID_Usuario`) REFERENCES `usuarios` (`ID_Usuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recetas`
--

LOCK TABLES `recetas` WRITE;
/*!40000 ALTER TABLE `recetas` DISABLE KEYS */;
INSERT INTO `recetas` VALUES ('R001','U001','C001','Tacos de Birria',4,35,48.00,165.00,71.00,'Media',NULL,NULL,NULL);
/*!40000 ALTER TABLE `recetas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tickets`
--

DROP TABLE IF EXISTS `tickets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tickets` (
  `ID_Ticket` varchar(10) NOT NULL,
  `ID_Pedido` varchar(10) NOT NULL,
  `IVA_Porcentaje` decimal(5,2) NOT NULL DEFAULT '16.00',
  `Fecha_Emision` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID_Ticket`),
  UNIQUE KEY `ID_Pedido` (`ID_Pedido`),
  CONSTRAINT `tickets_ibfk_1` FOREIGN KEY (`ID_Pedido`) REFERENCES `pedidos` (`ID_Pedido`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tickets`
--

LOCK TABLES `tickets` WRITE;
/*!40000 ALTER TABLE `tickets` DISABLE KEYS */;
/*!40000 ALTER TABLE `tickets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `ID_Usuario` varchar(10) NOT NULL COMMENT 'Clave principal del usuario',
  `Username` varchar(50) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Email` varchar(100) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `Rol` varchar(20) NOT NULL COMMENT 'Admin o Emprendedor',
  `Fecha_Creacion` date NOT NULL DEFAULT (curdate()),
  `Activo` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1 = activo, 0 = desactivado',
  `NombreNegocio` varchar(100) DEFAULT NULL,
  `Direccion` varchar(150) DEFAULT NULL,
  `TelefonoNegocio` varchar(15) DEFAULT NULL,
  `CorreoContacto` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID_Usuario`),
  UNIQUE KEY `Username` (`Username`),
  UNIQUE KEY `Email` (`Email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES ('U000','forkilia_admin','$2a$10$GPdaufomvWmIMMoDQrV8Lu196xbcPNH0RKefmt5a.MfmbCjsbJKWG','soporte@forkilia.com','Administrador Forkilia (Devs)','Admin','2024-01-15',1,NULL,NULL,NULL,NULL),('U001','chef_lux','$2a$10$GPdaufomvWmIMMoDQrV8Lu196xbcPNH0RKefmt5a.MfmbCjsbJKWG','admin@forkilia.com','Usuario Emprendedor Piloto','Emprendedor','2024-01-15',1,NULL,NULL,NULL,NULL),('U002','meli','$2a$10$GPdaufomvWmIMMoDQrV8Lu196xbcPNH0RKefmt5a.MfmbCjsbJKWG','meli@forkilia.com','Meli Admin','Admin','2026-08-06',1,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-05 16:58:35
