package BpTree;

import java.util.ArrayList;

public class Node {
	public static int maxDegree; //노드의 포인터 갯수, maxDegree-1은 data의 최대 갯수
	public int currNum; //현재 리스트의 key와 value가 들어있는 Data의 갯수
	boolean isLeaf; //indexNode인지 leafNode인지 구분
	public ArrayList<Data> dataList; //Data가 들어갈 리스트, 0번째는 key, value는 null이고 pointer만 사용

	
	Node(boolean isLeaf){
		this.currNum = 0;
		this.isLeaf = isLeaf;
		this.dataList = new ArrayList<Data>(maxDegree);
		Data firstData = new Data(0, 0);
		this.dataList.add(firstData);
		this.dataList.get(0).pointer = null;
	}
	
	public void putData(Data data) { //노드의 dataList에 data 추가
		if(this.currNum == 0) {
			this.dataList.add(data);
			this.currNum++;
		}
		else {
			int index = this.findIndexbyKey(data.key);
			index++;
			this.dataList.add(index, data); //다 한칸씩 밀림
			this.currNum++; //이 노드 현재 data 개수 + 1
		}
	}
	
	public Data removeData(int index) { //노드의 dataList의 data 삭제
		this.currNum--; //이 노드의 data 갯수 - 1
		return this.dataList.remove(index); //이 노드의 index번째 data 삭제후 그 data 반환, 한칸씩 땡겨짐
	}
	
	public int findIndexbyKey(int key) { //key로 노드의 dataList들과 비교하면서 pointer의 index를 찾음
		if(this.dataList.get(1).key > key) { //key가 dataList의 1번째 key보다 작으면
			return 0; //맨왼쪽 포인터가 가르키는 노드로
		}
		
		for(int i = 1; i < this.currNum; i++) {
			if(this.dataList.get(i).key <= key && key < this.dataList.get(i+1).key) {
				return i;
			}
		}
		return this.currNum; //key가 dataList의 마지막 key보다 크면 맨 오른쪽 포인터가 가르키는 노드로
	}
	
	public void printNode() {
		if(!this.isLeaf) { //이 노드가 indexNode였을 경우
			System.out.printf("Node : " + this + ", IndexNode, Number of Key : %d, Number of dataListSize : %d\n", this.currNum, this.dataList.size());
			System.out.printf("[ P0 : " + this.dataList.get(0).pointer + " ]  "); //맨 왼쪽 포인터가 가르키는 노드
			for(int i = 1; i <= this.dataList.size()-1; i++) {
				System.out.printf("[ K%d : %d ]  ", i, this.dataList.get(i).key);
				System.out.printf("[ P%d : " + this.dataList.get(i).pointer + " ]  ", i);
			}
			System.out.println();
		}
		else { //이 노드가 leafNode였을 경우
			System.out.printf(this + ", LeafNode, Number of Key : %d, Number of dataListSize : %d\n", this.currNum, this.dataList.size());
			for(int i = 1; i <= this.dataList.size()-1; i++) {
				System.out.printf("[ K%d : %d ]  ", i, this.dataList.get(i).key);
			}
			System.out.println();
			System.out.printf("< P%d : " + this.dataList.get(this.currNum).pointer + " >\n", this.currNum);
		}
	}
	
	public StringBuilder recordNode() {
		StringBuilder record = new StringBuilder();
		for(int i = 1; i <= this.currNum; i++) {
			record.append(this.dataList.get(i).key + "," + this.dataList.get(i).value + "\n");
		}
		return record;
	}
}
