package gahh;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;


public class Population{
	private Individual[] population;
	private double populationFitness;
	
	public Population(int populationSize){
		this.population = new Individual[populationSize];
	}
	
	public Population(int populationSize, int chromosomeLength){
		this.population = new Individual[populationSize];
		for(int i= 0; i < populationSize; i++){
			Individual individual = new Individual(chromosomeLength);
			this.population[i] = individual;
		}
	}
	
	public Individual getFittest(int offset){
                Individual[] tempPopulation = Arrays.copyOf(this.population,this.population.length);
		Arrays.sort(tempPopulation, new Comparator<Individual>() {
			@Override
			public int compare(Individual o1, Individual o2){
				if(o1.getFitness() > o2.getFitness()){
					return 1;
				}else if(o1.getFitness() < o2.getFitness()){
					return -1;
				}
				return 0;
			}
		});
		return tempPopulation[offset];
	}
	
	public Individual[] getIndividuals(){
		return this.population;
	}
        
        public void setIndividuals(Individual[] individuals){
            this.population=Arrays.copyOf(individuals, individuals.length);
        }
	
	public void setIndividual(int pos, Individual individual){
		this.population[pos] = individual;
	}
	
	public Individual getIndividual(int pos){
		return this.population[pos];
	}
	
	public void setPopulationFitness(double fitness){
		this.populationFitness = fitness;
	}
	
	public double getPopulationFitness(){
		return this.populationFitness;
	}
	
	public int size(){
		return this.population.length;
	}
	
        @Override
	public String toString() {
            String output = "";
            output = "Showing all individual in current population\n";
            for (int i = 0; i < this.population.length; i++) {
                output += "indivudal: "+i+",gene = "+this.population[i].toString()+"\n";
            }
            return output;
        }
        
        public void shuffle(){
            Random rnd = new Random();
            for (int i = this.population.length-1; i > 0; i--) {
                int index = rnd.nextInt(i + 1);
                Individual a = population[index];
                population[index] = population[i];
                population[i] = a;
            }
        }
}