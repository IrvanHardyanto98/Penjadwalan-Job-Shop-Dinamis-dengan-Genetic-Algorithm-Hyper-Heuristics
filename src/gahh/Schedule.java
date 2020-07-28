package gahh;

import jss.Operation;
import java.util.ArrayList;

/**
 * @author Irvan Hardyanto (2016730070)
 */
public class Schedule {

    public class Node {

        int operationIdx;
        int startTime;
        int finishTime;

        public Node(int operationIdx, int startTime, int finishTime) {
            this.operationIdx = operationIdx;
            this.startTime = startTime;
            this.finishTime = finishTime;
        }
        
        public int getOperationIdx(){
            return this.operationIdx;
        }
        
        public int getStartTime(){
            return this.startTime;
        }
        
        public int getFinishTime(){
            return this.finishTime;
        }
        
        public int getProcessingTime(){
            return this.finishTime-this.startTime;
        }
    }
    private ArrayList<Node>[] schedule;
    private int makespan;
    private double meanTardiness;

    public Schedule(int jmlMesin) {
        this.schedule = new ArrayList[jmlMesin];
        for(int i = 0 ; i < this.schedule.length; i++){
            this.schedule[i] = new ArrayList<>();
        }
        this.makespan = 0;
        this.meanTardiness = 0.0;
    }

    public void setMeanTardiness(double meanTardiness){
        this.meanTardiness = meanTardiness;
    }
    
    public double getMeanTardiness() {
        return this.meanTardiness;
    }

    public void setMakespan(int makespan) {
        this.makespan = makespan;
    }
    
    public int getMakespan() {
        return makespan;
    }
    
    public int getJmlMesin(){
        return this.schedule.length;
    }
    
    public ArrayList<Node>[] getSchedule(){
        return this.schedule;
    }
    
    @Override
    public String toString() {
        String sch = "";
        for (int i = 0; i < this.schedule.length; i++) {
            for (int j = 0; j < this.schedule[i].size(); j++) {
                sch += "(" + this.schedule[i].get(j).startTime + "," + this.schedule[i].get(j).operationIdx + "," + this.schedule[i].get(j).finishTime + ") ";
            }
            sch += "\n";
        }
        sch += "Makespan: "+this.getMakespan()+"\n";
        sch += "Mean Tardiness: "+this.getMeanTardiness()+"\n";
        return sch;
    }
    
    public boolean addOperation(Operation opr,int start,int end){
        int machineId = opr.getMachineNum();
        Node node = new Node(opr.getOperationId(), start, end);
        return this.schedule[machineId].add(node);
    }
}
