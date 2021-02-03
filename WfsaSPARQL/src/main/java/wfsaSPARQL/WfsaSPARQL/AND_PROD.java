package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase2;

public class AND_PROD extends FunctionBase2
{
public AND_PROD() { super() ; }

@Override
public NodeValue exec(NodeValue nv1, NodeValue nv2)
{ return XSDFuncOp.numMultiply(nv1, nv2) ; }
}
