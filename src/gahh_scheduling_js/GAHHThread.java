/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh_scheduling_js;

import gahh.GAHH;
import gahh.Individual;
import gahh.Population;
import javafx.application.Platform;

/**
 *
 * @author Irvan Hardyanto
 */
public class GAHHThread implements Runnable {

    private SchedulerMonitor monitor;
    private int id;
    private int time;

    public GAHHThread(SchedulerMonitor monitor, int id,int time) {
        this.monitor = monitor;
        this.id = id;
        this.time = time;
    }

    @Override
    public void run() {
        synchronized (this.monitor) {
            try {
                while (this.monitor.getThreadId() != id) {
                    this.monitor.wait();
                }
                Platform.runLater(new Runnable(){
                    public void run(){
                        monitor.updateLabel("GAHH process started");
                    }
                });
                long start = System.currentTimeMillis();
                GAHH gahh = this.monitor.getGAHHInstance();
                Population population = gahh.initPopulation();
                //System.out.println(population.toString());
                gahh.evalPopulation(population);
                int generation =0;
                while (!gahh.isTerminationConditionMet(population)) {
                    gahh.increaseGeneration();
                    generation++;
                    Platform.runLater(new Runnable(){
                        public void run(){
                            updateProgressBar(gahh.getMaxGeneration(),gahh.getCurrentGeneration());
                            monitor.updateLabel("GAHH process running, current generation is: "+gahh.getCurrentGeneration());
                        }
                    });
//                    Individual best = population.getFittest(0);
//                    System.out.println("Best fitness is: " + best.getFitness());

                    //System.out.println(population.toString());

                    //System.out.println("Selection + Crossover applied");
                    population = gahh.uniformCrossoverPopulation(population);

                    //System.out.println(population.toString());

                    //System.out.println("Mutation applied");
                    population = gahh.mutatePopulation(population);
                    //System.out.println(population.toString());

                    //System.out.println("evaluating current population");
                    gahh.evalPopulation(population);
                    //System.out.println();
                    //System.out.println(population.toString());                
                }
                long end = System.currentTimeMillis();
                Individual bestIndividual = population.getFittest(0);
                System.out.println("best schedule: "+bestIndividual.getSchedule());
                if(this.id==1){
                    this.monitor.setSchedule(bestIndividual.getSchedule());
                }else{
                    //gabung jadwal
                    this.monitor.combineSchedule(this.time, bestIndividual.getSchedule());
                }
                Platform.runLater(new Runnable(){
                    public void run(){
                        monitor.updateLabel("GAHH process finished, total time ellapsed: "+(end-start)/1000+" seconds");
                    }
                });
                ControllerMediator.getInstance().getGanttChart(this.monitor.getSchedule(), this.monitor.getProblem());
                this.monitor.incrementThreadId();
                this.monitor.notifyAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updateProgressBar(int totalWork,int currentWork){ 
        this.monitor.updateProgress((currentWork*1.0)/(totalWork*1.0));
    }
}
