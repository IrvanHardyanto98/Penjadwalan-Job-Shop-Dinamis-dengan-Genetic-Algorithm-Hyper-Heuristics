package gahh;
import jss.Operation;
import jss.Job;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;

public class DueDateDescendingComparator implements Comparator<Operation>{
	private HashMap<Integer,Job> job;
	
	public DueDateDescendingComparator(HashMap<Integer,Job> job){
		this.job=job;
	}
	
	@Override
	public int compare(Operation o1, Operation o2){
		if(this.job.get(o1.getJobId()).getDueDate() > this.job.get(o2.getJobId()).getDueDate()){
			return -1;
		}else if(this.job.get(o1.getJobId()).getDueDate() < this.job.get(o2.getJobId()).getDueDate()){
			return 1;
		}
				return 0;
	}
}