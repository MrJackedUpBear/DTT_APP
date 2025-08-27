/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-12.0.2-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: DTT_APP
-- ------------------------------------------------------
-- Server version	12.0.2-MariaDB

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
-- Table structure for table `tasklist`
--

DROP TABLE IF EXISTS `tasklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `tasklist` (
  `TaskLetter` char(4) NOT NULL,
  `Description` text NOT NULL,
  PRIMARY KEY (`TaskLetter`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tasklist`
--

LOCK TABLES `tasklist` WRITE;
/*!40000 ALTER TABLE `tasklist` DISABLE KEYS */;
set autocommit=0;
INSERT INTO `tasklist` VALUES
('A','Behaviorism and Philosophical Foundations'),
('A.1','Identify the goals of behavior analysis as a science (i.e., description, prediction, control).'),
('A.2','Explain the philosophical assumptions underlying the science of behavior analysis (e.g., selectionism, determinism, empiricism, parsimony, pragmatism).'),
('A.3','Explain behavior from the perspective of radical behaviorism.'),
('A.4','Distinguish among behaviorism, the experimental analysis of behavior, applied behavior analysis, and professional practice guided by the science of behavior analysis.'),
('A.5','Identify and describe dimensions of applied behavior analysis.'),
('B','Concepts and Principles'),
('B.1','Identify and distinguish among behavior, response, and response class.'),
('B.10','Identify and distinguish among concurrent, multiple, mixed, and chained schedules of reinforcement.'),
('B.11','Identify and distinguish between operant and respondent extinction as operations and processes.'),
('B.12','Identify examples of stimulus control.'),
('B.13','Identify examples of stimulus discrimination.'),
('B.14','Identify and distinguish between stimulus and response generalization.'),
('B.15','Identify examples of response maintenance.'),
('B.16','Identify examples of motivating operations.'),
('B.17','Distinguish between motivating operations and stimulus control.'),
('B.18','Identify and distinguish between rule-governed and contingency-shaped behavior.'),
('B.19','Identify and distinguish among verbal operants.'),
('B.2','Identify and distinguish between stimulus and stimulus class.'),
('B.20','Identify the role of multiple control in verbal behavior.'),
('B.21','Identify examples of processes that promote emergent relations and generative performance.'),
('B.22','Identify ways behavioral momentum can be used to understand response persistence.'),
('B.23','Identify ways the matching law can be used to interpret response allocation.'),
('B.24','Identify and distinguish between imitation and observational learning.'),
('B.3','Identify and distinguish between respondent and operant conditioning.'),
('B.4','Identify and distinguish between positive and negative reinforcement contingencies.'),
('B.5','Identify and distinguish between positive and negative punishment contingencies.'),
('B.6','Identify and distinguish between automatic and socially mediated contingencies.'),
('B.7','Identify and distinguish among unconditioned, conditioned, and generalized reinforcers.'),
('B.8','Identify and distinguish among unconditioned, conditioned, and generalized punishers.'),
('B.9','Identify and distinguish among simple schedules of reinforcement.'),
('C','Measurement, Data Display, and Interpretation'),
('C.1','Create operational definitions of behavior.'),
('C.10','Graph data to communicate relevant quantitative relations (e.g., equal-interval graphs, bar graphs, cumulative records).'),
('C.11','Interpret graphed data.'),
('C.12','Select a measurement procedure to obtain representative procedural integrity data that accounts for relevant dimensions (e.g., accuracy, dosage) and environmental constraints.'),
('C.2','Distinguish among direct, indirect, and product measures of behavior.'),
('C.3','Measure occurrence.'),
('C.4','Measure temporal dimensions of behavior (e.g., duration, latency, interresponse time).'),
('C.5','Distinguish between continuous and discontinuous measurement procedures.'),
('C.6','Design and apply discontinuous measurement procedures (e.g., interval recording, time sampling).'),
('C.7','Measure efficiency (e.g., trials to criterion, cost-benefit analysis, training duration).'),
('C.8','Evaluate the validity and reliability of measurement procedures.'),
('C.9','Select a measurement procedure to obtain representative data that accounts for the critical dimension of the behavior and environmental constraints.'),
('D','Experimental Design'),
('D.1','Distinguish between dependent and independent variables.'),
('D.2','Distinguish between internal and external validity.'),
('D.3','Identify threats to internal validity (e.g., history, maturation).'),
('D.4','Identify the defining features of single-case experimental designs (e.g., individuals serve as their own controls, repeated measures, prediction, verification, replication).'),
('D.5','Identify the relative strengths of single-case experimental designs and group designs.'),
('D.6','Critique and interpret data from single-case experimental designs.'),
('D.7','Distinguish among reversal, multiple-baseline, multielement, and changing-criterion designs.'),
('D.8','Identify rationales for conducting comparative, component, and parametric analyses.'),
('D.9','Apply single-case experimental designs.'),
('E','Ethical and Professional Issues'),
('E.1','Identify and apply core principles underlying the ethics codes for BACB certificants (e.g., benefit others; treat others with compassion, dignity, and respect; behave with integrity).'),
('E.10','Apply culturally responsive and inclusive service and supervision activities.'),
('E.11','Identify personal biases and how they might interfere with professional activity.'),
('E.12','Identify and apply the legal, regulatory, and practice requirements (e.g., licensure, jurisprudence, funding, certification) relevant to the delivery of behavior- analytic services.'),
('E.2','Identify the risks to oneself, others, and the profession as a result of engaging in unethical behavior.'),
('E.3','Develop and maintain competence by engaging in professional development activities (e.g., read literature, seek consultation, establish mentors).'),
('E.4','Identify and comply with requirements for collecting, using, protecting, and disclosing confidential information.'),
('E.5','Identify and comply with requirements for making public statements about professional activities (e.g., social media activity; misrepresentation of professional credentials, behavior analysis, and service outcomes).'),
('E.6','Identify the conditions under which services or supervision should be discontinued and apply steps that should be taken when transitioning clients and supervisees to another professional.'),
('E.7','Identify types of and risks associated with multiple relationships, and how to mitigate those risks when they are unavoidable.'),
('E.8','Identify and apply interpersonal and other skills (e.g., accepting feedback, listening actively, seeking input, collaborating) to establish and maintain professional relationships.'),
('E.9','Engage in cultural humility in service delivery and professional relationships.'),
('F','Behavior Assessment'),
('F.1','Identify relevant sources of information in records (e.g., educational, medical, historical) at the outset of the case.'),
('F.2','Identify and integrate relevant cultural variables in the assessment process.'),
('F.3','Design and evaluate assessments of relevant skill strengths and areas of need.'),
('F.4','Design and evaluate preference assessments.'),
('F.5','Design and evaluate descriptive assessments.'),
('F.6','Design and evaluate functional analyses.'),
('F.7','Interpret assessment data to determine the need for behavior-analytic services and/or referral to others.'),
('F.8','Interpret assessment data to identify and prioritize socially significant, client-informed, and culturally responsive behavior-change procedures and goals.'),
('G','Behavior-Change Procedures'),
('G.1','Design and evaluate positive and negative reinforcement procedures.'),
('G.10','Design and evaluate instructions and rules.'),
('G.11','Shape dimensions of behavior.'),
('G.12','Select and implement chaining procedures.'),
('G.13','Design and evaluate trial-based and free- operant procedures.'),
('G.14','Design and evaluate group contingencies.'),
('G.15','Design and evaluate procedures to promote stimulus and response generalization.'),
('G.16','Design and evaluate procedures to maintain desired behavior change following intervention (e.g., schedule thinning, transferring to naturally occurring reinforcers).'),
('G.17','Design and evaluate positive and negative punishment (e.g., time-out, response cost, overcorrection).'),
('G.18','Evaluate emotional and elicited effects of behavior- change procedures.'),
('G.19','Design and evaluate procedures to promote emergent relations and generative performance.'),
('G.2','Design and evaluate differential reinforcement (e.g., DRA, DRO, DRL, DRH) procedures with and without extinction.'),
('G.3','Design and evaluate time-based reinforcement (e.g., fixed- time) schedules.'),
('G.4','Identify procedures to establish and use conditioned reinforcers (e.g., token economies).'),
('G.5','Incorporate motivating operations and discriminative stimuli into behavior-change procedures.'),
('G.6','Design and evaluate procedures to produce simple and conditional discriminations.'),
('G.7','Select and evaluate stimulus and response prompting procedures (e.g., errorless, most-to-least, least-to-most).'),
('G.8','Design and implement procedures to fade stimulus and response prompts (e.g., prompt delay, stimulus fading).'),
('G.9','Design and evaluate modeling procedures.'),
('H','Selecting and Implementing Interventions'),
('H.1','Develop intervention goals in observable and measurable terms.'),
('H.2','Identify and recommend interventions based on assessment results, scientific evidence, client preferences, and contextual fit (e.g., expertise required for implementation, cultural variables, environmental resources).'),
('H.3','Select socially valid alternative behavior to be established or increased when a target behavior is to be decreased.'),
('H.4','Plan for and attempt to mitigate possible unwanted effects when using reinforcement, extinction, and punishment procedures.'),
('H.5','Plan for and attempt to mitigate possible relapse of the target behavior.'),
('H.6','Make data-based decisions about procedural integrity.'),
('H.7','Make data-based decisions about the effectiveness of the intervention and the need for modification.'),
('H.8','Collaborate with others to support and enhance client services.'),
('I','Personnel Supervision and Management'),
('I.1','Identify the benefits of using behavior-analytic supervision (e.g., improved client outcomes, improved staff performance and retention).'),
('I.2','Identify and apply strategies for establishing effective supervisory relationships (e.g., executing supervisor- supervisee contracts, establishing clear expectations, giving and accepting feedback).'),
('I.3','Identify and implement methods that promote equity in supervision practices.'),
('I.4','Select supervision goals based on an assessment of the supervisee’s skills, cultural variables, and the environment.'),
('I.5','Identify and apply empirically validated and culturally responsive performance management procedures (e.g., modeling, practice, feedback, reinforcement, task clarification, manipulation of response effort).'),
('I.6','Apply a function-based approach (e.g., performance diagnostics) to assess and improve supervisee behavior.'),
('I.7','Make data-based decisions about the efficacy of supervisory practices.');
/*!40000 ALTER TABLE `tasklist` ENABLE KEYS */;
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

-- Dump completed on 2025-08-27 18:27:32
