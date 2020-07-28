/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh_scheduling_js;

import javafx.fxml.FXML;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;
/**
 *
 * @author IrvanHardyanto
 */
public class GAHHServiceRunningHandler implements EventHandler<WorkerStateEvent>{
    @FXML
    private ProgressBar progress;
    
    public GAHHServiceRunningHandler(ProgressBar progress){
        this.progress = progress;
    }

    @Override
    public void handle(WorkerStateEvent event) {
        this.progress.progressProperty().bind(event.getSource().progressProperty());
    }
}
