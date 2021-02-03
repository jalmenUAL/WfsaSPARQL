package wfsaSPARQL.WfsaSPARQL;

import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.graph.Graph;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.aggregate.Accumulator;
import org.apache.jena.sparql.expr.aggregate.AccumulatorFactory;
import org.apache.jena.sparql.expr.aggregate.AggCustom;
import org.apache.jena.sparql.expr.aggregate.AggregateRegistry;
import org.apache.jena.sparql.function.FunctionEnv;
import org.apache.jena.sparql.graph.NodeConst;
import org.apache.jena.sparql.sse.SSE;

public class FSUM {
    
	static { LogCtl.setLogging(); }
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
        int count = 0 ;
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
                    if ( nv.isLiteral())
                        count ++ ;
                } catch (ExprEvalException ex) {}
            }
        }

        /** Function called to retrieve the value for a single group */
        @Override
        public NodeValue getValue() {
            return NodeValue.makeInteger(count) ;
        }
    }
    
    public static void main(String[] args) {
        
        // Example aggregate that counts literals.
        // Returns unbound for no rows. 
        String aggUri = "http://example/FSUM" ;
        
        
        /* Registration */
        AggregateRegistry.register(aggUri, myAccumulatorFactory, NodeConst.nodeMinusOne);
        
        
        // Some data.
        Graph g = SSE.parseGraph("(graph (:s :p :o) (:s :p 1) (:t :q :o) (:t :q :o))") ;
        String qs = "SELECT (<http://example/FSUM>(?o) AS ?x) {?s ?p ?o} GROUP BY ?s" ;
        
        // Execution as normal.
        Query q = QueryFactory.create(qs) ;
        try ( QueryExecution qexec = QueryExecutionFactory.create(q, ModelFactory.createModelForGraph(g)) ) {
            ResultSet rs = qexec.execSelect() ;
            ResultSetFormatter.out(rs);
        }
    }
}