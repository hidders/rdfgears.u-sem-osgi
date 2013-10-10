package $packageName$;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * A function that performs sentiment analysis
 * 
 */
public class $function$ extends SimplyTypedRGLFunction {

	/**
	 * The name of the input field providing the statement that will be analysed
	 */
	public static final String INPUT = "input";


	public $function$() {
		this.requireInputType(INPUT, RDFType.getInstance());
	}

	@Override
	public RGLType getOutputType() {
		return RDFType.getInstance();
	}

	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
		// typechecking the input
		RGLValue rdfValue = inputRow.get(INPUT);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		// extracting the twitter username from the input
		String input = rdfValue.asLiteral().getValueString();

		return ValueFactory.createLiteralPlain("Input: " + input , null);
	}

}
