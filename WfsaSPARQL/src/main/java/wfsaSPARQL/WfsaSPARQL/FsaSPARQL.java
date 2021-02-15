package wfsaSPARQL.WfsaSPARQL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;
import org.apache.jena.sparql.expr.E_Divide;
import org.apache.jena.sparql.expr.E_Equals;
import org.apache.jena.sparql.expr.E_GreaterThan;
import org.apache.jena.sparql.expr.E_GreaterThanOrEqual;
import org.apache.jena.sparql.expr.E_LessThan;
import org.apache.jena.sparql.expr.E_LessThanOrEqual;
import org.apache.jena.sparql.expr.E_Multiply;
import org.apache.jena.sparql.expr.E_Str;
import org.apache.jena.sparql.expr.E_StrConcat;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.ExprFunctionOp;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.aggregate.AggCount;
import org.apache.jena.sparql.expr.aggregate.AggMax;
import org.apache.jena.sparql.expr.aggregate.AggMin;
import org.apache.jena.sparql.expr.aggregate.AggSum;
import org.apache.jena.sparql.expr.aggregate.AggregateRegistry;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementAssign;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementExists;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementNotExists;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.apache.jena.util.FileUtils;



//AGG, BING, HAVING, WEIGTH COMPLEX EXPRESSIONS PENDING
//DISTINCT PENDING

public class FsaSPARQL {

	private String readFile(String pathname) throws IOException {

		File file = new File(pathname);
		StringBuilder fileContents = new StringBuilder((int) file.length());
		Scanner scanner = new Scanner(file);
		String lineSeparator = System.getProperty("line.separator");

		try {
			while (scanner.hasNextLine()) {
				fileContents.append(scanner.nextLine() + lineSeparator);
			}
			return fileContents.toString();
		} finally {
			scanner.close();
		}
	}

	public String FSAtoSPARQL(String queryString) {
		
		FunctionRegistry.get().put("http://www.lattice.org#AT_LEAST", AT_LEAST.class);
		FunctionRegistry.get().put("http://www.lattice.org#AT_MOST", AT_MOST.class);
		FunctionRegistry.get().put("http://www.lattice.org#CLOSE_TO", CLOSE_TO.class);
		FunctionRegistry.get().put("http://www.lattice.org#MORE_OR_LESS", MORE_OR_LESS.class);
		FunctionRegistry.get().put("http://www.lattice.org#VERY", VERY.class);
		FunctionRegistry.get().put("http://www.lattice.org#AND_PROD", AND_PROD.class);
		FunctionRegistry.get().put("http://www.lattice.org#OR_PROD", OR_PROD.class);
		FunctionRegistry.get().put("http://www.lattice.org#AND_LUK", AND_LUK.class);
		FunctionRegistry.get().put("http://www.lattice.org#OR_LUK", OR_LUK.class);
		FunctionRegistry.get().put("http://www.lattice.org#AND_GOD", AND_GOD.class);
		FunctionRegistry.get().put("http://www.lattice.org#OR_GOD", OR_GOD.class);
		FunctionRegistry.get().put("http://www.lattice.org#MIN", MIN.class);
		FunctionRegistry.get().put("http://www.lattice.org#MAX", MAX.class);
		FunctionRegistry.get().put("http://www.lattice.org#MEAN", MEAN.class);
		FunctionRegistry.get().put("http://www.lattice.org#WSUM", WSUM.class);
		FunctionRegistry.get().put("http://www.lattice.org#WMAX", WMAX.class);
		FunctionRegistry.get().put("http://www.lattice.org#WMIN", WMIN.class);
		FunctionRegistry.get().put("http://www.lattice.org#WMIN", WMEAN.class);
		AggregateRegistry.register("http://www.fuzzy.org#FSUM", null);
		AggregateRegistry.register("http://www.fuzzy.org#FMAX", null);
		AggregateRegistry.register("http://www.fuzzy.org#FMIN", null);
		AggregateRegistry.register("http://www.fuzzy.org#FCOUNT", null);
		AggregateRegistry.register("http://www.fuzzy.org#PCOUNT", null);
		AggregateRegistry.register("http://www.fuzzy.org#FAVG", null);
		FunctionRegistry.get().put("http://www.fuzzy.org#WEIGHT", WEIGHT.class);
		

		final Query query = QueryFactory.create(queryString);
		
		translate(query);
		
		return query.toString();
		
	};
		
		
	public void translate(final Query query)
	
	{
	
	final Map<Node, List<Node>> partition = new HashMap<Node, List<Node>>();
	final List<Node> individuals = new ArrayList<Node>();
	ElementWalker.walk(query.getQueryPattern(), new ElementVisitorBase() {

		@Override
		public void visit(ElementPathBlock el) {
			ListIterator<TriplePath> it = el.getPattern().iterator();
			while (it.hasNext()) {
				Item(it, partition,individuals);
			}	
		}

		@Override
		public void visit(ElementSubQuery el) {
			translate(el.getQuery());
		}

		@Override
		public void visit(ElementGroup group) {
			super.visit(group);

		}

		@Override
		public void visit(ElementAssign assign) {
			super.visit(assign);
		}

		@Override
		public void visit(ElementData data) {
			super.visit(data);
		}

		@Override
		public void visit(ElementExists exists) {
			super.visit(exists);
		}

		@Override
		public void visit(ElementMinus minus) {
			super.visit(minus);
		}

		@Override
		public void visit(ElementNamedGraph namedGraph) {
			super.visit(namedGraph);
		}

		@Override
		public void visit(ElementNotExists notExists) {
			super.visit(notExists);
		}

		@Override
		public void visit(ElementOptional optional) {
			super.visit(optional);
		}

		@Override
		public void visit(ElementService service) {
			super.visit(service);
		}

		public void visit(ElementTriplesBlock el) {
			super.visit(el);
		}

		@Override
		public void visit(ElementUnion arqUnion) {
			super.visit(arqUnion);
		}

		@Override
		public void visit(ElementFilter el) {

			if (el.getExpr().getFunction().getFunctionName(null) == "exists") {
				ElementWalker.walk(((ExprFunctionOp) el.getExpr().getFunction()).getElement(), this);
			}
			else
			if (el.getExpr().getFunction().getFunctionName(null) == "notexists") {
				ElementWalker.walk(((ExprFunctionOp) el.getExpr().getFunction()).getElement(), this);
			}
			else
			{
			 	 
			}
			
		}

	});
	
	// REPLACE FUZZY AGG BY CRISP AGG
	
	VarExprList pj = query.getProject();	
	
	Map<Var, Expr> epj = pj.getExprs();
	
	for (Entry<Var, Expr> vs: epj.entrySet())
	{
	   if (vs.getValue() instanceof ExprAggregator) 			   
	   {
		  	if (((ExprAggregator) vs.getValue()).getAggregator().toString().contains("http://www.fuzzy.org#FSUM")) {

		  		
				Expr arg = ((ExprAggregator) vs.getValue()).getAggregator().getExprList().get(0);
				Expr agg = fsum(partition, arg,individuals);					 
				query.getProject().getExprs().replace(vs.getKey(),agg);							 
			} 
			else
				if (((ExprAggregator) vs.getValue()).getAggregator().toString().contains("http://www.fuzzy.org#FMIN")) {
					Expr arg = ((ExprAggregator) vs.getValue()).getAggregator().getExprList().get(0);
					Expr agg = fmin(partition, arg,individuals);						 
					query.getProject().getExprs().replace(vs.getKey(),agg);
				} 
				else 
					if (((ExprAggregator) vs.getValue()).getAggregator().toString().contains("http://www.fuzzy.org#FMAX")) {
						Expr arg = ((ExprAggregator) vs.getValue()).getAggregator().getExprList().get(0);
						Expr agg = fmax(partition, arg,individuals);
						query.getProject().getExprs().replace(vs.getKey(),agg);
					} 
					else 
						if (((ExprAggregator) vs.getValue()).getAggregator().toString().contains("http://www.fuzzy.org#FAVG")) {
							Expr arg = ((ExprAggregator) vs.getValue()).getAggregator().getExprList().get(0);
							Expr agg = fsum(partition, arg,individuals);
							Expr cc = fcount(partition,individuals);
							Expr result = new E_Divide(agg,cc);					
							query.getProject().getExprs().replace(vs.getKey(),result);
						} 
						else 
							if (((ExprAggregator) vs.getValue()).getAggregator().toString().contains("http://www.fuzzy.org#FCOUNT")) {
								 
								Expr agg = fcount(partition, individuals);
								query.getProject().getExprs().replace(vs.getKey(),agg);
							} 
							else 
								if (((ExprAggregator) vs.getValue()).getAggregator().toString().contains("http://www.fuzzy.org#PCOUNT")) {
									Expr fcount = fcount(partition, individuals);										
									ExprList cc = new ExprList();										
									cc.add(new E_Str(fcount));  										
									cc.add(NodeValue.makeString("/"));										
									cc.add(new E_Str(new ExprAggregator(null, new AggCount()).getExpr()));										
									Expr result = new E_StrConcat(cc);
									query.getProject().getExprs().replace(vs.getKey(),result);
								} 				
		   else {}
	   }
	} 
	   // REPLACE FUZZY AGG BY CRISP AGG
	
		//HAVING
		List<Expr> hes = query.getHavingExprs();	
		List<Expr> nhes = new ArrayList<Expr>();
		for (Expr he : hes)
		{
			List<Expr> args = he.getFunction().getArgs();
			List<Expr> nargs = new ArrayList<Expr>();
			for (Expr arg : args)
			{
				if (arg instanceof ExprAggregator)
				{
				Expr narg = fuzzytocrisp((ExprAggregator) arg,partition,individuals);
				nargs.add(narg);
				}
				else {nargs.add(arg);}
			}
			
		Expr ne = null;
		if (he.getFunction().getOpName().toString()=="<") {
			ne = new E_LessThan(nargs.get(0),nargs.get(1));
		}	
		if (he.getFunction().getOpName().toString()=="<=") {		
				ne = new E_LessThanOrEqual(nargs.get(0),nargs.get(1));
				}
		if (he.getFunction().getOpName().toString()==">") {		
				ne = new E_GreaterThan(nargs.get(0),nargs.get(1));
				}
		if (he.getFunction().getOpName().toString()==">=") {		
				ne = new E_GreaterThanOrEqual(nargs.get(0),nargs.get(1));
				}
		if (he.getFunction().getOpName().toString()=="=") {		
				ne = new E_Equals(nargs.get(0),nargs.get(1));
				}
		nhes.add(ne);	
		}
		query.getHavingExprs().clear();
		query.getHavingExprs().addAll(nhes);
		//HAVING
		
	
		
	  //WEIGHT
		
		ElementGroup g = (ElementGroup) query.getQueryPattern();
		List<ElementFilter> filters = new ArrayList<ElementFilter>();
		for (Element e : g.getElements())
		{
			if (e instanceof ElementFilter) {
				
				if (((ElementFilter) e).getExpr().getFunction().getFunctionName(null) == "exists"
						|| ((ElementFilter) e).getExpr().getFunction().getFunctionName(null) == "notexists")
				{}
				else
				{
				if (((ElementFilter) e).getExpr().getFunction().getOpName().toString()=="=") {
					List<Expr> argsf = ((ElementFilter) e).getExpr().getFunction().getArgs();
					Expr narg0 = argsf.get(0);
					Expr narg1 = argsf.get(1);
					
					if ((argsf.get(0).isFunction() && 
							argsf.get(0).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT")) || 
							(argsf.get(0).isFunction() && 
									argsf.get(1).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT")))
						
					{
					if (argsf.get(0).isFunction() && argsf.get(0).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT"))
					{
					narg0 = weight(partition,argsf.get(0).getFunction().getArgs().get(0));
					
					} 
					if (argsf.get(1).isFunction() && argsf.get(1).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT"))
					{
					narg1 = weight(partition,argsf.get(1).getFunction().getArgs().get(0));
					 
					}
					Expr ne = new E_Equals(narg0,narg1);
					g.getElements().remove(e);
					filters.add(new ElementFilter(ne));
					}
			     
			
				}
				if (((ElementFilter) e).getExpr().getFunction().getOpName().toString()=="<") {
					List<Expr> argsf = ((ElementFilter) e).getExpr().getFunction().getArgs();
					Expr narg0 = argsf.get(0);
					Expr narg1 = argsf.get(1);
					
					if ((argsf.get(0).isFunction() && 
							argsf.get(0).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT")) || 
							(argsf.get(0).isFunction() && 
									argsf.get(1).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT")))
						
					{
					if (argsf.get(0).isFunction() && argsf.get(0).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT"))
					{
					narg0 = weight(partition,argsf.get(0).getFunction().getArgs().get(0));
					
					} 
					if (argsf.get(1).isFunction() && argsf.get(1).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT"))
					{
					narg1 = weight(partition,argsf.get(1).getFunction().getArgs().get(0));
					 
					}
					Expr ne = new E_LessThan(narg0,narg1);
					g.getElements().remove(e);
					filters.add(new ElementFilter(ne));
					}
			     
			
				}
				if (((ElementFilter) e).getExpr().getFunction().getOpName().toString()=="<=") {
					List<Expr> argsf = ((ElementFilter) e).getExpr().getFunction().getArgs();
					Expr narg0 = argsf.get(0);
					Expr narg1 = argsf.get(1);
					
					if ((argsf.get(0).isFunction() && 
							argsf.get(0).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT")) || 
							(argsf.get(0).isFunction() && 
									argsf.get(1).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT")))
						
					{
					if (argsf.get(0).isFunction() && argsf.get(0).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT"))
					{
					narg0 = weight(partition,argsf.get(0).getFunction().getArgs().get(0));
					
					} 
					if (argsf.get(1).isFunction() && argsf.get(1).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT"))
					{
					narg1 = weight(partition,argsf.get(1).getFunction().getArgs().get(0));
					 
					}
					Expr ne = new E_LessThanOrEqual(narg0,narg1);
					g.getElements().remove(e);
					filters.add(new ElementFilter(ne));
					}
			     
			
				}
				if (((ElementFilter) e).getExpr().getFunction().getOpName().toString()==">") {
					List<Expr> argsf = ((ElementFilter) e).getExpr().getFunction().getArgs();
					Expr narg0 = argsf.get(0);
					Expr narg1 = argsf.get(1);
					
					if ((argsf.get(0).isFunction() && 
							argsf.get(0).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT")) || 
							(argsf.get(0).isFunction() && 
									argsf.get(1).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT")))
						
					{
					if (argsf.get(0).isFunction() && argsf.get(0).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT"))
					{
					narg0 = weight(partition,argsf.get(0).getFunction().getArgs().get(0));
					
					} 
					if (argsf.get(1).isFunction() && argsf.get(1).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT"))
					{
					narg1 = weight(partition,argsf.get(1).getFunction().getArgs().get(0));
					 
					}
					Expr ne = new E_GreaterThan(narg0,narg1);
					g.getElements().remove(e);
					filters.add(new ElementFilter(ne));
					}
			     
			
				}
				if (((ElementFilter) e).getExpr().getFunction().getOpName().toString()==">=") {
					List<Expr> argsf = ((ElementFilter) e).getExpr().getFunction().getArgs();
					Expr narg0 = argsf.get(0);
					Expr narg1 = argsf.get(1);
					
					if ((argsf.get(0).isFunction() && 
							argsf.get(0).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT")) || 
							(argsf.get(0).isFunction() && 
									argsf.get(1).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT")))
						
					{
					if (argsf.get(0).isFunction() && argsf.get(0).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT"))
					{
					narg0 = weight(partition,argsf.get(0).getFunction().getArgs().get(0));
					
					} 
					if (argsf.get(1).isFunction() && argsf.get(1).getFunction().getFunctionIRI().equals("http://www.fuzzy.org#WEIGHT"))
					{
					narg1 = weight(partition,argsf.get(1).getFunction().getArgs().get(0));
					 
					}
					Expr ne = new E_GreaterThanOrEqual(narg0,narg1);
					g.getElements().remove(e);
					filters.add(new ElementFilter(ne));
					} 
				}
			
				}
			}
		}
		
		for (ElementFilter e : filters)
		{
			g.getElements().add(e);
		}
	
	//WEIGHT
	
};

		
	
	
	public Expr fuzzytocrisp (ExprAggregator exp, Map<Node, List<Node>> partition,  List<Node> individuals)
	{
		if (exp.getAggregator().toString().contains("http://www.fuzzy.org#FSUM")) {
			Expr arg = exp.getAggregator().getExprList().get(0);
			Expr agg = fsum(partition, arg,individuals);	 
			return agg;
		} 
		else
		if (exp.getAggregator().toString().contains("http://www.fuzzy.org#FMIN")) {
			Expr arg = exp.getAggregator().getExprList().get(0);
			Expr agg = fmin(partition, arg,individuals);
			return agg;
		} 
		else 
		if (exp.getAggregator().toString().contains("http://www.fuzzy.org#FMAX")) {
			Expr arg = exp.getAggregator().getExprList().get(0);
			Expr agg = fmax(partition, arg,individuals);
			return agg;
		} 
		else 
		if (exp.getAggregator().toString().contains("http://www.fuzzy.org#FAVG")) {
			Expr arg = exp.getAggregator().getExprList().get(0);
			Expr agg = fsum(partition, arg,individuals);
			Expr cc = fcount(partition,individuals);
			Expr result = new E_Divide(agg,cc);
			return result;
		} 
		else 
		if (exp.getAggregator().toString().contains("http://www.fuzzy.org#FCOUNT")) {
			Expr agg = fcount(partition, individuals);
			return agg;
		} 
		else 
		if (exp.getAggregator().toString().contains("http://www.fuzzy.org#PCOUNT")) {
			Expr fcount = fcount(partition, individuals);					
			ExprList cc = new ExprList();
			cc.add(new E_Str(fcount));  
			cc.add(NodeValue.makeString("/"));
			cc.add(new E_Str(new ExprAggregator(null, new AggCount()).getExpr()));
			Expr result = new E_StrConcat(cc);
			return result;
		} 
		else {return exp;}
	}
	
	
	public Expr fcount(Map<Node, List<Node>> partition, List<Node> individuals)
	{
		Expr v = null;
		for (Node p: partition.keySet())
		{
			if (individuals.contains(p))
			{
				Expr s = sum_simple(partition,new ExprVar(p.getName()));
				
				if (v==null && !(s==null)) { v=s;}	
				else
				{if (!(s==null)) { v = new E_Multiply(v,s); v = new E_Divide(v,new ExprAggregator(null,new AggCount()).getExpr());}
				}
			}
		}
		if (v==null) {return NodeValue.makeInteger("0");}
		else {return v;}
	}
	
	
	
	
	
	public Expr fsum(Map<Node, List<Node>> partition, Expr arg, List<Node> individuals)
	{
		Expr v = sum(partition,arg).getExpr();;
		for (Node p: partition.keySet())
		{
			if (individuals.contains(p) && 
					!partition.get(NodeFactory.createVariable(arg.asVar().getVarName())).containsAll(partition.get(p)))
			{
				Expr s = sum_simple(partition,new ExprVar(p.getName()));
				if (!(s==null)) {v = new E_Multiply(v,s);  v = new E_Divide(v,new ExprAggregator(null,new AggCount()).getExpr());}
			}
		}
		return v;
	}
	
	
	
	public Expr fmin(Map<Node, List<Node>> partition, Expr arg, List<Node> individuals)
	{
		Expr v = min(partition,arg).getExpr();;
		
		return v;
	}
	
	public Expr fmax(Map<Node, List<Node>> partition, Expr arg, List<Node> individuals)
	{
		Expr v = max(partition,arg).getExpr();;
		
		return v;
	}
	
	//MULTIPLY TRUE DEGREES OF A COMPONENT and ITEM

	public ExprAggregator sum(Map<Node, List<Node>> partition, Expr arg) {
		Node variable = NodeFactory.createVariable(arg.asVar().getVarName());
		List<Node> ln = partition.get(variable);
		if (ln.isEmpty()) {
			Expr ef = new ExprVar(arg.getVarName());	
			return new ExprAggregator(null,new AggSum(ef));
		} else {
			Expr ef = new ExprVar(arg.getVarName());
			for (Node vln : ln) {
				ef = new E_Multiply(ef, new ExprVar(vln.getName()));
			}	
			return new ExprAggregator(null,new AggSum(ef));
		}
	};
	
	
	public Expr weight(Map<Node, List<Node>> partition, Expr arg) {
		Node variable = NodeFactory.createVariable(arg.asVar().getVarName());
		List<Node> ln = partition.get(variable);
		if (ln.isEmpty()) {
			Expr ef = new ExprVar(arg.getVarName());	
			return ef;
		} else {
			Expr ef = new ExprVar(arg.getVarName());
			for (Node vln : ln) {
				ef = new E_Multiply(ef, new ExprVar(vln.getName()));
			}	
			return ef;
		}
	};
	
	public ExprAggregator min(Map<Node, List<Node>> partition, Expr arg) {
		Node variable = NodeFactory.createVariable(arg.asVar().getVarName());
		List<Node> ln = partition.get(variable);
		if (ln.isEmpty()) {
			Expr ef = new ExprVar(arg.getVarName());
			
			return new ExprAggregator(null,new AggMin(ef));
		} else {
			Expr ef = new ExprVar(arg.getVarName());
			for (Node vln : ln) {
				ef = new E_Multiply(ef, new ExprVar(vln.getName()));
			}	
			return new ExprAggregator(null,new AggMin(ef));
		}
	};
	
	public ExprAggregator max(Map<Node, List<Node>> partition, Expr arg) {
		Node variable = NodeFactory.createVariable(arg.asVar().getVarName());
		List<Node> ln = partition.get(variable);
		if (ln.isEmpty()) {
			Expr ef = new ExprVar(arg.getVarName());
			
			return new ExprAggregator(null,new AggMax(ef));
		} else {
			Expr ef = new ExprVar(arg.getVarName());
			for (Node vln : ln) {
				ef = new E_Multiply(ef, new ExprVar(vln.getName()));
			}	
			return new ExprAggregator(null,new AggMax(ef));
		}
	};
	
	
	public ExprAggregator sum_simple(Map<Node, List<Node>> partition, Expr arg) {
		Node variable = NodeFactory.createVariable(arg.asVar().getVarName());
		List<Node> ln = partition.get(variable);
		if (ln.isEmpty()) {
			return null;  
		} else {
			 
			Expr ef = new ExprVar(ln.get(0).getName());
			for (int i=1;  i < ln.size(); i++) {
				ef = new E_Multiply(ef, new ExprVar(ln.get(i).getName()));
			}	
			return new ExprAggregator(null,new AggSum(ef));
		}
	};

	public void Item(ListIterator<TriplePath> it, Map<Node, List<Node>> partition,List<Node> individuals) {
		TriplePath tp = it.next();
		// ADDING DEPENDENCES FOR AGGREGATORS
		Node subj = tp.getSubject();
		if (subj.isVariable()) {
			individuals.add(subj);
			if (partition.containsKey(subj)) {
			} else {
				List<Node> l = new ArrayList<Node>();
				partition.put(subj, l);
			}
		}

		// STANDARD TRIPLES
		if (tp.getSubject().isVariable() && tp.getObject().isVariable() && !tp.getObject().getName().startsWith("?") ) {
			if (partition.containsKey(tp.getObject())) {
			} else {
				List<Node> l = new ArrayList<Node>();
				partition.put(tp.getObject(), l);
			}
			List<Node> l = partition.get(tp.getSubject());
			partition.put(tp.getObject(), l);
		}
		// STANDARD TRIPLES

		// ADDING DEPENDENCES FOR AGGREGATORS

		if (tp.getObject().isVariable() && it.hasNext()) {
			TriplePath item = it.next(); // first
			if (item.getPredicate().hasURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#first")) {
				it.remove();// remove first
				if (tp.getPredicate().hasURI("http://www.fuzzy.org#type")) {
					it.add(new TriplePath(new Triple(item.getSubject(),
							NodeFactory.createURI("http://www.fuzzy.org#onProperty"), item.getObject())));
					it.next();
					it.remove();
					TriplePath cl = it.next();
					it.remove();
					it.add(new TriplePath(new Triple(item.getSubject(),
							NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), cl.getObject())));
				} else if (tp.getPredicate().hasURI("http://www.fuzzy.org#subPropertyOf")) {
					it.add(new TriplePath(new Triple(item.getSubject(),
							NodeFactory.createURI("http://www.w3.org/2000/01/rdf-schema#subPropertyOf"),
							item.getObject())));
				} else if (tp.getPredicate().hasURI("http://www.fuzzy.org#subClassOf")) {
					it.add(new TriplePath(new Triple(item.getSubject(),
							NodeFactory.createURI("http://www.w3.org/2000/01/rdf-schema#subClassOf"),
							item.getObject())));
				} else
					it.add(new TriplePath(new Triple(item.getSubject(),
							NodeFactory.createURI("http://www.fuzzy.org#item"), item.getObject())));

				// AGGREGATORS
				Node range = item.getObject();
				// AGGREGATORS

				it.next(); // rest
				it.remove();// remove rest
				TriplePath truth = it.next();// first
				it.remove();// remove first
				it.add(new TriplePath(new Triple(item.getSubject(), NodeFactory.createURI("http://www.fuzzy.org#truth"),
						truth.getObject())));
				it.next();// rest
				it.remove();// remove rest

				// ADDING DEPENDENCES FOR AGGREGATORS

				if (subj.isVariable()) {
					List<Node> l = partition.get(subj);
					l.add(truth.getObject());
					partition.remove(subj);
					partition.put(subj, l);
				}

				if (range.isVariable()) {
					if (partition.containsKey(range)) {
					} else {
						List<Node> l = new ArrayList<Node>();
						partition.put(range, l);
					}
				}
				if (range.isVariable()) {
					if (subj.isVariable()) {
						List<Node> l = partition.get(subj);
						partition.put(range, l);
					} else {
						List<Node> l = partition.get(range);
						l.add(truth.getObject());
						partition.remove(range);
						partition.put(range, l);
					}
				}
				// ADDING DEPENDENCES FOR AGGREGATORS
			} else {it.previous();}
		}
	};

	public String fsaSPARQL(String model, String query) {
		
		FunctionRegistry.get().put("http://www.lattice.org#AT_LEAST", AT_LEAST.class);
		FunctionRegistry.get().put("http://www.lattice.org#AT_MOST", AT_MOST.class);
		FunctionRegistry.get().put("http://www.lattice.org#CLOSE_TO", CLOSE_TO.class);
		FunctionRegistry.get().put("http://www.lattice.org#MORE_OR_LESS", MORE_OR_LESS.class);
		FunctionRegistry.get().put("http://www.lattice.org#VERY", VERY.class);
		FunctionRegistry.get().put("http://www.lattice.org#AND_PROD", AND_PROD.class);
		FunctionRegistry.get().put("http://www.lattice.org#OR_PROD", OR_PROD.class);
		FunctionRegistry.get().put("http://www.lattice.org#AND_LUK", AND_LUK.class);
		FunctionRegistry.get().put("http://www.lattice.org#OR_LUK", OR_LUK.class);
		FunctionRegistry.get().put("http://www.lattice.org#AND_GOD", AND_GOD.class);
		FunctionRegistry.get().put("http://www.lattice.org#OR_GOD", OR_GOD.class);
		FunctionRegistry.get().put("http://www.lattice.org#MIN", MIN.class);
		FunctionRegistry.get().put("http://www.lattice.org#MAX", MAX.class);
		FunctionRegistry.get().put("http://www.lattice.org#MEAN", MEAN.class);
		FunctionRegistry.get().put("http://www.lattice.org#WSUM", WSUM.class);
		FunctionRegistry.get().put("http://www.lattice.org#WMAX", WMAX.class);
		FunctionRegistry.get().put("http://www.lattice.org#WMIN", WMIN.class);
		FunctionRegistry.get().put("http://www.lattice.org#WMIN", WMEAN.class);
		AggregateRegistry.register("http://www.fuzzy.org#FSUM", null);
		AggregateRegistry.register("http://www.fuzzy.org#FMAX", null);
		AggregateRegistry.register("http://www.fuzzy.org#FMIN", null);
		AggregateRegistry.register("http://www.fuzzy.org#FCOUNT", null);
		AggregateRegistry.register("http://www.fuzzy.org#PCOUNT", null);
		AggregateRegistry.register("http://www.fuzzy.org#FAVG", null);
		FunctionRegistry.get().put("http://www.fuzzy.org#WEIGHT", WEIGHT.class);
		
		String s1 = FSAtoSPARQL(query);
		String s2 = SPARQL(model, s1);
		return s2;
	}

	public String SPARQL(String filei, String queryStr) {

		OntModel model = ModelFactory.createOntologyModel();
		model.read(filei);
		Query query = QueryFactory.create(queryStr);

		if (query.isSelectType()) {
			ResultSet result = (ResultSet) QueryExecutionFactory.create(query, model).execSelect();
			File theDir = new File("tmp-sparql");
			if (!theDir.exists()) {
				theDir.mkdir();
			}
			String fileName = "./tmp-sparql/" + "result.owl";
			File f = new File(fileName);
			FileOutputStream file;
			try {
				file = new FileOutputStream(f);
				ResultSetFormatter.outputAsXML(file, (ResultSet) result);
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			String s = "";
			try {
				s = readFile(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			final File[] files = theDir.listFiles();
			for (File g : files)
				g.delete();
			theDir.delete();
			return s;
		} else
		if (query.isConstructType()) {
			Model result = QueryExecutionFactory.create(query, model).execConstruct();
			File theDir = new File("tmp-sparql");
			if (!theDir.exists()) {
				theDir.mkdir();
			}
			String fileName = "./tmp-sparql/" + "result.owl";
			File f = new File(fileName);
			FileOutputStream file;
			try {
				file = new FileOutputStream(f);
				result.write(file, FileUtils.langXMLAbbrev);
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			String s = "";
			try {
				s = readFile(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			final File[] files = theDir.listFiles();
			for (File g : files)
				g.delete();
			theDir.delete();
			return s;
		} else
		if (query.isDescribeType()) {
			Model result = QueryExecutionFactory.create(query, model).execDescribe();
			File theDir = new File("tmp-sparql");
			if (!theDir.exists()) {
				theDir.mkdir();
			}
			String fileName = "./tmp-sparql/" + "result.owl";
			File f = new File(fileName);
			FileOutputStream file;
			try {
				file = new FileOutputStream(f);
				result.write(file, FileUtils.langXMLAbbrev);
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			String s = "";
			try {
				s = readFile(fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			final File[] files = theDir.listFiles();
			for (File g : files)
				g.delete();
			theDir.delete();
			return s;
		} 
		else
		{
			Boolean b = QueryExecutionFactory.create(query, model).execAsk();
			return b.toString();
		}

	};

	public static void main(String[] args) {
		String movies = "movies.rdf";
		String hotels = "hotels.rdf";
		String hashtagm = "Hashtag.rdf";
		String UserShowm = "UserShow.rdf";
		String User_Time_Linem = "User_Time_Line.rdf";
		String Retweetersm = "Retweeters.rdf";
		String UserIDm = "UserID.rdf";
		String User_Friendsm = "User_Friends.rdf";
		String User_Tipsm = "User_Tips.rdf";
		String VenueIDm = "VenueID.rdf";
		String Venue_Tipsm = "Venue_Tips.rdf";
		String Venue_SimilarVenuesm = "Venue_SimilarVenues.rdf";
		String Nearm = "Near.rdf";
		String MovieIDm = "MovieID.rdf";
		String Creditsm = "Credits.rdf";
		String Reviewsm = "Movie_Reviews.rdf";
		String Now_Playingm = "Now_Playing.rdf";
		String PersonIDm = "PersonID.rdf";
		String Searchm = "Search.rdf";

		String fuzzy1 = "PREFIX movie: <http://www.movies.org#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "PREFIX f: <http://www.fuzzy.org#>"
				+ "SELECT ?Name ?Rank " + "WHERE { ?Movie movie:name ?Name ."
				+ " ?Movie movie:leading_role (?Actor ?l) ." + " ?Actor movie:name 'George Clooney' ."
				+ " ?Movie f:type (movie:genre movie:Thriller ?t) ." + " BIND (f:AND_PROD(?t,?l) as ?Rank) . "
				+ " FILTER (?Rank > 0.5) }";

		String fuzzy2 = "PREFIX movie: <http://www.movies.org#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "PREFIX f: <http://www.fuzzy.org#>"
				+ "SELECT ?Name ?Rank " + "where { ?Movie movie:name ?Name ."
				+ "?Movie movie:leading_role (?Actor ?l) ." + "?Actor movie:name 'George Clooney' . "
				+ "{ ?Movie f:type (movie:genre movie:Thriller ?c) } "
				+ "union { ?Movie f:type (movie:genre movie:Comedy ?c) } ."
				+ "?Movie f:type (movie:quality movie:Good ?r) ." + "BIND(f:AND_PROD(f:AND_PROD(?r,?l),?c) as ?Rank)}"
				+ "ORDERBY DESC(?Rank) ";

		String fuzzy3 = "PREFIX movie: <http://www.movies.org#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "PREFIX f: <http://www.fuzzy.org#>"
				+ "SELECT ?Name  ?Rank " + "WHERE { ?Movie movie:name ?Name ."
				+ " ?Movie f:type (movie:quality movie:Excellent ?l) ."
				+ " ?Movie f:type (movie:genre movie:Thriller ?t) ." + " BIND(f:OR_PROD(?t,?l) as ?Rank) ."
				+ " FILTER (?Rank > 0.4) }";

		String fuzzy4 = "PREFIX hotel: <http://www.hotels.org#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "PREFIX f: <http://www.fuzzy.org#>"
				+ "SELECT ?Name ?pi ?l " + "WHERE { ?Hotel hotel:name ?Name ." + "?Hotel rdf:type hotel:Hotel ."
				+ "?Hotel hotel:close (?pi ?l) ." + "?pi hotel:name 'Empire State Building'." + " FILTER (?l > 0.25) }";

		String fuzzy5 = "PREFIX hotel: <http://www.hotels.org#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "PREFIX f: <http://www.fuzzy.org#>"
				+ "SELECT ?Name ?p ?d " + "WHERE { ?Hotel hotel:name ?Name ." + "?Hotel rdf:type hotel:Hotel ."
				+ "?Hotel hotel:price ?p ." + "BIND(f:AT_MOST(?p,200,300) as ?d) }";

		String fuzzy6 = "PREFIX hotel: <http://www.hotels.org#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "PREFIX f: <http://www.fuzzy.org#>"
				+ "SELECT ?Name ?p ?d " + "WHERE { ?Hotel hotel:name ?Name ." + "?Hotel rdf:type hotel:Hotel ."
				+ "?Hotel hotel:price ?p ." + "?Hotel f:type (hotel:quality hotel:Good ?g) ."
				+ "?Hotel f:type (hotel:style hotel:Elegant ?e) . " + "BIND(f:WSUM(0.1,f:AND_PROD(f:MORE_OR_LESS(?e),"
				+ "f:VERY(?g)),0.9,f:CLOSE_TO(?p,100,50)) as ?d) }";

		String fuzzy7 = "PREFIX hotel: <http://www.hotels.org#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "PREFIX f: <http://www.fuzzy.org#>"
				+ "SELECT ?Hotel ?g " + "WHERE " + "{ " + "?Hotel rdf:type hotel:Hotel " + "{ "
				+ "SELECT ?Name WHERE { ?Hotel f:type (hotel:quality hotel:Good ?g) } }" + "}";

		String fuzzy8 = "PREFIX hotel: <http://www.hotels.org#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "PREFIX f: <http://www.fuzzy.org#>"
				+ "SELECT ?Hotel ?g " + "WHERE " + "{ " + "?Hotel rdf:type hotel:Hotel "
				+ "OPTIONAL { ?Hotel f:type (hotel:quality hotel:Good ?g) } " + "}";

		String fuzzy9 = "PREFIX hotel: <http://www.hotels.org#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "PREFIX f: <http://www.fuzzy.org#>"
				+ "SELECT ?Hotel ?g " + "WHERE " + "{ {" + "?Hotel rdf:type hotel:Hotel " + "}"
				+ "MINUS { ?Hotel f:type (hotel:quality hotel:Good ?g) } " + "}";

		String fuzzy10 = "PREFIX hotel: <http://www.hotels.org#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "PREFIX f: <http://www.fuzzy.org#>"
				+ "SELECT * WHERE\r\n" + "{" + "{" + "    SELECT ?Hotel WHERE" + "    {"
				+ "         ?Hotel f:type (hotel:quality hotel:Good ?l)" + "    }" + "}" + "UNION" + "{"
				+ "    SELECT ?Hotel WHERE" + "    {" + "         ?Hotel f:type (hotel:quality hotel:Good ?l)" + "    }"
				+ "}" + "}";

		String fuzzy11 = "PREFIX hotel: <http://www.hotels.org#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "PREFIX f: <http://www.fuzzy.org#>"
				+ "SELECT ?Hotel ?g " + "WHERE " + "{ " + "?Hotel rdf:type hotel:Hotel " + "{ "
				+ "FILTER EXISTS {SELECT ?Name WHERE { ?Hotel f:type (hotel:quality hotel:Good ?g) } }}" + "}";

		String fuzzy12 = "PREFIX hotel: <http://www.hotels.org#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + "PREFIX f: <http://www.fuzzy.org#>"
				+ "SELECT ?Hotel ?g " + "WHERE " + "{ " + "?Hotel rdf:type hotel:Hotel ." + "GRAPH ?x { "
				+ " ?Hotel f:type (hotel:quality hotel:Good ?g) }" + "}";

		String hashtag = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX twt: <http://www.twitter.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?t ?rt ?o " + "WHERE { ?s rdf:type twt:tweet ."
				+ "?s twt:text ?t  . " + "?s f:type (twt:retweet__count twt:high ?rt) ."
				+ "?s f:type (twt:opinion twt:positive ?o) ." + "FILTER(f:OR_GOD(?rt,?o) >0.5)}";

		String UserShow = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX twt: <http://www.twitter.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?fc ?fdc " + "WHERE { ?s rdf:type twt:user ."
				+ "?s twt:followers__count ?fc ." + "?s twt:friends__count ?fdc}";

		String User_Time_Line = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX twt: <http://www.twitter.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?t ?o " + "WHERE { ?s rdf:type twt:tweet ."
				+ "?s twt:text ?t ." + "?s f:type (twt:opinion twt:positive ?o) ." + "FILTER(?o>0.5)}";

		String Retweeters = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX twt: <http://www.twitter.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?id " + "WHERE { ?s twt:ids ?ids . ?ids twt:_ ?id }";

		String UserID = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX fsq: <http://www.foursquare.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?hc " + "WHERE { ?s rdf:type fsq:user . "
				+ "?s fsq:firstName ?hc }";
		String User_Friends = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX fsq: <http://www.foursquare.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?n "
				+ "WHERE { ?s fsq:friends ?f . ?f fsq:firstName ?n}";

		String User_Tips = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX fsq: <http://www.foursquare.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?u ?tt ?r " + "WHERE { ?u fsq:tips ?s . "
				+ "?s fsq:text ?tt . " + "?s f:type (fsq:opinion fsq:positive ?r)  . " + "FILTER(?r>0.5) }";

		String VenueID = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX fsq: <http://www.foursquare.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?r ?rs ?b " + "WHERE { ?s rdf:type fsq:venue . "
				+ "?s fsq:rating ?r . ?s fsq:ratingSignals ?rs }";

		String Venue_Tips = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX fsq: <http://www.foursquare.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?tt ?r " + "WHERE {" + "?s fsq:text ?tt . "
				+ "?s f:type (fsq:opinion fsq:positive ?r) . " + "FILTER(?r >0.5)}";

		String Venue_SimilarVenues = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX fsq: <http://www.foursquare.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?n " + "WHERE { ?s fsq:similar ?t . ?t fsq:name ?n}";
		String Near = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX fsq: <http://www.foursquare.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?n " + "WHERE { ?s fsq:near ?t . ?t fsq:name ?n}";

		String MovieID = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX tmdb: <http://www.tmdb.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?r "
				+ "WHERE { ?s f:type (tmdb:categories tmdb:Action ?r)}";

		String Credits = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX tmdb: <http://www.tmdb.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?n ?v " + "WHERE {?s tmdb:cast ?l  ."
				+ " ?l  tmdb:name ?n ." + " ?l  f:type (tmdb:relevance tmdb:high ?v) . " + " FILTER(?v > 0.25)}";

		String Reviews = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX tmdb: <http://www.tmdb.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?tt ?r "
				+ "WHERE { ?s f:type (tmdb:opinion tmdb:positive ?r) . " + "?s tmdb:content ?tt }";

		String Now_Playing = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX tmdb: <http://www.tmdb.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?n ?vc ?va " + "WHERE { ?s rdf:type tmdb:movie ."
				+ " ?s tmdb:original__title ?n ." + "?s f:type (tmdb:vote__count tmdb:high ?vc) ."
				+ " ?s f:type (tmdb:vote__average tmdb:high ?va) ." + " FILTER(f:MIN(?vc,?va) > 0.7) }";

		String PersonID = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX tmdb: <http://www.tmdb.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?n ?p " + "WHERE { ?s rdf:type tmdb:person . "
				+ "?s tmdb:name ?n . ?s f:type (tmdb:popularity tmdb:high ?p)}";

		String Search = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "PREFIX tmdb: <http://www.tmdb.org#>"
				+ "PREFIX f: <http://www.fuzzy.org#>" + "SELECT ?s ?n ?va ?vc " + "WHERE { ?s rdf:type tmdb:movie ."
				+ " ?s tmdb:original__title ?n . " + "?s f:type (tmdb:vote__average  tmdb:high  ?va) ."
				+ "?s f:type (tmdb:vote__count tmdb:high ?vc) ." + " FILTER(f:MAX(?va,?vc) > 0.7)}";

		String Agg1 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + "PREFIX movie: <http://www.movies.org#>\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\n" + "SELECT ( f:FCOUNT('*')  as ?total )\n "
				+ "WHERE { ?s f:type (movie:genre movie:Comedy ?ta) .\n "
				+ "?k f:type (movie:genre movie:Drama ?tb) .  \n " + "?s movie:duration ?t . ?k movie:duration ?d }";
		
		String Agg2 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + "PREFIX movie: <http://www.movies.org#>\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\n" + "SELECT ?s (f:FSUM(?t) as ?total) \n "
				+ "WHERE { ?s f:type (movie:genre movie:Comedy ?ta) ."
				+ "?s movie:duration ?t  } GROUP BY ?s  ";
		
		String Agg3 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + "PREFIX movie: <http://www.movies.org#>\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\n" + "SELECT ?DEGREE (f:FSUM(?t) as ?total) \n "
				+ "WHERE { ?s f:type (movie:genre movie:Comedy ?ta) ."
				+ "?s movie:duration ?t . BIND(IF(?ta>0.5,'high','low') AS ?DEGREE)  "
				+ "} GROUP BY ?DEGREE  HAVING (f:FSUM(?t) > 100)";
		
		String Agg4 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + "PREFIX movie: <http://www.movies.org#>\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\n" + "SELECT ?c  ?total "
				+ "WHERE {  {SELECT ?c (f:FSUM(?t) as ?total) \n "
				+ "WHERE { ?s f:type (movie:genre ?c ?ta) ."
				+ "?s movie:duration ?t } GROUP BY ?c}}";
		
		String Agg5 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + "PREFIX movie: <http://www.movies.org#>\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\n" + "SELECT ?c  "
				+ "WHERE { ?c movie:duration ?d . FILTER(?d >= 100) .  "
				+ "FILTER EXISTS { SELECT ?c  WHERE { ?c f:type (movie:genre ?s ?ta) . FILTER(?ta >= 0.9) }}}";
		
		
		String Agg6 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + "PREFIX movie: <http://www.movies.org#>\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\n"  
				+ "SELECT ?s WHERE {\n"
				+ "?s f:type (movie:genre movie:Comedy ?ta) .\n"
				+ "?s movie:duration ?t .\n"
				+ "FILTER (f:WEIGHT(?t)=?total) .\n"
				+ "{SELECT (f:FMAX(?t) as ?total) \n"
				+ "WHERE {\n"
				+ "?s f:type (movie:genre movie:Comedy ?ta) .\n"
				+ "?s movie:duration ?t\n"
				+ "}}}";
						

		// org.apache.log4j.BasicConfigurator.configure(new NullAppender());

		FsaSPARQL fs = new FsaSPARQL();
		String t = fs.FSAtoSPARQL(Agg1);
		System.out.println("ORIGINAL");
		System.out.println(Agg1);
		//String s2 = fs.SPARQL(movies, t);
		System.out.println("TRADUCCI�N");
		System.out.println(t);
		//System.out.println("RESULTADO");
		//System.out.println(s2);

	};
};
