package gahh;
import jss.Operation;
import jss.Job;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import javafx.util.Pair;
/**
 * @author Irvan Hardyanto (2016730070)
 */
//low level heuristic yang dinyatakan oleh integer tertentu
public class LowLevelHeuristic{	
	public void orderOperation(ArrayList<Pair<Integer,Operation>> operations,HashMap<Integer,Job> jobMap,HashMap<Integer,Operation> opMap,HashMap<Integer, Integer> jobFinishTime, int[] machineFinishTime,int llh){
		switch(llh){
			case 1:
				//System.out.println("Minimum Release Time");
				Collections.sort(operations,new ReleaseDateAscendingComparator(jobMap));
				//cari operation job (order[0])
				break;
			case 2:
				//System.out.println("Shortest Processing Time");
				Collections.sort(operations,new Comparator<Pair<Integer,Operation>>(){
					@Override
					public int compare(Pair<Integer,Operation> o1,Pair<Integer,Operation> o2){
						if(o1.getValue().getProcTime() > o2.getValue().getProcTime()){
							return 1;
						}else if(o1.getValue().getProcTime() < o2.getValue().getProcTime()){
							return -1;
						}
						return 0;
					}
				});
				break;
			case 3:
				//System.out.println("Longest Processing Time");
				Collections.sort(operations,new Comparator<Pair<Integer,Operation>>(){
					@Override
					public int compare(Pair<Integer,Operation> o1,Pair<Integer,Operation> o2){
						if(o1.getValue().getProcTime() > o2.getValue().getProcTime()){
							return -1;
						}else if(o1.getValue().getProcTime() < o2.getValue().getProcTime()){
							return 1;
						}
						return 0;
					}
				});
				break;  
                                
			case 4:
                        //case 3:
				//System.out.println("Earliest Due Date");
				Collections.sort(operations,new DueDateAscendingComparator(jobMap));
				break;
                                
			case 5:
				//System.out.println("Latest Due Date");
				Collections.sort(operations,new DueDateDescendingComparator(jobMap));
				break;
                                
                        case 6:
                        //case 5:
                                //MWKR: MostWork Remaining
                                Collections.sort(operations, new MWKRComparator(jobMap));
                                break;
                                
                        //case 4: 
                        case 7:
                                //LWKR: Least Work Remaining
                                Collections.sort(operations, new LWKRComparator(jobMap));
                                break;
                        case 8:
                        //case 5:
                                //SRPT: Shortest Remaining Processing Time
                                Collections.sort(operations, new SRPTComparator(jobMap,opMap));
                               break;
                        case 9:
                                //Minimum slack time
                                Collections.sort(operations, new MSTComparator(jobMap,opMap,jobFinishTime,machineFinishTime));
                               break;
			default:
				System.out.println("LLH out of range!");
		}
		
	}
}