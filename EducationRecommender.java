
package jcolibri.EducationRecommender;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import es.ucm.fdi.gaia.ontobridge.OntoBridge;
import es.ucm.fdi.gaia.ontobridge.OntologyDocument;
import jcolibri.EducationRecommender.gui.AutoAdaptationDialog;
import jcolibri.EducationRecommender.gui.LoadDataSet;
import jcolibri.EducationRecommender.gui.QueryDialog;
import jcolibri.EducationRecommender.gui.ResultDialog;
import jcolibri.EducationRecommender.gui.RetainDialog;
import jcolibri.EducationRecommender.gui.RevisionDialog;
import jcolibri.EducationRecommender.gui.SimilarityDialog;
import jcolibri.casebase.LinealCaseBase;
import jcolibri.cbraplications.StandardCBRApplication;
import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.cbrcore.Connector;
import jcolibri.connector.DataBaseConnector;
import jcolibri.exception.ExecutionException;
import jcolibri.method.retrieve.RemoveRetrievalEvaluation;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.KNNretrieval.KNNConfig;
import jcolibri.method.retrieve.KNNretrieval.KNNretrievalMethod;
import jcolibri.method.retrieve.KNNretrieval.similarity.global.Average;
import jcolibri.method.reuse.NumericDirectProportionMethod;
import jcolibri.util.FileIO;

public class EducationRecommender implements StandardCBRApplication {

	private static EducationRecommender educaionInstance = null;
	public  static EducationRecommender getInstance()
	{
		if(educaionInstance == null)
			educaionInstance = new EducationRecommender();
		return educaionInstance;
	}
	
	Connector _connector;
	CBRCaseBase _caseBase;
	
	SimilarityDialog similarityDialog;
	ResultDialog resultDialog;
	AutoAdaptationDialog autoAdaptDialog;
	RevisionDialog revisionDialog;
	RetainDialog retainDialog;
	static LoadDataSet load;
	
	public void configure() throws ExecutionException {
		try {
			//Emulate data base server
			jcolibri.test.database.HSQLDBserver.init();
			
			// Create a data base connector
			_connector = new DataBaseConnector();
			
			_connector.initFromXMLfile(jcolibri.util.FileIO
					.findFile("jcolibri/EducationRecommender/databaseconfig.xml"));
			// Create a Lineal case base for in-memory organization
			_caseBase = new LinealCaseBase();
			
			
			similarityDialog = new SimilarityDialog(main);
			resultDialog     = new ResultDialog(main);
			autoAdaptDialog  = new AutoAdaptationDialog(main);
			revisionDialog   = new RevisionDialog(main);
			retainDialog     = new RetainDialog(main);
			
		} catch (Exception e) {
			throw new ExecutionException(e);
		}
	}
	
	public CBRCaseBase preCycle() throws ExecutionException {
		_caseBase.init(_connector);		
		
		java.util.Collection<CBRCase> cases = _caseBase.getCases();
		for(CBRCase c: cases)
			System.out.println(c);
		return _caseBase;
	}

	public void cycle(CBRQuery query) throws ExecutionException {
		
		similarityDialog.setVisible(true);
		KNNConfig simConfig = similarityDialog.getSimilarityConfig();
		simConfig.setDescriptionSimFunction(new Average());
		
		Collection<RetrievalResult> eval = KNNretrievalMethod.evaluateSimilarity(_caseBase.getCases(), query, simConfig);
		
		resultDialog.showCases(eval);
		resultDialog.setVisible(true);
		
		Collection<CBRCase> selectedcases = RemoveRetrievalEvaluation.removeRetrievalEvaluation(eval);
		
		
		autoAdaptDialog.setVisible(true);
		
		
		if(autoAdaptDialog.problem()){
			NumericDirectProportionMethod.directProportion(	new Attribute("Problem",EducationDescription.class), 
						new Attribute("Category",EducationSolution.class), 
						query, selectedcases);
		}
		if(autoAdaptDialog.psm()){
			NumericDirectProportionMethod.directProportion(	new Attribute("psm",EducationDescription.class), 
						new Attribute("Category",EducationSolution.class), 
						query, selectedcases);
		}
		if(autoAdaptDialog.ctg()){
			NumericDirectProportionMethod.directProportion(	new Attribute("ctg",EducationDescription.class), 
						new Attribute("Category",EducationSolution.class), 
						query, selectedcases);
		}
		if(autoAdaptDialog.stg()){
			NumericDirectProportionMethod.directProportion(	new Attribute("stg",EducationDescription.class), 
						new Attribute("Category",EducationSolution.class), 
						query, selectedcases);
		}
		if(autoAdaptDialog.assignment()){
			NumericDirectProportionMethod.directProportion(	new Attribute("assignment",EducationDescription.class), 
						new Attribute("Category",EducationSolution.class), 
						query, selectedcases);
		}
		if(autoAdaptDialog.attendance()){
			NumericDirectProportionMethod.directProportion(	new Attribute("attendance",EducationDescription.class), 
						new Attribute("Category",EducationSolution.class), 
						query, selectedcases);
		}
		
		if(autoAdaptDialog.predicate()){
			NumericDirectProportionMethod.directProportion(	new Attribute("predicate",EducationDescription.class), 
						new Attribute("Category",EducationSolution.class), 
						query, selectedcases);
		}
		
		if(autoAdaptDialog.participation()){
			NumericDirectProportionMethod.directProportion(	new Attribute("participation",EducationDescription.class), 
						new Attribute("Category",EducationSolution.class), 
						query, selectedcases);
		}

		
		revisionDialog.showCases(selectedcases);
		revisionDialog.setVisible(true);
		
		retainDialog.showCases(selectedcases, _caseBase.getCases().size());
		retainDialog.setVisible(true);
		
		Collection<CBRCase> casesToRetain = retainDialog.getCasestoRetain();
		_caseBase.learnCases(casesToRetain);
		
	

	}

	public void postCycle() throws ExecutionException {
		_connector.close();
		jcolibri.test.database.HSQLDBserver.shutDown();
	}

	static JFrame main;
	void showMainFrame()
	{
		main = new JFrame("Education Recommender");
		main.setResizable(false);
		main.setUndecorated(true);
		JLabel label = new JLabel(new ImageIcon(jcolibri.util.FileIO.findFile("/jcolibri/test/main/jcolibri2.jpg")));
		main.getContentPane().add(label);
		main.pack();
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		main.setBounds((screenSize.width - main.getWidth()) / 2,
			(screenSize.height - main.getHeight()) / 2, 
			main.getWidth(),
			main.getHeight());
		main.setVisible(true);
	}
	
	public static void main(String[] args) {
	
		EducationRecommender recommender = getInstance();
		recommender.showMainFrame();
		try
		{
			recommender.configure();
			recommender.preCycle();

			QueryDialog qf = new QueryDialog(main);
			

			boolean cont = true;
			while(cont)
			{
				qf.setVisible(true);
				CBRQuery query = qf.getQuery();
			
				recommender.cycle(query);
				int ans = javax.swing.JOptionPane.showConfirmDialog(null, "CBR cycle finished, query again?", "Cycle finished", javax.swing.JOptionPane.YES_NO_OPTION);
				cont = (ans == javax.swing.JOptionPane.YES_OPTION);
			}
			load = new LoadDataSet(main);
			load.setVisible(true);
			recommender.postCycle();
		}catch(Exception e)
		{
			org.apache.commons.logging.LogFactory.getLog(EducationRecommender.class).error(e);
			javax.swing.JOptionPane.showMessageDialog(null, e.getMessage());
		}
		System.exit(0);
	}
}
