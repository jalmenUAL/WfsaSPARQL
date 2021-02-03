package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase3;

public class AT_LEAST extends FunctionBase3
{
public AT_LEAST() { super() ; }

@Override
public NodeValue exec(NodeValue u, NodeValue Y, NodeValue w)
{ 
	NodeValue nv = null;
	
	if (u.getFloat() <= w.getFloat() ) {nv = NodeValue.makeInteger(0);}
	else if (w.getFloat() < u.getFloat() && u.getFloat() < Y.getFloat())
	{nv = XSDFuncOp.numDivide(XSDFuncOp.numSubtract(u, w),XSDFuncOp.numSubtract(Y, w));}
	else {nv = NodeValue.makeInteger(1);};
	return nv;
	
};
}