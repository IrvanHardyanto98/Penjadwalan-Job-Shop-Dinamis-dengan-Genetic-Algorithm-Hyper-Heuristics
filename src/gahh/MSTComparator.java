/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh;

import java.util.Comparator;
import java.util.HashMap;
import javafx.util.Pair;
import jss.Job;
import jss.Operation;

/**
 *
 * @author Irvan Hardyanto
 * NPM: 2016730070
 */
public class MSTComparator implements Comparator<Pair<Integer,Operation>>{

     private HashMap<Integer,Job>job;
     private HashMap<Integer,Operation>operation;
     private HashMap<Integer,Integer> jobFinishTime;
     private int[] machineFinishTime;

    public MSTComparator(HashMap<Integer, Job> job, HashMap<Integer, Operation> operation, HashMap<Integer, Integer> jobFinishTime, int[] machineFinishTime) {
        this.job = job;
        this.operation = operation;
        this.jobFinishTime = jobFinishTime;
        this.machineFinishTime = machineFinishTime;
    }
     
     
    @Override
    public int compare(Pair<Integer, Operation> o1, Pair<Integer, Operation> o2) {
         int o1OprNum=this.job.get(o1.getValue().getJobId()).getOperationNum();
        int o2OprNum=this.job.get(o2.getValue().getJobId()).getOperationNum();
         
        int o1remPt=0;
        int o2remPt=0;
        
        for(int i=o1.getValue().getOprOrderNum();i<o1OprNum;i++){
            int idx = this.job.get(o1.getValue().getJobId()).getOperationIdx(i);
            o1remPt+= this.operation.get(new Integer(idx)).getProcTime();
        }
        
        for(int i=o2.getValue().getOprOrderNum();i<o2OprNum;i++){
            int idx = this.job.get(o2.getValue().getJobId()).getOperationIdx(i);
            o2remPt+= this.operation.get(new Integer(idx)).getProcTime();
        }
        
        //slack time = d-t-p
        //d menyatakan due date
        //t menyatakan waktu saat ini
        //p menyatakan sisa waktu pemrosesan job yang dimiliki operation tertetntu
        int o1StartTime = Math.max(this.jobFinishTime.get(o1.getValue().getJobId()),this.machineFinishTime[o1.getValue().getMachineNum()]);
        int o1SlackTime = this.job.get(o1.getValue().getJobId()).getDueDate()-o1StartTime-o1remPt;
        
        int o2StartTime = Math.max(this.jobFinishTime.get(o2.getValue().getJobId()),this.machineFinishTime[o2.getValue().getMachineNum()]);
        int o2SlackTime = this.job.get(o2.getValue().getJobId()).getDueDate()-o2StartTime-o2remPt;
        
        if(o1SlackTime<o2SlackTime){
            return -1;
        }else if(o1SlackTime>o2SlackTime){
            return 1;
        }
        return 0;
    }
    
}
