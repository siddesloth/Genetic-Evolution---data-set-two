/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticevolutionmk2;
/**
 *
 * @author i2-rickwood
 */
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class runAlgorithm {
    int geneticLength = 80;
    int populationSize = 100;
    int individualFitness = 0;
    int generation = 0;
    boolean solutionFound = false;
    int generations = 200;
    RuleBase rules = new RuleBase();
    public boolean runNewAlgorithm(){
        //RuleBase rules = new RuleBase();
        Population myPop = new Population(populationSize, true);
        ArrayList<Population> pop = new ArrayList<>();
        
        pop.add(myPop);
        
        for (int k = 0; k < populationSize; k++){
                myPop.setIndividualFitness(rules, k); //checks all individuals against ruleBase to set fitness for initial population
        }
        for (int i = 0; i < generations; i++) {
//            for (int n = 0; n < populationSize; n++){
//                Individual testtest = cloneIndividual(pop.get(i).getIndividual(n));
//                for (int p = 0; p < geneticLength; p++){
//                    System.out.print(testtest.getSingleGene(p));
//                }
//            System.out.println();
//            }
            Population finalPop = clonePopulation(evolutions(pop.get(i)));
            for (int k = 0; k < populationSize; k++){
                finalPop.setIndividualFitness(rules, k); //checks all individuals agains ruleBase to set fitness for all evolved populations
            }
            pop.add(finalPop);
            int mutatedFitness = 0;
            int bestFitness = 0;
            int indv = 0;
            generation++;
            for (int j = 0; j < populationSize; j++){ //stores best fitness of the generation
                Individual mutatedFitnessTest = cloneIndividual(finalPop.getIndividual(j));
                mutatedFitness = mutatedFitnessTest.returnFitness();
                if (mutatedFitness > bestFitness){
                    indv = j;
                    bestFitness = mutatedFitness;
                }
                if (mutatedFitness >= 64){ //if best fitness is required fitness then program ends
                    for (int p = 0; p < geneticLength; p++){
                        System.out.print(mutatedFitnessTest.getSingleGene(p));
                    }
                    System.out.println();
                    System.out.println("solution found after " + generation + " generations");
                    solutionFound = true;
                }
            }
            System.out.println("The best fitness was " + bestFitness + " for generation " + generation + " from individual " + indv);
            if (solutionFound == true){ //prints out the individual that had the required fitness
                for (int o = 0; o < populationSize; o++){
                    Individual generationTest = cloneIndividual(finalPop.getIndividual(o));
                    for (int p = 0; p < geneticLength; p++){
                        int gene = generationTest.getSingleGene(p);
                        //System.out.print(gene);
                    }
                    //System.out.println();
                }
            } else if (solutionFound != true) { //proceeds to the next generation
                System.out.println("No solution in generation " + generation);
            }
            if (solutionFound == true){
                break;
            }
        }
        
            
        
        return solutionFound;
    }
    
    public Population evolutions(Population newPop){
        
        Population selectedPop = new Population(populationSize, false);
        
        for (int i = 0; i < populationSize; i++){
            boolean same = true;
            int randomOne = 0;
            int randomTwo = 1;
            while (same){ //selects two individuals from the population randomly
                randomOne = randomNumber(0, populationSize - 1);
                randomTwo = randomNumber(0, populationSize - 1);
                if (randomOne != randomTwo){
                    same = false;
                }
            }
            
            Individual selected = cloneIndividual(tournamentSelection(newPop.getIndividual(randomOne), newPop.getIndividual(randomTwo))); //returns the individual that wins tournamentSelection
            selectedPop.addIndividual(i, selected);
        }
        
        Population crossoverPop = new Population(populationSize, false);
        int positionOne = 0;
        int positionTwo = 1;
        int runThroughs = (int) Math.round(populationSize * 0.5);
        for (int i = 0; i < runThroughs; i++){ //for half of pop size (as returns two individuals)
            int crossMin = 0;//(int) Math.round(geneticLength * 0);
            int crossMax = geneticLength;//(int) Math.round(geneticLength * 1);
            int crossoverPoint = randomNumber(crossMin, crossMax);
            crossoverPop.addIndividual(i, crossover(selectedPop.getIndividual(i), selectedPop.getIndividual(i+1), crossoverPoint)); //crosses individual one
            
            crossoverPop.addIndividual(i, crossover(selectedPop.getIndividual(i+1), selectedPop.getIndividual(i), crossoverPoint)); //crosses individual two
            positionOne = positionOne + 2;
            positionTwo = positionTwo + 2;
        }
        
        Population mutatedPop = new Population(populationSize, false);
        for (int i = 0; i < populationSize; i++){
            mutatedPop.addIndividual(i, mutate(crossoverPop.getIndividual(i))); //runs mutation for the population
        }
        
        return mutatedPop;
    }
    
    public Individual tournamentSelection (Individual individualOne, Individual individualTwo){
        int fitnessOne = individualOne.returnFitness();
        int fitnessTwo = individualTwo.returnFitness();
        
        if (fitnessOne > fitnessTwo){ //selects the individual with the highest fitness and returns it as the winning individual
            Individual winner = cloneIndividual(individualOne);
            return winner;
        } else if (fitnessOne < fitnessTwo){
            Individual winner = cloneIndividual(individualTwo);
            return winner;
        } else {
            Individual winner = cloneIndividual(individualOne);
            return winner;
        }
    }
    
    public Individual crossover (Individual individualOne, Individual individualTwo, int crossoverPoint){
        for (int i = crossoverPoint; i < geneticLength; i++){
            int gene = individualTwo.getSingleGene(i); //takes the genes from the second individual and replaces the first's with them from the crossover point
            individualOne.changeSingleGene(i, gene);
        }
        return individualOne;
    }
    
    public Individual mutate (Individual individual){
        int mutationRate = 7; //0.7% chance of mutation
        int bitPosition = 0;
        float randChoice = 0;
        
        for (int i = 0; i < geneticLength; i++){
            int gene = individual.getSingleGene(i);
            int mutateChance = randomNumber(0, 1000); //if 7 or under will mutate
            if (mutateChance <= mutationRate){
                if (bitPosition < rules.getRule(0).getCondition().length()){ //checking gene isn't a result
                    if (gene == 0){
                        randChoice = randomNumber(0,1);
                        //System.out.println(randChoice);
                        if (randChoice == 0){
                            individual.changeSingleGene(i, 1); //mutates 0 to 1
                            bitPosition++;
                        } else if (randChoice == 1){
                            individual.changeSingleGene(i, 2); //mutates 0 to 2
                            bitPosition++;
                        } else {
                            System.out.println("we have a problem");
                        }
                    } else if (gene == 1){
                        randChoice = randomNumber(0,1);
                        //System.out.println(randChoice);
                        if (randChoice == 0){
                            individual.changeSingleGene(i, 0);
                            bitPosition++;
                        } else if (randChoice == 1){
                            individual.changeSingleGene(i, 2);
                            bitPosition++;
                        } else {
                            System.out.println("we have a problem");
                        }
                    } else if (gene == 2){
                        randChoice = randomNumber(0,1);
                        //System.out.println(randChoice);
                        if (randChoice == 0){
                            individual.changeSingleGene(i, 0);
                            bitPosition++;
                        } else if (randChoice == 1){
                            individual.changeSingleGene(i, 1);
                            bitPosition++;
                        } else {
                            System.out.println("we have a problem");
                        }
                    }
                } else { //if gene is a result it can't mutate into a wildcard
                    bitPosition = 0;
                    if (gene == 0){
                        individual.changeSingleGene(i, 1);
                    } else if (gene == 1){
                        individual.changeSingleGene(i, 0);
                    } else {
                        System.out.println("we have a problem");
                    }
                }
            } else {
                bitPosition++;
                if (bitPosition > rules.getRule(0).getCondition().length()){
                    bitPosition = 0;
                }
            }
        }
        Individual mutated = cloneIndividual(individual);
        return mutated;
    }
    
    public int randomNumber(int min, int max){ //produces a random number within parameters
        
        int chosen = ThreadLocalRandom.current().nextInt(min, max + 1);
        
        return chosen;
    }
    
    public Individual cloneIndividual(Individual individual){ //copies an individual for further use as java struggles to copy objects
        Individual newIndividual = new Individual();
        
        for (int i  = 0; i < geneticLength; i++){
            int gene = individual.getSingleGene(i);
            newIndividual.changeSingleGene(i, gene);
        }
        newIndividual.changeFitness(rules);
        //System.out.println("changing fitness for new individual");
        
        return newIndividual;
    }
    
    public Population clonePopulation(Population oldPop){ //copies a population as java struggles to copy objects
        Population newPop = new Population(populationSize, false);
        
        for(int i = 0; i < populationSize; i++){
            newPop.addIndividual(i, oldPop.getIndividual(i));
        }
        return newPop;
    }
    
    
}
