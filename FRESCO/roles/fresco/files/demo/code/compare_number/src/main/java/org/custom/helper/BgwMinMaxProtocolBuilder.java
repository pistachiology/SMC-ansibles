package org.custom.helper;


import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.conversion.IntegerToBitsFactory;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.builder.AbstractProtocolBuilder;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;
import dk.alexandra.fresco.lib.math.integer.binary.RightShiftFactory;

public class BgwMinMaxProtocolBuilder extends AbstractProtocolBuilder {

    private final BasicNumericFactory basicNumericFactory;
    private final BgwComparisonProtocolBuilder bgwComparisonProtocolBuilder;
    

    protected BasicNumericFactory getBasicNumericFactory() {
        return basicNumericFactory;
    }

	@Override
	public void addProtocolProducer(ProtocolProducer pp) {
		append(pp);
	}

    public BgwMinMaxProtocolBuilder(BasicNumericFactory basicNumericFactory, BgwComparisonProtocolBuilder bgwComparisonProtocolBuilder){
        this.basicNumericFactory = basicNumericFactory;
        this.bgwComparisonProtocolBuilder = bgwComparisonProtocolBuilder;
    }

    public SInt[] getMax(SInt[] numbers) {
		NumericProtocolBuilder npb = new NumericProtocolBuilder(basicNumericFactory);
        SInt[] max = new SInt[2];
        SInt cmpVal = basicNumericFactory.getSInt();
        max[0] = numbers[0];
        beginSeqScope();
        max[1] = basicNumericFactory.getSInt(0);
        for(int i = 1; i < numbers.length; i++){
            cmpVal = bgwComparisonProtocolBuilder.greaterEqual(numbers[i], max[0]);
            append(bgwComparisonProtocolBuilder.getProtocol());
			max[0] = npb.add(npb.mult(cmpVal, numbers[i]), npb.mult(npb.sub(npb.getSInt(1), cmpVal), max[0]));  
            max[1] = npb.add(npb.mult(cmpVal, npb.getSInt(i)), npb.mult(npb.sub(npb.getSInt(1), cmpVal), max[1]));  
            append(npb.getProtocol());
        }
        endCurScope();
        return max;
    }

    public SInt[] getMax(SInt[] numbers, SInt[] numbers2) {
        assert(numbers.length == numbers2.length);
		NumericProtocolBuilder npb = new NumericProtocolBuilder(basicNumericFactory);
        SInt[] max = new SInt[numbers.length];
        SInt cmpVal = basicNumericFactory.getSInt();
        beginParScope();
        for(int i = 0; i < numbers.length; i++){
            cmpVal = bgwComparisonProtocolBuilder.greaterEqual(numbers[i], numbers2[i]);
            append(bgwComparisonProtocolBuilder.getProtocol());
			max[i] = npb.add(npb.mult(cmpVal, numbers[i]), npb.mult(npb.sub(npb.getSInt(1), cmpVal), numbers2[i]));  
            append(npb.getProtocol());
        }
        endCurScope();
        return max;
    }

    public SInt[] getMin(SInt[] numbers, SInt[] numbers2) {
        assert(numbers.length == numbers2.length);
		NumericProtocolBuilder npb = new NumericProtocolBuilder(basicNumericFactory);
        SInt[] min = new SInt[numbers.length];
        SInt cmpVal = basicNumericFactory.getSInt();
        beginParScope();
        for(int i = 0; i < numbers.length; i++){
            cmpVal = bgwComparisonProtocolBuilder.lessThan(numbers[i], numbers2[i]);
            append(bgwComparisonProtocolBuilder.getProtocol());
			min[i] = npb.add(npb.mult(cmpVal, numbers[i]), npb.mult(npb.sub(npb.getSInt(1), cmpVal), numbers2[0]));  
            append(npb.getProtocol());
        }
        endCurScope();
        return min;
    }
    // public SInt[] getMax(SInt[] numbers) {
	// 	NumericProtocolBuilder npb = new NumericProtocolBuilder(basicNumericFactory);
    //     SInt cmpVal = basicNumericFactory.getSInt();
    //     SInt[] cnumbers = numbers.clone();
    //     SInt[] cnumbersId = new SInt[cnumbers.length];
    //     for(int i = 0; i < cnumbers.length; i++) cnumbersId[i] = basicNumericFactory.getSInt(i);
    //     int range = 1;

    //     beginSeqScope();
    //     while(range < cnumbers.length) {
    //         beginParScope();
    //         for(int i = 0; i < cnumbers.length; i += (range*2)) {
    //             if(i + range >= cnumbers.length) break;
    //             cmpVal = bgwComparisonProtocolBuilder.greaterEqual(cnumbers[i+range], cnumbers[i]);
    //             beginSeqScope();
    //             append(bgwComparisonProtocolBuilder.getProtocol());
	//  		    cnumbers[i]  = npb.add(npb.mult(cmpVal, cnumbers[i+range]), npb.mult(npb.sub(npb.getSInt(1), cmpVal), cnumbers[i]));  
    //             cnumbersId[i] = npb.add(npb.mult(cmpVal, cnumbersId[i+range]), npb.mult(npb.sub(npb.getSInt(1), cmpVal), cnumbersId[i]));  
    //             append(npb.getProtocol());
    //             endCurScope();
    //         }
    //         range = range * 2;
    //         endCurScope();
    //     }
    //     endCurScope();
    //     return new SInt[] { cnumbers[0], cnumbersId[0] };
    // }
    public SInt[] getMin(SInt[] numbers) {
		NumericProtocolBuilder npb = new NumericProtocolBuilder(basicNumericFactory);
        SInt[] min = new SInt[2];
        SInt cmpVal = null;
        min[0] = numbers[0];
        min[1] = basicNumericFactory.getSInt(0);
        for(int i = 1; i < numbers.length; i++){
            cmpVal = bgwComparisonProtocolBuilder.lessThan(numbers[i], min[0]);
            append(bgwComparisonProtocolBuilder.getProtocol());
			min[0] = npb.add(npb.mult(cmpVal, numbers[i]), npb.mult(npb.sub(npb.getSInt(1), cmpVal), min[0]));  
            min[1] = npb.add(npb.mult(cmpVal, npb.getSInt(i)), npb.mult(npb.sub(npb.getSInt(1), cmpVal), min[1]));  
            append(npb.getProtocol());
        }
        return min;
    }

}