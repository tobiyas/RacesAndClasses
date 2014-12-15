package de.tobiyas.racesandclasses.standalonegui.gui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import de.tobiyas.racesandclasses.standalonegui.data.GuiClass;
import de.tobiyas.racesandclasses.standalonegui.data.GuiLoader;
import de.tobiyas.racesandclasses.standalonegui.data.GuiRace;
import de.tobiyas.racesandclasses.standalonegui.data.GuiTrait;
import de.tobiyas.racesandclasses.standalonegui.data.option.TraitConfigOption;

public class TreeView {
	
	/**
	 * The Tree to use.
	 */
	private static JTree treeView;
	
	
	public TreeView(JFrame jFrame) {
		treeView = new JTree();
		jFrame.add(treeView);
	}
	
	
	public static void redraw(){
		DefaultMutableTreeNode racesNode = new DefaultMutableTreeNode("Races");
		DefaultMutableTreeNode classesNode = new DefaultMutableTreeNode("Classes");
		
		DefaultTreeModel model = (DefaultTreeModel)treeView.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		root.setUserObject("RacesAndClasses");
		root.removeAllChildren();
		
		
		//add the Races / Classes.
		for(GuiRace race : GuiLoader.getLoadedRaces()){
			DefaultMutableTreeNode raceNode = new DefaultMutableTreeNode(race);
			raceNode.add(getForTraits(new LinkedList<GuiTrait>(race.getTraits())));
			
			racesNode.add(raceNode);
		}

		for(GuiClass clazz : GuiLoader.getLoadedClasses()){
			DefaultMutableTreeNode clazzNode = new DefaultMutableTreeNode(clazz.getClassName());
			clazzNode.add(getForTraits(new LinkedList<GuiTrait>(clazz.getTraits())));
			
			racesNode.add(clazzNode);
		}
		
		
		//at last reload.
		root.add(racesNode);
		root.add(classesNode);

		model.reload(root);
	}
	
	
	private static DefaultMutableTreeNode getForTraits(List<GuiTrait> traits){
		DefaultMutableTreeNode traitsNode = new DefaultMutableTreeNode("Traits");
		Collections.sort(traits);
		
		for(GuiTrait trait : traits){
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(trait.getTraitType());
			for(TraitConfigOption option : trait.getTraitConfiguration()){
				node.add( new DefaultMutableTreeNode(option.toString()) );
			}
			
			traitsNode.add(node);
		}
		
		return traitsNode;
	}
}
