/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh_scheduling_js;

import gahh.Schedule;
import jss.Problem;

/**
 *
 * @author Jim Daehn (Februari 2015) pada situs StackOverflow (https://stackoverflow.com/questions/22178533/communication-between-two-javafx-controllers)
 * Dikutip oleh Irvan Hardyanto
 * NPM: 2016730070
 */
public class ControllerMediator {
    private FXMLDocumentController controller1;
    private AddNewJobController controller2;
    private static ControllerMediator INSTANCE;
    
    public void registerController1(FXMLDocumentController controller1){
        this.controller1 = controller1;
    }
    
    public void registerController2(AddNewJobController controller2){
        this.controller2 = controller2;
    }
    
    public void unregisterAll(){
        this.controller1 = null;
        this.controller2 = null;
    }
    
    public void addNewJob(String[][] msg,int jobNum){
        this.controller1.addNewJob(msg,jobNum);
    }
    
    public void clearAll(){
        this.controller2.clearAll();
    }
    
    public void getGanttChart(Schedule sch,Problem prob){
        this.controller1.getGanttChart(sch,prob);
    }
    
    private ControllerMediator(){}
    
    public static ControllerMediator getInstance(){
        if(INSTANCE==null){
            INSTANCE = new ControllerMediator();
        }
        return INSTANCE;
    }
}
