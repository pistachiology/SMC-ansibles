package org.custom.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import dk.alexandra.fresco.suite.bgw.ShamirShare;

public class PreprocessedFileInput{

    private String baseFoldername = "preprocessedInput-";
    private String inputFolder = "input";
    private Integer inputPartyId;
    private List<ShamirShare> secrets;
    private List<BigInteger> opens;
    private ListIterator<ShamirShare> secretsIt = null;
    private ListIterator<BigInteger> opensIt = null;

    public PreprocessedFileInput(Integer inputPartyId, String baseFoldername, String inputFolder){
        this.baseFoldername = baseFoldername;
        this.inputFolder = inputFolder;
        this.inputPartyId = inputPartyId;
        secrets = new ArrayList<ShamirShare>();
        opens = new ArrayList<BigInteger>();

        doParseInput();
    }

    public PreprocessedFileInput(Integer inputPartyId) {
        this(inputPartyId, "preprocessedInput-", "output");
    }

    public boolean hasNextSecret() {
        if(secretsIt == null) {
            secretsIt = secrets.listIterator();
        }
        return secretsIt.hasNext();
    }

    public ShamirShare getNextSecret() {
        if(secretsIt == null) {
            secretsIt = secrets.listIterator();
        }
        return secretsIt.next();
    }

    public boolean hasNextOpen() {
        if(opensIt == null) {
            opensIt = opens.listIterator();
        }
        return opensIt.hasNext();
    }

    public BigInteger getNextOpen() {
        if(opensIt == null) {
            opensIt = opens.listIterator();
        }
        return opensIt.next();
    }
    private void doParseInput() {
        this.doParseSecrets();
        this.doParseOpens();
    }
	private List<BigInteger> doParseOpens() {
        if(opens.size() != 0) return opens;
        try {
            BufferedReader reader = _getReaderFromFilePath(getOpenFilePath());
            for(String line = reader.readLine(); line != null; line = reader.readLine()){
                opens.add(new BigInteger(line));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return opens;
	}
	private List<ShamirShare> doParseSecrets() {
        if(secrets.size() != 0) return secrets;
        try {
            BufferedReader reader = _getReaderFromFilePath(getSecretFilePath());
            for(String line = reader.readLine(); line != null; line = reader.readLine()){
                String[] rawPointAndField = line.split(" ");
                Integer point = Integer.parseInt(rawPointAndField[0]);
                BigInteger fieldValue = new BigInteger(rawPointAndField[1]);
                secrets.add(new ShamirShare(point, fieldValue));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return secrets;
    }
    
    private String getInputFolderName() {
        return inputFolder + "/" + baseFoldername + inputPartyId + "/";
    }

    private BufferedReader _getReaderFromFilePath(String filePath) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        return reader;

    }

    private String getSecretFilePath() {
        return getInputFolderName() + "secrets";
    }

    private String getOpenFilePath() {
        return getInputFolderName() + "opens";
    }



}