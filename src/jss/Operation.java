package jss;

public class Operation{
	private int jobId;
	private int oprOrderNum;
	private int procTime;
	private int machineNum;//machine num nilainya 0 s/d m-1
	
	public Operation(int jobId,int oprOrderNum,int procTime,int machineNum){
		this.jobId = jobId;
		this.oprOrderNum = oprOrderNum;
		this.procTime = procTime;
		this.machineNum = machineNum;
	}
	
	public int getMachineNum(){
		return this.machineNum;
	}
	
	public int getProcTime(){
		return this.procTime;
	}
	
	public int getOprOrderNum(){
		return this.oprOrderNum;
	}
	
	public int getJobId(){
		return this.jobId;
	}
}