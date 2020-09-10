package gahh_scheduling_js;


import javafx.scene.paint.Color;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Irvan Hardyanto
 */
public class ScheduleLegend {
    private int jobId;
    private int releaseDate;
    private int dueDate;
    private Color color;
    private int finishTime;
    private int tardiness;

    public ScheduleLegend(int jobId, int releaseDate, int dueDate, Color color, int finishTime) {
        this.jobId = jobId;
        this.releaseDate = releaseDate;
        this.dueDate = dueDate;
        this.color = color;
        this.finishTime = finishTime;
        this.tardiness = Math.max(0,(finishTime-dueDate));
    }

    public int getJobId() {
        return jobId;
    }

    public int getReleaseDate() {
        return releaseDate;
    }

    public int getDueDate() {
        return dueDate;
    }

    public Color getColor() {
        return color;
    }

    public int getFinishTime() {
        return finishTime;
    }
    
    public int getTardiness(){
        return this.tardiness;
    }
    
    
}
