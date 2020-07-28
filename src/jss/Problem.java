package jss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Problem{
	private int jmlJob;
	private int jmlMesin;
	private int makespan;
	private float meanTardiness;
	private HashMap<Integer,Job> jobMap;
	private HashMap<Integer,Operation> opMap;
	private ArrayList<Integer> remainingOpr;
        
	public Problem(){
		this.jmlJob=0;
		this.jmlMesin=0;
		this.makespan = Integer.MAX_VALUE;
		this.meanTardiness= Float.MAX_VALUE;
		this.opMap = null;
		this.jobMap = null;
                this.remainingOpr = null;
	}
	
	public Problem(int jmlJob,int jmlMesin){
		this.jmlJob= jmlJob;
		this.jmlMesin=jmlMesin;
		this.makespan = Integer.MAX_VALUE;
		this.meanTardiness= Float.MAX_VALUE;
		this.opMap = new HashMap<>();
		this.jobMap = new HashMap<>();
	}
	
        public Operation getOperation(int oprIdx){
            return this.opMap.get(oprIdx);
        }
        
        public Job getJob(int jobId){
            return this.jobMap.get(jobId);
        }
        
        public Integer getRemainingOpr(int jobId){
            return this.remainingOpr.get(jobId);
        }
        
        public ArrayList<Integer> getRemainingOpr(){
            return this.remainingOpr;
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
	
	public void setOpMap(HashMap<Integer,Operation> opMap){
		this.opMap.putAll(opMap);
	}
	
	public HashMap<Integer,Operation> getOpMap(){
		return this.opMap;
	}
	
	public HashMap<Integer,Job> getJobMap(){
		return this.jobMap;
	}
	
        //method ini hanya dipanggil sekali di awal !
	public void readProblem(String fileName){
            System.out.println("readProblem start");
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
			                 System.out.println("jmlJob: "+this.jmlJob);
                                         System.out.println("jmlMesin: "+this.jmlMesin);
			//inisialisasi data job dan operation
			this.jobMap = new HashMap<>(this.jmlJob);
			this.opMap = new HashMap<>(this.jmlJob*this.jmlMesin);
                        this.remainingOpr = new ArrayList<>(this.jmlJob);
			
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
				
				this.jobMap.put(jobIdx,new Job(releaseDate,dueDate,this.jmlMesin));
                                this.remainingOpr.add(jobIdx,this.jmlMesin);
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
					
					this.opMap.put(idx++, operation);
                                        //this.opList[idx++] = operation;
                                        
					this.jobMap.get(i).setOperationIdx(j,opIdx);
					
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
                System.out.println("readProblem end");
	}
        
        public String toString(){
            String str = "";
            str += "PROBLEM SUMMARY\n";
            str += "Njob: "+this.jmlJob+"\n";
            str += "Nmachine: "+this.jmlMesin+"\n";
            str += "----------JOB DATA----------\n";
            for(int i = 0 ; i < this.jobMap.size();i++){
                str+="Job "+i+"\n";
                str+="Release Date: "+this.jobMap.get(i).getReleaseDate()+"\n";
                str+="Due Date: "+this.jobMap.get(i).getDueDate()+"\n";
//                str+="Weight: "+this.jobMap.get(i).getWeight()+"\n";
                int[] arr = this.jobMap.get(i).getOperationIdx();
                for(int j=0;j<arr.length;j++){
                    str+="operation["+j+"]="+arr[j]+"\n";
                }
                str+="\n";
            }
            str +="----------OPERATION DATA----------\n";
            for(int i = 0;i < this.opMap.size();i++){
                str+="Operation id: "+this.opMap.get(i).getOperationId()+"\n";
                str+="Job id: "+this.opMap.get(i).getJobId()+"\n";
                str+="Proc time: "+this.opMap.get(i).getProcTime()+"\n";
                str+="Machine No: "+this.opMap.get(i).getMachineNum()+"\n";
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