package jss;

public class Operation{
	private int jobId;
	private int operationId;
	private int procTime;
	private int machineNum;//machine num nilainya 0 s/d m-1
	
	public Operation(int jobId,int operationId,int procTime,int machineNum){
		this.jobId = jobId;
		this.operationId = operationId;
		this.procTime = procTime;
		this.machineNum = machineNum;
	}
	
	public int getMachineNum(){
		return this.machineNum;
	}
	
	public int getProcTime(){
		return this.procTime;
	}
	
	public int getOperationId(){
		return this.operationId;
	}
	
	public int getJobId(){
		return this.jobId;
	}
}