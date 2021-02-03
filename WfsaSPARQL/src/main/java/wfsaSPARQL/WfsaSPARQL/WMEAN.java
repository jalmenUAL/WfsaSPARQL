package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase3;
import org.apache.jena.sparql.function.FunctionBase4;

public class WMEAN extends FunctionBase3
{
public WMEAN() { super() ; }

@Override
public NodeValue exec(NodeValue weight, NodeValue truth1, NodeValue truth2)
{ return XSDFuncOp.numAdd(XSDFuncOp.numMultiply(truth1, weight),XSDFuncOp.numMultiply(truth2,
		XSDFuncOp.numSubtract(NodeValue.makeInteger(1),weight))) ; }
}
