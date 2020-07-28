/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh_scheduling_js;
import gahh.Schedule;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import jss.Problem;

/**
 *
 * @author Irvan Hardyanto
 */
public class GAHHService extends Service<Schedule>{

    private Task<Schedule> GAHHTask;
    private int populationSize;
    private int maxGeneration;
    private float crossoverProb;
    private float mutationProb;
    private Problem problem;

    public GAHHService(){
    
    }
    
    public GAHHService(int populationSize, int maxGeneration, float crossoverProb, float mutationProb, Problem problem) {
        this.populationSize = populationSize;
        this.maxGeneration = maxGeneration;
        this.crossoverProb = crossoverProb;
        this.mutationProb = mutationProb;
        this.problem = problem;
    }
    
    public void setParam(int populationSize, int maxGeneration, float crossoverProb, float mutationProb, Problem problem){
        this.populationSize = populationSize;
        this.maxGeneration = maxGeneration;
        this.crossoverProb = crossoverProb;
        this.mutationProb = mutationProb;
        this.problem = problem;
    }
    
    @Override
    protected Task<Schedule> createTask() {
        return new GAHHTask(this.problem, this.populationSize, this.crossoverProb, this.mutationProb, this.maxGeneration);
    }
}
