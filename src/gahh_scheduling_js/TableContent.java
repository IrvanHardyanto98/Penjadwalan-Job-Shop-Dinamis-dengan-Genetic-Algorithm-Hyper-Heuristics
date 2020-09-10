/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gahh_scheduling_js;

/**
 *
 * @author Irvan Hardyanto
 */
public class TableContent{
        private int jobId;
        private int releaseDate;
        private int dueDate;
        private int oprNum;
        
        public TableContent(int jobId,int releaseDate,int dueDate,int oprNum){
            this.jobId=jobId;
            this.releaseDate = releaseDate;
            this.dueDate = dueDate;
            this.oprNum = oprNum;
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

        public int getOprNum() {
            return oprNum;
        }
        
        
    }
