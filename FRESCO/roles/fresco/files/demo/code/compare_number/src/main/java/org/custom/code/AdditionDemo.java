package org.custom.code;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import dk.alexandra.fresco.framework.ProtocolFactory;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.configuration.CmdLineUtil;
import dk.alexandra.fresco.framework.sce.SCE;
import dk.alexandra.fresco.framework.sce.SCEFactory;
import dk.alexandra.fresco.framework.sce.configuration.SCEConfiguration;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.helper.builder.NumericIOBuilder;
import dk.alexandra.fresco.lib.helper.builder.NumericProtocolBuilder;
import dk.alexandra.fresco.framework.Application;

/**
 * A simple demo computing the distance between two secret points
 */
public class AdditionDemo implements Application {
	
	private static final long serialVersionUID = 6415583508947017554L;
	
	private int myId, myX;
	private SInt x1, x2, x3;
	public OInt sum;
	
	public AdditionDemo(int id, int x) {
		this.myId = id;
		this.myX = x;
	}

	public ProtocolProducer prepareApplication(ProtocolFactory factory) {
		BasicNumericFactory bnFac = (BasicNumericFactory)factory;
		NumericProtocolBuilder npb = new NumericProtocolBuilder(bnFac);
		NumericIOBuilder iob = new NumericIOBuilder(bnFac);
		// Input points
		iob.beginParScope();
			x1 = (myId == 1) ? iob.input(myX, 1) : iob.input(1); 
			x2 = (myId == 2) ? iob.input(myX, 2) : iob.input(2); 
			x3 = (myId == 3) ? iob.input(myX, 3) : iob.input(3); 
		iob.endCurScope();
		// Compute distance squared (note, square root computation can be done publicly)
		SInt result = npb.add(x1, npb.add(x2, x3));
		iob.addProtocolProducer(npb.getProtocol());
		// Output result
		sum = iob.output(result);
		return iob.getProtocol();
	}

	static void additionDemoMain(String[]args){
		CmdLineUtil cmdUtil = new CmdLineUtil();
		SCEConfiguration sceConf = null;
		int x, y;
		x = y = 0;
		try {	
			cmdUtil.addOption(Option.builder("x")
					.desc("The integer x coordinate of this party. "
							+ "Note only party 1,2 and 3 should supply this input.")
					.hasArg()
					.build());	
			CommandLine cmd = cmdUtil.parse(args);
			sceConf = cmdUtil.getSCEConfiguration();
			
			if (sceConf.getMyId() == 1 || sceConf.getMyId() == 2 || sceConf.getMyId() == 3) {
				if (!cmd.hasOption("x")) {
					throw new ParseException("Party 1 and 2 must submit input");
				} else {
					x = Integer.parseInt(cmd.getOptionValue("x"));
				}
			} else {
				if (cmd.hasOption("x")) 
					throw new ParseException("Only party 1 and 2 should submit input");
			}
			
		} catch (ParseException | IllegalArgumentException e) {
			System.out.println("Error: " + e);
			System.out.println();
			cmdUtil.displayHelp();
			System.exit(-1);	
		} 
		AdditionDemo additionDemo= new AdditionDemo(sceConf.getMyId(), x);
		SCE sce = SCEFactory.getSCEFromConfiguration(sceConf);
		try {
			sce.runApplication(additionDemo);
		} catch (Exception e) {
			System.out.println("Error while doing MPC: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		double dist = additionDemo.sum.getValue().intValue();

		System.out.println("Sum of party 1, 2 and 3 is " + dist);
	}
	

}

