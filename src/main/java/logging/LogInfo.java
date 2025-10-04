package logging;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import database.User;

public class LogInfo {
	private String typeOfRequest;
	private User user;
	private String level;
	private String logInfo;
	private ArrayList<LogInfo> logs = new ArrayList<>();
	
	public LogInfo() {
		typeOfRequest = "";
		user = new User();
		level = "";
		logInfo = "";
	}
	
	public void addLog(LogInfo logInfo) {		
		LogInfo l = new LogInfo();
		l.setLevel(logInfo.getLevel());
		l.setLogInfo(logInfo.getLogInfo());
		l.setTypeOfRequest(logInfo.getTypeOfRequest());
		l.setUser(logInfo.getUser());
		
		logs.add(l);
	}
	
	public void setTypeOfRequest(String typeOfRequest) {
		this.typeOfRequest = typeOfRequest;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}
	
	public void setLogInfo(String logInfo) {
		this.logInfo = logInfo;
	}
	
	public void log() {
		Log.getInstance().log(logs);
	}
	
	public String getTypeOfRequest() {
		return typeOfRequest;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getLevel() {
		return level;
	}
	
	public String getLogInfo() {
		return logInfo;
	}
	
	public String generateLog() {
		StringBuilder log = new StringBuilder();
		
		LocalDate date = LocalDate.now();
		LocalTime time = LocalTime.now();
		
		log.append("{");
		log.append("\n\t");
		log.append("\"Timestamp\":\"" + date + " " + time + "\",");
		log.append("\n\t");
		log.append("\"Level\":\"" + level + "\",");
		log.append("\n\t");
		log.append("\"Message\":\"" + logInfo + "\",");
		log.append("\n\t");
		log.append("\"UserId\":\"" + user.getUserId() + "\",");
		log.append("\n\t");
		log.append("\"UserName\":\"" + user.getLastName() + ", " + user.getFirstName() +"\",");
		log.append("\n\t");
		log.append("\"Action\":\"" + typeOfRequest + "\"");
		log.append("\n");
		log.append("}");
		
		return log.toString();
	}
}
