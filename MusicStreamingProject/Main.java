package MusicStreaming;

import java.util.*;
import java.sql.*;

public class Main {
	public static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		Program.connectDB();
		
		while (true) {
			System.out.println();
			System.out.println("======== 음악 스트리밍 프로그램 ========");
			System.out.println("|                                      |");
			System.out.println("|              1. 로그인               |");
			System.out.println("|              2. 회원가입             |");
			System.out.println("|              3. 종료                 |");
			System.out.println("|                                      |");
			System.out.println("========================================");
			System.out.print("메뉴를 선택해주세요 : "); int menu = sc.nextInt();
			switch(menu) {
				case 1:
					Program.login(); continue;
				case 2:
					Program.signUp(); continue;
				case 3:
					System.out.println("음악 스트리밍 프로그램을 종료합니다.");
					Program.unconnectDB();
					return;
				default:
					System.out.println("Wrong command"); continue;
			}
		}
	}
}