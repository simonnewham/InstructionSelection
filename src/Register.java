/**
 * @author simonnewham
 * 
 */
public class Register {

	int current_reg =-1;
	
	public Register(){
		
	}
	
	public String current(){
		return "v"+current_reg;
	}
	
	public String getNew(){
		
		current_reg +=1;
		
		return "v"+current_reg;
	}
}
