package gahh;
import jss.Operation;
import jss.Job;

import java.util.Comparator;
import java.util.HashMap;
import javafx.util.Pair;

public class ReleaseDateAscendingComparator implements Comparator<Pair<Integer,Operation>>{
	private HashMap<Integer,Job> job;
	
	public ReleaseDateAscendingComparator(HashMap<Integer,Job> job){
		this.job=job;
	}
	
	@Override
	public int compare(Pair<Integer,Operation> o1, Pair<Integer,Operation> o2){
		if(this.job.get(o1.getValue().getJobId()).getReleaseDate() > this.job.get(o2.getValue().getJobId()).getReleaseDate()){
			return 1;
		}else if(this.job.get(o1.getValue().getJobId()).getReleaseDate() < this.job.get(o2.getValue().getJobId()).getReleaseDate()){
			return -1;
		}
				return 0;
	}
}