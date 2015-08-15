package de.tobiyas.racesandclasses.standalonegui.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import de.tobiyas.racesandclasses.standalonegui.data.GuiClass;
import de.tobiyas.racesandclasses.standalonegui.data.GuiLoader;
import de.tobiyas.racesandclasses.standalonegui.data.GuiRace;
import de.tobiyas.racesandclasses.standalonegui.data.GuiTrait;
import de.tobiyas.racesandclasses.standalonegui.data.option.AbstractTraitConfigOption;
import de.tobiyas.racesandclasses.standalonegui.data.option.TraitConfigOption;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The Tree to use.
	 */
	private static JTree raceTree;
	
	/**
	 * The Tree to use.
	 */
	private static JTree classTree;
	
	
	/**
	 * The Tree to use.
	 */
	private JPanel rightSide;
	
	
	public MainFrame() {
		this.setPreferredSize(new Dimension(500,500));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		buildUI();
		rebuildTree();

		this.pack();
		this.setVisible(true);
	}
	
	

	/**
	 * Builds the UI.
	 */
	private void buildUI() {
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if(command == null || command.isEmpty()) return;
				if(command.equals("LOAD")){
					GuiLoader.openBaseFileSelection();
					rebuildTree();
					return;
				}
				
				if(command.equals("SAVE")){
					GuiLoader.save();
					JOptionPane.showMessageDialog(null, "Save done!");
					return;
				}
				
				if(command.equals("REMOVERACE")){
					if(raceTree == null || raceTree.getSelectionModel() == null) return;
					
					TreePath selectedPath = raceTree.getSelectionModel().getSelectionPath();
					if(selectedPath == null) return;
					
					Object selectedNode = selectedPath.getLastPathComponent();
					if(selectedNode == null) return;
					
					if(selectedNode instanceof DefaultMutableTreeNode){
						Object userInfo = ((DefaultMutableTreeNode) selectedNode).getUserObject();
						if(userInfo instanceof TraitConfigOption){
							((TraitConfigOption)userInfo).reset();
							rebuildSelection();
							treeUpdated();
						}
						return;
					}
					
					if(selectedNode instanceof DefaultMutableTreeNode){
						Object userInfo = ((DefaultMutableTreeNode) selectedNode).getUserObject();
						if(userInfo instanceof GuiTrait){
							((GuiTrait)userInfo).removeFromParent();
							rebuildSelection();
							treeUpdated();
						}
						return;
					}

					if(selectedNode instanceof DefaultMutableTreeNode){
						Object userInfo = ((DefaultMutableTreeNode) selectedNode).getUserObject();
						if(userInfo instanceof GuiRace){
							((DefaultMutableTreeNode)selectedNode).removeFromParent();
							GuiLoader.removeRace((GuiRace) userInfo);
							rebuildTree();
						}
						return;
					}
				}
				
				if(command.equals("REMOVECLASS")){
					if(classTree == null || classTree.getSelectionModel() == null) return;
					
					TreePath selectedPath = classTree.getSelectionModel().getSelectionPath();
					if(selectedPath == null) return;
					
					Object selectedNode = selectedPath.getLastPathComponent();
					if(selectedNode == null) return;
					
					if(selectedNode instanceof DefaultMutableTreeNode){
						Object userInfo = ((DefaultMutableTreeNode) selectedNode).getUserObject();
						if(userInfo instanceof TraitConfigOption){
							((TraitConfigOption)userInfo).reset();
							rebuildSelection();
							treeUpdated();
						}
						return;
					}

					if(selectedNode instanceof DefaultMutableTreeNode){
						Object userInfo = ((DefaultMutableTreeNode) selectedNode).getUserObject();
						if(userInfo instanceof GuiClass){
							((DefaultMutableTreeNode)selectedNode).removeFromParent();
							GuiLoader.removeClass((GuiClass) userInfo);
							rebuildTree();
						}
						return;
					}
				}
			}
		};
		
		
		JLabel titleLabel = new JLabel("Races And Classes Builder");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(titleLabel, BorderLayout.NORTH);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setContinuousLayout(true);
		splitPane.setResizeWeight(0.5);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		raceTree = new JTree();
		raceTree.setRootVisible(false);
		raceTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if(e == null || e.getNewLeadSelectionPath() == null) return;
				
				Object last = e.getNewLeadSelectionPath().getLastPathComponent();
				if(last == null) return;
				
				if(last instanceof DefaultMutableTreeNode){
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) last;
					Object obj = node.getUserObject();
					if(obj != null && obj instanceof AbstractTraitConfigOption){
						//we have a new Selection!
						AbstractTraitConfigOption option = (AbstractTraitConfigOption) obj;
						userSelectedSomething(node,option);
					}else{
						userUnSelected();
					}
				}
			}
		});
		
		JLabel raceHeader = new JLabel("Races");
		JPanel racePanel = new JPanel();
		racePanel.setLayout(new BorderLayout());
		racePanel.add(raceHeader, BorderLayout.NORTH);
		racePanel.add(new JScrollPane(raceTree), BorderLayout.CENTER);
		
		JPanel raceControl = new JPanel();
		raceControl.setLayout(new BorderLayout());
		
		JButton removeButton = new JButton("Remove");
		removeButton.setActionCommand("REMOVERACE");
		removeButton.addActionListener(listener);
		raceControl.add(removeButton, BorderLayout.EAST);
		
		JButton addButton = new JButton("Add");
		addButton.setActionCommand("ADDRACE");
		addButton.addActionListener(listener);
		raceControl.add(addButton, BorderLayout.WEST);
		
		
		racePanel.add(raceControl, BorderLayout.SOUTH);

		
		
		classTree = new JTree();
		classTree.setRootVisible(false);
		classTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				Object last = e.getNewLeadSelectionPath().getLastPathComponent();
				if(last instanceof DefaultMutableTreeNode){
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) last;
					Object obj = node.getUserObject();
					if(obj != null && obj instanceof AbstractTraitConfigOption){
						//we have a new Selection!
						AbstractTraitConfigOption option = (AbstractTraitConfigOption) obj;
						userSelectedSomething(node,option);
					}else{
						userUnSelected();
					}
				}
			}
		});
		
		
		//class Panel
		JLabel classHeader = new JLabel("Classes");
		JPanel classPanel = new JPanel();
		classPanel.setLayout(new BorderLayout());
		classPanel.add(classHeader, BorderLayout.NORTH);
		classPanel.add(new JScrollPane(classTree), BorderLayout.CENTER);
		
		JPanel classControl = new JPanel();
		classControl.setLayout(new BorderLayout());
		
		removeButton = new JButton("Remove");
		removeButton.setActionCommand("REMOVECLASS");
		removeButton.addActionListener(listener);
		classControl.add(removeButton, BorderLayout.EAST);
		
		addButton = new JButton("Add");
		addButton.setActionCommand("ADDCLASS");
		addButton.addActionListener(listener);
		classControl.add(addButton, BorderLayout.WEST);
		
		classPanel.add(classControl, BorderLayout.SOUTH);
		
		
		//splits definition.
		JSplitPane rcSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rcSplit.setTopComponent(racePanel);
		rcSplit.setBottomComponent(classPanel);
		rcSplit.setResizeWeight(0.5);
		
		splitPane.setLeftComponent(rcSplit);
		
		rightSide = new JPanel();
		rightSide.setLayout(new BorderLayout(0, 0));
		splitPane.setRightComponent(rightSide);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JButton saveButton = new JButton("Save");
		panel.add(saveButton, BorderLayout.WEST);
		
		JButton loadButton = new JButton("Load");
		panel.add(loadButton, BorderLayout.EAST);
		
		JLabel lblNewLabel = new JLabel("by tobiyas");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel, BorderLayout.CENTER);
		
	
		loadButton.setActionCommand("LOAD");
		loadButton.addActionListener(listener);
		saveButton.setActionCommand("SAVE");
		saveButton.addActionListener(listener);
	}
	
	
	
	/**
	 * The Selected node.
	 */
	private DefaultMutableTreeNode selectedNode = null;
	/**
	 * The selected Option.
	 */
	private AbstractTraitConfigOption selectedOption = null;
	
	
	/**
	 * The User selected a node.
	 * 
	 * @param node that was selected
	 * @param obj that was selected.
	 */
	protected void userSelectedSomething(DefaultMutableTreeNode node, AbstractTraitConfigOption obj) {
		this.selectedNode = node;
		this.selectedOption = obj;
		
		rebuildSelection();
	}
	
	/**
	 * The User selected a node.
	 * 
	 * @param node that was selected
	 * @param obj that was selected.
	 */
	protected void userUnSelected() {
		if(this.selectedNode == null || this.selectedOption == null) return;
		this.selectedNode = null;
		this.selectedOption = null;
		
		rebuildSelection();
	}


	/**
	 * Rebuilds the Selection Interface.
	 */
	private void rebuildSelection() {
		rightSide.removeAll();
		boolean empty = this.selectedNode == null || this.selectedOption == null;
		
		if(empty) rightSide.add(new JPanel());
		else selectedOption.addWithConfigOption(rightSide);
		
		rightSide.revalidate();
	}

	
	/**
	 * Update the Tree on data change.
	 */
	public static void treeUpdated(){
		if(raceTree != null) raceTree.revalidate();
		if(classTree != null) classTree.revalidate();
	}


	/**
	 * Rebuilds the Tree.
	 */
	public static void rebuildTree(){
		DefaultMutableTreeNode racesNode = new DefaultMutableTreeNode("Races");
		DefaultMutableTreeNode classesNode = new DefaultMutableTreeNode("Classes");
		
		DefaultTreeModel raceModel = (DefaultTreeModel)raceTree.getModel();
		DefaultTreeModel classModel = (DefaultTreeModel)classTree.getModel();
		
		
		//add the Races / Classes.
		for(GuiRace race : GuiLoader.getLoadedRaces()){
			DefaultMutableTreeNode raceNode = new DefaultMutableTreeNode(race);
			raceNode.setUserObject(race);
			raceNode.add(getForConfig(race));
			raceNode.add(getForTraits(new LinkedList<GuiTrait>(race.getTraits())));
			
			racesNode.add(raceNode);
		}

		for(GuiClass clazz : GuiLoader.getLoadedClasses()){
			DefaultMutableTreeNode classNode = new DefaultMutableTreeNode(clazz.getClassName());
			classNode.setUserObject(clazz);
			classNode.add(getForConfig(clazz));
			classNode.add(getForTraits(new LinkedList<GuiTrait>(clazz.getTraits())));
			
			classesNode.add(classNode);
		}
		
		
		//at last reload.
		raceModel.setRoot(racesNode);
		classModel.setRoot(classesNode);

		raceModel.reload();
		classModel.reload();
	}
	
	
	private static MutableTreeNode getForConfig(GuiClass clazz) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Configuration");
		
		for(TraitConfigOption option : clazz.getConfig()){
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(option);
			root.add(node);
		}
		
		return root;
	}


	
	/**
	 * Generates a Config Node.
	 * 
	 * @param race to generate for.
	 * @return a tree representation of the Config.
	 */
	private static MutableTreeNode getForConfig(GuiRace race) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Configuration");
		
		for(TraitConfigOption option : race.getConfig()){
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(option);
			root.add(node);
		}
		
		return root;
	}



	private static DefaultMutableTreeNode getForTraits(List<GuiTrait> traits){
		DefaultMutableTreeNode traitsNode = new DefaultMutableTreeNode("Traits");
		Collections.sort(traits);
		
		for(GuiTrait trait : traits){
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(trait.getTraitType());
			DefaultMutableTreeNode needed = new DefaultMutableTreeNode("Needed Options");
			DefaultMutableTreeNode optional = new DefaultMutableTreeNode("Optional Options");
			
			node.add(needed);
			node.add(optional);
			
			for(TraitConfigOption option : trait.getTraitConfigurationNeeded()){
				DefaultMutableTreeNode traitNode = new DefaultMutableTreeNode(option.toString());
				traitNode.setUserObject(option);
				needed.add(traitNode);
			}
			
			for(TraitConfigOption option : trait.getTraitConfigurationOptional()){
				DefaultMutableTreeNode traitNode = new DefaultMutableTreeNode(option.toString());
				traitNode.setUserObject(option);
				optional.add(traitNode);
			}
			
			traitsNode.add(node);
		}
		
		return traitsNode;
	}

}
