/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh_scheduling_js;

import gahh.GAHH;
import gahh.ScheduleGenerator;
import jss.Problem;

/**
 *
 * @author Irvan Hardyanto
 */
public class SetupThread implements Runnable {

    private SchedulerMonitor monitor;
    private int threadId;
    private int time;
    private String[][] data;

    public SetupThread(SchedulerMonitor monitor, int threadId, int time, String[][] data) {
        this.monitor = monitor;
        this.threadId = threadId;
        this.time = time;
        this.data = data;
    }

    @Override
    public void run() {
        synchronized (this.monitor) {
            try {
                while (this.monitor.getThreadId() != this.threadId) {
                    this.monitor.wait();
                }

                Problem subProblem = this.monitor.getProblem().reschedule(time, data);//tambahkan job ke kumpulan job awal dan return sub
                int popSize = this.monitor.getGAHHInstance().getPopulationSize();
                float crossoverProb = this.monitor.getGAHHInstance().getCrossoverProb();
                float mutationProb = this.monitor.getGAHHInstance().getMutationProb();
                int maxGeneration = this.monitor.getGAHHInstance().getMaximumGeneration();

                this.monitor.setGAHHInstance(new GAHH(new ScheduleGenerator(subProblem), popSize, crossoverProb, mutationProb, maxGeneration));

                this.monitor.incrementThreadId();
                this.monitor.notifyAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
