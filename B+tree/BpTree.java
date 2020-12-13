package BpTree;

public class BpTree {
	public Node root; //B+Tree의 root Node
	public int maxDegree; //B+Tree의 포인터 갯수, maxDegree-1은 data의 최대 갯수
	//=============================================================================================================================
	public BpTree(int maxDegree) { //maxDegree를 갖는 B+tree를 만드는 생성자
		this.maxDegree = maxDegree; //B+tree의 maxDegree
		Node.maxDegree = maxDegree; //B+tree의 노드들의 maxDegree
	}
	//=============================================================================================================================
	public void tree_keyInsert(int key, int value) { //key와 insert를 B+tree에 삽입
		Data insertData = new Data(0, 0); //삽입할 데이터
		Data temp_newChildData = new Data(0, 0);
		
		insertData.key = key;
		insertData.value = value;
		
		if(this.root == null) { //B+tree 처음 생성시
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
			Data temp_newChildData = new Data(0, 0); //아래 노드에서 split하면 올라올 수 있는 노드, indexNode에 키와 포인터 제공
			boolean exist_child = this.keyInsert(node.dataList.get(index).pointer, insertData, temp_newChildData);
			
			if(!exist_child) { //insert로 split을 하지 않아서 새로운 child가 생기지 않았을때
				return false; //새로운 child가 생기지 않았다면 변화가 필요없음 그대로 종료
			}
			else { //insert로 split해서 새로운 child가 생김 //newChildData의 키를 부모 node에 넣어야 함
				if(node.currNum < maxDegree-1) { //부모 노드에 넣을 자리가 있다.
					node.putData(temp_newChildData); //부모 노드에 child에 들어온 새 Data 저장
					return false; //새로운 child가 생기지 않았음을 알림
				}
				else { //부모 노드에 넣을 자리가 없으므로 부모노드 split!
					node.putData(temp_newChildData); //overflow 시킴
					Node newNode = new Node(false); //새로운 indexNode 생성, Node 생성자로 이미 0번째 key, value는 값이 0
					
					for(int i = maxDegree/2 + 1; i <= maxDegree; i++) { //이전 노드에서 Data 삭제하고 삭제한 Data들 새로운 노드에 옮김
						newNode.putData(node.removeData(maxDegree/2 + 1));
					}
					
					Data middleData = newNode.removeData(1); //새로운 노드의 첫번째 Data 삭제하고 그 값 저장
					newNode.dataList.get(0).pointer = middleData.pointer; //새로운 노드의 맨 왼쪽 포인터
					
					newChildData.key = middleData.key;
					newChildData.pointer = newNode;
					
					if(node == this.root) { //이 노드가 루트 노드여서 새로운 부모 만들어서 연결
						Node newRoot = new Node(false);
						newRoot.dataList.get(0).pointer = node;
						newRoot.dataList.add(new Data(middleData.key, 0));
						newRoot.dataList.get(1).pointer = newNode;
						newRoot.currNum = 1;
						this.root = newRoot;
					}
					return true; //새로운 child가 나타났음을 알림
				}
			}
		}
		else { //2. if node is leafNode
			if(node.currNum < maxDegree-1) { //2-1. not overflow
				Node nextNode = node.dataList.get(node.currNum).pointer; //다음 노드 가르키는 포인터
				node.putData(insertData);
				node.dataList.get(node.currNum).pointer = nextNode; //다음 노드 가르키는 포인터
				newChildData = null; //새로운 child가 없으므로
				return false; //새로운 child가 없음을 알림
			}
			else { //2-2. overflow so split
				Node nextNode = node.dataList.get(node.currNum).pointer; //다음 노드 가르키는 포인터
				node.dataList.get(node.currNum).pointer = null;
				node.putData(insertData); //노드를 overflow 시킴
				node.dataList.get(maxDegree).pointer = nextNode; //over된 맨 마지막 포인터가 다음 노드 가르킴
				
				Node newNode = new Node(true); //새 leaf node 생성, 오른쪽 노드, 기존 노드는 왼쪽 노드

				for(int i = maxDegree/2 + 1; i <= maxDegree; i++ ) { //이전 노드에서 Data 삭제하고 삭제한 Data들 새로운 노드에 옮김
					newNode.putData(node.removeData(maxDegree/2 + 1));
				}

				node.dataList.get(node.currNum).pointer = newNode; //리프 노드 연결
				
				newChildData.key = newNode.dataList.get(1).key;
				newChildData.pointer = newNode;
				
				if(node == this.root) { //이 노드가 루트였을시 root가 될 indexNode 1개와 자식노드인 leafNode 2개를 생성 후 이어줌 
					this.root = new Node(false);
					this.root.dataList.get(0).pointer = node;
					Data newData = new Data(newNode.dataList.get(1).key, 0);
					newData.pointer = newNode;
					this.root.dataList.add(newData);
					this.root.currNum++;
				}
				
				return true; //새로운 child가 생겼음을 알림
			}
		}
	}
	//=============================================================================================================================
	public void tree_keyDelete(int key) { //B+tree에서 key를 가진 data 삭제
		Data deleteData = new Data(0, 0);
		deleteData.key = key;
		deleteData.pointer = null;
		
		this.keyDelete(null, this.root, deleteData, 0);
	}
	
	public boolean keyDelete(Node parent, Node node, Data deleteData, int indexOnParent) {
		if(!node.isLeaf) { //1. indexNode
			int index = node.findIndexbyKey(deleteData.key);
			boolean removeOnThisNode = keyDelete(node, node.dataList.get(index).pointer, deleteData, index);
			
			if(node.currNum >= index && node.dataList.get(index).key == deleteData.key) { //1-0-1. indexNode에 delete key가 있으면
				//System.out.println("1-0-1");
				Node belowNode = node.dataList.get(index).pointer;
				while(belowNode.isLeaf != true) {
					belowNode = belowNode.dataList.get(0).pointer;
				}
				node.dataList.get(index).key = belowNode.dataList.get(1).key;
			}
			
			if(node != root && parent.currNum >= 1 && parent.dataList.get(indexOnParent).key == deleteData.key) { //1-0-2. 부모노드에 delete key가 있으면
				//System.out.println("1-0-2");
				Node belowNode = parent.dataList.get(indexOnParent).pointer;
				while(belowNode.isLeaf != true) {
					belowNode = belowNode.dataList.get(0).pointer;
				}
				parent.dataList.get(indexOnParent).key = belowNode.dataList.get(1).key;
			}
			
			if(!removeOnThisNode) { //1-1. 이 노드에서 remove 안일어났으면 위 노드들에서도 remove 안일어날 것
				//System.out.println("1-1");
				return false;
			}
			else { //1-2. 이 노드에서 remove 일어났다면
				//System.out.println("1-2");
				
				if(Math.ceil((float)maxDegree/2)-1 <= node.currNum || (node == this.root && 1 <= node.currNum)) { //1-2-1. no underflow on indexNode
					//System.out.println("1-2-1");
					return false;
				}
				else if(node == root && node.currNum == 0) { //1-2-3. 이 노드가 루트 노드이고 key가 0개일 때
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
					boolean isLeft = (indexOnParent == 0); //이 노드가 자식노드 중 맨 왼쪽인지
					boolean isRight = (indexOnParent == parent.currNum); //이 노드가 자식 노드 중 맨 오른쪽인지
					
					if(isLeft) { //1-2-2-1. 맨 왼쪽 노드일 경우 오른쪽 노드에서 borrow or merge
						//System.out.println("1-2-2-1");
						Node sibling = parent.dataList.get(1).pointer; //오른쪽 노드 ( get(indexOnParent+1)가능
						
						if(Math.ceil((float)maxDegree/2) <= sibling.currNum) { //1-2-2-1-1. sibling no underflow so borrow
							//System.out.println("1-2-2-1-1");
							//오른쪽 sibling에서 최솟값 key borrow
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
					else if(isRight){ //1-2-2-2. 맨 오른쪽 노드일 경우 왼쪽 노드에서 borrow or merge
						//System.out.println("1-2-2-2");
						Node sibling = parent.dataList.get(parent.currNum-1).pointer; //바로 왼쪽 노드
						if(Math.ceil((float)maxDegree/2) <= sibling.currNum) { //1-2-2-2-1. sibling no underflow
							//System.out.println("1-2-2-2-1");
							//왼쪽 sibling에서 최댓값 key borrow
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
					else { //1-2-2-3. 가운데 있는 노드일 경우 
						//System.out.println("1-2-2-3");
						/*
						 * 왼쪽 오른쪽에서 borrow 가능하면 왼쪽에서 borrow, 왼쪽 불가할 경우 오른쪽에서 borrow
						 * 둘 다에서 borrow 불가능하면 왼쪽과 merge --> underflow되서 합쳐질 노드가 0개일 경우와 1개 이상 분리!!!!!
						*/
						Node leftNode = parent.dataList.get(indexOnParent-1).pointer;
						Node rightNode = parent.dataList.get(indexOnParent+1).pointer;
						
						if(Math.ceil((float)maxDegree/2) <= leftNode.currNum) { //1-2-2-3-1. 양쪽에서 borrow 가능시 왼쪽에서 borrow
							//왼쪽 sibling에서 최댓값 key borrow
							//System.out.println("1-2-2-3-1");
							node.putData(parent.removeData(indexOnParent));
							node.dataList.get(1).pointer = node.dataList.get(0).pointer;
							node.dataList.get(0).pointer = leftNode.dataList.get(leftNode.currNum).pointer;
							parent.putData(leftNode.removeData(leftNode.currNum));
							parent.dataList.get(indexOnParent).pointer = node;
							
							return false;
						}
						else if(Math.ceil((float)maxDegree/2) <= rightNode.currNum) { //1-2-2-3-2. 왼쪽에서 borrow 불가능시 오른쪽에서 borrow
							//오른쪽 sibling에서 최솟값 key borrow
							//System.out.println("1-2-2-3-2");
							node.putData(parent.removeData(indexOnParent+1));
							node.dataList.get(node.currNum).pointer = rightNode.dataList.get(0).pointer;
							rightNode.dataList.get(0).pointer = rightNode.dataList.get(1).pointer;
							parent.putData(rightNode.removeData(0));
							parent.dataList.get(indexOnParent+1).pointer = rightNode;
							
							return false;
						}
						else { //1-2-2-3-3. 양쪽에서 borrow 불가능시 왼쪽으로 merge
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
			int deleteKeyIndex = node.findIndexbyKey(deleteData.key); //해당 leafNode에서 삭제할 key의 index
			
			if(Math.ceil((float)maxDegree/2) <= node.currNum) { //2-1. no underflow
				//System.out.println("2-1");
				if(deleteKeyIndex == node.currNum) { //2-1-1. 삭제하는 Data가 leafNode의 맨 마지막 데이터 일 경우
					//System.out.println("2-1-1");
					Node nextNode = node.dataList.get(node.currNum).pointer; //그 Data가, 이 노드가 가르키는 다음 leafNode
					node.removeData(deleteKeyIndex);
					node.dataList.get(node.currNum).pointer = nextNode;
				}
				else if(deleteKeyIndex == 1) { //2-1-2. 삭제하는 Data가 leafNode의 맨 처음일 때
					//System.out.println("2-1-2");
					node.removeData(deleteKeyIndex);
					parent.dataList.get(indexOnParent).key = deleteData.key;
				}
				else { //2-1-3. 삭제하는 Data가 leafNode의 중간일때
					//System.out.println("2-1-3");
					node.removeData(deleteKeyIndex);
				}
				return false; //부모노드에서 delete 없었음
			}
			else { //2-2. underflow
				//System.out.println("2-2");
				
				if(node == this.root && node.currNum == 1) {
					node.removeData(deleteKeyIndex); //노드에서 키 삭제
					this.root = null;
					return false;
				}
				
				boolean isLeft = (indexOnParent == 0); //이 노드가 자식노드 중 맨 왼쪽인지
				boolean isRight = (indexOnParent == parent.currNum); //이 노드가 자식 노드 중 맨 오른쪽인지
				
				
				if(isLeft) { //2-2-1. 맨 왼쪽 노드일 경우 오른쪽 노드에서 borrow or merge //특수해서 부모노드까지 미리 처리가능?
					//System.out.println("2-2-1");
					Node sibling = parent.dataList.get(1).pointer; //오른쪽 노드 ( get(indexOnParent+1)가능
					
					if(Math.ceil((float)maxDegree/2) <= sibling.currNum) { //2-2-1-1. sibling no underflow so borrow
						//오른쪽 sibling에서 최솟값 key borrow
						Node nextNode = node.dataList.get(node.currNum).pointer;
						node.dataList.get(node.currNum).pointer = null; //원래 다음 노드 가르키는 포인터 무력화
						node.removeData(deleteKeyIndex); //해당 키 가진 Data 삭제
						node.putData(sibling.removeData(1)); //underflow된 노드에 sibling 최솟값 key borrow
						node.dataList.get(node.currNum).pointer = nextNode;
						
						parent.dataList.get(1).key = sibling.dataList.get(1).key;
						return false;
					}
					else { //2-2-1-2. sibling underflow so merge
						//System.out.println("2-2-1-2");
						node.dataList.get(node.currNum).pointer = null; //원래 다음 노드 가르키던 포인터 무력화
						node.removeData(deleteKeyIndex);
						while(sibling.dataList.size() != 1) { //sibling 다 비울때까지
							node.putData(sibling.removeData(1));
						}
						
						sibling = null;
						parent.removeData(1);
						return true;
					}
				}
				else if(isRight){ //2-2-2. 맨 오른쪽 노드일 경우 왼쪽 노드에서 borrow or merge
					//System.out.println("2-2-2");
					Node sibling = parent.dataList.get(parent.currNum-1).pointer; //바로 왼쪽 노드
					if(Math.ceil((float)maxDegree/2) <= sibling.currNum) { //2-2-2-1. sibling no underflow
						//System.out.println("2-2-2-1");
						//왼쪽 sibling에서 최댓값 key borrow
						sibling.dataList.get(sibling.currNum-1).pointer = node; //왼쪽 노드가 가르키던 포인터 넘겨주기
						sibling.dataList.get(sibling.currNum).pointer = null; //왼쪽 노드가 가르키는 포인터 무력화
						Node nextNode = node.dataList.get(node.currNum).pointer;
						node.removeData(deleteKeyIndex);
						
						parent.dataList.get(parent.currNum).key = sibling.dataList.get(sibling.currNum).key; //부모 맨 끝 key를 sibling의 맨 오른쪽 key로 대체
						node.putData(sibling.removeData(sibling.currNum)); //왼쪽 노드의 맨 최댓값 key borrow
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
				else { //2-2-3. 가운데 있는 노드일 경우 
					//System.out.println("2-2-3");
					/*
					 * 왼쪽 오른쪽에서 borrow 가능하면 왼쪽에서 borrow, 왼쪽 불가할 경우 오른쪽에서 borrow
					 * 둘 다에서 borrow 불가능하면 왼쪽과 merge --> undeflow되서 합쳐질 노드가 0개일 경우와 1개 이상 분리!!!!!
					*/
					Node leftNode = parent.dataList.get(indexOnParent-1).pointer;
					Node rightNode = parent.dataList.get(indexOnParent+1).pointer;
					
					if(Math.ceil((float)maxDegree/2) <= leftNode.currNum) { //2-2-3-1. 양쪽에서 borrow 가능시 왼쪽에서 borrow
						//System.out.println("2-2-3-1");
						//왼쪽 sibling에서 최댓값 key borrow
						Node nextNode = node.dataList.get(node.currNum).pointer;
						leftNode.dataList.get(leftNode.currNum-1).pointer = leftNode.dataList.get(leftNode.currNum).pointer; //왼쪽 노드가 가르키던 포인터 넘겨주기
						leftNode.dataList.get(leftNode.currNum).pointer = null; //왼쪽 노드가 가르키는 포인터 무력화
						node.removeData(deleteKeyIndex);
						
						parent.dataList.get(indexOnParent).key = leftNode.dataList.get(leftNode.currNum).key; //부모 key를 sibling의 맨 오른쪽 key로 대체
						node.putData(leftNode.removeData(leftNode.currNum)); //왼쪽 노드의 맨 최댓값 key borrow
						node.dataList.get(node.currNum).pointer = nextNode;
						return false;
					}
					else if(Math.ceil((float)maxDegree/2) <= rightNode.currNum) { //2-2-3-2. 왼쪽에서 borrow 불가능시 오른쪽에서 borrow
						//System.out.println("2-2-3-2");
						//오른쪽 sibling에서 최솟값 key borrow
						Node nextNode = node.dataList.get(node.currNum).pointer; //다음 노드 가르키는 포인터
						node.dataList.get(node.currNum).pointer = null; //원래 다음 노드 가르키는 포인터 무력화
						node.removeData(deleteKeyIndex); //해당 키 가진 Data 삭제
						node.putData(rightNode.removeData(1)); //underflow된 노드에 sibling 최솟값 key borrow
						node.dataList.get(node.currNum).pointer = nextNode;
						
						parent.dataList.get(indexOnParent+1).key = rightNode.dataList.get(1).key;
						return false;
					}
					else { //2-2-3-3. 양쪽에서 borrow 불가능시 왼쪽으로 merge
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
	public void tree_keySearch(int key) { //B+tree에서 해당 key를 가진 data 찾기
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
			for(int i = 1; i <= node.currNum; i++) { //이 indexNode의 key값들 출력
				System.out.print(node.dataList.get(i).key + "  ");
			}
			System.out.println();
			
			if(key < node.dataList.get(1).key) { //key가 첫번째 값보다 작으면 //keySearch 앞에 this.붙여야 하나??
				keySearch(node.dataList.get(0).pointer, key); //이 노드의 맨 왼쪽 포인터가 가르키는 노드에서 다시 search
			}
			else if(node.currNum == maxDegree-1 && key >= node.dataList.get(node.currNum).key){ //노드가 가득 차있을때 key가 이 노드의 마지막 값보다 크면
				keySearch(node.dataList.get(node.currNum).pointer, key); //이 노드의 맨 오른쪽 포인터가 가르키는 노드에서 다시 search, maxDegree-1 대신 currNum여도 괜찮
			}
			else { //key가 노드 사이 값일때
				int index = node.findIndexbyKey(key); //그 키가 있을 인덱스 find
				keySearch(node.dataList.get(index).pointer, key); //이 노드의 인덱스를 인자로 갖는 포인터가 가르키는 노드에서 다시 search
			}
		}
	}
	
	public void tree_rangeSearch(int startKey, int endKey) { //B+tree에서 range search
		rangeSearch(this.root, startKey, endKey);
	}
	
	private void rangeSearch(Node node, int startKey, int endKey) {
		while(!node.isLeaf) { //leafNode 나올때까지 search
			int index = node.findIndexbyKey(startKey);
			rangeSearch(node.dataList.get(index).pointer, startKey, endKey);
			return;
		}
		
		int startIndex = 1;
		
		while(startIndex <= node.currNum && startKey > node.dataList.get(startIndex).key) { //startKey가 해당 인덱스의 키 값보다 작거나 같을 때까지
			startIndex++;
		}
		
		if(startIndex > node.currNum) { //해당 노드에 startKey보다 크거나 같은 값이 없을때
			//System.out.println("There is no proper key in this node so search next node");
			rangeSearch(node.dataList.get(node.currNum).pointer, startKey, endKey); // 옆의 노드에서 수행
			return;
		}
		
		int endIndex = startIndex;
		
		if(endKey < node.dataList.get(1).key) { //옆 노드를 탐색할때 endKey가 그 노드의 1번째 값보다 작을때
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
		
		if(node.dataList.get(node.currNum).pointer == null) { //옆 노드가 없을 때
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
		node.printNode(); //해당 node 결과 출력
		System.out.println(); //줄 바꿈
		if(!node.isLeaf) { //indexNode라면 child 탐색
			for(int i = 0; i <= node.currNum; i++) {
				this.printAll(node.dataList.get(i).pointer);
			}
		}
	}
	//============================================================================================================================
	public StringBuilder tree_recordAll() { //첫번째 leafNode를 찾아서 모든 leafNode들의 key, value 값을 반환
		Node firstLeafNode = this.root;
		while(!firstLeafNode.isLeaf) {
			firstLeafNode = firstLeafNode.dataList.get(0).pointer;
		}
		return this.recordAll(firstLeafNode);
	}
	private StringBuilder recordAll(Node node) { //leafNode들의 key, value 값을 반환
		//맨 처음 자식 노드 찾으면
		StringBuilder record = new StringBuilder();
		while(node.dataList.get(node.currNum).pointer != null) { //맨 마지막 노드까지
			record.append(node.recordNode());
			node = node.dataList.get(node.currNum).pointer; //다음 노드로
		}
		record.append(node.recordNode()); //마지막 노드 기록
		return record; //그럼 맨 마지막 노드부터 key, value 입력....구성은 상관 없다 했고 사실 삽입 순서는 상관 없으니...!
	}
	//============================================================================================================================
}
