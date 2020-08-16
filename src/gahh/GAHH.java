package gahh;

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
    private float crossoverProb;
    private float mutationProb;
    private ScheduleGenerator scheduleGenerator;

    public GAHH(ScheduleGenerator scheduleGenerator,int populationSize, float crossoverProb, float mutationProb, int maximumGeneration) {
        this.populationSize = populationSize;
        this.elitismCount = populationSize / 4;
        this.tournamentSize = populationSize / 4;//kalau terlalu tinggi, loss diversity, kalo terlalu rendah: slow convergence
        this.crossoverProb = crossoverProb;
        this.mutationProb = mutationProb;
        this.scheduleGenerator = scheduleGenerator;
        this.currentGeneration = 0;
        this.maximumGeneration = maximumGeneration;
    }

    public int getMaxGeneration(){
        return this.maximumGeneration;
    }
    /**
     * inisialisasi populasi awal secara random
     */
    public Population initPopulation(int chromosomeLength) {
        System.out.println("initPopulation start");
        Population population = new Population(this.populationSize, chromosomeLength);
        System.out.println("popSize: "+this.populationSize);
        System.out.println("chromoLen: "+chromosomeLength);
        Random rnd = new Random();
        for (int i = 0; i < this.populationSize; i++) {
            for (int j = 0; j < chromosomeLength; j++) {
                int gene = rnd.nextInt(5) + 1;
                population.getIndividual(i).setGene(j, gene);
            }
            System.out.println("finalChromoLen: "+population.getIndividual(i).getChromosomeLength());
        }
        System.out.println("initPopulation done");
        return population;
    }

    //TODO!
    //fitness function
    //hitung fitness sesuai kategori penjadwalan (makespan dan mean tardiness)
    public double calcFitness(Individual individual) {
        System.out.println("calc fitness started");
        double fitness;
        Schedule sch = this.scheduleGenerator.generateSchedule(individual);

        //System.out.println("Calculating Individual Fitness");
        //System.out.println("Schedule generated for this individual: ");
        //System.out.println(this.schedule.toString());
        
        fitness = 0.5 * sch.getMakespan() + 0.5 * sch.getMeanTardiness();
        
        //System.out.println("Individual fitness value: " + fitness);
        individual.setFitness(fitness);
        individual.setSchedule(sch);
        System.out.println("calc fitness done, fitness: "+fitness);
        return fitness;
    }

    public void increaseGeneration() {
        this.currentGeneration++;
    }

    public void evalPopulation(Population population) {
        System.out.println("evaluating population started");
        double populationFitness = 0;
        for (Individual individual : population.getIndividuals()) {
            populationFitness += calcFitness(individual);
        }
        System.out.println("evaluating population done");
        population.setPopulationFitness(populationFitness);
    }

    public Individual selectParent(Population population) {
        //pilih parent dengan metode selection tertentu
        if(this.tournamentSize > 0){
            return this.tournamentSelection(population);
        }else{
            return this.rouletteWheelSelection(population);
        }
    }

    private Individual rouletteWheelSelection(Population population) {
        //get individuals
        Individual[] individuals = population.getIndividuals();

        //Tambahan dari gue, baca dari literatur lain roulette wheel selection mensyaratkan populasi nya terurut menaik
        Arrays.sort(individuals, new IndividualAscendingByFitnessComparator());

        double populationFitness = population.getPopulationFitness();
        double wheelPos = Math.random() * populationFitness * 1.0;

        double spinWheel = 0;
        for (Individual individual : individuals) {
            spinWheel += individual.getFitness() * 1.0;
            if (spinWheel >= wheelPos) {
                return individual;
            }
        }

        return individuals[population.size() - 1];
    }
    
    private Individual tournamentSelection(Population population){
        Population tournament = new Population(this.tournamentSize);
        population.shuffle();
        for (int i = 0; i < this.tournamentSize; i++) {
            Individual tournamentIndividual = population.getIndividual(i);
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
                System.out.println("This Individual is not an elite individual, init mutation");
                //telusuri gene individu
                System.out.println("Individual chromosome BEFORE mutation: "+individual.toString());
                for (int geneIdx = 0; geneIdx < individual.getChromosomeLength(); geneIdx++) {
                    if (this.mutationProb > Math.random()) {
                        //System.out.println("Mutation occured on individual " + idx + " on gene: " + geneIdx);

                        int newGene = rnd.nextInt(5) + 1;

                        //System.out.println("new gene is: " + newGene);
                        individual.setGene(geneIdx, newGene);
                    }
                }
                System.out.println("Individual chromosome AFTER mutation: "+individual.toString());
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
                System.out.println("Crossover happens on individual: "+idx);
                
                Individual firstOffspring = new Individual(firstParent.getChromosomeLength());
                
                System.out.println("First parent: "+firstParent.toString());
                
                //pilih parent kedua
                Individual secondParent = this.selectParent(population);
                
                

                //tentukan crossover point
                int crossoverPoint = (int) (Math.random() * (firstParent.getChromosomeLength() + 1));
                System.out.println("crossover point at gene: "+crossoverPoint);
                //hasilkan dua offspring baru
                if (idx + 1 < newPopulation.size()) {
                    System.out.println("Two offspring generated");
                    Individual secondOffspring = new Individual(firstParent.getChromosomeLength());
                    System.out.println("Second parent: "+secondParent.toString());
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
                    System.out.println("First offspring: "+firstOffspring.toString());
                    System.out.println("Second offspring: "+secondOffspring.toString());
                } else {
                    System.out.println("One offspring generated");
                    System.out.println("Second parent: "+secondParent.toString());
                    for (int geneIdx = 0; geneIdx < firstParent.getChromosomeLength(); geneIdx++) {
                        if (geneIdx < crossoverPoint) {
                            firstOffspring.setGene(geneIdx, firstParent.getGene(geneIdx));
                        } else {
                            firstOffspring.setGene(geneIdx, secondParent.getGene(geneIdx));
                        }
                    }
                    newPopulation.setIndividual(idx, firstOffspring);
                    idx++;
                    System.out.println("Firstoffspring: "+firstOffspring.toString());
                }
            } else {
                System.out.println("No crossover happens");
                newPopulation.setIndividual(idx, firstParent);
                idx++;
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

        //telusuri seluruh individu pada populasi saat ini berdasarkan fitness value
        for (int i = 0; i < population.size(); i++) {
            //pilih parent pertama
            Individual firstParent = population.getFittest(i);

            //terapkan crossover pada individu saat ini
            if (this.crossoverProb > 0) {
                //Buat individu baru
                Individual offspring = new Individual(firstParent.getChromosomeLength());

                //Pilih parent kedua dengan menggunakan metode selection tertentu
                Individual secondParent = selectParent(population);

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

    public boolean isTerminationConditionMet(Population population) {

        if (this.currentGeneration == this.maximumGeneration) {
            return true;
        }
        return false;
    }
}
