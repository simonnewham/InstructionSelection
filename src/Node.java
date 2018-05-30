import java.util.ArrayList;

/**
 *@author simonnewham
 *
 */

public class Node {

	int level;
	ArrayList<Node> kids;
	String value;
	Node parent;
	
	public Node(String v){
		value = v;
		kids = new ArrayList<>();
		
	}
	
	public Node(Node n){
		value = n.getValue();
		kids = new ArrayList<>();
		
	}
	//each node will only have one parent
	public void addParent(Node n){
		this.parent = n;
	}
	
	public void addKid(Node n){
		kids.add(n);
	}
	
	public ArrayList<Node> getKids(){
		return kids;
	}
	
	public String getValue(){
		return value;
	}
	
	public void removeChild(Node n){
		
		for (int i=0; i <kids.size();i++){
			
			if(kids.get(i) == n){
				kids.remove(i);
			}
		}
		
	}
	
	public Node getKid(int i){
		
		return kids.get(i);
		
	}
	
	public String getKidVal(int i){
		
		return kids.get(i).getValue();
		
	}
	
	public void replace(Node n){
			
			this.kids = n.getKids();
			this.value = n.getValue();
			
	}
	
	public boolean getEqual(String s){
		
		String node = this.getValue();
		
		if(node.equals(s)){
			return true;
		}
		
		return false;
		
	}
	
}