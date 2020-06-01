package gahh;
import jss.Operation;
import jss.Job;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
/**
 * @author Irvan Hardyanto (2016730070)
 */
//low level heuristic yang dinyatakan oleh integer tertentu
public class LowLevelHeuristic{	
	public void orderOperation(ArrayList<Operation> operations,Job[] jobList,int llh){
		switch(llh){
			case 1:
				//System.out.println("Minimum Release Time");
				Collections.sort(operations,new ReleaseDateAscendingComparator(jobList));
				//cari operation job (order[0])
				break;
			case 2:
				//System.out.println("Shortest Processing Time");
				Collections.sort(operations,new Comparator<Operation>(){
					@Override
					public int compare(Operation o1,Operation o2){
						if(o1.getProcTime() > o2.getProcTime()){
							return 1;
						}else if(o1.getProcTime() < o2.getProcTime()){
							return -1;
						}
						return 0;
					}
				});
				break;
			case 3:
				//System.out.println("Longest Processing Time");
				Collections.sort(operations,new Comparator<Operation>(){
					@Override
					public int compare(Operation o1,Operation o2){
						if(o1.getProcTime() > o2.getProcTime()){
							return -1;
						}else if(o1.getProcTime() < o2.getProcTime()){
							return 1;
						}
						return 0;
					}
				});
				break;
			case 4:
				//System.out.println("Earliest Due Date");
				Collections.sort(operations,new DueDateAscendingComparator(jobList));
				break;
			case 5:
				//System.out.println("Latest Due Date");
				Collections.sort(operations,new DueDateDescendingComparator(jobList));
				break;
			default:
				System.out.println("LLH out of range!");
		}
		
	}
}