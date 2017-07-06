package org.custom.code;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

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
public class CompareDemo implements Application {
	
	private static final long serialVersionUID = 6415583508947017554L;
	private static final int numberofParties = 3;
	
	private int myId, myX;
	private SInt[] x = new SInt[numberofParties];
	public OInt partyId;
	
	public CompareDemo(int id, int x) {
		this.myId = id;
		this.myX = x;
	}

	public SInt doCompare(SInt A, SInt B, BasicNumericFactory bnFac, NumericProtocolBuilder npb){
		SInt result = npb.getSInt(0);
		SInt[] result_bits = new SInt[64];
		
		PreprocessedNumericBitFactory preprocessedNumericBitFactory = (PreprocessedNumericBitFactory) bnFac;
		RandomAdditiveMaskFactory randomAdditiveMaskFactory = new RandomAdditiveMaskFactoryImpl(bnFac, preprocessedNumericBitFactory);
		LocalInversionFactory localInversionFactory = (LocalInversionFactory) bnFac;
		RightShiftFactory rightShiftFactory = new RightShiftFactoryImpl(bnFac, randomAdditiveMaskFactory, localInversionFactory);
		IntegerToBitsFactory integerToBitsFactory = new IntegerToBitsFactoryImpl(bnFac, rightShiftFactory);
		SInt subtract_result = npb.sub(A, B);
		IntegerToBitsProtocol itbp = integerToBitsFactory.getIntegerToBitsCircuit(subtract_result, 64, result_bits);
		npb.addProtocolProducer(itbp);
		return result_bits[0];
	}

	public ProtocolProducer prepareApplication(ProtocolFactory factory) {
		BasicNumericFactory bnFac = (BasicNumericFactory)factory;
		NumericIOBuilder iob = new NumericIOBuilder(bnFac);
		LocalInversionFactory localInvFactory = (LocalInversionFactory) factory;
		PreprocessedNumericBitFactory numericBitFactory = (PreprocessedNumericBitFactory) factory;
		ExpFromOIntFactory expFromOIntFactory = (ExpFromOIntFactory) factory;
		PreprocessedExpPipeFactory expFactory = (PreprocessedExpPipeFactory) factory;
		SequentialProtocolProducer seq = new SequentialProtocolProducer();
							
		ComparisonProtocolFactoryImpl compFactory = new ComparisonProtocolFactoryImpl(
				80, bnFac, localInvFactory,
				numericBitFactory, expFromOIntFactory,
				expFactory);

		NumericProtocolBuilder npb = new NumericProtocolBuilder(bnFac);
		ComparisonProtocolBuilder compBuilder = new ComparisonProtocolBuilder(compFactory, bnFac);
		// Input points
		iob.beginParScope();
		for(int i = 1 ; i <= numberofParties; i++){
			x[i - 1] = (myId == (i)) ? iob.input(myX, (i)) : iob.input(i);
		}
		iob.endCurScope();
		seq.append(iob.getProtocol());
		// Compute distance squared (note, square root computation can be done publicly)
		// SInt result = npb.add(x1, npb.add(x2, x3));
		//SInt[] maxParty = new SInt[] { npb.getSInt(0), npb.getSInt(0) };
		// SInt[] maxPartyValue = new SInt[] { x[0], x[0] } ;
		// SInt maxParty = npb.getSInt(0);
		// SInt maxPartyValue = x[0];
		// SInt cmpVal = npb.getSInt(1);
		// for(int i = 1; i < numberofParties; i++){
		// 		cmpVal = compBuilder.compare(maxPartyValue, x[i]);
		// 		maxPartyValue = npb.add(npb.mult(cmpVal, x[i]), npb.mult(npb.sub(npb.getSInt(1), cmpVal), maxPartyValue));  
		// 		maxParty = npb.add(npb.mult(cmpVal, npb.getSInt(i)), npb.mult(npb.sub(npb.getSInt(1), cmpVal), maxParty));  
	    // }

		SInt maxParty = bnFac.getSInt(0);
		SInt maxPartyValue = x[0];
		// maxParty[0] = npb.getSInt(0);
		// // Read file SortingProtocolBuilder
		// maxPartyValue[0] = x[0];
		for(int i = 1; i < numberofParties; i++){
				SInt cmpVal  = compBuilder.compare(maxPartyValue, x[i]);
				seq.append(compBuilder.getProtocol());

				maxPartyValue = npb.add(npb.mult(cmpVal, x[i]), npb.mult(npb.sub(npb.getSInt(1), cmpVal), maxPartyValue));  
				maxParty = npb.add(npb.mult(cmpVal, npb.getSInt(i)), npb.mult(npb.sub(npb.getSInt(1), cmpVal), maxParty));  
				seq.append(npb.getProtocol());
	    }
		this.partyId = iob.output(maxParty);
		seq.append(iob.getProtocol());

		// Output result

		return seq;
	}

	static void compareDemoMain(String[]args){
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
		System.out.println("Hello ID " + sceConf.getMyId());
		CompareDemo compareDemo = new CompareDemo(sceConf.getMyId(), x);
		SCE sce = SCEFactory.getSCEFromConfiguration(sceConf);
		try {
			sce.runApplication(compareDemo);
		} catch (Exception e) {
			System.out.println("Error while doing MPC: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		int partyId = compareDemo.partyId.getValue().intValue();

		System.out.println("Most value is come from party " + (partyId + 1));
	}
}

