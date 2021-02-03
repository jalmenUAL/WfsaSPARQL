package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase3;

public class CLOSE_TO extends FunctionBase3
{
public CLOSE_TO() { super() ; }

@Override
public NodeValue exec(NodeValue u, NodeValue Y, NodeValue b)
{ return XSDFuncOp.numDivide(NodeValue.makeInteger(1),
		XSDFuncOp.numAdd(NodeValue.makeInteger(1),
				XSDFuncOp.numMultiply(XSDFuncOp.numDivide(XSDFuncOp.numSubtract(u,Y),b),
						XSDFuncOp.numDivide(XSDFuncOp.numSubtract(u,Y),b)))); }
}