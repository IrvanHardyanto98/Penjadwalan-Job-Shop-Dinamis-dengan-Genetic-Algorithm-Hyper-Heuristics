package gahh;

import jss.Operation;
import jss.Problem;
import jss.Job;
//import jss.Dependency;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Irvan Hardyanto (2016730070)
 */
public class Schedule {

    private class Node {

        int operationIdx;
        int startTime;
        int finishTime;

        public Node(int operationIdx, int startTime, int finishTime) {
            this.operationIdx = operationIdx;
            this.startTime = startTime;
            this.finishTime = finishTime;
        }
    }
    private Node[][] schedule;
    private Problem problem;
    private int[] freeCells;
    private int[] jobFinishTime;
    private int[] machineFinishTime;

    public Schedule(Problem problem) {
        this.problem = problem;
        this.schedule = null;
        this.jobFinishTime = null;
        this.freeCells = null;
        this.machineFinishTime = null;
    }

    public void generateSchedule(Individual individual) {     
        this.schedule = new Node[this.problem.getJmlMesin()][this.problem.getJmlJob()];
        this.freeCells = new int[this.problem.getJmlMesin()];
        this.jobFinishTime = new int[this.problem.getJmlJob()];
        this.machineFinishTime = new int[this.problem.getJmlMesin()];

        LowLevelHeuristic llh = new LowLevelHeuristic();
        int jobNum = this.problem.getJmlJob();
        int machineNum = this.problem.getJmlMesin();

        int[] currentOperation = new int[jobNum];
        int[] chromosome = individual.getChromosome();

        //inisialisasi
        for (int i = 0; i < currentOperation.length; i++) {
            currentOperation[i] = 0;
        }

        //asumsi jml Operation tiap job = jml Mesin
        for (int i = 0; i < this.freeCells.length; i++) {
            this.freeCells[i] = jobNum;
        }

        //inisialisasi finish time job
        for (int i = 0; i < this.jobFinishTime.length; i++) {
            this.jobFinishTime[i] = 0;
        }
        
        //inisialisasi finish time machine
        for(int i=0;i<this.machineFinishTime.length;i++){
            this.machineFinishTime[i] = 0;
        }

        //cari operation yang schedulable
        ArrayList<Operation> schedulableOperations = new ArrayList<Operation>();
        System.out.println();
        for (int i = 0; i < jobNum * machineNum; i++) {
            int currLLH = individual.getGene(i);

            for (int jobIdx = 0; jobIdx < jobNum; jobIdx++) {
                int opNum = currentOperation[jobIdx];
                if(opNum<machineNum){
                    //System.out.println("job: "+jobIdx+" currOperation: "+opNum);

                    int oprIdx = this.problem.getJobList()[jobIdx].getOperationIdx(opNum);
                    Operation opr = this.problem.getOpList()[oprIdx];
                    //still basic version (static deterministic)
                    //release date is not checked.
                    schedulableOperations.add(opr);
                }
            }
            
            llh.orderOperation(schedulableOperations, this.problem.getJobList(), currLLH);

            Operation currOpr = schedulableOperations.get(0);

            if (!this.put(currOpr)) {
                //System.out.println("Warning! Operation : " + currOpr.getOperationId() + " job: " + currOpr.getJobId() + " IS NOT succesfully put into schedule at machine: " + currOpr.getMachineNum());
            }else{
                //System.out.println("Operation : " + currOpr.getOperationId() + " job: " + currOpr.getJobId() + " succesfully put into schedule at machine: " + currOpr.getMachineNum());
            }
            //System.out.println();
            currentOperation[currOpr.getJobId()]++;
            schedulableOperations.clear();
        }
        individual.setSchedule(this.toString());
        //System.out.println("Makespan: "+this.getMakespan());
        //System.out.println("Mean tardiness: "+this.getMeanTardiness());
    }
private int countOperationStartTime(Operation opr){
    int jobId = opr.getJobId();
    int machineId = opr.getMachineNum();
    if(opr.getOperationId() % this.problem.getJmlMesin()==0){
        return Math.max(this.problem.getJobList()[opr.getJobId()].getReleaseDate(),this.machineFinishTime[machineId]);
    }
    return Math.max(this.jobFinishTime[jobId],this.machineFinishTime[machineId]);
}
    /**
     * Tempatkan operation pada matriks jadwal
     */

    //setiap operation pada job dikerjakan pada machine yang berbeda!
    private boolean put(Operation currOpr) {
        //finish time mencatat waktu selesai setiap operation milik job

        int machineId = currOpr.getMachineNum();
        if (this.freeCells[machineId] == 0) {
            return false;
        } else {
            int jobId = currOpr.getJobId();
            int oprId = currOpr.getOperationId();
               
            //referensi buku baker
            int start = this.countOperationStartTime(currOpr);
            int end = start + currOpr.getProcTime();

            Node node = new Node(currOpr.getOperationId(), start, end);

            int col = this.schedule[machineId].length - this.freeCells[machineId];

            this.schedule[machineId][col] = node;

            this.freeCells[machineId]--;
            this.jobFinishTime[jobId] = end;
            this.machineFinishTime[machineId] = end;
            return true;
        }
    }

    public int getMakespan() {
        int[] tempFinishTime = Arrays.copyOf(this.jobFinishTime, this.jobFinishTime.length);
        Arrays.sort(tempFinishTime);
        return tempFinishTime[tempFinishTime.length - 1];
    }

    public double getMeanTardiness() {
        int totalTardiness = 0;
        for (int i = 0; i < this.jobFinishTime.length; i++) {
            totalTardiness += Math.max(0, (this.jobFinishTime[i] - this.problem.getJobList()[i].getDueDate()));
        }
        return totalTardiness * 1.0 / this.problem.getJmlJob();
    }

    public Node[][] getSchedule(){
        return this.schedule;
    }
    
    @Override
    public String toString() {
        String sch = "";
        for (int i = 0; i < this.schedule.length; i++) {
            for (int j = 0; j < this.schedule[i].length; j++) {
                sch += "(" + this.schedule[i][j].startTime + "," + this.schedule[i][j].operationIdx + "," + this.schedule[i][j].finishTime + ") ";
            }
            sch += "\n";
        }
        sch += "Makespan: "+this.getMakespan()+"\n";
        sch += "Mean Tardiness: "+this.getMeanTardiness()+"\n";
        return sch;
    }
}
