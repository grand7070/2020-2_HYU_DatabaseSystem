package MusicStreaming;

import java.util.*;
import java.sql.*;

public class Main {
	public static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		Program.connectDB();
		
		while (true) {
			System.out.println();
			System.out.println("======== ���� ��Ʈ���� ���α׷� ========");
			System.out.println("|                                      |");
			System.out.println("|              1. �α���               |");
			System.out.println("|              2. ȸ������             |");
			System.out.println("|              3. ����                 |");
			System.out.println("|                                      |");
			System.out.println("========================================");
			System.out.print("�޴��� �������ּ��� : "); int menu = sc.nextInt();
			switch(menu) {
				case 1:
					Program.login(); continue;
				case 2:
					Program.signUp(); continue;
				case 3:
					System.out.println("���� ��Ʈ���� ���α׷��� �����մϴ�.");
					Program.unconnectDB();
					return;
				default:
					System.out.println("Wrong command"); continue;
			}
		}
	}
}