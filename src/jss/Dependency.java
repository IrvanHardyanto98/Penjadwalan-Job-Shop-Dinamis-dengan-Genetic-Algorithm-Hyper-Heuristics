package jss;

public class Dependency{
	private int operationPred;
	private int operationSucc;
	
	public Dependency(){
	}
	
	public Dependency(int operationPred,int operationSucc){
		this.operationPred = operationPred;
		this.operationSucc = operationSucc;
	}
	
	public int getOperationPred(){
		return this.operationPred;
	}
	
	public void setOperationPred(int operationPred){
		this.operationPred = operationPred;
	}
	
	public int getOperationSucc(){
		return this.operationSucc;
	}
	
	public void setOperationSucc(int operationSucc){
		this.operationSucc = operationSucc;
	}
}