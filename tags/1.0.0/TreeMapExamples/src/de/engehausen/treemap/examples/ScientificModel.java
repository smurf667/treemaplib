package de.engehausen.treemap.examples;

import java.math.BigDecimal;

import de.engehausen.treemap.impl.DefaultArithmetics;
import de.engehausen.treemap.impl.GenericTreeModelEx;

/**
 * Example tree model hosting a hierarchy of arbitrary precision numbers.
 * This model shows the composition of Earth's atmosphere, taken
 * from <a href="http://www.nc-climate.ncsu.edu/edu/k12/.AtmComposition">here</a>.
 */
public class ScientificModel extends GenericTreeModelEx<String, BigDecimal> {

	/** Model of atmosphere composition, in percentages */
	public static final ScientificModel ATMOSPHERE_COMPOSITION = new ScientificModel();
	
	protected ScientificModel() {
		super(DefaultArithmetics.bigDecimals());
		final String root = "Earth's atmosphere";
		add(root, BigDecimal.ZERO, null);
		add("Nitrogen", new BigDecimal("78"), root);
		add("Oxygen", new BigDecimal("21"), root);
		add("Argon", new BigDecimal("0.9"), root);
		final String traceGases = "Trace gases";
		add(traceGases, BigDecimal.ZERO, root);
		add("Neon", new BigDecimal("0.004674"), traceGases);
		add("Helium", new BigDecimal("0.001299"), traceGases);
		add("Methane", new BigDecimal("0.000442"), traceGases);
		add("Nitrous Oxide", new BigDecimal("0.000078"), traceGases);
		add("Ozone", new BigDecimal("0.00001"), traceGases);
		add("Carbon Dioxide", new BigDecimal("0.093497"), traceGases);
	}

}
