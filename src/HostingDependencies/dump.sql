/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-11.8.2-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: DTT_APP
-- ------------------------------------------------------
-- Server version	11.8.2-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `QuestionId` int(11) NOT NULL AUTO_INCREMENT,
  `Prompt` text DEFAULT NULL,
  `CorrectAnswer` text DEFAULT NULL,
  PRIMARY KEY (`QuestionId`),
  UNIQUE KEY `Prompt` (`Prompt`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `question` VALUES
(1,'All of the following are research designs used in ABA except:','Confounding Analysis'),
(4,'When delivering effective feedback, you should do all of the following except:','List every mistake made during the observation'),
(5,'Interobserver agreement most directly provides a measurement of:','Reliability'),
(6,'Antionette goes to her mom and tries to talk to her while she is working. Sometimes her mom responds, and sometimes she does not. Which compound reinforcement schedule is in place?','Mixed'),
(7,'All of the following are topographically similar except:','Biting resulting in attention'),
(8,'Which level of scientific understanding is demonstrated when calculating conditional probability?','Prediction'),
(9,'Kendrick\'s BCBA implements an extinction protocol to decrease spitting. The protocol is successful and the spitting behaviour is extinguished; however, one day he begins to spit again. What side effect of extinction has occurred?','Spontaneous Recovery');
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;
commit;

--
-- Table structure for table `wronganswer`
--

DROP TABLE IF EXISTS `wronganswer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `wronganswer` (
  `AnswerId` int(11) NOT NULL,
  `Answer` text DEFAULT NULL,
  `QuestionId` int(11) NOT NULL,
  PRIMARY KEY (`AnswerId`,`QuestionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wronganswer`
--

LOCK TABLES `wronganswer` WRITE;
/*!40000 ALTER TABLE `wronganswer` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `wronganswer` VALUES
(0,'Comparative Analysis',1),
(0,'Include a higher ratio of positive and praise statements',4),
(0,'Validity',5),
(0,'Multiple',6),
(0,'Hittings resulting in escape',7),
(0,'Control',8),
(0,'Resurgence',9),
(1,'Parametric Analysis',1),
(1,'Focus on offering alternative behaviors rather than telling the supervisee what \"not\" to do',4),
(1,'Accuracy',5),
(1,'Tandem',6),
(1,'High fives resulting in attention',7),
(1,'Confirmation',8),
(1,'Extinction Bursts',9),
(2,'Component Analysis',1),
(2,'Plan for next steps of how the feedback will be implemented and followed up on',4),
(2,'All of the above',5),
(2,'Alternative',6),
(2,'Patting someone on the back to get their attention',7),
(2,'Description',8),
(2,'Extinction Induced Variability',9);
/*!40000 ALTER TABLE `wronganswer` ENABLE KEYS */;
UNLOCK TABLES;
commit;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2025-07-08 12:51:46
