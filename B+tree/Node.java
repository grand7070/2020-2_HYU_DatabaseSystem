package BpTree;

import java.util.ArrayList;

public class Node {
	public static int maxDegree; //����� ������ ����, maxDegree-1�� data�� �ִ� ����
	public int currNum; //���� ����Ʈ�� key�� value�� ����ִ� Data�� ����
	boolean isLeaf; //indexNode���� leafNode���� ����
	public ArrayList<Data> dataList; //Data�� �� ����Ʈ, 0��°�� key, value�� null�̰� pointer�� ���

	
	Node(boolean isLeaf){
		this.currNum = 0;
		this.isLeaf = isLeaf;
		this.dataList = new ArrayList<Data>(maxDegree);
		Data firstData = new Data(0, 0);
		this.dataList.add(firstData);
		this.dataList.get(0).pointer = null;
	}
	
	public void putData(Data data) { //����� dataList�� data �߰�
		if(this.currNum == 0) {
			this.dataList.add(data);
			this.currNum++;
		}
		else {
			int index = this.findIndexbyKey(data.key);
			index++;
			this.dataList.add(index, data); //�� ��ĭ�� �и�
			this.currNum++; //�� ��� ���� data ���� + 1
		}
	}
	
	public Data removeData(int index) { //����� dataList�� data ����
		this.currNum--; //�� ����� data ���� - 1
		return this.dataList.remove(index); //�� ����� index��° data ������ �� data ��ȯ, ��ĭ�� ������
	}
	
	public int findIndexbyKey(int key) { //key�� ����� dataList��� ���ϸ鼭 pointer�� index�� ã��
		if(this.dataList.get(1).key > key) { //key�� dataList�� 1��° key���� ������
			return 0; //�ǿ��� �����Ͱ� ����Ű�� ����
		}
		
		for(int i = 1; i < this.currNum; i++) {
			if(this.dataList.get(i).key <= key && key < this.dataList.get(i+1).key) {
				return i;
			}
		}
		return this.currNum; //key�� dataList�� ������ key���� ũ�� �� ������ �����Ͱ� ����Ű�� ����
	}
	
	public void printNode() {
		if(!this.isLeaf) { //�� ��尡 indexNode���� ���
			System.out.printf("Node : " + this + ", IndexNode, Number of Key : %d, Number of dataListSize : %d\n", this.currNum, this.dataList.size());
			System.out.printf("[ P0 : " + this.dataList.get(0).pointer + " ]  "); //�� ���� �����Ͱ� ����Ű�� ���
			for(int i = 1; i <= this.dataList.size()-1; i++) {
				System.out.printf("[ K%d : %d ]  ", i, this.dataList.get(i).key);
				System.out.printf("[ P%d : " + this.dataList.get(i).pointer + " ]  ", i);
			}
			System.out.println();
		}
		else { //�� ��尡 leafNode���� ���
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
