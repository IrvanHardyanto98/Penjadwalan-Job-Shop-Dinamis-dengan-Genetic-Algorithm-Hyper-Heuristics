package gahh;
import jss.Operation;
import jss.Job;
import java.util.Comparator;

public class DueDateAscendingComparator implements Comparator<Operation>{
	private Job[] job;
	
	public DueDateAscendingComparator(Job[] job){
		this.job=job;
	}
	
	@Override
	public int compare(Operation o1, Operation o2){
		if(this.job[o1.getJobId()].getDueDate() > this.job[o2.getJobId()].getDueDate()){
			return 1;
		}else if(this.job[o1.getJobId()].getDueDate() < this.job[o2.getJobId()].getDueDate()){
			return -1;
		}
				return 0;
	}
}