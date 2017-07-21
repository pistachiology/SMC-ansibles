package org.custom.helper;


import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.conversion.IntegerToBitsFactory;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.builder.AbstractProtocolBuilder;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;
import dk.alexandra.fresco.lib.math.integer.binary.RightShiftFactory;
import dk.alexandra.fresco.lib.math.integer.division.DivisionFactory;

public class BgwDivisionProtocolBuilder extends AbstractProtocolBuilder {

    private final BasicNumericFactory basicNumericFactory;
    private final DivisionFactory divisionFactory;
    

    protected BasicNumericFactory getBasicNumericFactory() {
        return basicNumericFactory;
    }

	@Override
	public void addProtocolProducer(ProtocolProducer pp) {
		append(pp);
	}

    public BgwDivisionProtocolBuilder(BasicNumericFactory basicNumericFactory, DivisionFactory divisionFactory){
        this.divisionFactory = divisionFactory;
        this.basicNumericFactory = basicNumericFactory;
    }

    public SInt division(SInt divided, SInt divisor) {
        SInt result = basicNumericFactory.getSInt();
        append(divisionFactory.getDivisionProtocol(divided, 18, divisor, 18, result));
        return result;
    }



}