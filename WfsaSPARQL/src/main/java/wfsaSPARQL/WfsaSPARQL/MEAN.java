package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase2;

public class MEAN extends FunctionBase2
{
public MEAN() { super() ; }

@Override
public NodeValue exec(NodeValue nv1, NodeValue nv2)
{ return XSDFuncOp.numDivide(XSDFuncOp.numAdd(nv1, nv2),NodeValue.makeInteger(2)) ; }
}
