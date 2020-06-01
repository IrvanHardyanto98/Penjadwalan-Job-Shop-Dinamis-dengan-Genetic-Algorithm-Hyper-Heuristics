package gahh;

public class Individual{
	private int[] chromosome;
        private String schedule;
	private double fitness;
	
	public Individual(int chromosomeSize){
		this.chromosome = new int[chromosomeSize];
                this.fitness = Double.MAX_VALUE;
	}
	
	public Individual(int[] chromosome){
		// Create individual
		this.chromosome = chromosome;
                this.schedule = null;
	}
        
        public void setSchedule(String schedule){
            this.schedule=schedule;
        }
        
        public String getSchedule(){
            return this.schedule;
        }
	
	public int[] getChromosome(){
		return this.chromosome;
	}
	
	public int getChromosomeLength(){
		return this.chromosome.length;
	}
	
	public void setGene(int offset, int gene){
		this.chromosome[offset] = gene;
	}
	
	public int getGene(int offset){
		return this.chromosome[offset];
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
		for (int gene = 0; gene < this.chromosome.length; gene++){
                    if(first){
                        first = false;
                    }else{
                        output += "-";
                    }
			output += this.chromosome[gene];
		}
                output+=", fitness: "+this.fitness+"\n";
                output +=this.schedule+"\n";
		return output;
	}
}