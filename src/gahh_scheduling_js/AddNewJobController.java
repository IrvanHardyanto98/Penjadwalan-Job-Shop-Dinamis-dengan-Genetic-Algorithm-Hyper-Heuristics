/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh_scheduling_js;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Irvan Hardyanto
 * NPM: 2016730070
 */
public class AddNewJobController implements Initializable{
    @FXML
    private TextField TfNewReleaseDate;
    
    @FXML
    private TextField TfNewDueDate;
    
    @FXML
    private TextArea TaNewProcTime;
    
    @FXML
    private TextArea TaNewMachineAssignment;

    @FXML
    public void handleNewJob(ActionEvent event){
        String releaseDates = this.TfNewReleaseDate.getText().trim();
        String dueDates = this.TfNewDueDate.getText().trim();
        String procTimes = this.TaNewProcTime.getText().trim();
        String machineAssignments = this.TaNewMachineAssignment.getText();
        
        if(releaseDates.isEmpty()||dueDates.isEmpty()||procTimes.isEmpty()||machineAssignments.isEmpty()){
        
        }else{
        
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
}
