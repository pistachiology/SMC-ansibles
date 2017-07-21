package org.custom.code;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.custom.helper.BgwComparisonProtocolBuilder;
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
import dk.alexandra.fresco.lib.math.integer.binary.RightShiftFactory;
import dk.alexandra.fresco.lib.math.integer.binary.RightShiftFactoryImpl;
import dk.alexandra.fresco.lib.math.integer.exp.ExpFromOIntFactory;
import dk.alexandra.fresco.lib.math.integer.inv.LocalInversionFactory;

class BgwFindMaxMinDemo implements Application {

    private static final int numberofParties = 3;
    private static final int numberofInputParties = 500;

    private int myId;
    public OInt maxId, minId;


    public BgwFindMaxMinDemo(int id) {
        this.myId = id;
    }

    public ProtocolProducer prepareApplication(ProtocolFactory factory) {
        SequentialProtocolProducer seq = new SequentialProtocolProducer();
        SInt[] numbers = new SInt[numberofInputParties];
        PreprocessedFilesInputter pfi = new PreprocessedFilesInputter(numberofInputParties, "output/computationId-" + myId);
        BasicNumericFactory basicNumericFactory = (BasicNumericFactory) factory;

		LocalInversionFactory localInversionFactory = (LocalInversionFactory) factory;
		PreprocessedNumericBitFactory numericBitFactory = (PreprocessedNumericBitFactory) factory;
		ExpFromOIntFactory expFromOIntFactory = (ExpFromOIntFactory) factory;
        RandomAdditiveMaskFactory randomAdditiveMaskFactory = (RandomAdditiveMaskFactory) new RandomAdditiveMaskFactoryImpl(basicNumericFactory, numericBitFactory);
        RightShiftFactory rightShiftFactory = (RightShiftFactory) new RightShiftFactoryImpl(basicNumericFactory, randomAdditiveMaskFactory, localInversionFactory);
        IntegerToBitsFactory integerToBitsFactory = (IntegerToBitsFactory) new IntegerToBitsFactoryImpl(basicNumericFactory, rightShiftFactory);
        BgwComparisonProtocolBuilder bgwComparisonProtocolBuilder = new BgwComparisonProtocolBuilder(basicNumericFactory, integerToBitsFactory);
        BgwMinMaxProtocolBuilder bgwMinMaxProtocolBuilder = new BgwMinMaxProtocolBuilder(basicNumericFactory, bgwComparisonProtocolBuilder);

		NumericIOBuilder iob = new NumericIOBuilder(basicNumericFactory);

        try {
            for(int i = 1; i <= numberofInputParties; i++) {
                numbers[i - 1] = pfi.getSInt(i);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
        bgwMinMaxProtocolBuilder.beginParScope();
        SInt[] max = bgwMinMaxProtocolBuilder.getMax(numbers);
        // SInt[] min = bgwMinMaxProtocolBuilder.getMin(numbers);
        bgwMinMaxProtocolBuilder.endCurScope();
        seq.append(bgwMinMaxProtocolBuilder.getProtocol());
        maxId = iob.output(max[1]);
        // minId = iob.output(min[1]);
        seq.append(iob.getProtocol());

        return seq;
    }

    static void bgwFindMaxMinDemoMain(String[] args){
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

        BgwFindMaxMinDemo bgwFindMaxMinDemo = new BgwFindMaxMinDemo(sceConf.getMyId());
        SCE sce = SCEFactory.getSCEFromConfiguration(sceConf);
		try {
			System.out.println("Starting application");
			sce.runApplication(bgwFindMaxMinDemo);
		} catch (Exception e) {
			System.out.println("Error while doing MPC: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
        }    
        System.out.println("Max value is come from party " + (bgwFindMaxMinDemo.maxId.getValue().intValue() + 1));
        // System.out.println("Min value is come from party " + (bgwFindMaxMinDemo.minId.getValue().intValue() + 1));
    }

}