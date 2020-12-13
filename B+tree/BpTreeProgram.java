package BpTree;

import java.io.*;
import java.util.Scanner;

public class BpTreeProgram {

	public static void main(String[] args) throws IOException {
		if(args.length > 0) {
			if(args[0].equals("-c")) { //Data File Creation //ex) java bptree -c index.dat 8
				File indexFile = new File(args[1]);
				indexFile.createNewFile();
			
				int treeDegree = Integer.parseInt(args[2]);
				PrintWriter outputStream = null;
				
				try {
					outputStream = new PrintWriter(new FileOutputStream(indexFile));
				}
				catch(FileNotFoundException e) {
					System.out.println("IndexFile was not found");
					System.exit(0);
				}
				
				outputStream.println(treeDegree);
				outputStream.close();
			}
			else if(args[0].equals("-i")) { //Insertion //ex) java bptree -i index.dat input.csv
				File indexFile = new File(args[1]);
				File dataFile = new File(args[2]);
				
				Scanner inputStream = null;
				
				//indexFile에서 tree 만들기
				try {
					inputStream = new Scanner(new FileInputStream(indexFile));
				}
				catch(FileNotFoundException e){
					System.out.println("Index File was not found");
					System.exit(0);
				}
				
				int treeDegree = Integer.parseInt(inputStream.nextLine()); //첫째줄의 maxDegree 읽어오기
				
				BpTree bpt = new BpTree(treeDegree);
				
				while(inputStream.hasNext()) { //index.dat에서 다음 줄 없을 때까지
					String[] line = inputStream.nextLine().split(",");
					bpt.tree_keyInsert(Integer.parseInt(line[0]), Integer.parseInt(line[1])); //key, value
				}
				
				//dataFile에서 insert
				try {
					inputStream = new Scanner(new FileInputStream(dataFile));
				}
				catch(FileNotFoundException e){
					System.out.println("Insert File was not found");
					System.exit(0);
				}
				
				while(inputStream.hasNext()) { //input.csv에서 다음 줄 없을 때까지
					String[] line = inputStream.nextLine().split(",");
					bpt.tree_keyInsert(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
				}

				PrintWriter outputStream = null;
				
				try {
					outputStream = new PrintWriter(new FileOutputStream(indexFile));
				}
				catch(FileNotFoundException e) {
					System.out.println("IndexFile was not found");
					System.exit(0);
				}
				
				//System.out.println("insert bpt");
				//bpt.tree_printAll();
				
				outputStream.println(treeDegree);
				outputStream.println(bpt.tree_recordAll().toString());

				
				inputStream.close();
				outputStream.close();
			}
			else if(args[0].equals("-d")) { //Deletion //ex) java bptree -d index.dat delete.csv
				File indexFile = new File(args[1]);
				File dataFile = new File(args[2]);
				
				Scanner inputStream = null;
				
				//indexFile에서 tree 만들기
				try {
					inputStream = new Scanner(new FileInputStream(indexFile));
				}
				catch(FileNotFoundException e){
					System.out.println("Index File was not found");
					System.exit(0);
				}
				
				int treeDegree = Integer.parseInt(inputStream.nextLine());
				
				BpTree bpt = new BpTree(treeDegree);
				
				while(inputStream.hasNext()) {
					String[] line = inputStream.nextLine().split(",");
					bpt.tree_keyInsert(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
				}
				
				//dataFile로부터 delete
				try {
					inputStream = new Scanner(new FileInputStream(dataFile));
				}
				catch(FileNotFoundException e){
					System.out.println("Delete File was not found");
					System.exit(0);
				}

				while(inputStream.hasNext()) {
					bpt.tree_keyDelete(inputStream.nextInt());
				}
				
				//System.out.println("delete bpt");
				//bpt.tree_printAll();
				
				//indexFile update
				PrintWriter outputStream = null;
				
				try {
					outputStream = new PrintWriter(new FileOutputStream(indexFile));
				}
				catch(FileNotFoundException e) {
					System.out.println("IndexFile was not found");
					System.exit(0);
				}
				
				outputStream.println(treeDegree); //treeDegree
				outputStream.println(bpt.tree_recordAll().toString()); //record key,value

				
				inputStream.close();
				outputStream.close();
			}
			else if(args[0].equals("-s")) { //Key Search // ex) java bptree -s index.dat 125
				File indexFile = new File(args[1]);
				int key = Integer.parseInt(args[2]);
				
				Scanner inputStream = null;
				
				//indexFile에서 tree 만들기
				try {
					inputStream = new Scanner(new FileInputStream(indexFile));
				}
				catch(FileNotFoundException e){
					System.out.println("Index File was not found");
					System.exit(0);
				}
				
				int treeDegree = Integer.parseInt(inputStream.nextLine());
				
				BpTree bpt = new BpTree(treeDegree);
				
				while(inputStream.hasNext()) {
					String[] line = inputStream.nextLine().split(",");
					bpt.tree_keyInsert(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
				}
				
				//keySearch
				System.out.println("Key Search");
				bpt.tree_keySearch(key);
				
				inputStream.close();
			}
			else if(args[0].equals("-r")) { //Range Search // ex) java bptree -r index.dat 100 200
				File indexFile = new File(args[1]);
				int startKey = Integer.parseInt(args[2]);
				int endKey = Integer.parseInt(args[3]);
				
				Scanner inputStream = null;
				
				//indexFile에서 tree 만들기
				try {
					inputStream = new Scanner(new FileInputStream(indexFile));
				}
				catch(FileNotFoundException e){
					System.out.println("Index File was not found");
					System.exit(0);
				}
				
				int treeDegree = Integer.parseInt(inputStream.nextLine());
				
				BpTree bpt = new BpTree(treeDegree);
				
				while(inputStream.hasNext()) {
					String[] line = inputStream.nextLine().split(",");
					bpt.tree_keyInsert(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
				}
				
				//range Search
				bpt.tree_rangeSearch(startKey, endKey);
				
				inputStream.close();
			}
			/*else if(args[0].equals("-q")){ // Make bptree and print // ex) java bptree -q index.dat
				File indexFile = new File(args[1]);
				
				Scanner inputStream = null;
				
				//indexFile에서 tree 만들기
				try {
					inputStream = new Scanner(new FileInputStream(indexFile));
				}
				catch(FileNotFoundException e){
					System.out.println("Index File was not found");
					System.exit(0);
				}
				
				int treeDegree = Integer.parseInt(inputStream.nextLine()); //첫째줄의 maxDegree 읽어오기
				
				BpTree bpt = new BpTree(treeDegree);
				
				while(inputStream.hasNext()) { //index.dat에서 다음 줄 없을 때까지
					String[] line = inputStream.nextLine().split(",");
					bpt.tree_keyInsert(Integer.parseInt(line[0]), Integer.parseInt(line[1])); //key, value
				}
				
				System.out.println("bpt read");
				bpt.tree_printAll();
				
			}*/
			else {
				System.out.println("Wrong Command");
				return;
			}
		}
	}
}
