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
import jss.Problem;

/**
 *
 * @author Irvan Hardyanto NPM: 2016730070
 */
public class SchedulerMonitor {

    private volatile int threadId;
    private Problem problem;
    private GAHH gahh;

    public SchedulerMonitor(Problem problem, int populationSize, float crossoverProb, float mutationProb, int maxGeneration) {
        this.threadId = 1;
        this.problem = problem;
        this.gahh = new GAHH(new ScheduleGenerator(problem), populationSize, crossoverProb, mutationProb, maxGeneration);
    }

    public void createInitialSchedule() {

    }

    public void reschedule(int time, String[][] data) {

    }

    public Schedule getSchedule() {
        return this.problem.getBestSchedule();
    }

    public void setSchedule(Schedule schedule) {
        this.problem.setBestSchedule(schedule);
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

}

//thread untuk schedule dan reschedule
class GAHHThread implements Runnable {

    private SchedulerMonitor monitor;
    private int id;

    public GAHHThread(SchedulerMonitor monitor, int id) {
        this.monitor = monitor;
        this.id = id;
    }

    @Override
    public void run() {
        synchronized (this.monitor) {
            try {
                while (this.monitor.getThreadId() != id) {
                    this.monitor.wait();
                }
                long start = System.currentTimeMillis();
                GAHH gahh = this.monitor.getGAHHInstance();
                Population population = gahh.initPopulation(this.monitor.getProblem().getOpMap().size());
                //System.out.println(population.toString());
                gahh.evalPopulation(population);
                while (!gahh.isTerminationConditionMet(population)) {
                    gahh.increaseGeneration();
                    //System.out.println("-------------------------------------");
                    //System.out.println("Currentgeneration: " + generation);

                    Individual best = population.getFittest(0);
                    System.out.println("Best fitness is: " + best.getFitness());

                    System.out.println(population.toString());

                    System.out.println("Selection + Crossover applied");
                    population = gahh.onePointCrossover(population);

                    System.out.println(population.toString());

                    System.out.println("Mutation applied");
                    population = gahh.mutatePopulation(population);
                    System.out.println(population.toString());

                    System.out.println("evaluating current population");
                    gahh.evalPopulation(population);
                    System.out.println(population.toString());

                    //updateProgress(generation, maxGen);
                    //generation++;
                    //System.out.println("---------------------------------------");                    
                }
                Individual bestIndividual = population.getFittest(0);
                //System.out.println("best schedule: "+bestIndividual.getSchedule());
                if(this.id==1){
                    this.monitor.setSchedule(bestIndividual.getSchedule());
                }else{
                    //gabung jadwal
                }
                this.monitor.incrementThreadId();
                this.monitor.notifyAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
