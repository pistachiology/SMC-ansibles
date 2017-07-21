package org.custom.helper;


import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.conversion.IntegerToBitsFactory;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.builder.AbstractProtocolBuilder;
import dk.alexandra.fresco.lib.math.integer.binary.RightShiftFactory;

public class BgwComparisonProtocolBuilder extends AbstractProtocolBuilder {

    private final BasicNumericFactory basicNumericFactory;
    private final IntegerToBitsFactory integerToBitsFactory;

    protected BasicNumericFactory getBasicNumericFactory() {
        return basicNumericFactory;
    }

    protected IntegerToBitsFactory getIntegerToBitFactory() {
        return integerToBitsFactory;
    }

	@Override
	public void addProtocolProducer(ProtocolProducer pp) {
		append(pp);
	}

    public BgwComparisonProtocolBuilder(BasicNumericFactory basicNumericFactory, IntegerToBitsFactory integerToBitsFactory){
        this.basicNumericFactory = basicNumericFactory;
        this.integerToBitsFactory= integerToBitsFactory;
    }


    /**
     * Compare two number which one is greater by using 2's compliment method.
     * 
     * @param left
     *      An integer
     * @param right
     *      An another integer
     * @return 
     *      if left < right return 0 else return 1
     */
    public SInt greaterEqual(SInt left, SInt right) {
//        SInt 
        int bit_length = 64;
        SInt subtractResult = basicNumericFactory.getSInt();
        SInt flippedBit = basicNumericFactory.getSInt();
        SInt[] output_bits = new SInt[bit_length];
        for(int i = 0; i < bit_length; i++) output_bits[i] = basicNumericFactory.getSInt();
        append(basicNumericFactory.getSubtractProtocol(left, right, subtractResult));
        append(integerToBitsFactory.getIntegerToBitsCircuit(subtractResult, 64, output_bits));
        append(basicNumericFactory.getSubtractProtocol(basicNumericFactory.getSInt(1), output_bits[bit_length - 1], flippedBit));
        return flippedBit;
    }

    public SInt lessThan(SInt left, SInt right) {
//        SInt 
        int bit_length = 64;
        SInt subtractResult = basicNumericFactory.getSInt();
        SInt[] output_bits = new SInt[bit_length];
        for(int i = 0; i < bit_length; i++) output_bits[i] = basicNumericFactory.getSInt();
        append(basicNumericFactory.getSubtractProtocol(left, right, subtractResult));
        append(integerToBitsFactory.getIntegerToBitsCircuit(subtractResult, 64, output_bits));
        return output_bits[output_bits.length - 1];
    }

    public SInt lesserEqual(SInt left, SInt right) {
        int bit_length = 64;
        SInt subtractResult = basicNumericFactory.getSInt();
        SInt flippedBit = basicNumericFactory.getSInt();
        SInt[] output_bits = new SInt[bit_length];
        for(int i = 0; i < bit_length; i++) output_bits[i] = basicNumericFactory.getSInt();
        append(basicNumericFactory.getSubtractProtocol(right, left, subtractResult));
        append(integerToBitsFactory.getIntegerToBitsCircuit(subtractResult, 64, output_bits));
        append(basicNumericFactory.getSubtractProtocol(basicNumericFactory.getSInt(1), output_bits[bit_length - 1], flippedBit));
        return flippedBit;
    }

    public SInt greaterThan(SInt left, SInt right) {
        int bit_length = 64;
        SInt subtractResult = basicNumericFactory.getSInt();
        SInt flippedBit = basicNumericFactory.getSInt();
        SInt[] output_bits = new SInt[bit_length];
        for(int i = 0; i < bit_length; i++) output_bits[i] = basicNumericFactory.getSInt();
        append(basicNumericFactory.getSubtractProtocol(right, left, subtractResult));
        append(integerToBitsFactory.getIntegerToBitsCircuit(subtractResult, 64, output_bits));
        return output_bits[output_bits.length - 1];
    }

    public SInt notEqual(SInt left, SInt right) {
        SInt l = this.greaterThan(left, right); 
        SInt r = this.lessThan(left, right);
        SInt o = basicNumericFactory.getSInt();
        basicNumericFactory.getMultProtocol(l, r, o);
        return o;        
    }

    public SInt equal(SInt left, SInt right) {
        SInt l = this.greaterThan(left, right); 
        SInt r = this.lessThan(left, right);
        SInt o = basicNumericFactory.getSInt();
        SInt flippedBit = basicNumericFactory.getSInt();
        basicNumericFactory.getMultProtocol(l, r, o);
        append(basicNumericFactory.getSubtractProtocol(basicNumericFactory.getSInt(1), o, flippedBit));
        return o;
    }
}