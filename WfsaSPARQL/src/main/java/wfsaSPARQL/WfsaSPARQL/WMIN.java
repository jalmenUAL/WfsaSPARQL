package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase4;

public class WMIN extends FunctionBase4
{
public WMIN() { super() ; }

@Override
public NodeValue exec(NodeValue weight1, NodeValue truth1, NodeValue weight2, NodeValue truth2)
{ 
	NodeValue n1 = XSDFuncOp.max(XSDFuncOp.numSubtract(NodeValue.makeInteger(1),weight1), truth1);
	NodeValue n2 = XSDFuncOp.max(XSDFuncOp.numSubtract(NodeValue.makeInteger(1),weight2), truth2);
	return XSDFuncOp.min(n1,n2) ; }
}
