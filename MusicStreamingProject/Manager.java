package MusicStreaming;

public class Manager {
	private final int managerNum;
	private final String id;
	private String password;
	private String name;
	private String phoneNumber;
	private final String rrn;
	private String email;
	
	Manager(int managerNum, String id, String password, String name, String phoneNumber, String rrn, String email) {
		this.managerNum = managerNum;
		this.id = id;
		this.password = password;
		this.name =  name;
		this.phoneNumber =  phoneNumber;
		this.rrn = rrn;
		this.email =  email;
	}
	
	public String getId() {
		return id;
	}
	
	public int getManagerNum() {
		return managerNum;
	}
}
//final static?