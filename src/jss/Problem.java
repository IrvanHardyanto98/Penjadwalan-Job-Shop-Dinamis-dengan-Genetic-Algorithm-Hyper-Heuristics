package jss;

import java.util.Arrays;
import java.util.Random;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader; 

public class Problem{
	private int jmlJob;
	private int jmlMesin;
	private int makespan;
	private float meanTardiness;
	private Job[] jobList;
	private Operation[] opList;
	
	public Problem(){
		this.jmlJob=0;
		this.jmlMesin=0;
		this.makespan = Integer.MAX_VALUE;
		this.meanTardiness= Float.MAX_VALUE;
		this.opList = null;
		//this.depList = null;
		this.jobList = null;
	}
	
	public Problem(int jmlJob,int jmlMesin){
		this.jmlJob= jmlJob;
		this.jmlMesin=jmlMesin;
		this.makespan = Integer.MAX_VALUE;
		this.meanTardiness= Float.MAX_VALUE;
		this.opList = new Operation[jmlJob*jmlMesin];
		this.jobList = new Job[jmlJob];
	}
	
	public int getJmlJob(){
		return this.jmlJob;
	}
	
	public int getJmlMesin(){
		return this.jmlMesin;
	}
	
	public int getMakespan(){
		return this.makespan;
	}
	
	public float getMeanTardiness(){
		return this.meanTardiness;
	}
	
	public void setOpList(Operation[] opList){
		this.opList = Arrays.copyOf(opList,opList.length);
	}
	
	public Operation[] getOpList(){
		return this.opList;
	}
	
	public Job[] getJobList(){
		return this.jobList;
	}
	
	public void readProblem(String fileName){
		BufferedReader br;
		String line="";
		String separator=",";
		String[] lineSplit;
		try{
			//inisialisasi buffered reader
			br = new BufferedReader(new FileReader(fileName));
			
			//baca [jmlJob],[jmlMesin]
			line = br.readLine();
			lineSplit = line.split(separator);
			
			this.jmlJob = Integer.parseInt(lineSplit[0].trim()); 
			this.jmlMesin = Integer.parseInt(lineSplit[1].trim());
			
			//inisialisasi data job dan operation
			this.jobList = new Job[this.jmlJob];
			this.opList = new Operation[this.jmlJob*this.jmlMesin];
			//this.depList = new HashMap<Integer,Dependency>();
			
			br.readLine();
			
			//baca processingTime setiap operation yang ada
			int[][] processingTime = new int[this.jmlJob][this.jmlMesin];
			int[] sumPT = new int[this.jmlJob];
			
			for(int i=0;i < this.jmlJob;i++){
				line = br.readLine();
				lineSplit = line.split(separator);
				
				for(int j=0;j < lineSplit.length;j++){
					int pt = Integer.parseInt(lineSplit[j].trim());
					processingTime[i][j] = pt;
					sumPT[i] += pt;
				}
			}
			
			br.readLine();
			
			//baca machine routing(penugasan) setiap operation
			int[][] machineRout = new int[this.jmlJob][this.jmlMesin];
			
			for(int i = 0; i < this.jmlJob;i++){
				line = br.readLine();
				lineSplit = line.split(separator);
				for(int j = 0; j < this.jmlMesin;j++){
					int machineNum = Integer.parseInt(lineSplit[j].trim());
					machineRout[i][j] = machineNum-1;
				}
			}
			
			br.readLine();
			
			//baca [releaseDate,dueDate,weight] tiap job
			for(int jobIdx=0;jobIdx < this.jmlJob;jobIdx++){
				line = br.readLine();
				lineSplit = line.split(separator);
				
				int releaseDate= Integer.parseInt(lineSplit[0].trim());
				int dueDate = Integer.parseInt(lineSplit[1].trim());
				int weight = Integer.parseInt(lineSplit[2].trim());
				
				this.jobList[jobIdx] = new Job(releaseDate,dueDate,weight,this.jmlMesin);
			}
			
			//inisialisasi opList dan array operation pada setiap object job
			int idx=0;
			for(int i=0; i < this.jmlJob; i++){
				int jobIdx = i*this.jmlMesin;
				
				for(int j=0;j < this.jmlMesin;j++){
					int opIdx = jobIdx + j;
					int procTime = processingTime[i][j];
					int machineNum = machineRout[i][j];
					Operation operation = new Operation(i,opIdx,procTime,machineNum);
					
					this.opList[idx++] = operation;
					this.jobList[i].setOperationIdx(j,opIdx);
					
//					if(j==0){
//						dep = new Dependency(-1,opIdx+1);
//					}else if(j==this.jmlMesin-1){
//						dep = new Dependency(opIdx-1,-1);
//					}else{
//						dep = new Dependency(opIdx-1,opIdx+1);
//					}
					
//					this.depList.put(opIdx,dep);
				}
			}
			
		}catch(IOException ex){
			System.out.println("error reading input file");
		}
	}
        
        public String toString(){
            String str = "";
            str += "PROBLEM SUMMARY\n";
            str += "Njob: "+this.jmlJob+"\n";
            str += "Nmachine: "+this.jmlMesin+"\n";
            str += "----------JOB DATA----------\n";
            for(int i = 0 ; i < this.jobList.length;i++){
                str+="Job "+i+"\n";
                str+="Release Date: "+this.jobList[i].getReleaseDate()+"\n";
                str+="Due Date: "+this.jobList[i].getDueDate()+"\n";
                str+="Weight: "+this.jobList[i].getWeight()+"\n";
                int[] arr = this.jobList[i].getOperationIdx();
                for(int j=0;j<arr.length;j++){
                    str+="operation["+j+"]="+arr[j]+"\n";
                }
                str+="\n";
            }
            str +="----------OPERATION DATA----------\n";
            for(int i = 0;i < this.opList.length;i++){
                str+="Operation id: "+this.opList[i].getOperationId()+"\n";
                str+="Job id: "+this.opList[i].getJobId()+"\n";
                str+="Proc time: "+this.opList[i].getProcTime()+"\n";
                str+="Machine No: "+this.opList[i].getMachineNum()+"\n";
                str+="\n";
            }
//            str +="----------DEPENDENCY DATA----------\n";
//            for(int i = 0;i < this.opList.length;i++){
//                Dependency d = this.depList.get(i);
//                str+="Operation id: "+i+"\n";
//                str+="("+d.getOperationPred()+","+d.getOperationSucc()+")\n";
//            }
            str+="\n";
            return str;
        }
}