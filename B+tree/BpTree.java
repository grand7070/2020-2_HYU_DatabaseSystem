package BpTree;

public class BpTree {
	public Node root; //B+Tree�� root Node
	public int maxDegree; //B+Tree�� ������ ����, maxDegree-1�� data�� �ִ� ����
	//=============================================================================================================================
	public BpTree(int maxDegree) { //maxDegree�� ���� B+tree�� ����� ������
		this.maxDegree = maxDegree; //B+tree�� maxDegree
		Node.maxDegree = maxDegree; //B+tree�� ������ maxDegree
	}
	//=============================================================================================================================
	public void tree_keyInsert(int key, int value) { //key�� insert�� B+tree�� ����
		Data insertData = new Data(0, 0); //������ ������
		Data temp_newChildData = new Data(0, 0);
		
		insertData.key = key;
		insertData.value = value;
		
		if(this.root == null) { //B+tree ó�� ������
			this.root = new Node(true);
			this.root.dataList.add(insertData);
			this.root.currNum++;
		}else {
			this.keyInsert(this.root, insertData, temp_newChildData);
		}
	}
	
	private boolean keyInsert(Node node, Data insertData, Data newChildData) {
		if(!node.isLeaf) { //1. if node is indexNode
			int index = node.findIndexbyKey(insertData.key);
			Data temp_newChildData = new Data(0, 0); //�Ʒ� ��忡�� split�ϸ� �ö�� �� �ִ� ���, indexNode�� Ű�� ������ ����
			boolean exist_child = this.keyInsert(node.dataList.get(index).pointer, insertData, temp_newChildData);
			
			if(!exist_child) { //insert�� split�� ���� �ʾƼ� ���ο� child�� ������ �ʾ�����
				return false; //���ο� child�� ������ �ʾҴٸ� ��ȭ�� �ʿ���� �״�� ����
			}
			else { //insert�� split�ؼ� ���ο� child�� ���� //newChildData�� Ű�� �θ� node�� �־�� ��
				if(node.currNum < maxDegree-1) { //�θ� ��忡 ���� �ڸ��� �ִ�.
					node.putData(temp_newChildData); //�θ� ��忡 child�� ���� �� Data ����
					return false; //���ο� child�� ������ �ʾ����� �˸�
				}
				else { //�θ� ��忡 ���� �ڸ��� �����Ƿ� �θ��� split!
					node.putData(temp_newChildData); //overflow ��Ŵ
					Node newNode = new Node(false); //���ο� indexNode ����, Node �����ڷ� �̹� 0��° key, value�� ���� 0
					
					for(int i = maxDegree/2 + 1; i <= maxDegree; i++) { //���� ��忡�� Data �����ϰ� ������ Data�� ���ο� ��忡 �ű�
						newNode.putData(node.removeData(maxDegree/2 + 1));
					}
					
					Data middleData = newNode.removeData(1); //���ο� ����� ù��° Data �����ϰ� �� �� ����
					newNode.dataList.get(0).pointer = middleData.pointer; //���ο� ����� �� ���� ������
					
					newChildData.key = middleData.key;
					newChildData.pointer = newNode;
					
					if(node == this.root) { //�� ��尡 ��Ʈ ��忩�� ���ο� �θ� ���� ����
						Node newRoot = new Node(false);
						newRoot.dataList.get(0).pointer = node;
						newRoot.dataList.add(new Data(middleData.key, 0));
						newRoot.dataList.get(1).pointer = newNode;
						newRoot.currNum = 1;
						this.root = newRoot;
					}
					return true; //���ο� child�� ��Ÿ������ �˸�
				}
			}
		}
		else { //2. if node is leafNode
			if(node.currNum < maxDegree-1) { //2-1. not overflow
				Node nextNode = node.dataList.get(node.currNum).pointer; //���� ��� ����Ű�� ������
				node.putData(insertData);
				node.dataList.get(node.currNum).pointer = nextNode; //���� ��� ����Ű�� ������
				newChildData = null; //���ο� child�� �����Ƿ�
				return false; //���ο� child�� ������ �˸�
			}
			else { //2-2. overflow so split
				Node nextNode = node.dataList.get(node.currNum).pointer; //���� ��� ����Ű�� ������
				node.dataList.get(node.currNum).pointer = null;
				node.putData(insertData); //��带 overflow ��Ŵ
				node.dataList.get(maxDegree).pointer = nextNode; //over�� �� ������ �����Ͱ� ���� ��� ����Ŵ
				
				Node newNode = new Node(true); //�� leaf node ����, ������ ���, ���� ���� ���� ���

				for(int i = maxDegree/2 + 1; i <= maxDegree; i++ ) { //���� ��忡�� Data �����ϰ� ������ Data�� ���ο� ��忡 �ű�
					newNode.putData(node.removeData(maxDegree/2 + 1));
				}

				node.dataList.get(node.currNum).pointer = newNode; //���� ��� ����
				
				newChildData.key = newNode.dataList.get(1).key;
				newChildData.pointer = newNode;
				
				if(node == this.root) { //�� ��尡 ��Ʈ������ root�� �� indexNode 1���� �ڽĳ���� leafNode 2���� ���� �� �̾��� 
					this.root = new Node(false);
					this.root.dataList.get(0).pointer = node;
					Data newData = new Data(newNode.dataList.get(1).key, 0);
					newData.pointer = newNode;
					this.root.dataList.add(newData);
					this.root.currNum++;
				}
				
				return true; //���ο� child�� �������� �˸�
			}
		}
	}
	//=============================================================================================================================
	public void tree_keyDelete(int key) { //B+tree���� key�� ���� data ����
		Data deleteData = new Data(0, 0);
		deleteData.key = key;
		deleteData.pointer = null;
		
		this.keyDelete(null, this.root, deleteData, 0);
	}
	
	public boolean keyDelete(Node parent, Node node, Data deleteData, int indexOnParent) {
		if(!node.isLeaf) { //1. indexNode
			int index = node.findIndexbyKey(deleteData.key);
			boolean removeOnThisNode = keyDelete(node, node.dataList.get(index).pointer, deleteData, index);
			
			if(node.currNum >= index && node.dataList.get(index).key == deleteData.key) { //1-0-1. indexNode�� delete key�� ������
				//System.out.println("1-0-1");
				Node belowNode = node.dataList.get(index).pointer;
				while(belowNode.isLeaf != true) {
					belowNode = belowNode.dataList.get(0).pointer;
				}
				node.dataList.get(index).key = belowNode.dataList.get(1).key;
			}
			
			if(node != root && parent.currNum >= 1 && parent.dataList.get(indexOnParent).key == deleteData.key) { //1-0-2. �θ��忡 delete key�� ������
				//System.out.println("1-0-2");
				Node belowNode = parent.dataList.get(indexOnParent).pointer;
				while(belowNode.isLeaf != true) {
					belowNode = belowNode.dataList.get(0).pointer;
				}
				parent.dataList.get(indexOnParent).key = belowNode.dataList.get(1).key;
			}
			
			if(!removeOnThisNode) { //1-1. �� ��忡�� remove ���Ͼ���� �� ���鿡���� remove ���Ͼ ��
				//System.out.println("1-1");
				return false;
			}
			else { //1-2. �� ��忡�� remove �Ͼ�ٸ�
				//System.out.println("1-2");
				
				if(Math.ceil((float)maxDegree/2)-1 <= node.currNum || (node == this.root && 1 <= node.currNum)) { //1-2-1. no underflow on indexNode
					//System.out.println("1-2-1");
					return false;
				}
				else if(node == root && node.currNum == 0) { //1-2-3. �� ��尡 ��Ʈ ����̰� key�� 0���� ��
					//System.out.println("1-2-3");
					this.root = node.dataList.get(0).pointer;
					
					int newIndex = root.findIndexbyKey(deleteData.key);
					
					if(root.dataList.get(newIndex).key == deleteData.key) {
						Node belowNode = root.dataList.get(index+1).pointer;
						while(belowNode.isLeaf != true) {
							belowNode = belowNode.dataList.get(0).pointer;
						}
						root.dataList.get(newIndex).key = belowNode.dataList.get(1).key;
					}
					
					node = null;
					return false;
				}
				else { //1-2-2. underflow on indexNode
					//System.out.println("1-2-2");
					boolean isLeft = (indexOnParent == 0); //�� ��尡 �ڽĳ�� �� �� ��������
					boolean isRight = (indexOnParent == parent.currNum); //�� ��尡 �ڽ� ��� �� �� ����������
					
					if(isLeft) { //1-2-2-1. �� ���� ����� ��� ������ ��忡�� borrow or merge
						//System.out.println("1-2-2-1");
						Node sibling = parent.dataList.get(1).pointer; //������ ��� ( get(indexOnParent+1)����
						
						if(Math.ceil((float)maxDegree/2) <= sibling.currNum) { //1-2-2-1-1. sibling no underflow so borrow
							//System.out.println("1-2-2-1-1");
							//������ sibling���� �ּڰ� key borrow
							node.putData(parent.removeData(1)); //?
							node.dataList.get(node.currNum).pointer = sibling.dataList.get(0).pointer;
							sibling.dataList.get(0).pointer = sibling.dataList.get(1).pointer;
							parent.putData(sibling.removeData(1));
							parent.dataList.get(1).pointer = sibling;
							
							return false;
						}
						else { //1-2-2-1-2. sibling underflow so merge
							//System.out.println("1-2-2-1-2");
							node.putData(parent.removeData(1));
							node.dataList.get(node.currNum).pointer = sibling.dataList.get(0).pointer;
							while(sibling.dataList.size() != 1) {
								node.putData(sibling.removeData(1));
							}
							
							sibling = null;
							return true;
						}
					}
					else if(isRight){ //1-2-2-2. �� ������ ����� ��� ���� ��忡�� borrow or merge
						//System.out.println("1-2-2-2");
						Node sibling = parent.dataList.get(parent.currNum-1).pointer; //�ٷ� ���� ���
						if(Math.ceil((float)maxDegree/2) <= sibling.currNum) { //1-2-2-2-1. sibling no underflow
							//System.out.println("1-2-2-2-1");
							//���� sibling���� �ִ� key borrow
							node.putData(parent.removeData(parent.currNum));
							node.dataList.get(1).pointer = node.dataList.get(0).pointer;
							node.dataList.get(0).pointer = sibling.dataList.get(sibling.currNum).pointer;
							parent.putData(sibling.removeData(sibling.currNum));
							parent.dataList.get(parent.currNum).pointer = node;
							return false;
						}
						else { //1-2-2-2-2. sibling underflow so merge
							//System.out.println("1-2-2-2-2");
							sibling.putData(parent.removeData(parent.currNum));
							sibling.dataList.get(sibling.currNum).pointer = node.dataList.get(0).pointer;
							while(node.dataList.size() != 1) {
								sibling.putData(node.removeData(1));
							}
							
							node = null;
							return true;
						}
					}
					else { //1-2-2-3. ��� �ִ� ����� ��� 
						//System.out.println("1-2-2-3");
						/*
						 * ���� �����ʿ��� borrow �����ϸ� ���ʿ��� borrow, ���� �Ұ��� ��� �����ʿ��� borrow
						 * �� �ٿ��� borrow �Ұ����ϸ� ���ʰ� merge --> underflow�Ǽ� ������ ��尡 0���� ���� 1�� �̻� �и�!!!!!
						*/
						Node leftNode = parent.dataList.get(indexOnParent-1).pointer;
						Node rightNode = parent.dataList.get(indexOnParent+1).pointer;
						
						if(Math.ceil((float)maxDegree/2) <= leftNode.currNum) { //1-2-2-3-1. ���ʿ��� borrow ���ɽ� ���ʿ��� borrow
							//���� sibling���� �ִ� key borrow
							//System.out.println("1-2-2-3-1");
							node.putData(parent.removeData(indexOnParent));
							node.dataList.get(1).pointer = node.dataList.get(0).pointer;
							node.dataList.get(0).pointer = leftNode.dataList.get(leftNode.currNum).pointer;
							parent.putData(leftNode.removeData(leftNode.currNum));
							parent.dataList.get(indexOnParent).pointer = node;
							
							return false;
						}
						else if(Math.ceil((float)maxDegree/2) <= rightNode.currNum) { //1-2-2-3-2. ���ʿ��� borrow �Ұ��ɽ� �����ʿ��� borrow
							//������ sibling���� �ּڰ� key borrow
							//System.out.println("1-2-2-3-2");
							node.putData(parent.removeData(indexOnParent+1));
							node.dataList.get(node.currNum).pointer = rightNode.dataList.get(0).pointer;
							rightNode.dataList.get(0).pointer = rightNode.dataList.get(1).pointer;
							parent.putData(rightNode.removeData(0));
							parent.dataList.get(indexOnParent+1).pointer = rightNode;
							
							return false;
						}
						else { //1-2-2-3-3. ���ʿ��� borrow �Ұ��ɽ� �������� merge
							//System.out.println("1-2-2-3-3");
							leftNode.putData(parent.removeData(indexOnParent));
							leftNode.dataList.get(leftNode.currNum).pointer = node.dataList.get(0).pointer;
							
							while(node.dataList.size() != 1) {
								leftNode.putData(node.removeData(1));
							}
							node = null;
							return true;
						}
					}
				}
			}
		}
		else { //2. leafNode
			int deleteKeyIndex = node.findIndexbyKey(deleteData.key); //�ش� leafNode���� ������ key�� index
			
			if(Math.ceil((float)maxDegree/2) <= node.currNum) { //2-1. no underflow
				//System.out.println("2-1");
				if(deleteKeyIndex == node.currNum) { //2-1-1. �����ϴ� Data�� leafNode�� �� ������ ������ �� ���
					//System.out.println("2-1-1");
					Node nextNode = node.dataList.get(node.currNum).pointer; //�� Data��, �� ��尡 ����Ű�� ���� leafNode
					node.removeData(deleteKeyIndex);
					node.dataList.get(node.currNum).pointer = nextNode;
				}
				else if(deleteKeyIndex == 1) { //2-1-2. �����ϴ� Data�� leafNode�� �� ó���� ��
					//System.out.println("2-1-2");
					node.removeData(deleteKeyIndex);
					parent.dataList.get(indexOnParent).key = deleteData.key;
				}
				else { //2-1-3. �����ϴ� Data�� leafNode�� �߰��϶�
					//System.out.println("2-1-3");
					node.removeData(deleteKeyIndex);
				}
				return false; //�θ��忡�� delete ������
			}
			else { //2-2. underflow
				//System.out.println("2-2");
				
				if(node == this.root && node.currNum == 1) {
					node.removeData(deleteKeyIndex); //��忡�� Ű ����
					this.root = null;
					return false;
				}
				
				boolean isLeft = (indexOnParent == 0); //�� ��尡 �ڽĳ�� �� �� ��������
				boolean isRight = (indexOnParent == parent.currNum); //�� ��尡 �ڽ� ��� �� �� ����������
				
				
				if(isLeft) { //2-2-1. �� ���� ����� ��� ������ ��忡�� borrow or merge //Ư���ؼ� �θ������ �̸� ó������?
					//System.out.println("2-2-1");
					Node sibling = parent.dataList.get(1).pointer; //������ ��� ( get(indexOnParent+1)����
					
					if(Math.ceil((float)maxDegree/2) <= sibling.currNum) { //2-2-1-1. sibling no underflow so borrow
						//������ sibling���� �ּڰ� key borrow
						Node nextNode = node.dataList.get(node.currNum).pointer;
						node.dataList.get(node.currNum).pointer = null; //���� ���� ��� ����Ű�� ������ ����ȭ
						node.removeData(deleteKeyIndex); //�ش� Ű ���� Data ����
						node.putData(sibling.removeData(1)); //underflow�� ��忡 sibling �ּڰ� key borrow
						node.dataList.get(node.currNum).pointer = nextNode;
						
						parent.dataList.get(1).key = sibling.dataList.get(1).key;
						return false;
					}
					else { //2-2-1-2. sibling underflow so merge
						//System.out.println("2-2-1-2");
						node.dataList.get(node.currNum).pointer = null; //���� ���� ��� ����Ű�� ������ ����ȭ
						node.removeData(deleteKeyIndex);
						while(sibling.dataList.size() != 1) { //sibling �� ��ﶧ����
							node.putData(sibling.removeData(1));
						}
						
						sibling = null;
						parent.removeData(1);
						return true;
					}
				}
				else if(isRight){ //2-2-2. �� ������ ����� ��� ���� ��忡�� borrow or merge
					//System.out.println("2-2-2");
					Node sibling = parent.dataList.get(parent.currNum-1).pointer; //�ٷ� ���� ���
					if(Math.ceil((float)maxDegree/2) <= sibling.currNum) { //2-2-2-1. sibling no underflow
						//System.out.println("2-2-2-1");
						//���� sibling���� �ִ� key borrow
						sibling.dataList.get(sibling.currNum-1).pointer = node; //���� ��尡 ����Ű�� ������ �Ѱ��ֱ�
						sibling.dataList.get(sibling.currNum).pointer = null; //���� ��尡 ����Ű�� ������ ����ȭ
						Node nextNode = node.dataList.get(node.currNum).pointer;
						node.removeData(deleteKeyIndex);
						
						parent.dataList.get(parent.currNum).key = sibling.dataList.get(sibling.currNum).key; //�θ� �� �� key�� sibling�� �� ������ key�� ��ü
						node.putData(sibling.removeData(sibling.currNum)); //���� ����� �� �ִ� key borrow
						node.dataList.get(node.currNum).pointer = nextNode;
						
						return false;
					}
					else { //2-2-2-2. sibling underflow so merge
						//System.out.println("2-2-2-2");
						Node nextNode = node.dataList.get(node.currNum).pointer;
						node.removeData(deleteKeyIndex);
						while(node.dataList.size() != 1) {
							sibling.putData(node.removeData(1));
						}
						sibling.dataList.get(sibling.currNum).pointer = nextNode;

						node = null;
						parent.removeData(parent.currNum);
						return true;
					}
				}
				else { //2-2-3. ��� �ִ� ����� ��� 
					//System.out.println("2-2-3");
					/*
					 * ���� �����ʿ��� borrow �����ϸ� ���ʿ��� borrow, ���� �Ұ��� ��� �����ʿ��� borrow
					 * �� �ٿ��� borrow �Ұ����ϸ� ���ʰ� merge --> undeflow�Ǽ� ������ ��尡 0���� ���� 1�� �̻� �и�!!!!!
					*/
					Node leftNode = parent.dataList.get(indexOnParent-1).pointer;
					Node rightNode = parent.dataList.get(indexOnParent+1).pointer;
					
					if(Math.ceil((float)maxDegree/2) <= leftNode.currNum) { //2-2-3-1. ���ʿ��� borrow ���ɽ� ���ʿ��� borrow
						//System.out.println("2-2-3-1");
						//���� sibling���� �ִ� key borrow
						Node nextNode = node.dataList.get(node.currNum).pointer;
						leftNode.dataList.get(leftNode.currNum-1).pointer = leftNode.dataList.get(leftNode.currNum).pointer; //���� ��尡 ����Ű�� ������ �Ѱ��ֱ�
						leftNode.dataList.get(leftNode.currNum).pointer = null; //���� ��尡 ����Ű�� ������ ����ȭ
						node.removeData(deleteKeyIndex);
						
						parent.dataList.get(indexOnParent).key = leftNode.dataList.get(leftNode.currNum).key; //�θ� key�� sibling�� �� ������ key�� ��ü
						node.putData(leftNode.removeData(leftNode.currNum)); //���� ����� �� �ִ� key borrow
						node.dataList.get(node.currNum).pointer = nextNode;
						return false;
					}
					else if(Math.ceil((float)maxDegree/2) <= rightNode.currNum) { //2-2-3-2. ���ʿ��� borrow �Ұ��ɽ� �����ʿ��� borrow
						//System.out.println("2-2-3-2");
						//������ sibling���� �ּڰ� key borrow
						Node nextNode = node.dataList.get(node.currNum).pointer; //���� ��� ����Ű�� ������
						node.dataList.get(node.currNum).pointer = null; //���� ���� ��� ����Ű�� ������ ����ȭ
						node.removeData(deleteKeyIndex); //�ش� Ű ���� Data ����
						node.putData(rightNode.removeData(1)); //underflow�� ��忡 sibling �ּڰ� key borrow
						node.dataList.get(node.currNum).pointer = nextNode;
						
						parent.dataList.get(indexOnParent+1).key = rightNode.dataList.get(1).key;
						return false;
					}
					else { //2-2-3-3. ���ʿ��� borrow �Ұ��ɽ� �������� merge
						//System.out.println("2-2-3-3");
						Node nextNode = node.dataList.get(node.currNum).pointer;
						node.removeData(deleteKeyIndex);	
						while(node.dataList.size() != 1) {
							leftNode.putData(node.removeData(1));
						}
						leftNode.dataList.get(leftNode.currNum).pointer = nextNode;
						
						node = null;
						parent.removeData(indexOnParent);
						return true;
					}
				}
			}
		}
	}
	
	//=============================================================================================================================
	public void tree_keySearch(int key) { //B+tree���� �ش� key�� ���� data ã��
		keySearch(this.root, key);
	}
	
	private void keySearch(Node node, int key) {
		if(node.isLeaf) { //if node is leafNode
			for(int i = 1; i <= node.currNum ; i++) {
				if(node.dataList.get(i).key == key) {
					System.out.println(node.dataList.get(i).value);
					return;
				}
			}
			System.out.println("NOT FOUND");
		}
		else { //if node is indexNode
			for(int i = 1; i <= node.currNum; i++) { //�� indexNode�� key���� ���
				System.out.print(node.dataList.get(i).key + "  ");
			}
			System.out.println();
			
			if(key < node.dataList.get(1).key) { //key�� ù��° ������ ������ //keySearch �տ� this.�ٿ��� �ϳ�??
				keySearch(node.dataList.get(0).pointer, key); //�� ����� �� ���� �����Ͱ� ����Ű�� ��忡�� �ٽ� search
			}
			else if(node.currNum == maxDegree-1 && key >= node.dataList.get(node.currNum).key){ //��尡 ���� �������� key�� �� ����� ������ ������ ũ��
				keySearch(node.dataList.get(node.currNum).pointer, key); //�� ����� �� ������ �����Ͱ� ����Ű�� ��忡�� �ٽ� search, maxDegree-1 ��� currNum���� ����
			}
			else { //key�� ��� ���� ���϶�
				int index = node.findIndexbyKey(key); //�� Ű�� ���� �ε��� find
				keySearch(node.dataList.get(index).pointer, key); //�� ����� �ε����� ���ڷ� ���� �����Ͱ� ����Ű�� ��忡�� �ٽ� search
			}
		}
	}
	
	public void tree_rangeSearch(int startKey, int endKey) { //B+tree���� range search
		rangeSearch(this.root, startKey, endKey);
	}
	
	private void rangeSearch(Node node, int startKey, int endKey) {
		while(!node.isLeaf) { //leafNode ���ö����� search
			int index = node.findIndexbyKey(startKey);
			rangeSearch(node.dataList.get(index).pointer, startKey, endKey);
			return;
		}
		
		int startIndex = 1;
		
		while(startIndex <= node.currNum && startKey > node.dataList.get(startIndex).key) { //startKey�� �ش� �ε����� Ű ������ �۰ų� ���� ������
			startIndex++;
		}
		
		if(startIndex > node.currNum) { //�ش� ��忡 startKey���� ũ�ų� ���� ���� ������
			//System.out.println("There is no proper key in this node so search next node");
			rangeSearch(node.dataList.get(node.currNum).pointer, startKey, endKey); // ���� ��忡�� ����
			return;
		}
		
		int endIndex = startIndex;
		
		if(endKey < node.dataList.get(1).key) { //�� ��带 Ž���Ҷ� endKey�� �� ����� 1��° ������ ������
			return;
		}
		
		while(endKey > node.dataList.get(endIndex).key && endIndex < node.currNum) {
			endIndex++;
		}
		
		if(endKey < node.dataList.get(endIndex).key) {
			endIndex--;
		}
		
		for(int i = startIndex; i <= endIndex; i++) {
			System.out.println(node.dataList.get(i).key + "," + node.dataList.get(i).value);
		}
		
		if(node.dataList.get(node.currNum).pointer == null) { //�� ��尡 ���� ��
			return;
		}
		
		rangeSearch(node.dataList.get(node.currNum).pointer, node.dataList.get(node.currNum).pointer.dataList.get(1).key, endKey);
	}
	//==========================================================================================================================
	public void tree_printAll() {
		if(this.root == null) {
			System.out.println("There is no root node");
		}else {
			this.printAll(this.root);
		}
	}
	private void printAll(Node node) {
		node.printNode(); //�ش� node ��� ���
		System.out.println(); //�� �ٲ�
		if(!node.isLeaf) { //indexNode��� child Ž��
			for(int i = 0; i <= node.currNum; i++) {
				this.printAll(node.dataList.get(i).pointer);
			}
		}
	}
	//============================================================================================================================
	public StringBuilder tree_recordAll() { //ù��° leafNode�� ã�Ƽ� ��� leafNode���� key, value ���� ��ȯ
		Node firstLeafNode = this.root;
		while(!firstLeafNode.isLeaf) {
			firstLeafNode = firstLeafNode.dataList.get(0).pointer;
		}
		return this.recordAll(firstLeafNode);
	}
	private StringBuilder recordAll(Node node) { //leafNode���� key, value ���� ��ȯ
		//�� ó�� �ڽ� ��� ã����
		StringBuilder record = new StringBuilder();
		while(node.dataList.get(node.currNum).pointer != null) { //�� ������ ������
			record.append(node.recordNode());
			node = node.dataList.get(node.currNum).pointer; //���� ����
		}
		record.append(node.recordNode()); //������ ��� ���
		return record; //�׷� �� ������ ������ key, value �Է�....������ ��� ���� �߰� ��� ���� ������ ��� ������...!
	}
	//============================================================================================================================
}
