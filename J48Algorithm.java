package jcolibri.EducationRecommender;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import jcolibri.EducationRecommender.gui.LoadDataSet;
import jcolibri.EducationRecommender.gui.QueryDialog;
import jcolibri.EducationRecommender.gui.Utils;
import jcolibri.util.FileIO;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.matrix.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import weka.core.converters.*;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.FastVector;
import weka.core.Instances;
import weka.datagenerators.clusterers.*;
import weka.clusterers.*;

public class J48Algorithm extends JDialog {

	private static final long serialVersionUID = 1L;
	
	JTextPane tp = new JTextPane();
	JScrollPane fruitListScrollPane = new JScrollPane(tp);
	
	public J48Algorithm(JFrame parent)
	{
		super(parent,true);
		configureFrame();
		
	}
	public void j48Algorithm(BufferedReader datafile, readFromDataset files) throws Exception
    {
       
        Instances data = new Instances(datafile);
        data.setClassIndex(data.numAttributes() - 1);
        
        
        Instances[][] split = files.crossValidationSplit(data, 10);
        
        
        Instances[] trainingSplits = split[0];
        Instances[] testingSplits  = split[1];
        
        Classifier models = new J48();
                                    //new SMO()};
        
        //FastVector predictions = new FastVector();
        //double accuracy = files.calculateAccuracy(predictions);
                
            FastVector predictions = new FastVector();
            for(int i = 0; i < trainingSplits.length; i++) {
                Evaluation validation = files.simpleClassify(models, trainingSplits[i], testingSplits[i]);
                predictions.appendElements(validation.predictions());
                
               // System.out.println(models.toString());
            }
            
            double accuracy = files.calculateAccuracy(predictions);
            
            Evaluation eval = new Evaluation(data);
            Random rand = new Random(1);  // using seed = 1
            int folds = 10;
            eval.crossValidateModel(models, data, folds, rand);
            //System.out.println(eval.toMatrixString());
            
           // String[] minn ={models.getClass().getSimpleName().toString() , ": " ,String.format("%.2f%%", accuracy) , "\n=====================\n","\n\nConfussion Matxix"};

           tp.setText( models.getClass().getSimpleName().toString() +  ": " + String.format("%.2f%%", accuracy)+"\n"+"\n\nConfussion Matxix"+"\n"+eval.toMatrixString()+"\n"+
        		   eval.toClassDetailsString());
           tp.setBounds(85, 7, 495, 410);
            //f.getContentPane().add(tp);
            
        }
	
	private void configureFrame(){
		try
		{
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1)
		{
		}
		
		this.setTitle("DataSet Results");
		
		JLabel image;
		
		image = new JLabel();
		
		image.setIcon(new ImageIcon(FileIO.findFile("jcolibri/EducationRecommender/gui/step6.png")));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(image, BorderLayout.WEST);

		JPanel panel = new JPanel();
	  	panel.setLayout(new SpringLayout());
		
		
		//tp.setBounds(500, 500);
        panel.add(new JTextPane());
        this.getContentPane().add(tp);
  		
        JPanel panelAux = new JPanel();
 		panelAux.setLayout(new BorderLayout());
 		panelAux.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

 		panelAux.add(panel,BorderLayout.NORTH);
 		
 		JPanel buttons = new JPanel();
 		buttons.setLayout(new BorderLayout());
 		
 		JButton ok = new JButton("Close");
 		ok.addActionListener(new ActionListener(){
 			public void actionPerformed(ActionEvent e) {		
 				close();
 			}
 		});
 		buttons.add(ok,BorderLayout.CENTER);
 		JButton exit = new JButton("Exit");
 		exit.addActionListener(new ActionListener(){
 			public void actionPerformed(ActionEvent e) {	
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
	public void close(){
		this.setVisible(false);
	}
    
    
	    public static void main(String[] args) {
	    	J48Algorithm qf = new J48Algorithm(null);
			qf.setVisible(true);
			System.out.println("Bye");
		}
        //END

}
