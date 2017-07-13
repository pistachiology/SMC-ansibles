package org.custom.helper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import dk.alexandra.fresco.framework.util.Pair;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.suite.bgw.ShamirShare;

public class PreprocessGenerator {
    private Integer numberOfComputationParties;
    private Integer threshold;
    private Integer inputIterator;
    private Integer inputPartyId;
    private List<Pair<Integer, PreprocessDataType> > inputs;
    private BigInteger modulus = new BigInteger("618970019642690137449562111");

    public enum PreprocessDataType {
        SInt, OInt
    }

    public PreprocessGenerator(Integer inputPartyId, Integer numberOfComputationParties, Integer threshold) {
        this.numberOfComputationParties = numberOfComputationParties;
        this.threshold = threshold;
        this.inputPartyId = inputPartyId;
        this.inputs = new ArrayList<Pair<Integer, PreprocessDataType> >();
        this.resetInputIterator(); 
        ShamirShare.setPrimeNumber(modulus);
    }   

    public void input(Integer input, PreprocessDataType dataType) {
        this.inputs.add(new Pair<Integer, PreprocessDataType>(input, dataType));
    }


    public List<PreprocessedPlayer> generatePreprocessedPlayer() {
        List<PreprocessedPlayer> preprocessedPlayers = new ArrayList<PreprocessedPlayer>();
        for(int i = 0; i < numberOfComputationParties; i++) {
            preprocessedPlayers.add(new PreprocessedPlayer(i + 1));
        }
        this.resetInputIterator();
        while(this.haveNextInput()) {
            Pair<Integer, PreprocessDataType> pair = this.getNextInput();
            System.out.println("Processed ");
            switch(pair.getSecond()) {
                case SInt:
                   ShamirShare[] secret = this.preprocessSecretInt(pair.getFirst());
                    for(int i = 0; i < secret.length; i++) {
                        preprocessedPlayers.get(i).addToSecret(secret[i]);
                    }
                break;

                case OInt:
                    BigInteger[] openInt = this.preprocessOpenInteger(pair.getFirst());
                    for(int i = 0; i < openInt.length; i++) {
                        preprocessedPlayers.get(i).addToOpen(BigInteger.valueOf(pair.getFirst()));
                    }
                break;
                default:
            }
        }
        return preprocessedPlayers;
    }

    private ShamirShare[] preprocessSecretInt(Integer value) {
        return ShamirShare.createShares(BigInteger.valueOf(value), 
                            this.numberOfComputationParties, threshold);
    }

    private BigInteger[] preprocessOpenInteger(Integer value){
        BigInteger[] result = new BigInteger[this.numberOfComputationParties];
        for(Integer i = 0; i < this.numberOfComputationParties; i++){
            result[i] = BigInteger.valueOf(value);
        }
        return result;
    }

    private boolean haveNextInput() {
        if (inputIterator >= inputs.size()) return false;
        return true;

    }
    private Pair<Integer, PreprocessDataType> getNextInput() {
        if (inputIterator >= inputs.size()) return null;
        return inputs.get(inputIterator++);
    }

    public void resetInputIterator() {
        inputIterator = 0;
    }

    public static void exportPreprocessedPlayersToFile(List<PreprocessedPlayer> preprocessedPlayers, String baseFoldername) {
        int i =0;
        for(PreprocessedPlayer player: preprocessedPlayers){
            player.exportToFile("preprocessedInput-1/computationId-" + (i+1));
            i++;
        }
    }
    
    public void exportToFile(String baseFoldername, String outputFolder) {
        List<PreprocessedPlayer> preprocessedPlayers = this.generatePreprocessedPlayer();
        int i = 0;
        for(PreprocessedPlayer player: preprocessedPlayers){
            // player.exportToFile(baseFoldername + "/computationId-" + (i+1));
            player.exportToFile(outputFolder + "/computationId-" + (i+1) + "/" + baseFoldername + (inputPartyId));
            i++;
        }
    }

    public void exportToFile(String outputFolder){
        this.exportToFile("preprocessedInput-", outputFolder);
    }
    public void exportToFile(){
        this.exportToFile("output");
    }

}