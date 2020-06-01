package gahh;
import jss.Operation;
import java.util.Comparator;

public class ProcessingTimeDescendingComparator implements Comparator<Operation>{
	@Override
	public int compare(Operation o1, Operation o2){
		if(o1.getProcTime()> o2.getProcTime()){
			return -1;
		}else if(o1.getProcTime() < o2.getProcTime()){
			return 1;
		}
				return 0;
	}
}