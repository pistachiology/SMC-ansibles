package org.custom.code;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.custom.helper.BgwComparisonProtocolBuilder;
import org.custom.helper.BgwDivisionProtocolBuilder;
import org.custom.helper.BgwMinMaxProtocolBuilder;
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
import dk.alexandra.fresco.lib.compare.RandomAdditiveMaskFactory;
import dk.alexandra.fresco.lib.compare.RandomAdditiveMaskFactoryImpl;
import dk.alexandra.fresco.lib.conversion.IntegerToBitsFactory;
import dk.alexandra.fresco.lib.conversion.IntegerToBitsFactoryImpl;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.builder.NumericIOBuilder;
import dk.alexandra.fresco.lib.helper.sequential.SequentialProtocolProducer;
import dk.alexandra.fresco.lib.math.integer.PreprocessedNumericBitFactory;
import dk.alexandra.fresco.lib.math.integer.binary.BitLengthFactory;
import dk.alexandra.fresco.lib.math.integer.binary.BitLengthFactoryImpl;
import dk.alexandra.fresco.lib.math.integer.binary.RightShiftFactory;
import dk.alexandra.fresco.lib.math.integer.binary.RightShiftFactoryImpl;
import dk.alexandra.fresco.lib.math.integer.division.DivisionFactory;
import dk.alexandra.fresco.lib.math.integer.division.DivisionFactoryImpl;
import dk.alexandra.fresco.lib.math.integer.exp.ExpFromOIntFactory;
import dk.alexandra.fresco.lib.math.integer.exp.ExponentiationFactory;
import dk.alexandra.fresco.lib.math.integer.exp.ExponentiationFactoryImpl;
import dk.alexandra.fresco.lib.math.integer.inv.LocalInversionFactory;

class BgwDivisonDemo implements Application {

    private static final int numberofParties = 3;

    private int myId;
    public OInt output;


    public BgwDivisonDemo(int id) {
        this.myId = id;
    }

    public ProtocolProducer prepareApplication(ProtocolFactory factory) {
        SequentialProtocolProducer seq = new SequentialProtocolProducer();
        BasicNumericFactory basicNumericFactory = (BasicNumericFactory) factory;

		LocalInversionFactory localInversionFactory = (LocalInversionFactory) factory;
		PreprocessedNumericBitFactory numericBitFactory = (PreprocessedNumericBitFactory) factory;
		ExpFromOIntFactory expFromOIntFactory = (ExpFromOIntFactory) factory;
        RandomAdditiveMaskFactory randomAdditiveMaskFactory = (RandomAdditiveMaskFactory) new RandomAdditiveMaskFactoryImpl(basicNumericFactory, numericBitFactory);
        RightShiftFactory rightShiftFactory = (RightShiftFactory) new RightShiftFactoryImpl(basicNumericFactory, randomAdditiveMaskFactory, localInversionFactory);
        IntegerToBitsFactory integerToBitsFactory = (IntegerToBitsFactory) new IntegerToBitsFactoryImpl(basicNumericFactory, rightShiftFactory);
        BgwComparisonProtocolBuilder bgwComparisonProtocolBuilder = new BgwComparisonProtocolBuilder(basicNumericFactory, integerToBitsFactory);
        BgwMinMaxProtocolBuilder bgwMinMaxProtocolBuilder = new BgwMinMaxProtocolBuilder(basicNumericFactory, bgwComparisonProtocolBuilder);
        BitLengthFactory bitLengthFactory = new BitLengthFactoryImpl(basicNumericFactory, integerToBitsFactory);
        ExponentiationFactory exponentiationFactory = new ExponentiationFactoryImpl(basicNumericFactory, integerToBitsFactory);
        DivisionFactory divisionFactory = new DivisionFactoryImpl(basicNumericFactory, rightShiftFactory, bitLengthFactory, exponentiationFactory);
        BgwDivisionProtocolBuilder bgwDivisionProtocolBuilder = new BgwDivisionProtocolBuilder(basicNumericFactory, divisionFactory);
        NumericIOBuilder iob = new NumericIOBuilder(basicNumericFactory);
        SInt left =  basicNumericFactory.getSInt(1000);
        SInt right =  basicNumericFactory.getSInt(5);  
        SInt result = bgwDivisionProtocolBuilder.division(left, right);
        seq.append(bgwDivisionProtocolBuilder.getProtocol());

        // Protocol divisionProtocol 
        // seq.append(BgwDivisionProtocol)
        output = iob.output(result);
        seq.append(iob.getProtocol());

        return seq;
    }

    static void bgwDivisonDemo(String[] args){
        CmdLineUtil cmdUtil = new CmdLineUtil();
        SCEConfiguration sceConf = null;
        try {
            CommandLine cmd = cmdUtil.parse(args);
            sceConf = cmdUtil.getSCEConfiguration();
            if(sceConf.getMyId() <= 0 || sceConf.getMyId() > numberofParties) {
                throw new ParseException("Party Id must between 1 and " + numberofParties);
            }
        } catch (ParseException e){
            e.printStackTrace();
            System.exit(-1);
        }

        BgwDivisonDemo bgwDivisonDemo = new BgwDivisonDemo(sceConf.getMyId());
        SCE sce = SCEFactory.getSCEFromConfiguration(sceConf);
		try {
			System.out.println("Starting application");
			sce.runApplication(bgwDivisonDemo);
		} catch (Exception e) {
			System.out.println("Error while doing MPC: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
        }    
        System.out.println("Max value is come from party " + (bgwDivisonDemo.output.getValue().intValue() + 1));
    }

}