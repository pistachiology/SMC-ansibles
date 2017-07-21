package org.custom.code;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.ObjectUtils.Null;
import org.custom.helper.BgwComparisonProtocolBuilder;
import org.custom.helper.PreprocessedFileInput;
import org.custom.helper.PreprocessedFilesInputter;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.ProtocolFactory;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.configuration.CmdLineUtil;
import dk.alexandra.fresco.framework.sce.SCE;
import dk.alexandra.fresco.framework.sce.SCEFactory;
import dk.alexandra.fresco.framework.sce.configuration.SCEConfiguration;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.compare.ComparisonProtocolFactory;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.builder.ComparisonProtocolBuilder;
import dk.alexandra.fresco.lib.helper.builder.NumericIOBuilder;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;
import dk.alexandra.fresco.lib.helper.sequential.SequentialProtocolProducer;
import dk.alexandra.fresco.lib.compare.ComparisonProtocolFactoryImpl;
import dk.alexandra.fresco.lib.compare.RandomAdditiveMaskFactory;
import dk.alexandra.fresco.lib.compare.RandomAdditiveMaskFactoryImpl;
import dk.alexandra.fresco.lib.conversion.IntegerToBitsFactory;
import dk.alexandra.fresco.lib.conversion.IntegerToBitsFactoryImpl;
import dk.alexandra.fresco.lib.conversion.IntegerToBitsProtocol;
import dk.alexandra.fresco.lib.math.integer.PreprocessedNumericBitFactory;
import dk.alexandra.fresco.lib.math.integer.binary.RightShiftFactory;
import dk.alexandra.fresco.lib.math.integer.binary.RightShiftFactoryImpl;
import dk.alexandra.fresco.lib.math.integer.exp.ExpFromOIntFactory;
import dk.alexandra.fresco.lib.math.integer.exp.PreprocessedExpPipeFactory;
import dk.alexandra.fresco.lib.math.integer.inv.LocalInversionFactory;
import dk.alexandra.fresco.lib.math.integer.min.MinInfFracProtocol;


/**
 * A simple demo computing the distance between two secret points
 */
public class BgwCompareDemo implements Application {
	
	private static final long serialVersionUID = 6415583508947017554L;
	private static final int numberofParties = 3;
	
	private int myId, myX;
	private SInt[] x = new SInt[numberofParties];
	public OInt partyId;
	
	public BgwCompareDemo(int id, int x) {
		this.myId = id;
		this.myX = x;
	}


	public ProtocolProducer prepareApplication(ProtocolFactory factory) {
		BasicNumericFactory bnFac = (BasicNumericFactory)factory;
		NumericIOBuilder iob = new NumericIOBuilder(bnFac);
		LocalInversionFactory localInversionFactory = (LocalInversionFactory) factory;
		PreprocessedNumericBitFactory numericBitFactory = (PreprocessedNumericBitFactory) factory;
		ExpFromOIntFactory expFromOIntFactory = (ExpFromOIntFactory) factory;
        RandomAdditiveMaskFactory randomAdditiveMaskFactory = (RandomAdditiveMaskFactory) new RandomAdditiveMaskFactoryImpl(bnFac, numericBitFactory);
        RightShiftFactory rightShiftFactory = (RightShiftFactory) new RightShiftFactoryImpl(bnFac, randomAdditiveMaskFactory, localInversionFactory);

		SequentialProtocolProducer seq = new SequentialProtocolProducer();
        IntegerToBitsFactory itbFactory = (IntegerToBitsFactory) new IntegerToBitsFactoryImpl(bnFac, rightShiftFactory);
							
		NumericProtocolBuilder npb = new NumericProtocolBuilder(bnFac);
		BgwComparisonProtocolBuilder cmpb = new BgwComparisonProtocolBuilder(bnFac, itbFactory);

		PreprocessedFilesInputter pfi = new PreprocessedFilesInputter(2, "output/computationId-" + myId);
		// Input points

		long startTime = System.currentTimeMillis();
		iob.beginParScope();
		for(int i = 1 ; i <= numberofParties; i++){
			x[i - 1] = (myId == (i)) ? iob.input(myX, (i)) : iob.input(i);
		}
		iob.endCurScope();
		seq.append(iob.getProtocol());

		SInt maxParty = npb.getSInt(0);
		SInt maxPartyValue = x[0];
		SInt cmpVal = npb.getSInt();

		for(int i = 1; i < numberofParties; i++){
			cmpVal = cmpb.greaterEqual(x[i], maxPartyValue);
			seq.append(cmpb.getProtocol());

			maxPartyValue = npb.add(npb.mult(cmpVal, x[i]), npb.mult(npb.sub(npb.getSInt(1), cmpVal), maxPartyValue));  
			maxParty = npb.add(npb.mult(cmpVal, npb.getSInt(i)), npb.mult(npb.sub(npb.getSInt(1), cmpVal), maxParty));  
			seq.append(npb.getProtocol());
		}


		// Output result
		this.partyId = iob.output(maxPartyValue);
		seq.append(iob.getProtocol());
		long endTime = System.currentTimeMillis();
		System.out.println("Elapsed time: " + ((endTime - startTime) / 1000.0));


		return seq;
	}

	static void bgwCompareDemoMain(String[]args){
		CmdLineUtil cmdUtil = new CmdLineUtil();
		SCEConfiguration sceConf = null;
		int x;
		boolean inParty = false;
		x = 0;
		try {	
			cmdUtil.addOption(Option.builder("x")
					.desc("The integer x coordinate of this party. "
							+ "Note only party 1,2 and 3 should supply this input.")
					.hasArg()
					.build());	
			CommandLine cmd = cmdUtil.parse(args);
			sceConf = cmdUtil.getSCEConfiguration();
			for(int i = 1; i <= numberofParties; i++) {
				if (sceConf.getMyId() == i){
					if(!cmd.hasOption("x"))
						throw new ParseException("Party " + i + " must submit input");
					inParty = true;
					x = Integer.parseInt(cmd.getOptionValue("x"));
				}
			}
			if(!inParty) {
				throw new ParseException("Only first " + numberofParties + " must submit input");
			}
		} catch (ParseException | IllegalArgumentException e) {
			System.out.println("Error: " + e);
			System.out.println();
			cmdUtil.displayHelp();
			System.exit(-1);	
		} 

		BgwCompareDemo bgwCompareDemo = new BgwCompareDemo(sceConf.getMyId(), x);
		SCE sce = SCEFactory.getSCEFromConfiguration(sceConf);
		try {
			System.out.println("Starting application");
			sce.runApplication(bgwCompareDemo);
		} catch (Exception e) {
			System.out.println("Error while doing MPC: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		int partyId = bgwCompareDemo.partyId.getValue().intValue();
		System.out.println("Most value is come from party " + (partyId + 1));
	}
}

