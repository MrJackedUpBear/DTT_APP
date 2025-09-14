package logging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;

public class Log {
	final String logLoc = System.getenv("DTT_LOGS");
	
	private static Log log = new Log();
	
	public static Log getInstance() {
		return log;
	}
	
	public void log(ArrayList<LogInfo> logInfo) {
		LocalDate date = LocalDate.now();
		
		String fileName = logLoc + "\\" + date + "\\" + "Request-" + logInfo.get(0).getTypeOfRequest() + ".log";
		
		try {
			Files.createDirectories(Paths.get(logLoc + "\\" + date));

			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(logLoc + "/temp " + logInfo.get(0).getTypeOfRequest() + ".log"));
			
			String line = "";
			int i = 0;
			while ((line = reader.readLine()) != null) {
				if (i != 0) {
					writer.newLine();
				}
				writer.write(line);
				i++;
			}
			
			reader.close();
			
			for (LogInfo log : logInfo) {
				writer.write(",");
				writer.newLine();
				writer.write(log.generateLog());
			}
			
			writer.close();
			
			Path tmp = Paths.get(logLoc + "/temp " + logInfo.getFirst().getTypeOfRequest() + ".log");
			Path logFile = Paths.get(fileName);
			
			Files.delete(logFile);
			Files.move(tmp, logFile);
			System.out.println("Log for " + logInfo.getFirst().getTypeOfRequest() +" updated.");
		}catch (IOException e) {
			System.out.println("Error opening log: " + e.getMessage());
			createLog(logInfo, fileName);
		}
	}
	
	public void log(LogInfo logInfo) {
		LocalDate date = LocalDate.now();
		
		String fileName = logLoc + "\\" + date + "\\" + "Request-" + logInfo.getTypeOfRequest() + ".log";
		
		try {
			Files.createDirectories(Paths.get(logLoc + "\\" + date));

			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(logLoc + "/temp " + logInfo.getTypeOfRequest() + ".log"));
			
			String line = "";
			int i = 0;
			while ((line = reader.readLine()) != null) {
				if (i != 0) {
					writer.newLine();
				}
				writer.write(line);
				i++;
			}
			
			reader.close();
			
			writer.write(",");
			writer.newLine();
			writer.write(logInfo.generateLog());
			
			writer.close();
			
			Path tmp = Paths.get(logLoc + "/temp.log");
			Path logFile = Paths.get(fileName);
			
			Files.delete(logFile);
			Files.move(tmp, logFile);
			System.out.println("Log for " + logInfo.getTypeOfRequest() +" updated.");
		}catch (IOException e) {
			System.out.println("Error opening log: " + e.getMessage());
			createLog(logInfo, fileName);
		}
	}
	
	private void createLog(ArrayList<LogInfo> logs, String fileName) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			int i = 0;
			for (LogInfo logInfo : logs) {
				if (i != 0) {
					writer.write(",");
					writer.newLine();
				}
				
				writer.write(logInfo.generateLog());
				i++;
			}
			
			writer.close();
			
			System.out.println("Log for " + logs.getFirst().getTypeOfRequest() + " successfully created.");
		} catch (IOException e) {
			System.out.println("Error creating log: " + e.getMessage());
		}
	}
	
	private void createLog(LogInfo logInfo, String fileName) {		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			
			writer.write(logInfo.generateLog());
			
			writer.close();
			
			System.out.println("Log for " + logInfo.getTypeOfRequest() + " successfully created.");
		} catch (IOException e) {
			System.out.println("Error creating log: " + e.getMessage());
		}
	}
}