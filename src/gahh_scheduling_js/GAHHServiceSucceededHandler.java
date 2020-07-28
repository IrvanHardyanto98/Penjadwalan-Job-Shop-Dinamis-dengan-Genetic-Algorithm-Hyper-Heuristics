/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh_scheduling_js;

import gahh.Schedule;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import jss.Problem;

/**
 *
 * @author Irvan Hardyanto
 */
public class GAHHServiceSucceededHandler implements EventHandler<WorkerStateEvent>{
    @FXML
    private TextArea textAreaSchedule;
    
    public GAHHServiceSucceededHandler(TextArea textAreaSchedule){
        this.textAreaSchedule = textAreaSchedule;
    }
    @Override
    public void handle(WorkerStateEvent event) {
        Schedule s =  (Schedule) event.getSource().getValue();
        System.out.println("ched: "+s.toString());
        
        this.textAreaSchedule.setText(s.toString());
    }   
}
