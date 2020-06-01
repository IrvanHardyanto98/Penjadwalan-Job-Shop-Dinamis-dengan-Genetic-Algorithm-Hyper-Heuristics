package gahh;
import java.util.Comparator;

public class IndividualAscendingByFitnessComparator implements Comparator<Individual>{
	@Override
			public int compare(Individual o1, Individual o2){
				if(o1.getFitness() > o2.getFitness()){
					return 1;
				}else if(o1.getFitness() < o2.getFitness()){
					return -1;
				}
				return 0;
			}
}