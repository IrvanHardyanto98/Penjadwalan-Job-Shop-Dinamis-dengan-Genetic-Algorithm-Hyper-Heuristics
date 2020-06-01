package gahh;
import jss.Operation;
import jss.Job;

import java.util.Comparator;

public class ReleaseDateAscendingComparator implements Comparator<Operation>{
	private Job[] job;
	
	public ReleaseDateAscendingComparator(Job[] job){
		this.job=job;
	}
	
	@Override
	public int compare(Operation o1, Operation o2){
		if(this.job[o1.getJobId()].getReleaseDate() > this.job[o2.getJobId()].getReleaseDate()){
			return 1;
		}else if(this.job[o1.getJobId()].getReleaseDate() < this.job[o2.getJobId()].getReleaseDate()){
			return -1;
		}
				return 0;
	}
}