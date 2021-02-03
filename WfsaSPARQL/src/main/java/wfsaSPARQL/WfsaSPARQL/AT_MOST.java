package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase3;

public class AT_MOST extends FunctionBase3
{
public AT_MOST() { super() ; }

@Override
public NodeValue exec(NodeValue u, NodeValue Y, NodeValue d)
{ 
	NodeValue nv = null;
	
	if (u.getFloat() <= Y.getFloat() ) {nv = NodeValue.makeInteger(1);}
	else if (Y.getFloat() < u.getFloat() && u.getFloat() < d.getFloat())
	{nv = XSDFuncOp.numDivide(XSDFuncOp.numSubtract(d, u),XSDFuncOp.numSubtract(d, Y));}
	else {nv = NodeValue.makeInteger(0);};
	return nv;
	
};
}
