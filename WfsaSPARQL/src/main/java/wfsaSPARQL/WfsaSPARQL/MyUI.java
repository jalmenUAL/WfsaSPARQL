package wfsaSPARQL.WfsaSPARQL;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.Position;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

 

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of an HTML page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

	Integer step = 0;
	List<List<String>> rules = null;
	//W Grid<HashMap<String, Term>> answers = new Grid<>();

	public static String readStringFromURL(String requestURL) throws IOException {
		try (Scanner scanner = new Scanner(new URL(requestURL).openStream(), StandardCharsets.UTF_8.toString())) {
			scanner.useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		}
	}

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		
		setErrorHandler(new ErrorHandler() {
			  
			  @Override public void error(com.vaadin.server.ErrorEvent event) {
			  
			  show_error("Wrong Syntax", "Unexpected element." ); }
			  
			  });

		final VerticalLayout layout = new VerticalLayout();

		Image lab = new Image(null, new ThemeResource("banner-fsa.png"));
		lab.setWidth("100%");
		lab.setHeight("150px");
		
		

		Label ds = new Label("RDF Dataset");
		TextField file = new TextField();
		file.setSizeFull();
		file.setValue("http://minerva.ual.es:8080/fsa/movies-fuzzy.rdf");
		
		Label fsaq = new Label("FSA-SPARQL Query");
		Label cv = new Label("SPARQL Crisp Version");

		Button run = new Button("Execute");
		run.setWidth("100%");
		run.setStyleName(ValoTheme.BUTTON_FRIENDLY);

		Panel edS = new Panel();
		Panel edP = new Panel();
		edS.setSizeFull();
		edP.setSizeFull();

		AceEditor editor = new AceEditor();
		editor.setHeight("300px");
		editor.setWidth("2000px");
		editor.setFontSize("12pt");
		editor.setMode(AceMode.sql);
		editor.setTheme(AceTheme.eclipse);
		editor.setUseWorker(true);
		editor.setReadOnly(false);
		editor.setShowInvisibles(false);
		editor.setShowGutter(false);
		editor.setShowPrintMargin(false);
		editor.setUseSoftTabs(false);
		
		String Mov1 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" 
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" 
				+ "PREFIX movie: <http://www.movies.org#>\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\n" 
				+ "SELECT ( f:FCOUNT('*')  as ?total )\n"
				+ "WHERE {\n"
				+ "?s f:type (movie:genre movie:Comedy ?ta) \n"
				+ "}";
		
		String Mov2 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" 
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" 
				+ "PREFIX movie: <http://www.movies.org#>\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\n" 
				+ "SELECT ( f:FCOUNT('*')  as ?total )\n"
				+ "WHERE {\n"
				+ "?s f:type (movie:genre movie:Comedy ?ta) .\n"
				+ "?s f:type (movie:genre movie:Drama ?tb) \n" 
				+ "}";
		
		String Mov3 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" 
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" 
				+ "PREFIX movie: <http://www.movies.org#>\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\n" 
				+ "SELECT ( f:FCOUNT('*')  as ?total )\n"
				+ "WHERE {\n"
				+ "?s f:type (movie:genre movie:Comedy ?ta) .\n"
				+ "?k f:type (movie:genre movie:Drama ?tb) \n" 
				+ "}";
		
		String Mov4 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" 
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" 
				+ "PREFIX movie: <http://www.movies.org#>\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\n" 
				+ "SELECT ?s (f:FSUM(?t) as ?total) \n"
				+ "WHERE {\n"
				+ "?s f:type (movie:genre movie:Comedy ?ta) .\n"
				+ "?s movie:duration ?t \n"
				+ "}\n"
				+ "GROUP BY ?s";
		
		String Mov5 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" 
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" 
				+ "PREFIX movie: <http://www.movies.org#>\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\n" 
				+ "SELECT ?d (f:FSUM(?t) as ?total) \n"
				+ "WHERE {\n"
				+ "?s f:type (movie:genre movie:Comedy ?ta) .\n"
				+ "?s movie:duration ?t . \n"
				+ "BIND(IF(?ta>0.5,'high','low') AS ?d)\n"
				+ "}\n"
				+ "GROUP BY ?d \n"
				+ "HAVING (f:FSUM(?t) > 100)";
		
		 
		
		String Mov6 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" 
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" 
				+ "PREFIX movie: <http://www.movies.org#>\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\n" 
				+ "SELECT ?c (f:FAVG(?t) as ?total) \n"
				+ "WHERE {\n"
				+ "?s f:type (movie:genre ?c ?ta) .\n"
				+ "?s movie:duration ?t\n"
				+ "}\n"
				+ "GROUP BY ?c\n";
		
		
		String Mov7 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" 
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" 
				+ "PREFIX movie: <http://www.movies.org#>\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\n"  
				+ "SELECT ?s\n"
				+ "WHERE {\n"
				+ "?s f:type (movie:genre movie:Comedy ?ta) .\n"
				+ "?s movie:duration ?t .\n"
				+ "FILTER (f:WEIGHT(?t)=?total) .\n"
				+ "{\n"
				+ "SELECT (f:FMAX(?t) as ?total)\n"
				+ "WHERE {\n"
				+ "?s f:type (movie:genre movie:Comedy ?ta) .\n"
				+ "?s movie:duration ?t\n"
				+ "}\n"
				+ "}\n"
				+ "}";
		
		String Mov8=
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
						+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" 
						+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
						+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" 
						+ "PREFIX movie: <http://www.movies.org#>\n"
						+ "PREFIX f: <http://www.fuzzy.org#>\n" +
				"SELECT ?fs (f:FCOUNT('*') as ?total)\r\n" + 
				"WHERE {\r\n" + 
				"?Movie f:type (movie:genre ?fs ?t) }\r\n"+
				"GROUP BY ?fs\r\n" + 
				"HAVING (f:FCOUNT('*')>=4)";
		
		
		
		String Mov9=
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
						+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" 
						+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
						+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" 
						+ "PREFIX movie: <http://www.movies.org#>\n"
						+ "PREFIX f: <http://www.fuzzy.org#>\n" +
				"SELECT ?s\r\n" + 
				"WHERE {\r\n" + 
				"?s f:type (movie:genre movie:Comedy ?ts) .\r\n" +
				"?s movie:duration ?ds .\r\n" + 
				"FILTER (f:WEIGHT(?ds)=?max) .\r\n" + 
				"{\r\n" + 
				"SELECT (f:FMAX(?dk) as ?max)\r\n" + 
				"WHERE {\r\n" + 
				"?k f:type (movie:genre movie:Comedy ?tk) .\r\n " +
				"?k movie:duration ?dk}\r\n" + 
				"}}\r\n";
		
		String Mov10 = "PREFIX movie: <http://www.movies.org#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\r\n" 
				+ "PREFIX l: <http://www.lattice.org#>\r\n"
				+ "SELECT ?Name ?Rank \r\n" 
				+ "WHERE {\n"
				+ "?Movie movie:name ?Name . \r\n"
				+ "?Movie movie:leading_role (?Actor ?l) . \r\n" 
				+ "?Actor movie:name \"George Clooney\". \r\n"
				+ "?Movie f:type (movie:quality movie:Good ?r) . \r\n" 
				+ "BIND(l:AND_PROD(?r,?l) as ?Rank) . \r\n"
				+ "FILTER (?Rank > 0.3)\n"
				+ "}";

		String Hot1=
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
						+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" 
						+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
						+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" 
						+ "PREFIX hotel: <http://www.hotels.org#>\n"
						+ "PREFIX f: <http://www.fuzzy.org#>\n" +
				"SELECT ?q ?d (f:FAVG(?p) as ?total)\r\n"+
				"WHERE {\r\n" + 
				"?s f:type (hotel:quality ?q ?ta) .\r\n" + 
				"?s hotel:price ?p .\r\n" +
				"BIND(IF(?ta>0.5,'high','low') AS ?d) }\r\n" +
				"GROUP BY ?d ?q\r\n";
		
		
		String Hot2 = "PREFIX hotel: <http://www.hotels.org#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\r\n" 
				+ "PREFIX l: <http://www.lattice.org#>\r\n"
				+ "SELECT ?Name ?l \r\n" 
				+ "WHERE {\n"
				+ "?Hotel hotel:name ?Name . \r\n"
				+ "?Hotel rdf:type hotel:Hotel . \r\n" 
				+ "?Hotel hotel:close (?pi ?l) . \r\n"
				+ "?pi hotel:name \"Empire State Building\" \r\n" 
				+ "FILTER (?l > 0.75)\n"
				+ "}";
			

		String Hot3 = "PREFIX hotel: <http://www.hotels.org#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\r\n" 
				+ "PREFIX l: <http://www.lattice.org#>\r\n"
				+ "SELECT ?Name ?d \r\n" 
				+ "WHERE { ?Hotel hotel:name ?Name .\r\n"
				+ "?Hotel rdf:type hotel:Hotel .\r\n" 
				+ "?Hotel hotel:price ?p \r\n"
				+ "BIND(l:AT_MOST(?p,200,300) as ?d)\n"
				+ "}";

		String Hot4 = "PREFIX hotel: <http://www.hotels.org#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\r\n" 
				+ "PREFIX l: <http://www.lattice.org#>\r\n"
				+ "SELECT ?Name ?d \r\n" 
				+ "WHERE {\n"
				+ "?Hotel hotel:name ?Name . \r\n"
				+ "?Hotel rdf:type hotel:Hotel . \r\n" 
				+ "?Hotel hotel:price ?p . \r\n"
				+ "?Hotel f:type (hotel:quality hotel:Good ?g) . \r\n"
				+ "?Hotel f:type (hotel:style hotel:Elegant ?e) \r\n"
				+ "BIND(l:WSUM(0.1,l:AND_PROD(l:MORE_OR_LESS(?e), \r\n"
				+ "l:VERY(?g)),0.9,l:CLOSE_TO(?p,100,50)) as ?d) .\r\n" 
				+ "FILTER(?d > 0.4)\n"
				+ "}";

		String Hot5 = "PREFIX hotel: <http://www.hotels.org#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\r\n" 
				+ "PREFIX l: <http://www.lattice.org#>\r\n"
				+ "SELECT ?Name ?l ?pi ?pi2 \r\n" 
				+ "WHERE {\n"
				+ "?Hotel hotel:name ?Name . \r\n"
				+ "?Hotel rdf:type hotel:Hotel . \r\n" 
				+ "{\n"
				+ "?Hotel hotel:close (?pi ?l) . \r\n"
				+ "?pi hotel:name \"Empire State Building\"\n"
				+ "}\n"
				+ "UNION\n"
				+ "{ ?Hotel hotel:close (?pi2 ?l) .\n"
				+ "?pi2 hotel:name \"Central Park\" \n"
				+ "}\r\n"
				+ "FILTER(?l >0.7)\n"
				+ "}";

		String Hot6 = "PREFIX hotel: <http://www.hotels.org#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\r\n" 
				+ "PREFIX l: <http://www.lattice.org#>\r\n"
				+ "SELECT ?Name ?l1 ?l2 ?pi ?pi2 \r\n" 
				+ "WHERE {\n"
				+ "?Hotel hotel:name ?Name . \r\n"
				+ "?Hotel rdf:type hotel:Hotel . \r\n" 
				+ "{\n"
				+ "?Hotel hotel:close (?pi ?l1) . \r\n"
				+ "?pi hotel:name \"Empire State Building\" .\n"
				+ "FILTER(?l1 >0.3)\n"
				+ "}\n"
				+ "OPTIONAL {\n"
				+ "?Hotel hotel:close (?pi2 ?l2) .\n"
				+ "?pi2 hotel:name \"Central Park\"  \r\n"
				+ "FILTER(?l2 >0.8)\n"
				+ "}\n"
				+ "}";

		String Hot7 = "PREFIX hotel: <http://www.hotels.org#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX f: <http://www.fuzzy.org#>\r\n" 
				+ "PREFIX l: <http://www.lattice.org#>\r\n"
				+ "SELECT ?Name ?l1  \r\n" 
				+ "WHERE {\n"
				+ "?Hotel hotel:name ?Name . \r\n"
				+ "?Hotel rdf:type hotel:Hotel . \r\n" 
				+ "{\n"
				+ "?Hotel hotel:close (?pi ?l1) . \r\n"
				+ "?pi hotel:name \"Empire State Building\" .\n"
				+ "FILTER(?l1 >0.3)\n"
				+ "} "
				+ "MINUS\n"
				+ "{\n"
				+ "?Hotel hotel:close (?pi2 ?l2) .\n"
				+ "?pi2 hotel:name \"Central Park\"  \r\n"
				+ "FILTER(?l2 >0.8)\n"
				+ "}\n"
				+ "}";

		

		ComboBox<String> examples = new ComboBox<>("Select an Example");
		examples.setItems("Example 1", "Example 2", "Example 3", "Example 4", "Example 5", "Example 6",
				"Example 7","Example 8", "Example 9", "Example 10","Example 11","Example 12", "Example 13", "Example 14",
				"Example 15","Example 16","Example 17");

		examples.setPageLength(18);
		examples.setWidth("100%");
		examples.addValueChangeListener(event -> {
			if (event.getSource().isEmpty()) {
				show_error("Warning","No example selected");
			} else {
				if (event.getValue() == "Example 1") {
					file.setValue("http://minerva.ual.es:8080/fsa/movies-fuzzy.rdf");
					editor.setValue(Mov1);
				} else if (event.getValue() == "Example 2") {
					file.setValue("http://minerva.ual.es:8080/fsa/movies-fuzzy.rdf");
					editor.setValue(Mov2);
				} else if (event.getValue() == "Example 3") {
					file.setValue("http://minerva.ual.es:8080/fsa/movies-fuzzy.rdf");
					editor.setValue(Mov3);
				} else if (event.getValue() == "Example 4") {
					file.setValue("http://minerva.ual.es:8080/fsa/movies-fuzzy.rdf");
					editor.setValue(Mov4);
				} else if (event.getValue() == "Example 5") {
					file.setValue("http://minerva.ual.es:8080/fsa/movies-fuzzy.rdf");
					editor.setValue(Mov5);
				} else if (event.getValue() == "Example 6") {
					file.setValue("http://minerva.ual.es:8080/fsa/movies-fuzzy.rdf");
					editor.setValue(Mov6);
				} else if (event.getValue() == "Example 7") {
					file.setValue("http://minerva.ual.es:8080/fsa/movies-fuzzy.rdf");
					editor.setValue(Mov7);
				} else if (event.getValue() == "Example 8") {
					file.setValue("http://minerva.ual.es:8080/fsa/movies-fuzzy.rdf");
					editor.setValue(Mov8);
				} else if (event.getValue() == "Example 9") {
					file.setValue("http://minerva.ual.es:8080/fsa/movies-fuzzy.rdf");
					editor.setValue(Mov9);
				} else if (event.getValue() == "Example 10") {
					file.setValue("http://minerva.ual.es:8080/fsa/movies-fuzzy.rdf");
					editor.setValue(Mov10);
				} else if (event.getValue() == "Example 11") {
				file.setValue("http://minerva.ual.es:8080/fsa/hotels-fuzzy.rdf");
				editor.setValue(Hot1);
				}
				else if (event.getValue() == "Example 12") {
					file.setValue("http://minerva.ual.es:8080/fsa/hotels-fuzzy.rdf");
					editor.setValue(Hot2);
					}
				else if (event.getValue() == "Example 13") {
					file.setValue("http://minerva.ual.es:8080/fsa/hotels-fuzzy.rdf");
					editor.setValue(Hot3);
					}
				else if (event.getValue() == "Example 14") {
					file.setValue("http://minerva.ual.es:8080/fsa/hotels-fuzzy.rdf");
					editor.setValue(Hot4);
					}
				else if (event.getValue() == "Example 15") {
					file.setValue("http://minerva.ual.es:8080/fsa/hotels-fuzzy.rdf");
					editor.setValue(Hot5);
					}
				else if (event.getValue() == "Example 16") {
					file.setValue("http://minerva.ual.es:8080/fsa/hotels-fuzzy.rdf");
					editor.setValue(Hot6);
					}
				else if (event.getValue() == "Example 17") {
					file.setValue("http://minerva.ual.es:8080/fsa/hotels-fuzzy.rdf");
					editor.setValue(Hot7);
					}

			}
		});
		

		editor.setValue(Mov1);
		editor.setDescription("FSA-SPARQL Query");

		AceEditor editorP = new AceEditor();
		editorP.setHeight("300px");
		editorP.setWidth("2000px");
		editorP.setFontSize("12pt");
		editorP.setMode(AceMode.sql);
		editorP.setTheme(AceTheme.eclipse);
		editorP.setUseWorker(true);
		editorP.setReadOnly(false);
		editorP.setShowInvisibles(false);
		editorP.setShowGutter(false);
		editorP.setShowPrintMargin(false);
		editorP.setUseSoftTabs(false);
		editorP.setDescription("CRISP SPARQL Query");

		AceEditor editorOntology = new AceEditor();
		Panel edO = new Panel();
		edO.setSizeFull();

		FsaSPARQL fs = new FsaSPARQL(); 
		
		Grid<HashMap<String, RDFNode>> answers = new Grid<>("Execution Result");
		answers.setWidth("100%");
		answers.setHeight("100%");
		answers.setVisible(false);
		List<HashMap<String, RDFNode>> rows = new ArrayList<>();
		
		run.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				String t = fs.FSAtoSPARQL(editor.getValue());
				editorP.setValue(t);
				OntModel model = ModelFactory.createOntologyModel();
				model.read(file.getValue());
				Query query = QueryFactory.create(t);
				ResultSet result = (ResultSet) QueryExecutionFactory.create(query, model).execSelect();
				answers.removeAllColumns();
				List<String> variables = result.getResultVars();
				rows.clear();
				while (result.hasNext()) {
					QuerySolution solution = result.next();
					HashMap<String, RDFNode> sol = new HashMap<String, RDFNode>();
					for (String vari : variables) {
						sol.put(vari, solution.get(vari));
					}
					rows.add(sol);
				}
				answers.setItems(rows);
				if (rows.size() > 0) {
					answers.setVisible(true);
					cv.setVisible(true);
					editorP.setVisible(true);
					HashMap<String, RDFNode> sr = rows.get(0);
					for (Map.Entry<String, RDFNode> entry : sr.entrySet()) {
						answers.addColumn(h -> h.get(entry.getKey())).setCaption(entry.getKey());
					}
				} else {
					show_error("Successful Execution","Empty Answer of Query");
				}

				 
				 

			}

		});

		edS.setContent(editor);
		edP.setContent(editorP);

		layout.addComponent(lab);
		layout.addComponent(examples);
		layout.addComponent(ds);
		layout.addComponent(file);
		layout.addComponent(fsaq);
		layout.addComponent(edS);	
		layout.addComponent(run);
		layout.addComponent(answers);
		layout.addComponent(cv);
		layout.addComponent(edP);
		 
		
		String ontology;
		try {
			ontology = readStringFromURL(file.getValue());
			editorOntology.setValue(ontology);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			show_error("Ontology Error",e.getMessage());
		}

		edO.setContent(editorOntology);
		editorOntology.setHeight("300px");
		editorOntology.setWidth("2000px");
		editorOntology.setFontSize("12pt");
		editorOntology.setMode(AceMode.sql);
		editorOntology.setTheme(AceTheme.eclipse);
		editorOntology.setUseWorker(true);
		editorOntology.setReadOnly(false);
		editorOntology.setShowInvisibles(false);
		editorOntology.setShowGutter(false);
		editorOntology.setShowPrintMargin(false);
		editorOntology.setUseSoftTabs(false);
		
		cv.setVisible(false);
		editorP.setVisible(true);

		//QUITO EL EDITOR DE RDF
		//layout.addComponent(edO);

		setContent(layout);
		this.setSizeFull();

	}
	
	/* SHOW_ERROR*/
	
	public void show_error(String type, String message) {
		Notification notif = new Notification(type, message, Notification.Type.ERROR_MESSAGE);
		notif.setDelayMsec(20000);
		notif.setPosition(Position.BOTTOM_RIGHT);
		notif.show(Page.getCurrent());
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}
}
