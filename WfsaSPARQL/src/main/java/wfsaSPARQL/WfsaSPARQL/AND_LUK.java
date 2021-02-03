package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase2;

public class AND_LUK extends FunctionBase2
{
public AND_LUK() { super() ; }

@Override
public NodeValue exec(NodeValue nv1, NodeValue nv2)
{ return XSDFuncOp.max(XSDFuncOp.numSubtract(XSDFuncOp.numAdd(nv1, nv2),NodeValue.makeInteger(1)),NodeValue.makeInteger(0)) ; }
}
