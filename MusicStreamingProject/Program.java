package MusicStreaming;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Program {
	private static Connection con = null;
	private static PreparedStatement pstmt = null;
	private static ResultSet rs = null;
	
	public static void connectDB() {
		try {
            Class.forName("org.mariadb.jdbc.Driver"); //Drive 클래스 로드
            con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/musicstreaming", "root", "1qw@3er$");
            System.out.println("연결성공");
        } catch(ClassNotFoundException e) {
        	System.out.println("드라이브 로딩 실패");
        	System.exit(0);
        } catch(SQLException e) {
        	System.out.println("DB 연결 실패");
        	System.exit(0);
        }
	}
	
	public static void unconnectDB() {
		try {
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			if(con != null) con.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public static void login() {
		try {
			System.out.println();
			System.out.println("------------------  Login  -------------------");
			System.out.print("Id : "); String id = Main.sc.next();
			System.out.print("Password : "); String password = Main.sc.next();
			boolean tf;
			pstmt = con.prepareStatement("SELECT * FROM user WHERE id = ?"); pstmt.setString(1, id);rs = pstmt.executeQuery();
			tf = rs.next(); //user에 해당하는 아이디 존재 유무
			if(!tf) {
				pstmt = con.prepareStatement("SELECT * FROM manager WHERE id = ?"); pstmt.setString(1, id); rs = pstmt.executeQuery();
				tf = rs.next(); //manager에 해당하는 아이디 존재 유무
				if(!tf) System.out.println("아이디 또는 비밀번호가 잘못되었습니다.");
				else { //manager일 경우
					if(!rs.getString("password").equals(password)) System.out.println("아이디 또는 비밀번호가 잘못되었습니다.");
					else {
						pstmt = con.prepareStatement("SELECT * FROM manager WHERE id = ?"); pstmt.setString(1, id); rs = pstmt.executeQuery(); rs.next();
						int managerNum = rs.getInt("managerno"); String name = rs.getString("name"); String phoneNumber = rs.getString("phonenumber");
						String rrn  = rs.getString("rrn"); String email = rs.getString("email");
						Manager manager = new Manager(managerNum, id, password, name, phoneNumber, rrn, email);
						managerProgram(manager);
					}
				}
			}
			else { //user일 경우
				if(!rs.getString("password").equals(password)) System.out.println("\n아이디 또는 비밀번호가 잘못되었습니다.");
				else {
					pstmt = con.prepareStatement("SELECT * FROM user WHERE id = ?"); pstmt.setString(1, id); rs = pstmt.executeQuery(); rs.next();
					int userNum = rs.getInt("userno"); String name = rs.getString("name"); String phoneNumber = rs.getString("phonenumber");
					String rrn  = rs.getString("rrn"); String email = rs.getString("email");
					User user = new User(userNum, id, password, name, phoneNumber, rrn, email);
					userProgram(user);
				}
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void signUp() {
		try {
			boolean tf;
			System.out.println();
			System.out.println("------------------  SignUp  -------------------");
			System.out.print("Manager code : "); String mgr = Main.sc.next();
			if(mgr.equals("2017029643")) { //1. if manager signup
				String id; int managerNum; String rrn; String phoneNumber;
				System.out.print("Name : "); String name = Main.sc.next();
				while(true) {
					System.out.print("Resident Registration Number (XXXXXX-XXXXXXX): "); rrn = Main.sc.next();
					if(rrn.length() != 14) {
						System.out.println("잘못 입력하셨습니다.");
						continue;
					}
					break;
				}
				pstmt = con.prepareStatement("SELECT * FROM manager WHERE rrn = ?"); pstmt.setString(1, rrn); rs = pstmt.executeQuery(); tf = rs.next();
				if(tf) {
					System.out.println("이미 존재하는 매니저입니다.");
					return;
				}
				while(true) {
					System.out.print("Id : "); id = Main.sc.next();
					pstmt = con.prepareStatement("SELECT m.id FROM manager as m, user as u WHERE m.id = ? OR u.id = ?");
					pstmt.setString(1, id); pstmt.setString(2, id); rs = pstmt.executeQuery(); tf = rs.next();
					if(tf) {
						System.out.println("해당 ID는 존재하는 ID입니다.");
						System.out.println("다시 입력해주세요.");
						continue;
					}
					else break;
				}
				System.out.print("Password : "); String password = Main.sc.next();
				while(true) {
					System.out.print("PhoneNumber (010-XXXX-XXXX) : "); phoneNumber = Main.sc.next();
					if(phoneNumber.length() != 13) {
						System.out.println("잘못 입력하셨습니다.");
						continue;
					}
					break;
				}
				System.out.print("Email : "); String email = Main.sc.next();
				while(true) {
					managerNum = (int)(Math.random()*999999999); //랜덤값 0~999999999
					pstmt = con.prepareStatement("SELECT * FROM manager WHERE managerno = ?"); pstmt.setInt(1, managerNum);
					rs = pstmt.executeQuery();
					if(rs.next()) continue; //랜덤값이 중복
					else break;
				}
					
				pstmt = con.prepareStatement("INSERT INTO manager VALUES(?, ?, ?, ?, ?, ?, ?)");
				pstmt.setInt(1, managerNum); pstmt.setString(2, id); pstmt.setString(3, password); pstmt.setString(4, name);
				pstmt.setString(5, phoneNumber); pstmt.setString(6, rrn); pstmt.setString(7, email); pstmt.executeUpdate();
			}
			else { //2. if user signup
				String id; int userNum; String rrn;	String phoneNumber;
				System.out.print("Name : "); String name = Main.sc.next();
				while(true) {
					System.out.print("Resident Registration Number (XXXXXX-XXXXXXX): "); rrn = Main.sc.next();
					if(rrn.length() != 14) {
						System.out.println("잘못 입력하셨습니다.");
						continue;
					}
					break;
				}
				pstmt = con.prepareStatement("SELECT * FROM user WHERE rrn = ?"); pstmt.setString(1, rrn); rs = pstmt.executeQuery(); tf = rs.next();
				if(tf) {
					System.out.println("이미 존재하는 회원입니다.");
					return;
				}
				while(true) {
					System.out.print("Id : "); id = Main.sc.next();
					pstmt = con.prepareStatement("SELECT m.id FROM manager as m, user as u WHERE m.id = ? OR u.id = ?");
					pstmt.setString(1, id); pstmt.setString(2, id); rs = pstmt.executeQuery(); tf = rs.next();
					if(tf) {
						System.out.println("해당 ID는 존재하는 ID입니다.");
						System.out.println("다시 입력해주세요.");
						continue;
					}
					else break;
				}
				System.out.print("Password : "); String password = Main.sc.next();
				while(true) {
					System.out.print("PhoneNumber (010-XXXX-XXXX) : "); phoneNumber = Main.sc.next();
					if(phoneNumber.length() != 13) {
						System.out.println("잘못 입력하셨습니다.");
						continue;
					}
					break;
				}
				System.out.print("Email : "); String email = Main.sc.next();
				while(true) {
					userNum = (int)(Math.random()*999999999); //랜덤값 0~999999999
					pstmt = con.prepareStatement("SELECT * FROM user WHERE userno = ?");
					pstmt.setInt(1, userNum); rs = pstmt.executeQuery();
					if(rs.next()) continue;
					else break;
				}
					
				pstmt = con.prepareStatement("INSERT INTO user VALUES(?, ?, ?, ?, ?, ?, ?)");
				pstmt.setInt(1, userNum); pstmt.setString(2, id); pstmt.setString(3, password);	pstmt.setString(4, name);
				pstmt.setString(5, phoneNumber); pstmt.setString(6, rrn); pstmt.setString(7, email); pstmt.executeUpdate();
				System.out.println("회원가입 되셨습니다.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}
	
	public static void userProgram(User user) {
		System.out.println();
		System.out.println("========================  Welcome, " + user.getId() + "  ==============================");
		while(true) {
			System.out.println();
			System.out.print("1. Music chart, 2. Latest Music, 3. Genre Music, 4. Show Playlists, 5. Exit : "); int menu = Main.sc.nextInt();
			switch(menu) {
			case 1:
				showMusics(user, 1); continue;
			case 2:
				showMusics(user, 2); continue;
			case 3:
				showMusics(user, 3); continue;
			case 4:
				showPlaylists(user); continue;
			case 5:
				System.out.println("Good Bye, " + user.getId()); return;
			default:
				System.out.println("====  Wrong command  ===="); continue;
			}
		}
	}
	
	public static void showMusics(User user, int menu) {
		String sql = null; String genre = null; String title = null; boolean tf;
		switch(menu) {
		case 1 :
			sql = "SELECT * FROM music ORDER BY playnum DESC, likenum DESC, name ASC";
			title = "Music chart";
			break;
		case 2 :
			sql = "SELECT * FROM music ORDER BY releasedate DESC, name ASC";
			title = "Latest Music";
			break;
		case 3 :
			while(true) {
				System.out.print("1. 발라드, 2. 댄스, 3. 랩/힙합, 4. R&B/Soul, 5.록/메탈, 6. 트로트 : "); int genreMenu = Main.sc.nextInt();
				sql = "SELECT * FROM music WHERE genre = ? ORDER BY playnum DESC, likenum DESC, name ASC";
				switch(genreMenu) {
				case 1 :
					genre =  "발라드"; title = "발라드 Music";
					break;
				case 2 :
					genre =  "댄스"; title = "댄스 Music";
					break;
				case 3 :
					genre =  "랩/힙합"; title = "랩/힙합 Music";
					break;
				case 4 :
					genre =  "R&B/Soul"; title = "R&B/Soul Music";
					break;
				case 5 :
					genre = "록/메탈"; title = "록/메탈 Music";
					break;
				case 6 :
					genre =  "트로트"; title = "트로트 Music";
					break;
				default :
					System.out.println("====  Wrong command  ====");
					continue;
				}
				break;
			}
		}
		
		try {
			while(true) {
				pstmt = con.prepareStatement(sql); if(menu == 3) pstmt.setString(1, genre); rs = pstmt.executeQuery();
				System.out.println();
				System.out.println("=================================  " + title + "  =================================");
				System.out.println("|        제목        |      아티스트      |     장르     | 좋아요 |    발매일    |");
				while(rs.next()) {
					System.out.println("---------------------------------------------------------------------------------");
					System.out.println("|     " + rs.getString("name") + "    |    " + rs.getString("artist") + "    |    " +
							rs.getString("genre") + "    |   " + rs.getInt("likenum") + "   |    " + rs.getDate("releasedate") + "    |");
				}
				System.out.println("=================================================================================");
				System.out.print("1. Music select, 2. Exit : "); int menu2 = Main.sc.nextInt();
				rs = pstmt.executeQuery(); tf = rs.next();
				switch(menu2) {
				case 1:
					if(!tf) {
						System.out.println("There is no music");
						continue;
					}
					Main.sc.nextLine();
					String musicName;
					while(true) {
						System.out.print("Music name : "); musicName = Main.sc.nextLine();
						if(menu == 1 || menu == 2) {
							pstmt = con.prepareStatement("SELECT * FROM music WHERE name = ?"); pstmt.setString(1, musicName); rs = pstmt.executeQuery(); //해당 음원
						}
						else if(menu == 3) {
							pstmt = con.prepareStatement("SELECT * FROM music WHERE genre = ? AND name = ?");
							pstmt.setString(1, genre); pstmt.setString(2, musicName); rs = pstmt.executeQuery(); //해당 음원
						}
						tf = rs.next();
						if(!tf) { //이름 잘못 입력 시
							System.out.println("Wrong name!");
							continue;
						}
						else break;
					}
					int musicNum = rs.getInt("musicno");
					while(true) {
						pstmt = con.prepareStatement("SELECT * FROM music WHERE musicno = ?"); pstmt.setInt(1, musicNum); rs = pstmt.executeQuery(); rs.next();
						System.out.println();
						System.out.println("=============================================================================");
						System.out.println("|   " + rs.getString("name") + "    |   " + rs.getString("artist") + "   |   " + rs.getString("genre") +
											"   |   " + rs.getInt("likenum") + "   |   " + rs.getDate("releaseDate") +  "   |");
						System.out.println("=============================================================================");
						System.out.print("1. Play, 2. Add to playlist, 3. Like, 4. Exit : "); int menu3 = Main.sc.nextInt();
						switch(menu3) {
						case 1:
							System.out.println("Play music \"" + rs.getString("name") + "\" ~~~");
							pstmt = con.prepareStatement("UPDATE music SET playnum = ? WHERE musicno = ?");
							pstmt.setInt(1, rs.getInt("playnum")+1); pstmt.setInt(2, rs.getInt("musicno")); pstmt.executeUpdate();
							continue;
						case 2:
							while(true) {
								pstmt = con.prepareStatement("SELECT name FROM playlist WHERE uno = ?");
								pstmt.setInt(1, user.getUserNum()); rs = pstmt.executeQuery(); //유저의 플레이리스트들
								System.out.println(); int i = 1;
								System.out.println("---------Playlists----------");
								while(rs.next()) System.out.println("| " + i++ + ".        " + rs.getString("name") + "           |");
								System.out.println("----------------------------");
								System.out.print("1. Select playlist to add, 2. Make new playist to add, 3. Exit : "); int menu4 = Main.sc.nextInt();
								rs = pstmt.executeQuery(); tf = rs.next();
								//플레이리스트에 중복 노래 제거?
								String playlistName;
								switch(menu4) {
								case 1:
									if(!tf) {
										System.out.println("There is no music");
										continue;
									}
									Main.sc.nextLine();
									while(true) {
										System.out.print("Playlist name : "); playlistName = Main.sc.nextLine();
										pstmt = con.prepareStatement("SELECT * FROM playlist WHERE name = ?"); pstmt.setString(1, playlistName); rs = pstmt.executeQuery();
										tf = rs.next();
										if(!tf) {  //이름 잘못 입력 시
											System.out.println("Wrong name!");
											continue;
										}
										else break;
									}
									pstmt = con.prepareStatement("INSERT INTO playlistupload(uno, name, mno) VALUES(?, ?, ?)");
									pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); pstmt.setInt(3, musicNum); pstmt.executeUpdate();
									System.out.println("Add Music \"" + musicName + " to your playlist \"" + playlistName + "\"");
									break;
								case 2 :
									Main.sc.nextLine();
									while(true) {
										System.out.print("New playlist name : "); playlistName = Main.sc.nextLine();
										pstmt = con.prepareStatement("SELECT * FROM playlist WHERE uno = ? AND name = ?");
										pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); rs = pstmt.executeQuery();
										tf = rs.next();
										if(tf) {
											System.out.println("이미 존재하는 이름입니다.");
											continue;
										}
										break;
									}
									pstmt = con.prepareStatement("INSERT INTO playlist(uno, name) VALUES(?, ?)");
									pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); pstmt.executeUpdate();
									pstmt = con.prepareStatement("INSERT INTO playlistupload(uno, name, mno) VALUES(?, ?, ?)");
									pstmt.setInt(1, user.getUserNum());	pstmt.setString(2, playlistName); pstmt.setInt(3, musicNum); pstmt.executeUpdate();
									System.out.println("Add Music \"" + musicName + "\" to your playlist \"" + playlistName + "\"");
									break;
								case 3:
									break;
								default:
									System.out.println("====  Wrong command  ===="); continue;
								}
								break;
							}
							continue;
						case 3:
							int likenum = rs.getInt("likenum");
							pstmt = con.prepareStatement("SELECT * FROM userlike WHERE uno = ? AND mno = ?");
							pstmt.setInt(1, user.getUserNum()); pstmt.setInt(2, musicNum); rs = pstmt.executeQuery();
							tf = rs.next();
							if(tf) System.out.println("Already like it");
							else {
								pstmt = con.prepareStatement("UPDATE music SET likenum = ? WHERE musicno = ?");
								pstmt.setInt(1, likenum + 1); pstmt.setInt(2, musicNum); pstmt.executeUpdate();
								pstmt = con.prepareStatement("INSERT INTO userlike VALUES(?, ?)");
								pstmt.setInt(1, user.getUserNum()); pstmt.setInt(2, musicNum); pstmt.executeUpdate();
								System.out.println("Like Music \"" + musicName + "\"");
							}
							continue;
						case 4:
							break;
						default:
							System.out.println("====  Wrong command  ===="); continue;	
						}
						break;
					}
					continue;
				case 2:
					return;
				default:
					System.out.println("====  Wrong command  ===="); continue;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void showPlaylists(User user) {
		try {
			boolean tf;
			while(true) {
				pstmt = con.prepareStatement("SELECT name FROM playlist WHERE uno = ?"); pstmt.setInt(1, user.getUserNum()); rs = pstmt.executeQuery(); //플레이리스트들
				System.out.println(); int i = 1;
				System.out.println("---------Playlists----------");
				while(rs.next()) System.out.println("| " + i++ + ".        " + rs.getString("name") + "           |");
				System.out.println("----------------------------");
				System.out.print("1. Select playlist, 2. Make playlist, 3. Exit : "); int menu = Main.sc.nextInt();
				rs = pstmt.executeQuery(); tf = rs.next();
				switch(menu) {
				case 1 :
					if(!tf) {
						System.out.println("There is no playlists");
						continue;
					}
					String playlistName;
					Main.sc.nextLine();
					while(true) {
						System.out.print("Playlist name : "); playlistName = Main.sc.nextLine();
						pstmt = con.prepareStatement("SELECT * FROM playlist WHERE uno = ? AND name = ?");
						pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); rs = pstmt.executeQuery(); //해당 플레이리스트
						tf = rs.next();
						if(!tf) {
							System.out.println("Wrong name!");
							continue;
						}
						else break;
					}
					while(true) {
						pstmt = con.prepareStatement("SELECT * FROM playlistupload as p, music as m WHERE p.uno = ? AND p.name = ? AND p.mno = m.musicno ORDER BY m.releasedate DESC, m.name ASC");
						pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); rs = pstmt.executeQuery();
						System.out.println();
						System.out.println("====================================  " + playlistName + "  ====================================");
						System.out.println("|         제목         |        아티스트        |     장르     | 좋아요 |    발매일    |");
						while(rs.next()) {
							System.out.println("---------------------------------------------------------------------------------");
							System.out.println("|     " + rs.getString("m.name") + "    |    " + rs.getString("artist") + "    |    " +
									rs.getString("genre") + "    |   " + rs.getInt("likenum") + "   |    " + rs.getDate("releasedate") + "    |");
						}
						System.out.println("=================================================================================");
						System.out.print("1. Select Music, 2. Delete Playlist, 3. Change name, 4. Exit : "); int menu2 = Main.sc.nextInt();
						rs = pstmt.executeQuery(); tf = rs.next();
						switch(menu2) {
						case 1:
							if(!tf) {
								System.out.println("There is no music here");
								continue;
							}
							String musicName;
							Main.sc.nextLine();
							while(true) {
								System.out.print("Music name : "); musicName = Main.sc.nextLine();
								pstmt = con.prepareStatement("SELECT * FROM playlistupload as p, music as m WHERE p.uno = ? AND p.name = ? AND p.mno = m.musicno AND m.name = ?");
								pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); pstmt.setString(3, musicName); rs = pstmt.executeQuery(); //해당 음원
								tf = rs.next();
								if(!tf) { //이름 잘못 입력 시
									System.out.println("Wrong name!");
									continue;
								}
								else break;
							}
							int musicNum = rs.getInt("musicno");
							
							while(true) {
								System.out.println();
								System.out.println("=========================================================================");
								System.out.println("|   " + rs.getString("m.name") + "    |   " + rs.getString("artist") + "   |   " + rs.getString("genre") +
													"   |   " + rs.getInt("likenum") + "   |   " + rs.getDate("releaseDate") +  "   |");
								System.out.println("=========================================================================");
								System.out.print("1. Play, 2. Delete from playlist, 3. Like, 4. Exit : "); int menu3 = Main.sc.nextInt();
								switch(menu3) {
								case 1:
									System.out.println("Play music \"" + rs.getString("m.name") + "\" ~~~");
									pstmt = con.prepareStatement("UPDATE music SET playnum = ? WHERE musicno = ?");
									pstmt.setInt(1, rs.getInt("playnum")+1); pstmt.setInt(2, rs.getInt("musicno")); pstmt.executeUpdate();
									continue;
								case 2:
									System.out.print("Delete this from playlist? (yes or no) : "); String yn = Main.sc.next();
									if(yn.equals("no")) continue;
									else if(yn.equals("yes")) {
										pstmt = con.prepareStatement("DELETE FROM playlistupload WHERE uno = ? AND name = ? AND mno = ?");
										pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); pstmt.setInt(3, musicNum);
										pstmt.executeUpdate();
										System.out.println("Delete music \"" + rs.getString("m.name") + "\" from playlist \"" + playlistName + "\"");
										break;
									}
									else {
										System.out.println("====  Wrong command  ====");
										continue;
									}
								case 3:
									int likenum = rs.getInt("likenum");
									pstmt = con.prepareStatement("SELECT * FROM userlike WHERE uno = ? AND mno = ?");
									pstmt.setInt(1, user.getUserNum()); pstmt.setInt(2, musicNum); rs = pstmt.executeQuery();
									tf = rs.next();
									if(tf) { //이미 좋아요 눌렀을 경우
										System.out.println("Already like it");
										pstmt = con.prepareStatement("SELECT * FROM playlistupload as p, music as m WHERE p.uno = ? AND p.name = ? AND p.mno = m.musicno AND m.name = ?");
										pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); pstmt.setString(3, musicName); rs = pstmt.executeQuery(); rs.next();
										continue;
									}
									pstmt = con.prepareStatement("UPDATE music SET likenum = ? WHERE musicno = ?");
									pstmt.setInt(1, likenum + 1); pstmt.setInt(2, musicNum); pstmt.executeUpdate();
									pstmt = con.prepareStatement("INSERT INTO userlike VALUES(?, ?)");
									pstmt.setInt(1, user.getUserNum()); pstmt.setInt(2, musicNum); pstmt.executeUpdate();
									System.out.println("Like music \"" + musicName + "\"");
									pstmt = con.prepareStatement("SELECT * FROM playlistupload as p, music as m WHERE p.uno = ? AND p.name = ? AND p.mno = m.musicno AND m.name = ?");
									pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); pstmt.setString(3, musicName); rs = pstmt.executeQuery(); rs.next();
									continue;
								case 4:
									break;
								default:
									System.out.println("====  Wrong command  ===="); continue;
								}
								break;
							}
							continue;
						case 2:
							System.out.print("Delete this playlist? (yes or no) : "); String yn = Main.sc.next();
							if(yn.equals("no")) continue;
							else if (yn.equals("yes")) {
								pstmt = con.prepareStatement("DELETE FROM playlistupload WHERE uno = ? AND name = ?");
								pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); pstmt.executeUpdate();
								pstmt = con.prepareStatement("DELETE FROM playlist WHERE uno = ? AND name = ?");
								pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); pstmt.executeUpdate();
								System.out.println("Delete playlist \"" + playlistName + "\"");
								break;
							}
							else {
								System.out.println("====  Wrong command  ====");
								continue;
							}
						case 3:
							String newplaylistName;
							while(true) {
								System.out.print("Change Name : "); newplaylistName = Main.sc.next();
								if(playlistName.equals(newplaylistName)) {
									System.out.println("기존 이름과 동일합니다.");
									continue;
								}
								pstmt = con.prepareStatement("SELECT * FROM playlist WHERE uno = ? AND name = ?");
								pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, newplaylistName); rs = pstmt.executeQuery();
								tf = rs.next();
								if(tf) {
									System.out.println("이미 존재하는 이름입니다.");
									continue;
								}
								break;
							}
							pstmt = con.prepareStatement("INSERT INTO playlist VALUES(?, ?)");
							pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, newplaylistName); pstmt.executeUpdate();
							pstmt = con.prepareStatement("UPDATE playlistupload SET name = ? WHERE name = ?");
							pstmt.setString(1, newplaylistName); pstmt.setString(2, playlistName); pstmt.executeUpdate();
							pstmt = con.prepareStatement("DELETE FROM playlist WHERE uno = ? AND name = ?");
							pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); pstmt.executeUpdate();
							playlistName = newplaylistName;
							System.out.println("Change playlistname to \"" + newplaylistName + "\"");
							continue;
						case 4:
							break;
						default:
							System.out.println("====  Wrong command  ===="); continue;
						}
						break;
					}
					continue;
				case 2:
					Main.sc.nextLine();
					while(true) {
						System.out.print("New playlist name : "); playlistName = Main.sc.nextLine();
						pstmt = con.prepareStatement("SELECT * FROM playlist WHERE uno = ? AND name = ?");
						pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); rs = pstmt.executeQuery();
						tf = rs.next();
						if(tf) {
							System.out.println("이미 존재하는 이름입니다.");
							continue;
						}
						break;
					}
					pstmt = con.prepareStatement("INSERT INTO playlist(uno, name) VALUES(?, ?)");
					pstmt.setInt(1, user.getUserNum()); pstmt.setString(2, playlistName); pstmt.executeUpdate();
					System.out.println("Make new playlist \"" + playlistName + "\"");
					break;
				case 3:
					return;
				default:
					System.out.println("====  Wrong command  ===="); continue;
				}
				continue;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void managerProgram(Manager manager) {
		System.out.println();
		System.out.println("=================================  Welcome manager, " + manager.getId() + "  =================================");
		while(true) {
			System.out.println();
			System.out.print("1. Show Musics, 2. Show Users, 3. Show my upload, 4. Statistics, 5. Exit : "); int menu = Main.sc.nextInt();
			switch(menu) {
			case 1:
				showMusics(manager); continue;
			case 2:
				showUsers(); continue;
			case 3:
				showUpload(manager); continue;
			case 4:
				showStatistics(); continue;
			case 5:
				System.out.println("Good Bye, " + manager.getId()); return;
			default:
				System.out.println("====  Wrong command  ===="); continue;
			}
		}
	}
	
	public static void showMusics(Manager manager) {
		try {
			boolean tf;
			while(true) {
				pstmt = con.prepareStatement("SELECT * FROM music ORDER BY releasedate DESC, name ASC"); rs = pstmt.executeQuery();
				System.out.println();
				System.out.println("==================================================  Music  ==================================================");
				System.out.println("|  음악 번호  |         제목        |      아티스트      |     장르     | 좋아요 | 재생 횟수 |    발매일    |");
				while(rs.next()) {
					System.out.println("-------------------------------------------------------------------------------------------------------------");
					System.out.println("|  " + rs.getInt("musicno") + "  |     " + rs.getString("name") + "    |    " + rs.getString("artist") + "    |    " +
							rs.getString("genre") + "    |   " + rs.getInt("likenum") + "   |   " + rs.getInt("playnum") + "  |  " +  rs.getDate("releasedate") + "   |");
				}
				System.out.println("=============================================================================================================");
				System.out.print("1. Music select, 2. Add Music, 3. Exit : "); int menu = Main.sc.nextInt();
				rs = pstmt.executeQuery(); tf = rs.next();
				switch(menu) {
				case 1:
					if(!tf) {
						System.out.println("There is no music here");
						continue;
					}
					int musicNum;
					while(true) {
						System.out.print("Music number : "); musicNum = Main.sc.nextInt();
						pstmt = con.prepareStatement("SELECT * FROM music WHERE musicno = ?"); pstmt.setInt(1, musicNum); rs  = pstmt.executeQuery(); //해당 음원
						tf = rs.next();
						if(!tf) { //번호 잘못 입력 시
							System.out.println("Wrong number!");
							continue;
						}
						else break;
					}
					System.out.println();
					System.out.println("======================================================================================");
					System.out.println("|  " + rs.getInt("musicno")+ "  |   " + rs.getString("name") + "  |  " + rs.getString("artist") + "  |  " +
										rs.getString("genre") + "  |  " + rs.getInt("likenum") + "  |  "  + rs.getInt("playnum") + "  |  " + rs.getDate("releaseDate") +  "   |");
					System.out.println("======================================================================================");
					System.out.print("1. Update, 2. Delete, 3. Exit : "); int menu2 = Main.sc.nextInt();
					switch(menu2) {
					case 1 :
						System.out.print("1. Name, 2. Artist, 3. Genre, 4. Release Date : "); int menu3 = Main.sc.nextInt();
						switch(menu3) {
						case 1:
							Main.sc.nextLine();
							System.out.print("Name : "); String name = Main.sc.nextLine();
							pstmt = con.prepareStatement("UPDATE music SET name = ? WHERE musicno = ?");
							pstmt.setString(1, name); pstmt.setInt(2, musicNum);
							break;
						case 2:
							Main.sc.nextLine();
							System.out.print("Artist : "); String artist = Main.sc.nextLine();
							pstmt = con.prepareStatement("UPDATE music SET artist = ? WHERE musicno = ?");
							pstmt.setString(1, artist); pstmt.setInt(2, musicNum);
							break;
						case 3:
							System.out.print("Genre : "); String genre = Main.sc.next();
							pstmt = con.prepareStatement("UPDATE music SET genre = ? WHERE musicno = ?");
							pstmt.setString(1, genre); pstmt.setInt(2, musicNum);
							break;
						case 4:
							System.out.println();
							System.out.print("Release Date (yyyy-MM-dd): "); String str = Main.sc.next();
							pstmt = con.prepareStatement("UPDATE music SET releasedate = ? WHERE musicno = ?");
							pstmt.setDate(1, transDate(str)); pstmt.setInt(2, musicNum);
							break;
						default:
							System.out.println("====  Wrong command  ===="); continue;
						}
						pstmt.executeUpdate();
						pstmt = con.prepareStatement("SELECT * FROM music WHERE musicno = ?"); pstmt.setInt(1, musicNum); rs = pstmt.executeQuery(); //수정된 음원
						rs.next();
						System.out.println();
						System.out.println("Change To");
						System.out.println("======================================================================================");
						System.out.println("|  " + rs.getInt("musicno")+ "  |   " + rs.getString("name") + "  |  " + rs.getString("artist") +
											"  |  " + rs.getString("genre") + "  |  " + rs.getInt("likenum") + "  |  "  + rs.getInt("playnum") + "  |  " + rs.getDate("releaseDate") +  "   |");
						System.out.println("======================================================================================");
						break;
					case 2 :
						pstmt = con.prepareStatement("DELETE FROM musicupload WHERE mno = ?"); pstmt.setInt(1, musicNum); pstmt.executeUpdate();
						pstmt = con.prepareStatement("DELETE FROM playlistupload WHERE mno = ?"); pstmt.setInt(1, musicNum); pstmt.executeUpdate();
						pstmt = con.prepareStatement("DELETE FROM userlike WHERE mno = ?"); pstmt.setInt(1, musicNum); pstmt.executeUpdate();
						pstmt = con.prepareStatement("DELETE FROM music WHERE musicno = ?"); pstmt.setInt(1, musicNum); pstmt.executeUpdate();
						System.out.println("Delete Music \"" + rs.getString("name") + "\"");
						break;
					case 3 :
						break;
					default:
						System.out.println("====  Wrong command  ===="); continue;
					}
					continue;
				case 2:
					System.out.println();
					System.out.println("------------------  New Music  ---------------------");
					Main.sc.nextLine();
					int newMusicNum;
					while(true) {
						newMusicNum = (int)(Math.random()*999999999); //랜덤값 0~999999999
						pstmt = con.prepareStatement("SELECT * FROM music WHERE musicno = ?");
						pstmt.setInt(1, newMusicNum); rs = pstmt.executeQuery();
						if(rs.next()) continue;
						else break;
					}
					pstmt = con.prepareStatement("INSERT INTO music values(?, ?, ?, ?, ?, ?, ?)");
					System.out.print("Name : "); String name = Main.sc.nextLine();
					System.out.print("Artist : "); String artist = Main.sc.nextLine();
					System.out.print("Genre (발라드, 댄스, 랩/힙합, R&B/Soul, 록/메탈, 트로트) : "); String genre = Main.sc.next();
					System.out.print("Release Date (yyyy-MM-dd): "); String str = Main.sc.next();
					pstmt.setInt(1, newMusicNum); pstmt.setString(2, name); pstmt.setString(3, artist); pstmt.setString(4, genre);
					pstmt.setInt(5, 0); /*likeNum*/ pstmt.setInt(6, 0); /*playNum*/ pstmt.setDate(7, transDate(str)); pstmt.executeUpdate();
					pstmt = con.prepareStatement("INSERT INTO musicupload VALUES(?, ?)"); pstmt.setInt(1, manager.getManagerNum()); pstmt.setInt(2, newMusicNum); pstmt.executeUpdate();
					System.out.println("Add new Music");
					continue;
				case 3:
					return;
				default:
					System.out.println("====  Wrong command  ===="); continue;
				}
			}
		} catch (SQLException e) {
				e.printStackTrace();
		}
	}
	
	public static java.sql.Date transDate(String str) {
		java.util.Date date;
		java.sql.Date sqlDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.parse(str);
			sqlDate = new java.sql.Date(date.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sqlDate;
	}
	
	public static void showUsers() {
		try {
			boolean tf;
			while(true) {
				pstmt = con.prepareStatement("SELECT * FROM user ORDER BY name ASC, userno ASC"); rs = pstmt.executeQuery();
				System.out.println();
				System.out.println("============================================  User Databases  =============================================");
				System.out.println("|  유저번호  |  아이디  |  비밀번호  |  이름   |      전화번호      |      주민번호      |      이메일      |");
				while(rs.next()) {
					System.out.println("-----------------------------------------------------------------------------------------------------------");
					System.out.println("|  " + rs.getInt("userno") + "   |   " + rs.getString("id") + "   |   " + rs.getString("password") + "   |   " +
										rs.getString("name") + "   |   " + rs.getString("phoneNumber") + "   |   " + rs.getString("rrn") + "   |   " + rs.getString("email") + "  |");
				}
				System.out.println("===========================================================================================================");
				System.out.print("1. Change info, 2.Delete user, 3. Exit : "); int menu = Main.sc.nextInt();
				rs = pstmt.executeQuery(); tf = rs.next();
				switch(menu) {
				case 1:
					if(!tf) {
						System.out.println("There is no user");
						continue;
					}
					int userno;
					while(true) {
						System.out.print("User number : "); userno = Main.sc.nextInt();
						pstmt = con.prepareStatement("SELECT * FROM user WHERE userno = ?"); pstmt.setInt(1, userno); rs = pstmt.executeQuery();
						if(!rs.next()) {
							System.out.println("잘못 입력하셨습니다.");
							continue;
						}
						break;
					}
					while(true) {
						System.out.println();
						System.out.println("============================================  User Databases  =============================================");
						System.out.println("|  유저번호  |  아이디  |  비밀번호  |  이름   |      전화번호      |      주민번호      |      이메일      |");
						System.out.println("-----------------------------------------------------------------------------------------------------------");
						System.out.println("|  " + rs.getInt("userno") + "   |   " + rs.getString("id") + "   |   " + rs.getString("password") + "   |   " +
								rs.getString("name") + "   |   " + rs.getString("phoneNumber") + "   |   " + rs.getString("rrn") + "   |   " + rs.getString("email") + "  |");
						System.out.println("===========================================================================================================");
						System.out.print("1. Id, 2. Password, 3. Name, 4. PhoneNumber, 5. Email, 6. Exit : "); int menu2 = Main.sc.nextInt();
						if(menu2 == 6) break;
						switch(menu2) {
						case 1:
							System.out.print("New id : "); String id = Main.sc.next();
							pstmt = con.prepareStatement("UPDATE user SET id = ? WHERE userno = ?"); pstmt.setString(1, id); pstmt.setInt(2, userno); pstmt.executeUpdate();
							break;
						case 2:
							System.out.print("New password : "); String password = Main.sc.next();
							pstmt = con.prepareStatement("UPDATE user SET password = ? WHERE userno = ?"); pstmt.setString(1, password); pstmt.setInt(2, userno); pstmt.executeUpdate();
							break;
						case 3:
							System.out.print("New name : "); String name = Main.sc.next();
							pstmt = con.prepareStatement("UPDATE user SET name = ? WHERE userno = ?"); pstmt.setString(1, name); pstmt.setInt(2, userno); pstmt.executeUpdate();
							break;
						case 4:
							String phonenumber;
							while(true) {
								System.out.print("New phone number (010-XXXX-XXXX) : "); phonenumber = Main.sc.next();
								if(phonenumber.length() != 13) {System.out.println("잘못 입력하셨습니다."); continue;}
								break;
							}
							pstmt = con.prepareStatement("UPDATE user SET phonenumber = ? WHERE userno = ?"); pstmt.setString(1, phonenumber); pstmt.setInt(2, userno); pstmt.executeUpdate();
							break;
						case 5:
							System.out.print("New email : "); String email = Main.sc.next();
							pstmt = con.prepareStatement("UPDATE user SET email = ? WHERE userno = ?"); pstmt.setString(1, email); pstmt.setInt(2, userno); pstmt.executeUpdate();
							break;
						default:
							System.out.println("====  Wrong command  ===="); continue;
						}
						System.out.println();
						System.out.println("Change to");
						pstmt = con.prepareStatement("SELECT * FROM user WHERE userno = ?"); pstmt.setInt(1, userno); rs = pstmt.executeQuery(); rs.next();
						continue;
					}
					continue;
				case 2:
					int userno2;
					while(true) {
						System.out.print("User number : "); userno2 = Main.sc.nextInt();
						pstmt = con.prepareStatement("SELECT * FROM user WHERE userno = ?"); pstmt.setInt(1, userno2); rs = pstmt.executeQuery();
						if(!rs.next()) {
							System.out.println("잘못 입력하셨습니다.");
							continue;
						}
						break;
					}
					
					System.out.println();
					System.out.println("============================================  User Databases  =============================================");
					System.out.println("|  유저번호  |  아이디  |  비밀번호  |  이름   |      전화번호      |      주민번호      |      이메일      |");
					System.out.println("-----------------------------------------------------------------------------------------------------------");
					System.out.println("|  " + rs.getInt("userno") + "   |   " + rs.getString("id") + "   |   " + rs.getString("password") + "   |   " +
							rs.getString("name") + "   |   " + rs.getString("phoneNumber") + "   |   " + rs.getString("rrn") + "   |   " + rs.getString("email") + "  |");
					System.out.println("===========================================================================================================");
					System.out.print("Delete this user? (yes or no) : "); String yn = Main.sc.next();
					if(yn.equals("no")) continue;
					else if(yn.equals("yes")) {
						pstmt = con.prepareStatement("DELETE FROM userlike WHERE uno = ?"); pstmt.setInt(1, userno2); pstmt.executeUpdate();
						pstmt = con.prepareStatement("DELETE FROM playlistupload WHERE uno = ?"); pstmt.setInt(1, userno2); pstmt.executeUpdate();
						pstmt = con.prepareStatement("DELETE FROM playlist WHERE uno = ?"); pstmt.setInt(1, userno2); pstmt.executeUpdate();
						pstmt = con.prepareStatement("DELETE FROM user WHERE userno = ?"); pstmt.setInt(1, userno2); pstmt.executeUpdate();
						System.out.println("Delete user");
						continue;
					}
					else {
						System.out.println("====  Wrong command  ====");
						continue;
					}
				case 3:
					return;
				default:
					System.out.println("====  Wrong command  ====");
					continue;
				} 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void showUpload(Manager manager) {
		try {
			pstmt = con.prepareStatement("SELECT * FROM musicupload as p, music as m WHERE p.mgrno =? AND p.mno = m.musicno ORDER BY m.releasedate DESC, m.name ASC");
			pstmt.setInt(1, manager.getManagerNum()); rs = pstmt.executeQuery();
			System.out.println();
			System.out.println("==================================================  Music  ==================================================");
			System.out.println("|  음악 번호  |         제목        |      아티스트      |     장르     | 좋아요 | 재생 횟수 |    발매일    |");
			while(rs.next()) {
				System.out.println("-------------------------------------------------------------------------------------------------------------");
				System.out.println("|  " + rs.getInt("musicno") + "  |     " + rs.getString("name") + "    |    " + rs.getString("artist") + "    |    " +
						rs.getString("genre") + "    |   " + rs.getInt("likenum") + "   |   " + rs.getInt("playnum") + "  |  " +  rs.getDate("releasedate") + "   |");
			}
			System.out.println("=============================================================================================================");
			pstmt = con.prepareStatement("SELECT count(*) FROM musicupload WHERE mgrno =?"); pstmt.setInt(1, manager.getManagerNum()); rs = pstmt.executeQuery(); rs.next();
			System.out.println("Total upload : "  + rs.getInt(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void showStatistics() {
		try {
			while(true) {
				System.out.print("1. 장르별, 2. 발매일별, 3. Exit : "); int menu = Main.sc.nextInt();
				switch(menu) {
				case 1: //장르별
					boolean tf;
					pstmt = con.prepareStatement("SELECT count(*), sum(playnum), sum(likenum) FROM music WHERE genre = ?");
					pstmt.setString(1, "발라드"); rs = pstmt.executeQuery(); tf = rs.next(); int[] baladNum = new int[3];
					if(!tf) for(int i = 0; i < 3; i++) baladNum[i] = 0; else for(int i = 0; i < 3; i++) baladNum[i] = rs.getInt(i+1);
					pstmt.setString(1, "댄스"); rs = pstmt.executeQuery(); tf = rs.next(); int[] danceNum = new int[3];
					if(!tf) for(int i = 0; i < 3; i++) danceNum[i] = 0; else for(int i = 0; i < 3; i++) danceNum[i] = rs.getInt(i+1);
					pstmt.setString(1, "랩/힙합"); rs = pstmt.executeQuery(); tf = rs.next(); int[] raphiphopNum = new int[3];
					if(!tf) for(int i = 0; i < 3; i++) raphiphopNum[i] = 0; else for(int i = 0; i < 3; i++) raphiphopNum[i] = rs.getInt(i+1);
					pstmt.setString(1, "R&B/Soul"); rs = pstmt.executeQuery(); tf = rs.next(); int[] rnbsoulNum = new int[3];
					if(!tf) for(int i = 0; i < 3; i++) rnbsoulNum[i] = 0; else for(int i = 0; i < 3; i++) rnbsoulNum[i] = rs.getInt(i+1);
					pstmt.setString(1, "록/메탈"); rs = pstmt.executeQuery(); tf = rs.next(); int[] rockmetalNum = new int[3];
					if(!tf) for(int i = 0; i < 3; i++) rockmetalNum[i] = 0; else for(int i = 0; i < 3; i++) rockmetalNum[i] = rs.getInt(i+1);
					pstmt.setString(1, "트로트"); rs = pstmt.executeQuery(); tf = rs.next(); int[] trotNum = new int[3];
					if(!tf) for(int i = 0; i < 3; i++) trotNum[i] = 0; else for(int i = 0; i < 3; i++) trotNum[i] = rs.getInt(i+1);
					System.out.println();
					System.out.println("=======================================================================================");
					System.out.println("|     장르    |   발라드  |    댄스   |  랩/힙합  |  R&B/Soul |  록/메탈  |   트로트  |");
					System.out.println("---------------------------------------------------------------------------------------");
					System.out.println("|     개수    |     " + baladNum[0] + "     |     " + danceNum[0] + "     |     " + raphiphopNum[0] + "     |     " + rnbsoulNum[0] + "     |     " + rockmetalNum[0] + "     |     " + trotNum[0] + "     |");
					System.out.println("---------------------------------------------------------------------------------------");
					System.out.println("| 플레이 횟수 |     " + baladNum[1] + "     |     " + danceNum[1] + "     |     " + raphiphopNum[1] + "     |     " + rnbsoulNum[1] + "     |     " + rockmetalNum[1] + "     |     " + trotNum[1] + "     |");
					System.out.println("---------------------------------------------------------------------------------------");
					System.out.println("|  좋아요 수  |     " + baladNum[2] + "     |     " + danceNum[2] + "     |     " + raphiphopNum[2] + "     |     " + rnbsoulNum[2] + "     |     " + rockmetalNum[2] + "     |     " + trotNum[2] + "     |");
					System.out.println("=======================================================================================");
					continue;
				case 2: //날짜별
					while(true) {
						System.out.print("1. 연도별, 2. 월별, 3. Exit : "); int menu2 = Main.sc.nextInt();
						switch(menu2) {
						case 1:
							System.out.print("Year (2011 ~ 2020) : "); String year = Main.sc.next();
							pstmt = con.prepareStatement("SELECT * FROM music WHERE releasedate BETWEEN ? AND ? order by releasedate DESC, name ASC");
							pstmt.setDate(1, transDate(year + "-01-01")); pstmt.setDate(2, transDate(year + "-12-31")); rs = pstmt.executeQuery();
							System.out.println();
							System.out.println("=============================================  " + year + " Music"  + "  =============================================");
							System.out.println("|  음악 번호  |         제목        |      아티스트      |     장르     | 좋아요 | 재생 횟수 |    발매일    |");
							while(rs.next()) {
								System.out.println("-------------------------------------------------------------------------------------------------------------");
								System.out.println("|  " + rs.getInt("musicno") + "  |     " + rs.getString("name") + "    |    " + rs.getString("artist") + "    |    " +
										rs.getString("genre") + "    |   " + rs.getInt("likenum") + "   |   " + rs.getInt("playnum") + "  |  " +  rs.getDate("releasedate") + "   |");
							}
							System.out.println("=============================================================================================================");
							pstmt = con.prepareStatement("SELECT count(*) FROM music WHERE releasedate BETWEEN ? AND ? order by releasedate DESC");
							pstmt.setDate(1, transDate(year + "-01-01")); pstmt.setDate(2, transDate(year + "-12-31")); rs = pstmt.executeQuery();
							tf = rs.next(); int num; if(!tf) num = 0; else num = rs.getInt(1);
							System.out.println("Total num : " + num);
							continue;
						case 2:
							System.out.print("Year (2011 ~ 2020) : "); String year2 = Main.sc.next();
							System.out.print("Month (01 ~ 12) : "); String month = Main.sc.next();
							if(month.length() == 1) month = "0" + month;
							pstmt = con.prepareStatement("SELECT * FROM music WHERE releasedate BETWEEN ? AND ? order by releasedate DESC, name ASC");
							pstmt.setDate(1, transDate(year2 + "-" + month + "-01")); pstmt.setDate(2, transDate(year2 + "-" + month + "-31")); rs = pstmt.executeQuery();
							System.out.println();
							System.out.println("============================================  " + year2 + "-" + month + " Music"  + "  =============================================");
							System.out.println("|  음악 번호  |         제목        |      아티스트      |     장르     | 좋아요 | 재생 횟수 |    발매일    |");
							while(rs.next()) {
								System.out.println("-------------------------------------------------------------------------------------------------------------");
								System.out.println("|  " + rs.getInt("musicno") + "  |     " + rs.getString("name") + "    |    " + rs.getString("artist") + "    |    " +
										rs.getString("genre") + "    |   " + rs.getInt("likenum") + "   |   " + rs.getInt("playnum") + "  |  " +  rs.getDate("releasedate") + "   |");
							}
							System.out.println("=============================================================================================================");
							pstmt = con.prepareStatement("SELECT count(*) FROM music WHERE releasedate BETWEEN ? AND ?");
							pstmt.setDate(1, transDate(year2 + "-" + month + "-01")); pstmt.setDate(2, transDate(year2 + "-" + month + "-31")); rs = pstmt.executeQuery();
							tf = rs.next(); int num2; if(!tf) num2 = 0; else num2 = rs.getInt(1);
							System.out.println("Total num : " + num2);
							continue;
						case 3:
							break;
						default :
							System.out.println("====  Wrong command  ===="); continue;
						}
						break;
					}
					continue;
				case 3:
					return;
				default :
					System.out.println("====  Wrong command  ===="); continue;	
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}