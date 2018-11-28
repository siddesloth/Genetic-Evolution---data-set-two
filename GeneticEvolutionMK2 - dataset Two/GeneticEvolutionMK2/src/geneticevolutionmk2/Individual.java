/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticevolutionmk2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author i2-rickwood
 */
public class Individual {
    int geneticLength = 80; //sets amount of genes
    int gene;
    int[] genes = new int[geneticLength];
    public int fitness = 0;
    
    public Individual(){
        int position = 0;
        for (int i = 0; i < geneticLength; i++){ //generates a random original individual
            if (position != 7){
                gene = ThreadLocalRandom.current().nextInt(0, 2 + 1);
                genes[i] = gene;
                position++;
            } else {
                gene = ThreadLocalRandom.current().nextInt(0, 1 + 1);
                genes[i] = gene;
                position = 0; 
            }
        }
    }
    
    public void setFitness(RuleBase ruleBase){
        ArrayList<String> geneRules = new ArrayList<>();
        int newFitness = 0;
        String Gene = "";
        int stringMax = 0;
        int setsAdded = 0;
        
        for (int i = 0; i < geneticLength; i++){ //for all genes of the individual
            String Sgene = Integer.toString(getSingleGene(i)); //set single gene to string
            Gene = Gene + Sgene; //add gene to existing string
            stringMax++;
            if (stringMax == (ruleBase.getRule(0).getCondition().length() + 1)){ //if string length is now equal to condition length + result
                geneRules.add(setsAdded, Gene); //add string to arrayList
                setsAdded++;
                stringMax = 0; //reset string count
                Gene = ""; //reset string of genes
            }
        }
        for (int j = 0; j < ruleBase.getListSize(); j++){ //selects a rule from the data set
            //System.out.println("Testing for ruleBase rule " + j);
            String Con = ruleBase.getRule(j).getCondition();
            String Res = ruleBase.getRule(j).getResult();
            boolean flag = false;
            for (int k = 0; !flag && k < geneRules.size(); k++){ //compare to all local rules
                boolean conMatch = false;
                boolean resMatch = false;
                String localRule = geneRules.get(k);
                //System.out.println("The condition is " + Con + " the result is " + Res + " the local rule is " + localRule);
                conMatch = matchCon(Con, localRule, ruleBase.getRule(k).getCondition().length()); //comparing conditions
                if (conMatch){
                    resMatch = matchRes(Res, localRule, ruleBase.getRule(k).getCondition().length());
                    if (resMatch){
                        newFitness++; //increase fitness if condition + result match
                        //System.out.println("increasing fitness");
                        flag = true;
                    } else {
                        //System.out.println("result didn't match");
                        flag = true; //break if result didn't match
                    }
                }
            }
        }
        fitness = newFitness;
    }
    
    public boolean matchCon(String Con, String localRule, int conLength){
        boolean matched = false;
        int matches = 0;
        boolean subFlag = false;
        for (int o = 0; !subFlag && o < conLength; o++){ //simply compares chars from local rule set to condition to see if all match, if they do returns true
            char localChar = localRule.charAt(o);
            char conChar = Con.charAt(o);
            if (localChar == conChar){
                matches++;
                //System.out.println("Sucessful match");
            } else if (localChar == '2'){
                matches++;
                //System.out.println("Sucessful match");
            } else {
                matches = 0;
                //System.out.println("Failed match");
                subFlag = true;
            }
        }
        if (matches == conLength){
            matched = true;
        }
        return matched;
    }
    
    public boolean matchRes(String Res, String localRule, int conLength){ //simply compares the results and if they match returns true
        boolean matched = false;
        char result = Res.charAt(0);
        char localResult = localRule.charAt(conLength);
        if (result == localResult){
            matched = true;
        }
        return matched;
    }
    
    public int returnFitness(){
        return fitness;
    }
    
    public void changeFitness(RuleBase ruleBase){
        setFitness(ruleBase);
    }
    
    
    public int getSingleGene(int gene){ //returns a gene
        return genes[gene];
    }
    
    public void changeSingleGene(int gene, int newgene){ //changes an existing gene
        genes[gene] = newgene;
    }
}
