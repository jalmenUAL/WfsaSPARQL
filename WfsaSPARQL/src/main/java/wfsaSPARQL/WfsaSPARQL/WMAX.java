package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase4;

public class WMAX extends FunctionBase4
{
public WMAX() { super() ; }

@Override
public NodeValue exec(NodeValue weight1, NodeValue truth1, NodeValue weight2, NodeValue truth2)
{ 
	NodeValue n1 = XSDFuncOp.min(weight1, truth1);
	NodeValue n2 = XSDFuncOp.min(weight2, truth2);
	return XSDFuncOp.max(n1,n2) ; }
}
