/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh_scheduling_js;
import gahh.GAHH;
import gahh.Schedule;
import gahh.ScheduleGenerator;
import gahh.Individual;
import gahh.Population;
import javafx.concurrent.Task;
import jss.Problem;
/**
 *
 * @author Irvan Hardyanto
 */
public class GAHHTask extends Task<Schedule>{
    private Problem problem;
    private GAHH gahh;
    
    public GAHHTask(Problem problem,int populationSize,float crossoverProb, float mutationProb,int maxGeneration){
        this.problem =problem;
        this.gahh = new GAHH(new ScheduleGenerator(problem),populationSize,crossoverProb,mutationProb,maxGeneration);
    }
    public void succeeded(){
        super.succeeded();
        updateMessage("sukses!");
    }
    
    @Override
    protected Schedule call() throws Exception {
        long start = System.currentTimeMillis();
        Population population = this.gahh.initPopulation(problem.getJmlJob()*problem.getJmlMesin());
        System.out.println(population.toString());
        this.gahh.evalPopulation(population);
        
        int generation = 1;
        int maxGen = this.gahh.getMaxGeneration();
        while(!gahh.isTerminationConditionMet(population)){
            this.gahh.increaseGeneration();
            System.out.println("-------------------------------------");
            System.out.println("Currentgeneration: "+generation);
            
            Individual best = population.getFittest(0);
            System.out.println("Best fitness is: "+best.getFitness());
            
            System.out.println(population.toString());
            
            System.out.println("Selection + Crossover applied");
            population = gahh.onePointCrossover(population);
            
            System.out.println(population.toString());
            
            System.out.println("Mutation applied");
            population = gahh.mutatePopulation(population);
            System.out.println(population.toString());
            
            System.out.println("evaluating current population");
            this.gahh.evalPopulation(population);
            System.out.println(population.toString());
            
            updateProgress(generation,maxGen);
            generation++;
            System.out.println("---------------------------------------");
        }
        
        Individual bestIndividual = population.getFittest(0);
        System.out.println("best schedule: "+bestIndividual.getSchedule());
        return bestIndividual.getSchedule();
    }
    
}
