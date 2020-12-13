package MusicStreaming;

public class User {
	private final int userNum;
	private final String id;
	private String password;
	private String name;
	private String phoneNumber; //char(13)
	private final String rrn; //¸ø ¹Ù²Þ, final static?
	private String email;
	
	User(int userNum, String id, String password, String name, String phoneNumber, String rrn, String email) {
		this.userNum = userNum;
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
	
	public int getUserNum() {
		return userNum;
	}
}