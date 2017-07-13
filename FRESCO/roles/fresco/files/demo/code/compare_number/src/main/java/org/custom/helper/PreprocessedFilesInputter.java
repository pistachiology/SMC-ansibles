package org.custom.helper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.custom.exception.EndOfInputException;

import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.suite.bgw.ShamirShare;
import dk.alexandra.fresco.suite.bgw.integer.BgwOInt;
import dk.alexandra.fresco.suite.bgw.integer.BgwSInt;

public class PreprocessedFilesInputter {
    private String baseFoldername = "preprocessedInput-";
    private String inputFolder = "output";
    private Integer numberOfInputParties;
    private List<PreprocessedFileInput> filesInput;

    public PreprocessedFilesInputter (Integer numberOfInputParties, String baseFoldername, String inputFolder) {
        this.numberOfInputParties = numberOfInputParties;
        this.baseFoldername = baseFoldername;
        this.inputFolder = inputFolder;
        this.filesInput = new ArrayList<PreprocessedFileInput>();
        doInit();
    }

    private void doInit() {
        for(int i = 1; i<= numberOfInputParties; i++) {
            filesInput.add(new PreprocessedFileInput(i, baseFoldername, inputFolder));
        }
	}

	public PreprocessedFilesInputter (Integer numberOfInputParties) {
        this(numberOfInputParties, "preprocessedInput-", "output");
    }

	public PreprocessedFilesInputter (Integer numberOfInputParties, String inputFolder) {
        this(numberOfInputParties, "preprocessedInput-", inputFolder);
    }

    public SInt getSInt(int inputPartyId) throws EndOfInputException{
        PreprocessedFileInput fileInput = filesInput.get(inputPartyId - 1);
        if(!fileInput.hasNextSecret()) {
            throw new EndOfInputException("end of secret file input");
        }
        ShamirShare share = fileInput.getNextSecret();
        SInt ret = new BgwSInt(share);
        return ret;
    }

    public OInt getOInt(int inputPartyId) throws EndOfInputException {
        PreprocessedFileInput fileInput = filesInput.get(inputPartyId - 1);
        if(!fileInput.hasNextOpen()) {
            throw new EndOfInputException("end of open file input");
        }
        BigInteger value = fileInput.getNextOpen();
        OInt ret = new BgwOInt(value);
        return ret;

    }

}