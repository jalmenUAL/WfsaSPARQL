package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase4;

public class WSUM extends FunctionBase4
{
public WSUM() { super() ; }

@Override
public NodeValue exec(NodeValue weight1, NodeValue truth1, NodeValue weight2, NodeValue truth2)
{ return XSDFuncOp.numAdd(XSDFuncOp.numMultiply(truth1, weight1),XSDFuncOp.numMultiply(truth2,weight2)) ; }
}
