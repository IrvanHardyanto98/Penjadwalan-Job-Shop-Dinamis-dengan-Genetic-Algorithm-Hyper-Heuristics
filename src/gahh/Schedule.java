package gahh;

import jss.Operation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import javafx.util.Pair;

/**
 * @author Irvan Hardyanto (2016730070)
 */
public class Schedule {

    public class Node {

        int operationId;
        int startTime;
        int finishTime;

        public Node(int operationId,int startTime, int finishTime) {
            this.startTime = startTime;
            this.finishTime = finishTime;
            this.operationId = operationId;
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
    private HashMap<Integer,Pair<Integer,Integer>> address;
    private int[] setupTime;
    private int makespan;
    private double meanTardiness;

    public Schedule(int jmlMesin) {
        this.schedule = new ArrayList[jmlMesin];
        for (int i = 0; i < jmlMesin; i++) {
            this.schedule[i]=new ArrayList<>();
        }
        this.makespan = 0;
        this.meanTardiness = 0.0;
        this.setupTime = null;
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
    
    public int getOperationNum(int machineId){
        return this.schedule[machineId].size();
    }
    
    public void setAddress(int oprId,Pair<Integer,Integer> addr){
        this.address.put(oprId,addr);
    }
    
    private void resetAddress(){
        this.address.clear();
    }
    
    public Pair<Integer,Integer> getAddress(int oprId){
        return this.address.get(oprId);
    }
    
    public int getFinishTime(int oprId){
        Pair<Integer,Integer> tmp = this.getAddress(oprId);
        return this.schedule[tmp.getKey()].get(tmp.getValue()).finishTime;
    }
    
    //TODO!! update address
    public void combine(int time,Schedule otherSchedule){
        //gabungkan jadwal ini dengan jadwal lain;
        if(otherSchedule.getJmlMesin() != this.getJmlMesin()){
            System.out.println("Error! jumlah mesin tidak sama");
            return;
        }else if(this.schedule==null){
            this.schedule = otherSchedule.schedule;
            this.makespan = otherSchedule.makespan;
            this.meanTardiness = otherSchedule.meanTardiness;
        }else{
        //yang berubah itu THIS
        ArrayList<Node>[] newSchedule = new ArrayList[this.schedule.length];
        
        int idx = 0;
        //update mapping finish time
            this.resetAddress();
        
        for(int i = 0 ; i < newSchedule.length; i++){
            newSchedule[i] = new ArrayList<>();
            Node n = this.schedule[i].get(idx);
            
            while(n.startTime < time){
                Pair<Integer,Integer> newAdress = new Pair(i,idx);
                newSchedule[i].add(new Node(n.operationId,n.startTime,n.finishTime));
                this.setAddress(n.operationId, newAdress);
                idx++;
                n = this.schedule[i].get(idx);
            }
            //iterasi satu satu
            for (int j = 0; j < otherSchedule.schedule[i].size(); j++) {
                Pair<Integer,Integer> tmpAddress = new Pair(i,newSchedule[i].size());
                Node node = otherSchedule.schedule[i].get(j);
                newSchedule[i].add(new Node(node.operationId,node.startTime,node.finishTime));
                this.setAddress(node.operationId, tmpAddress);
            }
           //newSchedule[i].addAll(otherSchedule.schedule[i]);
        }
            this.schedule=newSchedule;
        }
    }
    
    public HashSet<Integer> getRemainingOperation(int time){
        if(time >= this.makespan){
            return null;
        }
        this.setupTime = new int[this.getJmlMesin()];
        //indeks paling belakang untuk mencatat setup time mesin
        HashSet<Integer> remainingOperations = new HashSet<>();
        
        for(int i = 0;i<this.schedule.length;i ++){
            int tempEnd=0;
            boolean firstOpr=true;//HERE
            for(int j = 0 ; j < this.schedule[i].size(); j++){
                Node n = this.schedule[i].get(j);
                if(n.startTime>=time){
                    //tidak ada setupTime
                    if(firstOpr){
                        this.setupTime[i]=0;
                        firstOpr = false;
                    }
                    remainingOperations.add(n.operationId);
                    //hapus operasi lama
                }else if(n.startTime<time&&time<n.finishTime){
                    //hitung setupTime mesinIni
                    if(firstOpr){
                        this.setupTime[i]=Math.max(tempEnd, time);
                        //remainingOperations[remainingOperations.length-1].add(Math.max(tempEnd, time));
                        firstOpr= false;
                    }
                    //remainingOperations.add(n.operationIdx);
                }else if(n.startTime < time){
                    if(n.finishTime > tempEnd){
                        tempEnd = n.finishTime;
                    }
                }
            }
        }
        return remainingOperations;
    }
    
    //PENTING: method ini hanya boleh dipanggil setelah getRemainingOperation
    public int[] getSetupTime(){
        return this.setupTime;
    }
    
    @Override
    public String toString() {
        String sch = "";
        for (int i = 0; i < this.schedule.length; i++) {
            for (int j = 0; j < this.schedule[i].size(); j++) {
                sch += "(" + this.schedule[i].get(j).startTime + "," + this.schedule[i].get(j).operationId + "," + this.schedule[i].get(j).finishTime + ") ";
            }
            sch += "\n";
        }
        sch += "Makespan: "+this.getMakespan()+"\n";
        sch += "Mean Tardiness: "+this.getMeanTardiness()+"\n";
        return sch;
    }
    
    public boolean addOperation(int oprId,Operation opr,int start,int end){
        int machineId = opr.getMachineNum();
        int col = this.schedule[machineId].size();
        
        Node node = new Node(oprId,start, end);
        boolean ret = this.schedule[machineId].add(node);
        
        this.setAddress(oprId, new Pair(machineId,col));
        return ret;
    }
}
