package org.custom.code;

import java.lang.Math;
import java.math.BigDecimal;
import org.custom.helper.PreprocessGenerator;
import org.custom.helper.PreprocessGenerator.PreprocessDataType;


class EconomicDispatchPreprocessor{

    public static void sin(BigDecimal val, int partyId, PreprocessGenerator gen){
        int fixPointPower = 5;
        gen.input(val.scaleByPowerOfTen(fixPointPower).intValueExact(), PreprocessDataType.SInt);
    }
    public static void main(String args[]) {
        int numberOfInputParties = 3;
        int numberOfComputationParties = 3;
        int threshold = 1;
        BigDecimal[] Ad = { new BigDecimal("255"), new BigDecimal("729"), new BigDecimal("400")};
        BigDecimal[] Bd = { new BigDecimal("8.4"), new BigDecimal("6.3"), new BigDecimal("7.5")};
        BigDecimal[] Cd = { new BigDecimal("0.0025"), new BigDecimal("0.0081"), new BigDecimal("0.0025")};
        BigDecimal[] mind = { new BigDecimal("45"), new BigDecimal("45"), new BigDecimal("47.5")};
        BigDecimal[] maxd = { new BigDecimal("350"), new BigDecimal("350"), new BigDecimal("450")};

        PreprocessGenerator[] generators = new PreprocessGenerator[numberOfInputParties];
        for(int i = 1; i <= numberOfInputParties; i++) {
            generators[i - 1] = new PreprocessGenerator(i, numberOfComputationParties, threshold);
        }
        
        /* generate input */
        for(int i = 1; i <= numberOfInputParties; i++){
            sin(Ad[i-1], i, generators[i-1]);
            sin(Bd[i-1], i, generators[i-1]);
            sin(Cd[i-1], i, generators[i-1]);
            sin(mind[i-1], i, generators[i-1]);
            sin(maxd[i-1], i, generators[i-1]);
        }

        /* export input */
        for(int i = 1; i <= numberOfInputParties; i++) {
            generators[i - 1].exportToFile();
        }
    }
}