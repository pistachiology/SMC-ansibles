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
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;
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

class EconomicDispatchSimulator implements Application {

    /* refs : https://de.mathworks.com/matlabcentral/fileexchange/49456-economic-dispatch-including-losses?requestedDomain=www.mathworks.com */

    private static final int numberofParties = 3;
    private static final int numberofInputParties = 3;
    private static final int fixPointFactor = 5;
    private static final int Draw = integerToFixedPoint(900);

    private int myId;
    private SInt[] a = new SInt[numberofInputParties];
    private SInt[] b = new SInt[numberofInputParties];
    private SInt[] c = new SInt[numberofInputParties];
    private SInt[] c2 = new SInt[numberofInputParties];
    private SInt[] invC = new SInt[numberofInputParties];
    private SInt sumInvC = null;
    private SInt[] minList = new SInt[numberofInputParties];
    private SInt[] maxList = new SInt[numberofInputParties];
    private SInt compareFactor = null;
    private SInt x = null;
    private SInt D = null;
    private SInt dP = null;

    public OInt isLoop;

    static Integer integerToFixedPoint(Integer value) {
       return (int) (value * Math.pow(10, fixPointFactor));
    }
    static Integer fixedPointToInteger(Integer value) {
       return (int) (value / Math.pow(10, fixPointFactor));
    }
    public EconomicDispatchSimulator(int id) {
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
        BitLengthFactory bitLengthFactory = new BitLengthFactoryImpl(basicNumericFactory, integerToBitsFactory);
        ExponentiationFactory exponentiationFactory = new ExponentiationFactoryImpl(basicNumericFactory, integerToBitsFactory);
        DivisionFactory divisionFactory = new DivisionFactoryImpl(basicNumericFactory, rightShiftFactory, bitLengthFactory, exponentiationFactory);

        BgwComparisonProtocolBuilder bgwComparisonProtocolBuilder = new BgwComparisonProtocolBuilder(basicNumericFactory, integerToBitsFactory);
        BgwMinMaxProtocolBuilder bgwMinMaxProtocolBuilder = new BgwMinMaxProtocolBuilder(basicNumericFactory, bgwComparisonProtocolBuilder);
        BgwDivisionProtocolBuilder bgwDivisionProtocolBuilder = new BgwDivisionProtocolBuilder(basicNumericFactory, divisionFactory);

        NumericIOBuilder iob = new NumericIOBuilder(basicNumericFactory);
        NumericProtocolBuilder npb = new NumericProtocolBuilder(basicNumericFactory);
        SInt two = npb.getSInt(2);
        if(compareFactor == null) compareFactor = npb.getSInt(10000);
        if(x == null) {
            PreprocessedFilesInputter pfi = new PreprocessedFilesInputter(numberofInputParties, "output/computationId-" + myId);
            try {
                for(int i = 1; i <= numberofInputParties; i++) {
                    a[i - 1] = pfi.getSInt(i);
                    b[i - 1] = pfi.getSInt(i);
                    c[i - 1] = pfi.getSInt(i);
                    minList[i - 1] = pfi.getSInt(i);
                    maxList[i - 1] = pfi.getSInt(i);
                }
            } catch (Exception e){
                e.printStackTrace();
                System.exit(-1);
            }
            SInt[] ret = bgwMinMaxProtocolBuilder.getMax(b);
            seq.append(bgwMinMaxProtocolBuilder.getProtocol());
            x = ret[0];
            D = npb.getSInt(Draw);
            dP = D;
            for(int i = 0; i< c.length; i++){
                c2[i] = npb.mult(c[i], two);
                invC[i] = bgwDivisionProtocolBuilder.division(npb.getSInt(integerToFixedPoint(1)), c[i]);
                seq.append(npb.getProtocol());
                seq.append(bgwComparisonProtocolBuilder.getProtocol());
            }
            npb.add(invC[1], invC[0]);
            // sumInvC = invC[0];
            // for(int i = 1; i < 2; i++) {
            //     sumInvC = npb.add(sumInvC, invC[i]);
            // }
            sumInvC = npb.sum(c2);
            seq.append(npb.getProtocol());
        }

        // SInt[] P = new SInt[c.length];
        // P = (x-b) ./c /2;
        // for(int i = 0; i < c.length; i++) {
        //     P[i] = npb.sub(x,b[i]);
        //     seq.append(npb.getProtocol());
        //     P[i] = bgwDivisionProtocolBuilder.division(P[i], c2[i]);
        //     seq.append(npb.getProtocol());
        // }
        // // P = min(P, Ph);
        // P = bgwMinMaxProtocolBuilder.getMin(P, maxList);
        // seq.append(bgwMinMaxProtocolBuilder.getProtocol());
        // P = bgwMinMaxProtocolBuilder.getMax(P, minList);
        // seq.append(bgwMinMaxProtocolBuilder.getProtocol());
        // // dP = D - sum(p);
        // dP = npb.sub(D, npb.sum(P));
        // seq.append(npb.getProtocol());

        // // x = x + dP * 2 / sum(1./c))
        // x = npb.add(x, npb.mult(dP, two));
        // seq.append(npb.getProtocol());
        // x = bgwDivisionProtocolBuilder.division(x, sumInvC);
        // seq.append(bgwDivisionProtocolBuilder.getProtocol());

        // SInt absFactor = bgwComparisonProtocolBuilder.lessThan(dP, npb.getSInt(0));
        // seq.append(bgwComparisonProtocolBuilder.getProtocol());
        // // abs(dP)
        // SInt absdP = npb.add( npb.mult(npb.mult(absFactor, npb.getSInt(-1)), dP), npb.mult(npb.sub(npb.getSInt(1), absFactor), dP) );
        // seq.append(npb.getProtocol());
        // SInt isLoopSecret = bgwComparisonProtocolBuilder.lessThan(absdP, compareFactor);
        // seq.append(bgwComparisonProtocolBuilder.getProtocol());
        SInt isLoopSecret = npb.getSInt(0);

        isLoop = iob.output(isLoopSecret);
        seq.append(iob.getProtocol());

        return seq;
    }

    static void EconomicDispatchSimulatorMain(String[] args){
        CmdLineUtil cmdUtil = new CmdLineUtil();
        SCEConfiguration sceConf = null;
        int isLoop = 0;
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
        System.out.println(sceConf.getMyId());
        EconomicDispatchSimulator economicDispatchSimulator = new EconomicDispatchSimulator(sceConf.getMyId());

        if(Math.abs(Draw) < 10000) isLoop = 0;
        else isLoop = 1;
        while(isLoop == 1) {
            SCE sce = SCEFactory.getSCEFromConfiguration(sceConf);
            try {
                System.out.println("Starting application");
                sce.runApplication(economicDispatchSimulator);
            } catch (Exception e) {
                System.out.println("Error while doing MPC: " + e.getMessage());
                e.printStackTrace();
                System.exit(-1);
            }    
            isLoop = economicDispatchSimulator.isLoop.getValue().intValue();
            System.out.println("loop");
        }
        // System.out.println("Min value is come from party " + (EconomicDispatchSimulator.minId.getValue().intValue() + 1));
    }

}