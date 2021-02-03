package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase1;

public class VERY extends FunctionBase1
	{
	public VERY() { super() ; }

	@Override
	public NodeValue exec(NodeValue nv)
	{ return XSDFuncOp.numMultiply(nv, nv);}
	}

