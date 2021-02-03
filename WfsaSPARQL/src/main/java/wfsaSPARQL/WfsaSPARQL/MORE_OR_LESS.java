package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionBase1;

public class MORE_OR_LESS extends FunctionBase1
{
public MORE_OR_LESS() { super() ; }

@Override
public NodeValue exec(NodeValue nv)
{ return XSDFuncOp.sqrt(nv);}
}
