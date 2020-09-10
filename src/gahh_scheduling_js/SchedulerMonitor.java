/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh_scheduling_js;

import gahh.GAHH;
import gahh.Individual;
import gahh.Population;
import gahh.Schedule;
import gahh.ScheduleGenerator;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import jss.Problem;

/**
 *
 * @author Irvan Hardyanto NPM: 2016730070
 */
public class SchedulerMonitor {

    private volatile int threadId;
    private volatile Problem problem;
    private volatile GAHH gahh;
    private ProgressBar progress;
    private Label labelDuration;

    public SchedulerMonitor(Problem problem,ProgressBar progress, Label labelDuration, int populationSize, float crossoverProb, float mutationProb, int maxGeneration) {
        this.threadId = 1;
        this.problem = problem;
        this.gahh = new GAHH(new ScheduleGenerator(problem), populationSize, crossoverProb, mutationProb, maxGeneration);
        this.progress = progress;
        this.labelDuration = labelDuration;
    }
    
    public void updateProgress(double progress){
        this.progress.setProgress(progress);
    }
    
    public void updateLabel(String message){
        this.labelDuration.setText(message);
    }

    public Schedule getSchedule() {
        return this.problem.getBestSchedule();
    }

    public void setSchedule(Schedule schedule) {
        this.problem.setBestSchedule(schedule);
    }
    
    public void combineSchedule(int time,Schedule schedule){
        //jangan lupa update makespan dan mean tardiness
        System.out.println("best Schedule: "+this.problem.getBestSchedule());
        this.problem.getBestSchedule().combine(time, schedule);
        System.out.println("FINAL SCHEDULE: "+this.problem.getBestSchedule());
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }
    
    public void incrementThreadId(){
        this.threadId++;
    }

    public GAHH getGAHHInstance() {
        return this.gahh;
    }
    
    public void setGAHHInstance(GAHH gahh){
        this.gahh = gahh;
    }

}
