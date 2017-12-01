
package jcolibri.EducationRecommender.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import jcolibri.EducationRecommender.EducationDescription;
import jcolibri.EducationRecommender.EducationRecommender;
import jcolibri.EducationRecommender.EducationSolution;
import jcolibri.cbrcore.CBRCase;
import jcolibri.util.FileIO;

public class RetainDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static int numcases = 0;
	
	JLabel image;
	
	JLabel caseId;
	JLabel educationType;
	JLabel  problem;
	JLabel psm;
	JLabel ctg;
	JLabel  stg;
	JLabel assignment;
	JLabel attendance;
	JLabel predicate;
	JLabel participation;
	JTextField idEditor;
	JLabel category;
	JLabel technique;
	JButton setId;
	JCheckBox saveCheck;
	
	ArrayList<CBRCase> cases;
	int currentCase;
	
	ArrayList<CBRCase> casesToRetain;
	
	public RetainDialog(JFrame main)
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
		
		this.setTitle("Revise cases");

		
		image = new JLabel();
		image.setIcon(new ImageIcon(FileIO.findFile("jcolibri/EducationRecommender/gui/step6.png")));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(image, BorderLayout.WEST);
		
		
		/**********************************************************/
		JPanel panel = new JPanel();
		//panel.setLayout(new GridLayout(8,2));
		panel.setLayout(new SpringLayout());
		
		JLabel label;

		panel.add(label = new JLabel("Description"));
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		panel.add(label = new JLabel());

		panel.add(new JLabel("EducationType"));
		panel.add(educationType = new JLabel());
		
		panel.add(new JLabel("Problem"));
		panel.add(this.problem = new JLabel());
		
		panel.add(new JLabel("Previous Semester Mark"));
		panel.add(this.psm = new JLabel());
		
		panel.add(new JLabel("Class Test Grade"));
		panel.add(this.ctg = new JLabel());
		
		panel.add(new JLabel("Semester Test Grade"));
		panel.add(this.stg = new JLabel());
		
		panel.add(new JLabel("Assignment"));
		panel.add(this.assignment = new JLabel());
		
		panel.add(new JLabel("Attendance"));
		panel.add(this.attendance = new JLabel());
		
		panel.add(new JLabel("Predicate"));
		panel.add(this.predicate = new JLabel());
		
		panel.add(new JLabel("Participation"));
		panel.add(this.participation = new JLabel());
		
		panel.add(label = new JLabel("Solution"));
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		panel.add(label = new JLabel());
		
		panel.add(new JLabel("Category"));
		panel.add(category = new JLabel());
		
		panel.add(new JLabel("Technique"));
		panel.add(technique = new JLabel());
		
//		Lay out the panel.
		Utils.makeCompactGrid(panel,
		                13, 2, //rows, cols
		                6, 6,        //initX, initY
		                30, 10);       //xPad, yPad
		
		JPanel casesPanel = new JPanel();
		casesPanel.setLayout(new BorderLayout());
		casesPanel.add(panel, BorderLayout.CENTER);
		
		JPanel casesIterPanel = new JPanel();
		casesIterPanel.setLayout(new FlowLayout());
		JButton prev = new JButton("<<");
		casesIterPanel.add(prev);
		casesIterPanel.add(caseId = new JLabel("Case id"));
		JButton follow = new JButton(">>");
		casesIterPanel.add(follow);
		
		prev.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				currentCase = (currentCase+cases.size()-1) % cases.size();
				showCase();
			}
		});
		
		follow.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				currentCase = (currentCase+1) % cases.size();
				showCase();
			}
		});
		casesPanel.add(casesIterPanel, BorderLayout.NORTH);
		
		JPanel defineIdsPanel = new JPanel();
		saveCheck = new JCheckBox("Save Case with new Id:");
		defineIdsPanel.add(saveCheck);
		saveCheck.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				enableSaveCase();
			}
		});
		idEditor = new JTextField(20);
		defineIdsPanel.add(idEditor);
		setId = new JButton("Apply");
		defineIdsPanel.add(setId);
		
		setId.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setId();
			}
		});
		enableSaveCase();
		
		casesPanel.add(defineIdsPanel, BorderLayout.SOUTH);
		
		JPanel panelAux = new JPanel();
		panelAux.setLayout(new BorderLayout());
		panelAux.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		panelAux.add(casesPanel,BorderLayout.NORTH);
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new BorderLayout());
		
		JButton ok = new JButton("Next >>");
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				next();
			}
		});
		buttons.add(ok,BorderLayout.CENTER);
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
	
	void next()
	{
		this.setVisible(false);
	}
	
	void enableSaveCase()
	{
		idEditor.setEnabled(saveCheck.isSelected());
		setId.setEnabled(saveCheck.isSelected());
	}
	
	public void showCases(Collection<CBRCase> eval, int casebasesize)
	{
		cases = new ArrayList<CBRCase>(eval);
		casesToRetain = new ArrayList<CBRCase>();
		currentCase = 0;
		if(numcases<casebasesize)
			numcases = casebasesize+1;
		idEditor.setText("10"+(++numcases));
		showCase();
	}
	
	void showCase()
	{
		
		CBRCase _case = cases.get(currentCase);
		this.caseId.setText(_case.getID().toString()+" ("+(currentCase+1)+"/"+cases.size()+")");
		
		EducationDescription desc = (EducationDescription) _case.getDescription();
		this.educationType.setText(desc.getEducationType().toString());
		this.problem.setText(desc.getProblem().toString());
		this.psm.setText(desc.getPsm().toString());
		this.ctg.setText(desc.getCtg().toString());
		this.stg.setText(desc.getStg().toString());
		this.assignment.setText(desc.getAssignment().toString());
		this.attendance.setText(desc.getAttendance().toString());
		this.predicate.setText(desc.getPredicate().toString());
		this.participation.setText(desc.getParticipation().toString());
		
		EducationSolution solution = (EducationSolution)_case.getSolution();
		this.category.setText(solution.getCategory().toString());
		this.technique.setText(solution.getTechnique().toString());
		
	}
	
	void setId()
	{
		CBRCase _case = cases.get(currentCase);
		cases.remove(_case);
		
		casesToRetain.add(_case);
		
		currentCase = 0;
		idEditor.setText("10"+(++numcases));
		saveCheck.setSelected(false);
		enableSaveCase();
		showCase();
	}
	public Collection<CBRCase> getCasestoRetain()
	{
		return casesToRetain;
	}

	public static void main(String[] args) {
		RetainDialog qf = new RetainDialog(null);
		qf.setVisible(true);
		System.out.println("Bye");
	}
}
