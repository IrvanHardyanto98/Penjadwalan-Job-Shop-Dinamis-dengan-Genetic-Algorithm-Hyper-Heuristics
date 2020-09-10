package gahh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Irvan Hardyanto
 * NPM: 2016730070
 */
public class GAHH {

    private int populationSize;
    private int currentGeneration;
    private int maximumGeneration;
    private int elitismCount;
    private int tournamentSize;
    private int jmlLLH;
    private float crossoverProb;
    private float mutationProb;
    private ScheduleGenerator scheduleGenerator;

    public GAHH(ScheduleGenerator scheduleGenerator,int populationSize, float crossoverProb, float mutationProb, int maximumGeneration) {
        this.populationSize = populationSize;
        this.elitismCount = 5;
        this.tournamentSize = 10;//kalau terlalu tinggi, loss diversity, kalo terlalu rendah: slow convergence
        this.crossoverProb = crossoverProb;
        this.mutationProb = mutationProb;
        this.scheduleGenerator = scheduleGenerator;
        this.currentGeneration = 0;
        this.maximumGeneration = maximumGeneration;
        this.jmlLLH = 9;
    }

    public int getMaxGeneration(){
        return this.maximumGeneration;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public int getCurrentGeneration() {
        return currentGeneration;
    }

    public void setCurrentGeneration(int currentGeneration) {
        this.currentGeneration = currentGeneration;
    }

    public int getMaximumGeneration() {
        return maximumGeneration;
    }

    public void setMaximumGeneration(int maximumGeneration) {
        this.maximumGeneration = maximumGeneration;
    }

    public int getElitismCount() {
        return elitismCount;
    }

    public void setElitismCount(int elitismCount) {
        this.elitismCount = elitismCount;
    }

    public int getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    public float getCrossoverProb() {
        return crossoverProb;
    }

    public void setCrossoverProb(float crossoverProb) {
        this.crossoverProb = crossoverProb;
    }

    public float getMutationProb() {
        return mutationProb;
    }

    public void setMutationProb(float mutationProb) {
        this.mutationProb = mutationProb;
    }

    public ScheduleGenerator getScheduleGenerator() {
        return scheduleGenerator;
    }

    public void setScheduleGenerator(ScheduleGenerator scheduleGenerator) {
        this.scheduleGenerator = scheduleGenerator;
    }
    
    
    /**
     * inisialisasi populasi awal secara random
     */
    public Population initPopulation() {
        System.out.println("initPopulation start");
        int chromosomeLength = this.scheduleGenerator.getProblem().getOpMap().size();
        Population population = new Population(this.populationSize, chromosomeLength);
        System.out.println("popSize: "+this.populationSize);
        System.out.println("chromoLen: "+chromosomeLength);
        Random rnd = new Random();
        for (int i = 0; i < this.populationSize; i++) {
            for (int j = 0; j < chromosomeLength; j++) {
                int gene = rnd.nextInt(this.jmlLLH) + 1;
                population.getIndividual(i).setGene(j, gene);
            }
        }
        System.out.println("initPopulation done");
        return population;
    }

    //TODO!
    //fitness function
    //hitung fitness sesuai kategori penjadwalan (makespan dan mean tardiness)
    public double calcFitness(Individual individual) {
        double fitness;
        Schedule sch = this.scheduleGenerator.generateSchedule(individual);
        
        fitness = 0.5 * sch.getMakespan() + 0.5 * sch.getMeanTardiness();
        
        individual.setFitness(fitness);
        individual.setSchedule(sch);
        //System.out.printf("%.2f",fitness);
        //System.out.print(" ");
        return fitness;
    }

    public void increaseGeneration() {
        this.currentGeneration++;
    }

    public void evalPopulation(Population population) {
        double populationFitness = 0;
        for (Individual individual : population.getIndividuals()) {
            populationFitness += calcFitness(individual);
        }
        population.setPopulationFitness(populationFitness);
    }

    public Individual selectParent(Individual[] individuals,double populationFitness) {
        //pilih parent dengan metode selection tertentu
        if(this.tournamentSize > 0){
            return this.tournamentSelection(individuals);
        }else{
            return this.rouletteWheelSelection(individuals,populationFitness);
        }
    }

    private Individual rouletteWheelSelection(Individual[] individuals,double populationFitness) {
        //get individuals

        double wheelPos = Math.random() * populationFitness * 1.0;

        double spinWheel = 0;
        for (Individual individual : individuals) {
            spinWheel += individual.getFitness() * 1.0;
            if (spinWheel >= wheelPos) {
                return individual;
            }
        }

        return individuals[individuals.length - 1];
    }
    
    private Individual tournamentSelection(Individual[] individuals){
        Population tournament = new Population(this.tournamentSize);
        Random rnd = new Random();
        int idx = rnd.nextInt(individuals.length);
        for (int i = 0; i < this.tournamentSize; i++) {
            Individual tournamentIndividual = individuals[idx];
            tournament.setIndividual(i,tournamentIndividual);
        }
        return tournament.getFittest(0);
    }

    public Population mutatePopulation(Population population) {
        //Inisialisasikan Populasi baru
        //Population newPopulation = new Population(this.populationSize);
        Population newPopulation = new Population(population.size());
        Random rnd = new Random();

        Individual[] arrIndividual = population.getIndividuals();
        Arrays.sort(arrIndividual, new IndividualAscendingByFitnessComparator());
        //telusuri seluruh individu pada populasi saat ini berdasarkan fitness value
        for (int idx = 0; idx < population.size(); idx++) {
            Individual individual = arrIndividual[idx];
            if (idx >= this.elitismCount) {
               // System.out.println("This Individual is not an elite individual, init mutation");
                //telusuri gene individu
                //System.out.println("Individual chromosome BEFORE mutation: "+individual.toString());
                for (int geneIdx = 0; geneIdx < individual.getChromosomeLength(); geneIdx++) {
                    if (this.mutationProb > Math.random()) {
                        //System.out.println("Mutation occured on individual " + idx + " on gene: " + geneIdx);

                        int newGene = rnd.nextInt(this.jmlLLH) + 1;

                        //System.out.println("new gene is: " + newGene);
                        individual.setGene(geneIdx, newGene);
                    }
                }
               // System.out.println("Individual chromosome AFTER mutation: "+individual.toString());
            }
            newPopulation.setIndividual(idx, individual);
        }
        return newPopulation;
    }

    //kondisi awal: dua parent
    //kondisi akhir: dua offspring baru
    public Population onePointCrossover(Population population) {
        //buat populasi baru
        //Population newPopulation = new Population(this.populationSize);
        Population newPopulation = new Population(population.size());
        int idx = 0;

        Individual[] arrIndividual = population.getIndividuals();
        Arrays.sort(arrIndividual, new IndividualAscendingByFitnessComparator());

        //hasilkan offspring
        while (idx < newPopulation.size()) {
            //pilih parent pertama
            Individual firstParent = arrIndividual[idx];

            if (this.crossoverProb > Math.random() && idx > this.elitismCount) {
                //System.out.println("Crossover happens on individual: "+idx);
                
                Individual firstOffspring = new Individual(firstParent.getChromosomeLength());
                
                //System.out.println("First parent: "+firstParent.toString());
                
                //pilih parent kedua
                Individual secondParent = this.selectParent(arrIndividual,population.getPopulationFitness());
                
                

                //tentukan crossover point
                int crossoverPoint = (int) (Math.random() * (firstParent.getChromosomeLength() + 1));
                //System.out.println("crossover point at gene: "+crossoverPoint);
                //hasilkan dua offspring baru
                if (idx + 1 < newPopulation.size()) {
                   // System.out.println("Two offspring generated");
                    Individual secondOffspring = new Individual(firstParent.getChromosomeLength());
                    //System.out.println("Second parent: "+secondParent.toString());
                    for (int geneIdx = 0; geneIdx < firstParent.getChromosomeLength(); geneIdx++) {
                        if (geneIdx < crossoverPoint) {
                            firstOffspring.setGene(geneIdx, firstParent.getGene(geneIdx));
                            secondOffspring.setGene(geneIdx, secondParent.getGene(geneIdx));
                        } else {
                            firstOffspring.setGene(geneIdx, secondParent.getGene(geneIdx));
                            secondOffspring.setGene(geneIdx, firstParent.getGene(geneIdx));
                        }
                    }
                    
                    //simpan individu baru ke populasi
                    newPopulation.setIndividual(idx, firstOffspring);
                    idx++;
                    newPopulation.setIndividual(idx, secondOffspring);
                    idx++;
                   // System.out.println("First offspring: "+firstOffspring.toString());
                   // System.out.println("Second offspring: "+secondOffspring.toString());
                } else {
                    //System.out.println("One offspring generated");
                    //System.out.println("Second parent: "+secondParent.toString());
                    for (int geneIdx = 0; geneIdx < firstParent.getChromosomeLength(); geneIdx++) {
                        if (geneIdx < crossoverPoint) {
                            firstOffspring.setGene(geneIdx, firstParent.getGene(geneIdx));
                        } else {
                            firstOffspring.setGene(geneIdx, secondParent.getGene(geneIdx));
                        }
                    }
                    newPopulation.setIndividual(idx, firstOffspring);
                    idx++;
                    //System.out.println("Firstoffspring: "+firstOffspring.toString());
                }
            } else {
                //System.out.println("No crossover happens");
                newPopulation.setIndividual(idx, firstParent);
                idx++;
            }
        }
        return newPopulation;
    }
    
    public Population singleOffspringOnePointCrossover(Population population){
        Population newPopulation = new Population(population.size());

        Individual[] arrIndividual = population.getIndividuals();
        Arrays.sort(arrIndividual, new IndividualAscendingByFitnessComparator());
        for (int i = 0; i < newPopulation.size(); i++) {
            
            Individual firstParent = arrIndividual[i];
            
            if (this.crossoverProb > Math.random() && i > this.elitismCount) {
                int crossoverPoint = (int) (Math.random() * (firstParent.getChromosomeLength() + 1));
                Individual firstOffspring = new Individual(firstParent.getChromosomeLength());
                Individual secondParent = this.selectParent(arrIndividual,population.getPopulationFitness());
                for (int geneIdx = 0; geneIdx < firstParent.getChromosomeLength(); geneIdx++) {
                        if (geneIdx < crossoverPoint) {
                            firstOffspring.setGene(geneIdx, firstParent.getGene(geneIdx));
                            //secondOffspring.setGene(geneIdx, secondParent.getGene(geneIdx));
                        } else {
                            firstOffspring.setGene(geneIdx, secondParent.getGene(geneIdx));
                            //secondOffspring.setGene(geneIdx, firstParent.getGene(geneIdx));
                        }
                    }
                newPopulation.setIndividual(i, firstOffspring);
            }else{
                newPopulation.setIndividual(i, firstParent);
            }
        }
        return newPopulation;
    }

    public void updatePopulation(Population oldPopulation, Population newPopulation) {
        oldPopulation.setIndividuals(newPopulation.getIndividuals());
        oldPopulation.setPopulationFitness(newPopulation.getPopulationFitness());
    }

    //input: dua parent
    //output: SATU offspring
    public Population uniformCrossoverPopulation(Population population) {
        //buat populasi baru
        //Population newPopulation = new Population(this.populationSize);
        Population newPopulation = new Population(population.size());
        
        Individual[] arrIndividual = population.getIndividuals();
        Arrays.sort(arrIndividual, new IndividualAscendingByFitnessComparator());
        //telusuri seluruh individu pada populasi saat ini berdasarkan fitness value
        for (int i = 0; i < population.size(); i++) {
            //pilih parent pertama
            Individual firstParent = arrIndividual[i];

            //terapkan crossover pada individu saat ini
            if (this.crossoverProb > 0 && i > this.elitismCount) {
                //Buat individu baru
                Individual offspring = new Individual(firstParent.getChromosomeLength());

                //Pilih parent kedua dengan menggunakan metode selection tertentu
                Individual secondParent = selectParent(population.getIndividuals(),population.getPopulationFitness());

                //iterasi seluruh gene
                for (int geneIdx = 0; geneIdx < firstParent.getChromosomeLength(); geneIdx++) {
                    //ambil setengah gene parent pertama dan setengah gene parent kedua
                    if (0.5 > Math.random()) {
                        offspring.setGene(geneIdx, firstParent.getGene(geneIdx));
                    } else {
                        offspring.setGene(geneIdx, secondParent.getGene(geneIdx));
                    }
                }

                //tambahkan individu ke populasi baru
                newPopulation.setIndividual(i, offspring);
            } else {
                newPopulation.setIndividual(i, firstParent);
            }
        }
        return newPopulation;
    }
    
    /**
     * Referensi:Bhatt, Nisha dan Chauhan, Nathi Ram. (2015) Genetic algorithm applications on Job Shop Scheduling Problem: A Review. 2015 International Conference on Soft Computing Techniques and Implementations, Faridabad, India, 8-10 October, pp. 7-14. IEEE, New Jersey.
     */
    public Population PPXCrossover(Population population){
        Population newPopulation = new Population(population.size());
        
        Individual[] arrIndividual = population.getIndividuals();
        Arrays.sort(arrIndividual, new IndividualAscendingByFitnessComparator());
        
        for (int i = 0; i < population.size(); i++) {
            //pilih parent pertama
            Individual firstParent = arrIndividual[i];

            //terapkan crossover pada individu saat ini
            if (this.crossoverProb > 0 && i > this.elitismCount) {
                Individual offspring = new Individual(firstParent.getChromosomeLength());
                Individual secondParent = selectParent(population.getIndividuals(),population.getPopulationFitness());
                
                ArrayList<Integer> temp1 = new ArrayList(firstParent.getChromosome());
                ArrayList<Integer> temp2 = new ArrayList(secondParent.getChromosome());
                
                int idx1=0;
                int idx2=0;
          
                for (int geneIdx = 0; geneIdx < offspring.getChromosomeLength(); geneIdx++) {
                    if (0.5 > Math.random()) {
                        int gene = temp1.get(idx1);
                        Integer objGene = new Integer(gene);
                        offspring.setGene(geneIdx, gene);
                        idx1++;
                        
                        int tempidx1=0;
                        int tempidx2=0;
                        while(tempidx1<temp1.size()||tempidx2<temp2.size()){
                            if(tempidx1<temp1.size()){
                                if(temp1.get(tempidx1).equals(objGene)){
                                    temp1.remove(tempidx1);
                                }
                                tempidx1++;
                            }
                            if(tempidx2<temp2.size()){
                                if(temp2.get(tempidx2).equals(objGene)){
                                    temp2.remove(tempidx2);
                                }
                                tempidx2++;
                            }
                        }
                    } else {
                        int gene = temp2.get(idx2);
                        Integer objGene = new Integer(gene);
                        offspring.setGene(geneIdx, gene);
                        idx2++;
                        
                        int tempidx1=0;
                        int tempidx2=0;
                        while(tempidx1<temp1.size()||tempidx2<temp2.size()){
                            if(tempidx1<temp1.size()){
                                if(temp1.get(tempidx1).equals(objGene)){
                                    temp1.remove(tempidx1);
                                }
                                tempidx1++;
                            }
                            if(tempidx2<temp2.size()){
                                if(temp2.get(tempidx2).equals(objGene)){
                                    temp2.remove(tempidx2);
                                }
                                tempidx2++;
                            }
                        }
                    }
                }
                newPopulation.setIndividual(i, offspring);
            }else{
                 newPopulation.setIndividual(i, firstParent);
            }
        }
        
        return newPopulation;
    }
    public boolean isTerminationConditionMet(Population population) {

        if (this.currentGeneration == this.maximumGeneration) {
            return true;
        }
        return false;
    }
}
