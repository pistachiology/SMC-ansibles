package org.custom.code;

import org.custom.helper.PreprocessGenerator;
import org.custom.helper.PreprocessGenerator.PreprocessDataType;


class GeneratePreprocessedInputDemo {
    public static void main(String args[]) {
        int numberOfInputParties = 500;
        int numberOfComputationParties = 3;
        int threshold = 1;
        PreprocessGenerator[] generators = new PreprocessGenerator[numberOfInputParties];
        for(int i = 1; i <= numberOfInputParties; i++) {
            generators[i - 1] = new PreprocessGenerator(i, numberOfComputationParties, threshold);
        }
        
        /* generate input */
        int maxPartyId = ((int)(Math.random() * numberOfInputParties)) + 1;
        for(int i = 1; i <= numberOfInputParties; i++){
            if(i == maxPartyId) {
                generators[i - 1].input(1000000, PreprocessDataType.SInt);
            } else { 
                generators[i - 1].input(((int)Math.random() * 100000) + 1, PreprocessDataType.SInt);
            }
        }
        System.out.println("Max party id is " + maxPartyId);

        /* export input */
        for(int i = 1; i <= numberOfInputParties; i++) {
            generators[i - 1].exportToFile();
        }
    }
}