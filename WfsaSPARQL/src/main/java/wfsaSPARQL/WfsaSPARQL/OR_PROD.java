package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase2;

public class OR_PROD extends FunctionBase2
{
public OR_PROD() { super() ; }

@Override
public NodeValue exec(NodeValue nv1, NodeValue nv2)
{ return XSDFuncOp.numSubtract(XSDFuncOp.numAdd(nv1, nv2),XSDFuncOp.numMultiply(nv1, nv2)); }
}
