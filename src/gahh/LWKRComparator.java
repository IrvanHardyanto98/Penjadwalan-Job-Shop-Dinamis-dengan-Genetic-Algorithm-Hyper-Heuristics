/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh;

import java.util.Comparator;
import java.util.HashMap;
import javafx.util.Pair;
import jss.Job;
import jss.Operation;

/**
 *
 * @author Irvan Hardyanto
 * NPM: 2016730070
 */
public class LWKRComparator implements Comparator<Pair<Integer,Operation>>{
    private HashMap<Integer,Job> job;
    
    public LWKRComparator(HashMap<Integer,Job> job){
        this.job=job;
    }

    @Override
    public int compare(Pair<Integer,Operation> o1, Pair<Integer,Operation> o2) {
        
        int remOpr1 = this.job.get(o1.getValue().getJobId()).getOperationNum()-o1.getValue().getOprOrderNum();
        int remOpr2 = this.job.get(o2.getValue().getJobId()).getOperationNum()-o2.getValue().getOprOrderNum();
        if(remOpr1<remOpr2){
            return 1;
        }else if(remOpr1>remOpr2){
            return -1;
        }
        return 0;
    }
}
