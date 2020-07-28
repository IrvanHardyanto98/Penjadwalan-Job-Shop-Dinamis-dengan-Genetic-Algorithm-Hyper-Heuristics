/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import jss.Operation;
import jss.Problem;

/**
 *
 * @author Irvan Hardyanto (2016730070)
 */
public class ScheduleGenerator {
    private Problem problem;
    private ArrayList<Integer> jobFinishTime;
    private int[] machineFinishTime;
    
    public ScheduleGenerator(Problem problem){
        this.problem = problem;
        this.jobFinishTime = new ArrayList<>(this.problem.getJmlJob());
        for (int i = 0; i < this.problem.getJmlJob(); i++) {
            this.jobFinishTime.add(i,0);
        }
        this.machineFinishTime = new int[this.problem.getJmlMesin()];
    }
    
    public Schedule generateSchedule(Individual individual) {  
        System.out.println("generate schedule start");
        //this.schedule = new Node[this.problem.getJmlMesin()][this.problem.getJmlJob()];
        int jobNum = this.problem.getJmlJob();
        int machineNum = this.problem.getJmlMesin();
        
        System.out.println("jml job:"+ jobNum);
        System.out.println("machineNum :"+ machineNum);
        
        Schedule schedule = new Schedule(machineNum);
//        this.jobFinishTime = new ArrayList<>(jobNum);
//        this.machineFinishTime = new int[machineNum];

        LowLevelHeuristic llh = new LowLevelHeuristic();        
        
        System.out.println("copy aL start");
        ArrayList<Integer> remainingOperation = new ArrayList<>(this.problem.getRemainingOpr());
        //Collections.copy(remainingOperation, this.problem.getRemainingOpr());
        System.out.println("copy aL done");
        ArrayList<Integer> chromosome = individual.getChromosome();

        //inisialisasi
//        for (int i = 0; i < jobNum; i++) {
//            currentOperation[i] = 0;
//        }

        //inisialisasi finish time job
        for (int i = 0; i < jobNum; i++) {
            this.jobFinishTime.set(i,0);
        }
        System.out.println("init job finish time done");
        //inisialisasi finish time machine
        for(int i=0;i<machineNum;i++){
            this.machineFinishTime[i] = 0;
        }
        System.out.println("init machine finish time done");
        //cari operation yang schedulable
        ArrayList<Operation> schedulableOperations = new ArrayList<>();
        System.out.println();
        
        //baris ini mengasumsikan jumlah operation setiap job selalu sama dengan jumlah mesin
        //pada kasus dinamis, hal ini tidak selalu terpenuhi.
        //sehingga baris ini mengalami perubahan
        
        //Pastikan panjang kromosom == jmlOperation yg ada!
        System.out.println("loop outer start");
        for (int i = 0; i < individual.getChromosomeLength(); i++) {
            int currLLH = individual.getGene(i);
            
             //masalah baru... job nya bisa saja tidak urut
            for (int jobIdx = 0; jobIdx < jobNum; jobIdx++) {
                int sisaOpr = remainingOperation.get(jobIdx);
                
                if(sisaOpr > 0){
                    int oprIdx = this.problem.getJob(jobIdx).getOperationIdx(machineNum - sisaOpr);
                    Operation opr = this.problem.getOperation(oprIdx);
                    schedulableOperations.add(opr);
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
            
            Operation currOpr = schedulableOperations.get(0);

            if (!this.put(schedule,currOpr)) {
                //System.out.println("Warning! Operation : " + currOpr.getOperationId() + " job: " + currOpr.getJobId() + " IS NOT succesfully put into schedule at machine: " + currOpr.getMachineNum());
            }else{
                //System.out.println("Operation : " + currOpr.getOperationId() + " job: " + currOpr.getJobId() + " succesfully put into schedule at machine: " + currOpr.getMachineNum());
            }
            //System.out.println();
            int tmp = remainingOperation.get(currOpr.getJobId());
            tmp--;
            remainingOperation.set(currOpr.getJobId(),tmp);
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
    private boolean put(Schedule schedule,Operation currOpr) {
        System.out.println("put opr start");
        //finish time mencatat waktu selesai setiap operation milik job
        int machineId = currOpr.getMachineNum();
        int jobId = currOpr.getJobId();
        int oprId = currOpr.getOperationId();
               
            //referensi buku baker
            int start = this.countOperationStartTime(currOpr);
            int end = start + currOpr.getProcTime();

            //Node node = new Node(currOpr.getOperationId(), start, end);

            //int col = this.schedule[machineId].length - this.freeCells[machineId];

            //this.schedule[machineId][col] = node;
           
            //this.freeCells[machineId]--;
            this.jobFinishTime.set(jobId,end);
            this.machineFinishTime[machineId] = end;
            System.out.println("put opr done");
            return schedule.addOperation(currOpr, start, end);
        
    }
    
    private int countMakespan(Schedule schedule) {
        Collections.sort(this.jobFinishTime);
        return this.jobFinishTime.get(this.jobFinishTime.size()-1);
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