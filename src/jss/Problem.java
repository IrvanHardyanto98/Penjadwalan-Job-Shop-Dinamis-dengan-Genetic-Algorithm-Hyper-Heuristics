package jss;

import gahh.Schedule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Problem{
	private int jmlJob;
	private int jmlMesin;
	private int makespan;
	private float meanTardiness;
        private Schedule bestSchedule;
        private int[] setupTime;
	private HashMap<Integer,Job> jobMap;
	private HashMap<Integer,Operation> opMap;
	private HashMap<Integer,Integer> remainingOpr;
        private HashMap<Integer,Integer> jobFinishTime;
        
	public Problem(){
		this.jmlJob=0;
		this.jmlMesin=0;
		this.makespan = Integer.MAX_VALUE;
		this.meanTardiness= Float.MAX_VALUE;
		this.opMap = new HashMap<>();
		this.jobMap = new HashMap<>();
                this.remainingOpr = new HashMap<>();
                this.setupTime=null;
                this.bestSchedule = null;
                this.jobFinishTime= new HashMap<>();
	}
	
	public Problem(int jmlJob,int jmlMesin){
		this.jmlJob= jmlJob;
		this.jmlMesin=jmlMesin;
		this.makespan = Integer.MAX_VALUE;
		this.meanTardiness= Float.MAX_VALUE;
		this.opMap = new HashMap<>();
		this.jobMap = new HashMap<>();
	}
        
        public void setBestSchedule(Schedule schedule){
            this.bestSchedule = schedule;
        }
        
        public Schedule getBestSchedule(){
            return this.bestSchedule;
        }
	
        public Operation getOperation(int oprIdx){
            return this.opMap.get(oprIdx);
        }
        
        public void addOperation(int oprId,Operation operation){
            this.opMap.put(oprId, operation);
        }
        
        public Job getJob(int jobId){
            return this.jobMap.get(jobId);
        }
        
        public void addJob(int jobId,Job job){
            this.jobMap.put(jobId,job);
        }
        
        public Integer getRemainingOpr(int jobId){
            return this.remainingOpr.get(jobId);
        }
        
        public HashMap<Integer,Integer> getRemainingOpr(){
            return this.remainingOpr;
        }
        
        public void setRemainingOpr(int jobId,int remainingOpr){
            this.remainingOpr.put(jobId, remainingOpr);
        }
        
        public void setJmlJob(int jmlJob){
            this.jmlJob = jmlJob;
        }
                    
	public int getJmlJob(){
		return this.jmlJob;
	}
        
        public void setJmlMesin(int jmlMesin){
            this.jmlMesin= jmlMesin;
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
        
        public void setSetupTime(int[] setupTime){
            this.setupTime = setupTime;
        }
        
        public int getSetupTime(int machineId){
            return this.setupTime[machineId];
        }
        
        public int[] getSetupTime(){
            return this.setupTime;
        }
        
        public HashMap<Integer,Integer> getJobFinishTime(){
            return this.jobFinishTime;
        }
        
        public void setJobFinishTime(int jobId,int finishTime){
            if(this.jobFinishTime.containsKey(jobId)){
                this.jobFinishTime.replace(jobId,finishTime);
            }else{
                this.jobFinishTime.put(jobId,finishTime);
            }
        }
        
        public int getJobFinishTime(int jobId){
            return this.jobFinishTime.get(jobId);
        }
	
        //TODO!!!
        //Tambahin jobFinishTime setiapJob yang belum beres
        public Problem reschedule(int time,String[][] data){            
            HashSet<Integer> tmp = this.bestSchedule.getRemainingOperation(time);
            System.out.println("REmaining operations is: "+tmp.toString());
            Problem subProblem = new Problem();
            int[] setupTime=this.bestSchedule.getSetupTime();
            subProblem.setSetupTime(setupTime);
            //tentukan job yang sudah selesai dan yang belum selesai
            
            //kalau kodenya seperti ini artinya iterasi semua job, dari awal sampai akhir,
            //ada kemungkinan job yang sudah beres semua operasi nya di iterasi.
            for (Map.Entry<Integer,Job> entry: this.jobMap.entrySet()) {
                Job oldJob = entry.getValue();
                Job newJob = new Job(this.countReleaseDate(oldJob, time), oldJob.getDueDate(), oldJob.getOperationNum());           
                boolean finished = true;
                int remOpr = oldJob.getOperationNum();
                int newReleaseDate= 0;
                for (int j = 0; j < oldJob.getOperationNum(); j++) {
                    if(tmp.contains(oldJob.getOperationIdx(j))){
                        finished = false;
                        //operation ini belum selesai, perbarui atribut remainingOperation
                        //misalkan sebuah job punya empat operasi, kalau 
                        //operasi kedua ada di daftar operasi yang belum selesai, maka operasi ketiga dan keempat
                        //pasti ada di daftar tersebut.
                        Operation oldOpr = this.opMap.get(oldJob.getOperationIdx(j));
                        int jobId = oldOpr.getJobId();
                        int oprOrd = oldOpr.getOprOrderNum();
                        int machNum = oldOpr.getMachineNum();
                        int procTime = oldOpr.getProcTime();
                        subProblem.addOperation(oldJob.getOperationIdx(j),new Operation(jobId,oprOrd,procTime, machNum));
                        newJob.setOperationIdx(j,oldJob.getOperationIdx(j));
                    }else{
                        //operation ini sudah selesai, hapus dari daftar operation
                        subProblem.setJobFinishTime(entry.getKey(), this.bestSchedule.getFinishTime(oldJob.getOperationIdx(j))); 
                        newJob.setOperationIdx(j,-1);
                        remOpr--;
                    }
                }
                if(!finished){
                    subProblem.addJob(entry.getKey(), newJob);
                }
                subProblem.setRemainingOpr(entry.getKey(), remOpr);
            }
            
            //tambahkan job yang baru ditambahkan pada waktu time ke problem dan sub problem
            for(int i = 0 ; i < data[0].length;i++){
                int releaseDate = Integer.parseInt(data[0][i]);
                int dueDate = Integer.parseInt(data[1][i]);
                String[] splitProcTimes = data[2][i].split(",");
                String[] splitMachineAssignment = data[3][i].split(",");
            
                Job newJob= new Job(releaseDate,dueDate,splitProcTimes.length);
                
                
                int jobId = this.jmlJob*this.jmlMesin;
                for(int j = 0 ;j < splitProcTimes.length;j++){
                    int oprId = jobId+j;
                    int procTime = Integer.parseInt(splitProcTimes[j]);
                    int machineId = Integer.parseInt(splitMachineAssignment[j])-1;
                    
                    Operation newOperation = new Operation(jobId, oprId, procTime, machineId);
                    
                    subProblem.addOperation(oprId,newOperation);
                    this.addOperation(oprId, newOperation);
                    
                    newJob.setOperationIdx(j, oprId);
                }
                this.jmlJob++;
                
                subProblem.addJob(jobId, newJob);
                subProblem.setRemainingOpr(jobId, splitProcTimes.length);
                subProblem.setJobFinishTime(jobId,0);
                this.addJob(jobId, newJob);
            }
            System.out.println("jobMap: "+subProblem.getJobMap().toString());
            System.out.println("opMap: "+subProblem.getOpMap().toString()+" SIZE: "+subProblem.getOpMap().size());
            subProblem.setJmlJob(this.jmlJob);
            subProblem.setJmlMesin(this.jmlMesin);
            return subProblem;
        }
        
        private int countReleaseDate(Job job,int time){
            int releaseDate = 0;
            for(int i=0;i<job.getOperationNum();i++){
                int oprId = job.getOperationIdx(i);
                if(this.bestSchedule.getStartTime(oprId)<time){
                    int max =Math.max(releaseDate,this.bestSchedule.getFinishTime(oprId));
                    releaseDate = max;
                }
            }
            return releaseDate;
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
                        this.setupTime=new int[this.jmlMesin];
			this.jobMap = new HashMap<>(this.jmlJob);
			this.opMap = new HashMap<>(this.jmlJob*this.jmlMesin);
                        this.remainingOpr = new HashMap<>(this.jmlJob);
                        this.jobFinishTime=new HashMap<>(this.jmlJob);
			
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
                                this.remainingOpr.put(jobIdx,this.jmlMesin);
                                this.jobFinishTime.put(jobIdx,0);
			}
			
			//inisialisasi opList dan array operation pada setiap object job
			int idx=0;
			for(int i=0; i < this.jmlJob; i++){
				int jobIdx = i*this.jmlMesin;
				
				for(int j=0;j < this.jmlMesin;j++){
					int opIdx = jobIdx + j;
					int procTime = processingTime[i][j];
					int machineNum = machineRout[i][j];
					Operation operation = new Operation(i,j,procTime,machineNum);
					
					this.opMap.put(opIdx, operation);
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
                str+="Operation id: "+this.opMap.get(i).getOprOrderNum()+"\n";
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