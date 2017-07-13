package org.custom.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import dk.alexandra.fresco.suite.bgw.ShamirShare;

public class PreprocessedPlayer {

   private List<ShamirShare> secretInts;
   private List<BigInteger> openInts;
   private Integer computationPartyId;
   //private BigInteger modulus = new BigInteger("618970019642690137449562111");

    
    public PreprocessedPlayer (Integer computationPartyId) {
        this.secretInts = new LinkedList<ShamirShare>();
        this.openInts = new LinkedList<BigInteger>();
        this.computationPartyId = computationPartyId;
    }

    public void addToSecret(ShamirShare secret) {
        secretInts.add(secret);
    }

    public void addToOpen(BigInteger value) {
        openInts.add(value);
    }
    
    public void exportToFile(String baseFoldername){
        this.exportSecretsToFile(baseFoldername);
        this.exportOpenIntsToFile(baseFoldername);
    }


    public void exportOpenIntsToFile(String baseFoldername) {
        this.exportOpenIntsToFile(baseFoldername, "opens");
    }    

    private void exportOpenIntsToFile(String baseFoldername, String filename) {
        PrintWriter writer = _getPrintWriter(baseFoldername, filename);
        for(BigInteger openInt: openInts){
            writer.println(openInt);
        }
        writer.close();
	}

	public void exportSecretsToFile(String baseFoldername, String filename) {
        PrintWriter writer = _getPrintWriter(baseFoldername, filename);
        for(ShamirShare secret : secretInts) {
            writer.println(Byte.toString(secret.getPoint()) + " " + secret.getField());
        }
        writer.close();
    }

    public void exportSecretsToFile(String baseFoldername) {
        this.exportSecretsToFile(baseFoldername, "secrets");
    }


    private PrintWriter _getPrintWriter(String baseFoldername, String filename) {
        File file = new File(baseFoldername, filename);

        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if(file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter fileWriter;
        try {
            fileWriter = new PrintWriter(file);
            return fileWriter;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}