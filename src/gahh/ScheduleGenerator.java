/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javafx.util.Pair;
import jss.Job;
import jss.Operation;
import jss.Problem;

/**
 *
 * @author Irvan Hardyanto (2016730070)
 */
public class ScheduleGenerator {
    private Problem problem;
    private HashMap<Integer,Integer> jobFinishTime;
    private int[] machineFinishTime;
    
    public ScheduleGenerator(Problem problem){
        this.problem = problem;
        this.jobFinishTime = new HashMap<>(this.problem.getJmlJob());
        for (Map.Entry<Integer,Job> entry: this.problem.getJobMap().entrySet()) {
            this.jobFinishTime.put(entry.getKey(),0);
        }
        this.machineFinishTime = new int[this.problem.getJmlMesin()];
    }
    
    public ScheduleGenerator(Problem problem,int[] machineFinishTime){
        this.problem = problem;
        this.jobFinishTime = new HashMap<>(this.problem.getJmlJob());
        for (Map.Entry<Integer,Job> entry: this.problem.getJobMap().entrySet()) {
            this.jobFinishTime.put(entry.getKey(),0);
        }
        this.machineFinishTime = machineFinishTime;
    }
    
    public Schedule generateSchedule(Individual individual) {  
        System.out.println("generate schedule start");
        //this.schedule = new Node[this.problem.getJmlMesin()][this.problem.getJmlJob()];
        int jobNum = this.problem.getJmlJob();
        int machineNum = this.problem.getJmlMesin();
        
        System.out.println("jml job:"+ jobNum);
        System.out.println("machineNum :"+ machineNum);
        
        Schedule schedule = new Schedule(machineNum);

        LowLevelHeuristic llh = new LowLevelHeuristic();        
        
        ArrayList<Integer> chromosome = individual.getChromosome();
        
        this.jobFinishTime = new HashMap<>(this.problem.getJobFinishTime());
        this.machineFinishTime = this.problem.getSetupTime();
        HashMap<Integer,Integer> remainingOperation = new HashMap<>(this.problem.getRemainingOpr());
        
        //cari operation yang schedulable
        ArrayList<Pair<Integer,Operation>> schedulableOperations = new ArrayList<>();
        
        //baris ini mengasumsikan jumlah operation setiap job selalu sama dengan jumlah mesin
        //pada kasus dinamis, hal ini tidak selalu terpenuhi.
        //sehingga baris ini mengalami perubahan
        
        //Pastikan panjang kromosom == jmlOperation yg ada!
        System.out.println("loop outer start");
        for (int i = 0; i < individual.getChromosomeLength(); i++) {
            int currLLH = individual.getGene(i);
            
             //masalah baru... job nya bisa saja tidak urut
            for (Map.Entry<Integer,Job> entry: this.problem.getJobMap().entrySet()) {
                int jobId = entry.getKey();
                int sisaOpr = remainingOperation.get(jobId);
                
                if(sisaOpr > 0){
                    int jmlTotOpr = this.problem.getJob(jobId).getOperationNum();
                    int oprIdx = this.problem.getJob(jobId).getOperationIdx(jmlTotOpr- sisaOpr);
                    
                    Operation opr = this.problem.getOperation(oprIdx);
                    schedulableOperations.add(new Pair(oprIdx,opr));
                }
//                if(opNum<machineNum){
//                    int oprIdx = this.problem.getJob(jobIdx).getOperationIdx(opNum);
//                    Operation opr = this.problem.getOperation(oprIdx);
//                    schedulableOperations.add(opr);
//                }
            }
            System.out.println("sort schedulable start");
            llh.orderOperation(schedulableOperations, this.problem.getJobMap(), currLLH);
            System.out.println("sort schedulable done");
            Pair<Integer,Operation> currOpr = schedulableOperations.get(0);
            if (!this.put(schedule,currOpr)) {
                //System.out.println("Warning! Operation : " + currOpr.getOperationId() + " job: " + currOpr.getJobId() + " IS NOT succesfully put into schedule at machine: " + currOpr.getMachineNum());
            }else{
                //System.out.println("Operation : " + currOpr.getOperationId() + " job: " + currOpr.getJobId() + " succesfully put into schedule at machine: " + currOpr.getMachineNum());
            }
            //System.out.println();
            int tmp = remainingOperation.get(currOpr.getValue().getJobId());
            tmp--;
            remainingOperation.replace(currOpr.getValue().getJobId(),tmp);
            schedulableOperations.clear();
        }
        
        System.out.println("outer loop end");
        //individual.setSchedule(this.toString());
        //System.out.println("Makespan: "+this.getMakespan());
        //System.out.println("Mean tardiness: "+this.getMeanTardiness());
        
        //hitung makespan dan mean tardiness jadwal yang dihasilkan
        System.out.println("countMkspn start");
        int mkspn=this.countMakespan(schedule);
        System.out.println("countMkspn end");
        System.out.println("countMt start");
        double mt = this.countMeanTardiness(schedule);
        System.out.println("countMT end");
        schedule.setMakespan(mkspn);
        schedule.setMeanTardiness(mt);
        
        return schedule;
    }
private int countOperationStartTime(Operation opr){
    int jobId = opr.getJobId();
    int machineId = opr.getMachineNum();
//    if(opr.getOperationId() % this.problem.getJmlMesin()==0){
//        return Math.max(this.problem.getJobList().get(opr.getJobId()).getReleaseDate(),this.machineFinishTime[machineId]);
//    }
    return Math.max(this.jobFinishTime.get(jobId),this.machineFinishTime[machineId]);
}
    /**
     * Tempatkan operation pada matriks jadwal
     */

    //setiap operation pada job dikerjakan pada machine yang berbeda!
    private boolean put(Schedule schedule,Pair<Integer,Operation> currOpr) {
        System.out.println("put opr start");
        Operation opr = currOpr.getValue();
        //finish time mencatat waktu selesai setiap operation milik job
        int machineId = opr.getMachineNum();
        int jobId = opr.getJobId();
        int oprId = currOpr.getKey();
               
            //referensi buku baker
            int start = this.countOperationStartTime(opr);
            int end = start + opr.getProcTime();

            //Node node = new Node(currOpr.getOperationId(), start, end);

            //int col = this.schedule[machineId].length - this.freeCells[machineId];

            //this.schedule[machineId][col] = node;
           
            //this.freeCells[machineId]--;
            this.jobFinishTime.replace(jobId,end);
            this.machineFinishTime[machineId] = end;
            System.out.println("put opr done");
            return schedule.addOperation(oprId,opr, start, end);
        
    }
    
    private int countMakespan(Schedule schedule) {
        ArrayList<Map.Entry<Integer,Integer>> values = new ArrayList<>(this.jobFinishTime.entrySet());
        Collections.sort(values, new Comparator<Map.Entry<Integer,Integer>>(){
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                if(o1.getValue() > o2.getValue()){
                    return -1;
                }else if(o1.getValue() < o2.getValue()){
                    return 1;
                }
                return 0;
            }
        });
        return values.get(0).getValue();
    }
    
    private double countMeanTardiness(Schedule schedule){
        int totalTardiness = 0;
        System.out.println("jobfinishTime.size() = "+this.jobFinishTime.size());
        for (int i = 0; i < this.jobFinishTime.size(); i++) {
            System.out.println("i = "+i);
            totalTardiness += Math.max(0, (this.jobFinishTime.get(i) - this.problem.getJob(i).getDueDate()));
            System.out.println("done");
        }
        return totalTardiness * 1.0 / this.problem.getJmlJob();
    }
}