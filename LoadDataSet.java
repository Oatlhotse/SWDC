package jcolibri.EducationRecommender.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import jcolibri.EducationRecommender.AssociationRule;
import jcolibri.EducationRecommender.EMAlgorithm;
import jcolibri.EducationRecommender.EducationRecommender;
import jcolibri.EducationRecommender.IbkKNN;
import jcolibri.EducationRecommender.J48Algorithm;
import jcolibri.EducationRecommender.Naive;
import jcolibri.EducationRecommender.SimpleKAlgorithm;
import jcolibri.EducationRecommender.readFromDataset;
import jcolibri.cbrcore.CBRCase;
import jcolibri.util.FileIO;

public class LoadDataSet extends JDialog {

	private static final long serialVersionUID = 1L;

	JLabel image;
    String currentLine;
    BufferedReader br = null;
    
	JLabel caseId;
	JLabel category;
	JLabel technique;
	
	readFromDataset run = new readFromDataset();
	ArrayList<CBRCase> cases;
	int currentCase;
	
	BufferedReader read;
	readFromDataset files = new readFromDataset();
	File selectedFile;

	JComboBox<String> cProblem;
	JComboBox<String> cCategory;
	JComboBox<String> cAlgorithm;
	
	
	public LoadDataSet(JFrame main)
	{
		super(main,true);
		configureFrame();
	}
	
	private void configureFrame()
	{
		try
		{
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1)
		{
		}
		
		this.setTitle("Reuse Cases");

		image = new JLabel();
		image.setIcon(new ImageIcon(FileIO.findFile("jcolibri/EducationRecommender/gui/step5.png")));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(image, BorderLayout.WEST);
				
		/**********************************************************/
		JPanel panel = new JPanel();
		panel.setLayout(new SpringLayout());

		//*************---- FileChooser*******************
	
		JButton btn = new JButton(" Browse ");
		panel.add(new JLabel("Load Data Set"));
		panel.add(btn);

		panel.add(new JLabel("Choose Problem"));
		String[] chooseProbelm ={"PredictResultsOfStudents", "EvaluatePerformance", "PredictFinalGrade", "GroupStudentsBehavior","DetectStudentsProblems","SearchForInterestingRelationship"};
		panel.add(cProblem = new JComboBox<String>(chooseProbelm));
		
		panel.add(new JLabel("Choose Category"));
		String[] chooseCategory = {"Classification","Clustering","Association Rules"};
		panel.add(cCategory = new JComboBox<String>(chooseCategory));
		
		panel.add(new JLabel("Choose Algorithm"));
		String[] classiFicationAlgorith = {"J48","Naive Bayes","IBK"};
		panel.add(cAlgorithm = new JComboBox<String>(classiFicationAlgorith));
		
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					JFileChooser choose = (new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()));
	            	  int returnValue = choose.showOpenDialog(null);

	          		  if (returnValue == JFileChooser.APPROVE_OPTION) {
	          			   selectedFile = choose.getSelectedFile();
	          			   try{
	          				   
		          				read = readFromDataset.readDataFile(selectedFile.getAbsolutePath());
		          	
	          			   }catch(Exception ex){
	          				   ex.printStackTrace();
	          			   }
	          		  }
				}
		            
		});
		
		
		
		cCategory.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
	       	  if (cCategory.getSelectedIndex()==0){
	       
	       		  cAlgorithm.setModel(new DefaultComboBoxModel<String>(new String[]  {"J48","Naive Bayes","IBK"}));
	       		
	       		  		
	
	       	  } else if(cCategory.getSelectedIndex()==1){
	
	       		  cAlgorithm.setModel(new DefaultComboBoxModel<String>(new String[] {"Expectation-Maximum","K-Means"}));
	       		
	           		  
	      		   
	       	  } else if(cCategory.getSelectedIndex()==2){
	       		  
	       		  cAlgorithm.setModel(new DefaultComboBoxModel<String>(new String[] {"Apriori"})); 
	       		
	       	  } 
			}
        });

		Utils.makeCompactGrid(panel,
                4, 2,
                6, 80,      
                10, 10); 
		
		JPanel panelAux = new JPanel();
		panelAux.setLayout(new BorderLayout());
		panelAux.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		panelAux.add(panel,BorderLayout.NORTH);
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new BorderLayout());
		
		JButton run = new JButton("Run");
		run.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				if(read==null){
					javax.swing.JOptionPane.showConfirmDialog(null, "DataSet Not Loaded", "Load DataSet", javax.swing.JOptionPane.PLAIN_MESSAGE);
					
					
				}else if((cCategory.getSelectedIndex()==0) && (cAlgorithm.getSelectedIndex()==0)){
					read = readFromDataset.readDataFile(selectedFile.getAbsolutePath());
     				try {
     					J48Algorithm j48 = new J48Algorithm(null);
     					j48.j48Algorithm(read, files);
     					j48.setVisible(true);
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
     			
     			}else if((cCategory.getSelectedIndex()==0) && (cAlgorithm.getSelectedIndex()==1)){
     				read = readFromDataset.readDataFile(selectedFile.getAbsolutePath());
     				try {
     					Naive naive = new Naive(null);
						naive.naiveBys(read, files);
						naive.setVisible(true);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
     				}else if((cCategory.getSelectedIndex()==0) && (cAlgorithm.getSelectedIndex()==2)){
     				read = readFromDataset.readDataFile(selectedFile.getAbsolutePath());
     				try {
     					IbkKNN ibk = new IbkKNN(null);
						ibk.knnAlgorithm(read, files);
						ibk.setVisible(true);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					}else if((cCategory.getSelectedIndex()==1) && (cAlgorithm.getSelectedIndex()==0)){
          				try {
         					EMAlgorithm em = new EMAlgorithm(null);
    						em.emAlgorithm(selectedFile.getAbsolutePath());
    						em.setVisible(true);
    					} catch (Exception e1) {
    						e1.printStackTrace();
    					}
					}else if((cCategory.getSelectedIndex()==1) && (cAlgorithm.getSelectedIndex()==1)){
						
             				try {
             					SimpleKAlgorithm simpleK = new SimpleKAlgorithm(null);
             					simpleK.simpleKAlgorithm(selectedFile.getAbsolutePath());
             					simpleK.setVisible(true);
             					
        					} catch (Exception e1) {
        						e1.printStackTrace();
        				}
					}else if((cCategory.getSelectedIndex()==2) && (cAlgorithm.getSelectedIndex()==0)){
						
                  		try {
                  			AssociationRule associationRule = new AssociationRule(null);
                  			associationRule.associationRu(selectedFile.getAbsolutePath());
                  			associationRule.setVisible(true);
             			} catch (Exception e1) {
             			e1.printStackTrace();
             		}
				}else{
					javax.swing.JOptionPane.showConfirmDialog(null, "Error", "Check you DataSet", javax.swing.JOptionPane.YES_NO_OPTION);
				}
			}
		});
		buttons.add(run,BorderLayout.CENTER);
		
		
		JButton exit = new JButton("Exit");
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try {
					EducationRecommender.getInstance().postCycle();
				} catch (Exception ex) {
					org.apache.commons.logging.LogFactory.getLog(EducationRecommender.class).error(ex);
				}
				System.exit(-1);
			}
		});
		buttons.add(exit,BorderLayout.WEST);
		
		panelAux.add(buttons, BorderLayout.SOUTH);
		this.getContentPane().add(panelAux, BorderLayout.CENTER);

		/**********************************************************/
		
		
		this.pack();
		this.setSize(600, this.getHeight());
		this.setResizable(false);
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width - this.getWidth()) / 2,
			(screenSize.height - this.getHeight()) / 2, 
			getWidth(),
			getHeight());
		}
	
	
	public static void main(String[] args) throws Exception {
		LoadDataSet load = new LoadDataSet(null);
		load.setVisible(true);
		System.out.println("Bye");
        
	}

	

}
