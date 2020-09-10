/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh_scheduling_js;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
    
    private Alert inputAlert;

    @FXML
    public void handleNewJob(ActionEvent event){
        String releaseDates = this.TfNewReleaseDate.getText().trim();
        String dueDates = this.TfNewDueDate.getText().trim();
        String procTimes = this.TaNewProcTime.getText().trim();
        String machineAssignments = this.TaNewMachineAssignment.getText();
        
        String pattern1 = "(\\d+)(\\,\\d+)+";
        String pattern2 = "(((\\d+)(\\,\\d+)+)\\n?)+";
        if(releaseDates.isEmpty()||dueDates.isEmpty()||procTimes.isEmpty()||machineAssignments.isEmpty()){
            this.inputAlert.setContentText("input field should not be empty!");
            this.inputAlert.show();
        }else if(!releaseDates.matches(pattern1)||!dueDates.matches(pattern1)||!procTimes.matches(pattern2)||!machineAssignments.matches(pattern2)){
            this.inputAlert.setContentText("input format invalid!");
            this.inputAlert.show();
        }else{
            String[] splitRd = releaseDates.split(",");
            String[] splitDd = dueDates.split(",");
            String[] splitPt = procTimes.split("\n");
            String[] splitMa = machineAssignments.split("\n");
            
            int[] tmp = new int[4];
            tmp[0] = splitRd.length;
            tmp[1] = splitDd.length;
            tmp[2] = splitPt.length;
            tmp[3] = splitMa.length;
            
            if(checkArray(tmp)){
                String[][] jobData= new String[4][];
                jobData[0]=splitRd;
                jobData[1]=splitDd;
                jobData[2]=splitPt;
                jobData[3]=splitMa;
                
                ControllerMediator.getInstance().addNewJob(jobData, tmp[0]);
            }else{
                this.inputAlert.setContentText("Job data is not complete!");
                this.inputAlert.show();
            }
        }
    }
    
    public void clearAll(){
        this.TaNewMachineAssignment.clear();
        this.TaNewProcTime.clear();
        this.TfNewDueDate.clear();
        this.TfNewReleaseDate.clear();
    }
    private boolean checkArray(int[] arr){
         int len=arr[0];
            for (int i = 1; i < arr.length; i++) {
                if(arr[i]!=len){
                    return false;
                }
            }
           return true;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.inputAlert = new Alert(Alert.AlertType.ERROR);
    }
    
}
