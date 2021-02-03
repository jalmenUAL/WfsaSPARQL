package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase2;

public class MIN extends FunctionBase2
{
public MIN() { super() ; }

@Override
public NodeValue exec(NodeValue nv1, NodeValue nv2)
{ return XSDFuncOp.min(nv1, nv2) ; }
}
