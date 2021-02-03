package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.aggregate.Accumulator;
import org.apache.jena.sparql.expr.aggregate.AccumulatorFactory;
import org.apache.jena.sparql.expr.aggregate.AggCustom;
import org.apache.jena.sparql.function.FunctionEnv;

 
public class FSUM_EXAMPLE {
   // static { LogCtl.setLogging(); }
    /**
     * Execution of a custom aggregate is with accumulators. One accumulator is
     * created for the factory for each group in a query execution.
     */
    static AccumulatorFactory myAccumulatorFactory = new AccumulatorFactory() {
        @Override
        public Accumulator createAccumulator(AggCustom agg, boolean distinct) { return new MyAccumulator(agg) ; }
    } ;
    
    /**
     * Example accumulators - counts the number of valid literals
     * of an expression over a group. 
     */
    static class MyAccumulator implements Accumulator {
        float count = 0 ;
        private AggCustom agg ;
        MyAccumulator(AggCustom agg) { this.agg = agg ; }

        /** Function called on each row in a group */
        @Override
        public void accumulate(Binding binding, FunctionEnv functionEnv) {
            ExprList exprList = agg.getExprList() ;
            for(Expr expr: exprList) {
                try {
                    NodeValue nv = expr.eval(binding, functionEnv) ;
                    // Evaluation succeeded.
                    count = count + nv.getFloat(); 
                        
                } catch (Exception ex) {}
            }
            //System.out.println(count);
        }

        /** Function called to retrieve the value for a single group */
        @Override
        public NodeValue getValue() {
            return NodeValue.makeFloat(count) ;
        }
    }
    
    
    
}