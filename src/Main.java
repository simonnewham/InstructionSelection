import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;
/**{
 * @author simonnewham
 * Program to simulate instruction selection using maximal munch
 * May 2018
 **/

public class Main {
	static Register reg; 
	
	public static void main(String[] args) throws FileNotFoundException {
		reg = new Register(); //used to keep track of register allocation
		
		//read in IR code
		//Scanner a = new Scanner(new File("testdata/while_if.ir"));
		Scanner a = new Scanner(new File(args[0]));
 		Stack<Node> stack = new Stack<Node>();
		a.close();
		while(a.hasNextLine()){
			String temp = a.nextLine();	//get line of input
			int level = temp.lastIndexOf("=") +1; //find index of last =
			String temp1 = temp.substring(level);  //remove ='s
						
			while(stack.size() > level){
				stack.pop();
			}
			Node node = new Node(temp1);
			if (stack.size() > 0){
				Node prt = (Node) stack.pop();
				//node.addParent(prt);
				prt.addKid(node);
				stack.push(prt);	
			}
			stack.push(node);
		}
		Node root = (Node) stack.get(0); //reference to root of tree
		munchStmt(root);
	}
	/*{
	 * /Method to munch statements and return simplified Python code
	 * start at root and try apply statement tile, might call munchExp to get value of tiles
	 */
	public static void munchStmt(Node node){
		
		if(node.getValue().equals("SEQ")){
			// (assuming 2 kids)
			Node child1 = node.getKid(0);
			Node child2 = node.getKid(1);
			
			//check for if statement
			if(child1.getValue().equals("SEQ") && child2.getValue().equals("LABEL")){
				Node child3 = child1.getKid(0); //SEQ
				Node child4 = child1.getKid(1); //either statement or JUMP
				
				String done = child2.getKidVal(0);
				
				//while loop
				if(child3.getEqual("SEQ") && child4.getEqual("JUMP")){
					Node child5 = child3.getKid(0); //SEQ
					Node child6 = child3.getKid(1); //body to munch
										
					if(child5.getEqual("SEQ")){
						Node child7 = child5.getKid(0); //SEQ
						Node child8 = child5.getKid(1); //LABEL t1
						
						if(child7.getEqual("SEQ") && child8.getEqual("LABEL")){
							String t1 = child8.getKidVal(0); //label of t1
							
							Node child9 = child7.getKid(0); //LABEL
							Node child10 = child7.getKid(1); //CJUMP
							
							if(child9.getEqual("LABEL")&&child10.getEqual("CJUMP")){
									
								Node child11 = child10.getKid(0); //exp to munch
								
								//while loop true
								String var = munchExp(child11);
								String cons = munchExp(child10.getKid(2));
								String LT = child10.getKidVal(1);
								String s2 = child10.getKidVal(3);
								String s1 = child10.getKidVal(4);
									
								//format instructions 
								System.out.println("while "+var+"<"+reg.current()+":");
								munchStmt(child6);
								
							}				
						}			
					}
				}
								
				//if statement
				else if (child3.getValue().equals("SEQ")){
					Node child5 = child3.getKid(0); //SEQ
					Node child6 = child3.getKid(1); //LABEL
						
					if(child5.getValue().equals("SEQ") && child6.getValue().equals("LABEL")){
						
						//store label
						String s2 = child6.getKidVal(0);
						
						Node child7 = child5.getKid(0); //SEQ
						Node child8 = child5.getKid(1); //JUMP
						
						if(child7.getEqual("SEQ") && child8.getEqual("JUMP")){
							
							String jump = child8.getKidVal(0);
							
							Node child9 = child7.getKid(0); //SEQ
							Node child10 = child7.getKid(1); //if s1 statement (much later)
							
							
							if(child9.getEqual("SEQ")){
								Node child11 =child9.getKid(0); //CJUMP
								Node child12 = child9.getKid(1); //LABEL
								 
								if(child11.getEqual("CJUMP")&& child12.getEqual("LABEL")){
									
									//if statement = true
									String s1 = child12.getKidVal(0);
									
									String var = munchExp(child11.getKid(0)); //CONST or MEM probably
									String op = child11.getKidVal(1); //LT
									String con = munchExp(child11.getKid(2)); //CONST returns reg (use current reg)
									String op1 = child11.getKid(3).getKidVal(0); //s1
									String op2 = child11.getKid(4).getKidVal(0); //s2
									 
									//format instructions
									//System.out.println("if ("+var+"<"+con+"): ");
									System.out.println("if ("+var+"<"+reg.current()+"):");
									//System.out.println("#Label: "+s1);
									munchStmt(child10);
									//System.out.println("#Jump: "+jump);
									//System.out.println("#Label: "+s2);
									System.out.println("else:");
									munchStmt(child4);
									//System.out.println("#Label:"+ done);	
									
								}		
							}
						}
					}					
				}
			}	
			
			//if not if then munchStmt
			else{
				for(int i=0; i<node.getKids().size(); i++){	
					munchStmt(node.getKid(i));
				}	
			}
		}
		
		//variable assignment 
		else if(node.getValue().equals("MOVE")){
			
			Node child0 = node.getKid(0); //should be TEMP or MEM
			Node child1 = node.getKid(1); //WILL BE expression to move into left child 
			
			String temp = (munchExp(child1)); //(new reg)
			
			//MOVE[MEM[+[CONST variable, TEMP FP]], e]
			//MOVE[MEM[+[TEMP FP, CONST variable]], e]
			if(child0.getValue().equals("MEM") || child0.getValue().equals("TEMP")){
				//MEM[+[CONST x, TEMP FP]]
				Node child2 = child0.getKid(0); 
				//verify structure of variable 
				if(child2.getValue().equals("+")){
					Node child3 = child2.getKid(0); 
					Node child4 = child2.getKid(1);
						
					if(child3.getValue().equals("TEMP") && child4.getValue().equals("CONST")){
						System.out.println(child4.getKidVal(0)+"="+reg.current()); //(old)
					}
					else if(child3.getValue().equals("CONST") && child4.getValue().equals("TEMP")){
						System.out.println(child3.getKidVal(0)+"="+reg.current()); //(old)
					}
				}
			}
		}
		
		//check for any method call (print, input)
		else if(node.getValue().equals("CALL")){
			Node temp = node.getKid(0);
			String tempVal = temp.getValue();
			
			//check for print (assume NAME is always first)
			//CALL[NAME[print],e]
			if(tempVal.equals("NAME") && temp.getKidVal(0).equals("print")){
				//evaluate e to memory hole
				munchExp(node.getKid(1));
				//print with memory hole (old reg)
				System.out.println("print("+reg.current()+")");
			}
		}
		//variable
		else if(node.getValue().equals("MEM")){
			//MEM[+[CONST x, TEMP FP]]
			Node child0 = node.getKid(0); //should be "+"
			//verify structure of variable 
			if(child0.getValue().equals("+")){
				Node child1 = child0.getKid(0); 
				Node child2 = child0.getKid(1);
				
				if(child1.getValue().equals("TEMP") && child2.getValue().equals("CONST")){
					System.out.println(child2.getKidVal(0)); //return the constant 
				}
				else if(child1.getValue().equals("CONST") && child2.getValue().equals("TEMP")){
					System.out.println(child1.getKidVal(0)); //return the constant 
				}
			}			
		}		
	}
	
	/*{
	 * Method to munch an expression
	 * /will return a single value to munchStmt caller
	 */
	public static String munchExp(Node node){
		
		//BINOP (assuming always has 2 kids)
		//+[e,e]
		if(node.getValue().equals("+") || node.getValue().equals("-") || node.getValue().equals("*") || node.getValue().equals("/")){
			
			String code = node.getValue();
			Node child1 = node.getKid(0);
			Node child2 = node.getKid(1);
			
			//2 CONSTS (old reg)
			String out = null;
			if(code.equals("+")){
				out = "("+munchExp(child1)+"+"+munchExp(child2)+")";
				System.out.println(reg.current()+"="+out);
				
			}
			else if(code.equals("-")){
				out = "("+munchExp(child1)+"-"+munchExp(child2)+")";
				System.out.println(reg.current()+"="+out);
			}
			else if(code.equals("*")){
				out = "("+munchExp(child1)+"*"+munchExp(child2)+")";
				System.out.println(reg.current()+"="+out);
			}
			else if(code.equals("/")){
				out = "("+munchExp(child1)+"/"+munchExp(child2)+")";
				System.out.println(reg.current()+"="+out);
			}
			return out;
		}
		
		//CONST (1 child) (new reg)
		else if(node.getValue().equals("CONST")){
			System.out.println(reg.getNew()+"="+ node.getKidVal(0));
			return node.getKidVal(0);
		}
		
		//MEM
		else if(node.getValue().equals("MEM")){
			//MEM[+[CONST x, TEMP FP]]
			Node child0 = node.getKid(0); //should be "+"
			//verify structure of variable 
			if(child0.getValue().equals("+")){
				Node child1 = child0.getKid(0); 
				Node child2 = child0.getKid(1);
				
				if(child1.getValue().equals("TEMP") && child2.getValue().equals("CONST")){
					return child2.getKidVal(0); //return the constant 
				}
				else if(child1.getValue().equals("CONST") && child2.getValue().equals("TEMP")){
					return child1.getKidVal(0); //return the constant 
				}
			}
			return "error";
		}
		//CALL (exp of MOVE)		
		if(node.getValue().equals("CALL")){
			//CALL[NAME[input]]
			Node child0 = node.getKid(0);
			if(child0.getValue().equals("NAME")){
				if(child0.getKidVal(0).equals("input")){
					System.out.println(reg.getNew()+"=eval(input())");
					return "eval(input())";
				}
			}
		}
		
		//OTHER
		return null;	
	}	
	
	//output for TRACING
	public static void printME(Node prt, String indent){
	     System.out.println(indent+prt.getValue());
	      for (int i=0; i<prt.getKids().size(); i++){
	             printME(prt.getKids().get(i), indent+"=");
	        	
	      }
	}
}
