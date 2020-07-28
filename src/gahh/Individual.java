package gahh;
import java.util.ArrayList;
import java.util.Collections;

public class Individual{
	private ArrayList<Integer> chromosome;
        private Schedule schedule;
	private double fitness;
	
	public Individual(int chromosomeSize){
		this.chromosome = new ArrayList<>(chromosomeSize);
                for(int i = 0;i<chromosomeSize;i++){
                    this.chromosome.add(i,0);
                }
                this.fitness = Double.MAX_VALUE;
                this.schedule = null;
	}
	
	public Individual(){
		// Create individual
		this.chromosome = new ArrayList<>();
                this.schedule = null;
                this.fitness = Double.MAX_VALUE;
	}
        
        public Individual(ArrayList<Integer> chromosome){
            Collections.copy(this.chromosome, chromosome);
        }
        
        public void setSchedule(Schedule schedule){
            this.schedule=schedule;
        }
        
        public Schedule getSchedule(){
            return this.schedule;
        }
	
	public ArrayList<Integer> getChromosome(){
		return this.chromosome;
	}
	
	public int getChromosomeLength(){
		return this.chromosome.size();
	}
	
	public void setGene(int offset, int gene){
                this.chromosome.set(offset, gene);
	}
	
	public int getGene(int offset){
		return this.chromosome.get(offset);
	}
	
	public void setFitness(double fitness){
		this.fitness = fitness;
	}
	
	public double getFitness(){
		return this.fitness;
	}
	
	public String toString(){
		String output = "";
                boolean first=true;
		for (int gene = 0; gene < this.chromosome.size(); gene++){
                    if(first){
                        first = false;
                    }else{
                        output += "-";
                    }
			output += this.chromosome.get(gene);
		}
                output+=", fitness: "+this.fitness+"\n";
                output +=this.schedule+"\n";
		return output;
	}
}