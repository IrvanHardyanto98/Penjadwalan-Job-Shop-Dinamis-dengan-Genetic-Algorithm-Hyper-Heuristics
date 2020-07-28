package gahh;
import jss.Operation;
import jss.Job;

import java.util.Comparator;
import java.util.HashMap;

public class ReleaseDateAscendingComparator implements Comparator<Operation>{
	private HashMap<Integer,Job> job;
	
	public ReleaseDateAscendingComparator(HashMap<Integer,Job> job){
		this.job=job;
	}
	
	@Override
	public int compare(Operation o1, Operation o2){
		if(this.job.get(o1.getJobId()).getReleaseDate() > this.job.get(o2.getJobId()).getReleaseDate()){
			return 1;
		}else if(this.job.get(o1.getJobId()).getReleaseDate() < this.job.get(o2.getJobId()).getReleaseDate()){
			return -1;
		}
				return 0;
	}
}