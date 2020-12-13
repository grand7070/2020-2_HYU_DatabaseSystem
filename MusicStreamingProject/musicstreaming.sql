-- MariaDB dump 10.17  Distrib 10.5.6-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: musicstreaming
-- ------------------------------------------------------
-- Server version	10.5.6-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `manager`
--

DROP TABLE IF EXISTS `manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `manager` (
  `managerno` int(11) NOT NULL,
  `id` varchar(20) NOT NULL,
  `password` varchar(30) NOT NULL,
  `name` varchar(10) NOT NULL,
  `phonenumber` char(13) DEFAULT NULL,
  `rrn` char(14) NOT NULL,
  `email` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`managerno`),
  UNIQUE KEY `rrn` (`rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manager`
--

LOCK TABLES `manager` WRITE;
/*!40000 ALTER TABLE `manager` DISABLE KEYS */;
INSERT INTO `manager` VALUES (6257624,'1','1','1','010-1111-1111','990612-1792926','1@naver.com');
/*!40000 ALTER TABLE `manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `music`
--

DROP TABLE IF EXISTS `music`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `music` (
  `musicno` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `artist` varchar(20) NOT NULL,
  `genre` varchar(15) NOT NULL,
  `likenum` int(11) NOT NULL,
  `playnum` int(11) NOT NULL,
  `releasedate` date NOT NULL,
  PRIMARY KEY (`musicno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `music`
--

LOCK TABLES `music` WRITE;
/*!40000 ALTER TABLE `music` DISABLE KEYS */;
INSERT INTO `music` VALUES (21157696,'뻔한남자','이승기','발라드',1,13,'2020-11-15'),(22665025,'잠이 오질 않네요','장범준','발라드',1,12,'2020-10-24'),(145747484,'아무노래','지코 (ZICO)','랩/힙합',2,18,'2020-01-13'),(217571464,'How You Like That','BLACKPINK','댄스',2,23,'2020-06-26'),(350189046,'DON\'T TOUCH ME','환불원정대','댄스',0,7,'2020-10-10'),(363870470,'Dynamite','방탄소년단','댄스',0,14,'2020-08-21'),(386875506,'늦은 밤 너의 집 앞 골목길에서','노을','발라드',1,6,'2019-11-07'),(403459683,'Blueming','아이유','록/메탈',2,12,'2019-11-18'),(407108694,'Pretty Savage','BLACKPINK','댄스',2,13,'2020-10-02'),(433299918,'이제 나만 믿어요','임영웅','트로트',0,0,'2020-04-03'),(507265672,'홀로','이하이','R&B/Soul',1,13,'2020-07-23'),(556840151,'METEOR','창모 (CHANGMO)','랩/힙합',1,14,'2019-11-29'),(565561088,'Love poem','아이유','록/메탈',2,13,'2019-11-18'),(631506608,'Lovesick Girls','BLACKPINK','댄스',1,12,'2020-10-02'),(746098077,'적외선 카메라','원슈타인','랩/힙합',4,14,'2020-12-05'),(828442694,'아로하','조정석','발라드',1,15,'2020-03-27'),(899977698,'주저하는 연인들을 위해','잔나비','록/메탈',1,14,'2019-03-13'),(947943094,'I CAN\'T STOP ME','TWICE (트와이스)','댄스',0,0,'2020-10-26'),(956356531,'마음을 드려요','아이유','발라드',0,13,'2020-02-15'),(958743454,'힘든 건 사랑이 아니다','임창정','발라드',2,12,'2020-10-19');
/*!40000 ALTER TABLE `music` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `musicupload`
--

DROP TABLE IF EXISTS `musicupload`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `musicupload` (
  `mgrno` int(11) NOT NULL,
  `mno` int(11) NOT NULL,
  PRIMARY KEY (`mgrno`,`mno`),
  KEY `mno` (`mno`),
  CONSTRAINT `musicupload_ibfk_1` FOREIGN KEY (`mgrno`) REFERENCES `manager` (`managerno`),
  CONSTRAINT `musicupload_ibfk_2` FOREIGN KEY (`mno`) REFERENCES `music` (`musicno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `musicupload`
--

LOCK TABLES `musicupload` WRITE;
/*!40000 ALTER TABLE `musicupload` DISABLE KEYS */;
INSERT INTO `musicupload` VALUES (6257624,21157696),(6257624,22665025),(6257624,145747484),(6257624,217571464),(6257624,350189046),(6257624,363870470),(6257624,386875506),(6257624,403459683),(6257624,407108694),(6257624,433299918),(6257624,507265672),(6257624,556840151),(6257624,565561088),(6257624,631506608),(6257624,746098077),(6257624,828442694),(6257624,899977698),(6257624,947943094),(6257624,956356531),(6257624,958743454);
/*!40000 ALTER TABLE `musicupload` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist`
--

DROP TABLE IF EXISTS `playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playlist` (
  `uno` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  PRIMARY KEY (`uno`,`name`),
  CONSTRAINT `playlist_ibfk_1` FOREIGN KEY (`uno`) REFERENCES `user` (`userno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist`
--

LOCK TABLES `playlist` WRITE;
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
INSERT INTO `playlist` VALUES (12446546,'test'),(12446546,'test2'),(48469298,'qwe'),(52747333,'asd'),(203799206,'My Favorite');
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlistupload`
--

DROP TABLE IF EXISTS `playlistupload`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playlistupload` (
  `uno` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `mno` int(11) NOT NULL,
  PRIMARY KEY (`uno`,`name`,`mno`),
  KEY `mno` (`mno`),
  CONSTRAINT `playlistupload_ibfk_1` FOREIGN KEY (`uno`, `name`) REFERENCES `playlist` (`uno`, `name`),
  CONSTRAINT `playlistupload_ibfk_2` FOREIGN KEY (`mno`) REFERENCES `music` (`musicno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlistupload`
--

LOCK TABLES `playlistupload` WRITE;
/*!40000 ALTER TABLE `playlistupload` DISABLE KEYS */;
INSERT INTO `playlistupload` VALUES (12446546,'test',145747484),(12446546,'test',217571464),(12446546,'test',403459683),(12446546,'test',407108694),(12446546,'test',556840151),(12446546,'test',565561088),(12446546,'test2',746098077),(48469298,'qwe',217571464),(48469298,'qwe',828442694),(52747333,'asd',22665025),(52747333,'asd',746098077),(203799206,'My Favorite',631506608),(203799206,'My Favorite',956356531);
/*!40000 ALTER TABLE `playlistupload` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `userno` int(11) NOT NULL,
  `id` varchar(20) NOT NULL,
  `password` varchar(30) NOT NULL,
  `name` varchar(10) NOT NULL,
  `phonenumber` char(13) DEFAULT NULL,
  `rrn` char(14) NOT NULL,
  `email` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`userno`),
  UNIQUE KEY `rrn` (`rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (12446546,'test','test','test','010-5355-2234','980402-1179395','test@naver.com'),(48469298,'qwe','qwe','qwe','010-8946-7358','001020-4576633','qwe@naver.com'),(52747333,'asd','asd','asd','010-5436-8388','020421-4555622','asd@naver.com'),(203799206,'yoda123','verry','James','010-6896-5688','990817-1194547','yoda123@naver.com'),(773125796,'wanda','wanda','Vision','010-1299-5325','881121-1345461','magic@naver.com');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userlike`
--

DROP TABLE IF EXISTS `userlike`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userlike` (
  `uno` int(11) NOT NULL,
  `mno` int(11) NOT NULL,
  PRIMARY KEY (`uno`,`mno`),
  KEY `mno` (`mno`),
  CONSTRAINT `userlike_ibfk_1` FOREIGN KEY (`uno`) REFERENCES `user` (`userno`),
  CONSTRAINT `userlike_ibfk_2` FOREIGN KEY (`mno`) REFERENCES `music` (`musicno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userlike`
--

LOCK TABLES `userlike` WRITE;
/*!40000 ALTER TABLE `userlike` DISABLE KEYS */;
INSERT INTO `userlike` VALUES (12446546,21157696),(12446546,145747484),(12446546,403459683),(12446546,407108694),(12446546,507265672),(12446546,556840151),(12446546,565561088),(12446546,746098077),(48469298,217571464),(48469298,746098077),(48469298,828442694),(48469298,958743454),(52747333,22665025),(52747333,403459683),(52747333,746098077),(203799206,145747484),(203799206,217571464),(203799206,386875506),(203799206,565561088),(203799206,631506608),(203799206,746098077),(203799206,899977698),(773125796,407108694),(773125796,958743454);
/*!40000 ALTER TABLE `userlike` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-12-06 18:33:54
