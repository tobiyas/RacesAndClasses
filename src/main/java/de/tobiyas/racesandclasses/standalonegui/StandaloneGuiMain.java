package de.tobiyas.racesandclasses.standalonegui;

import java.awt.GraphicsEnvironment;

import javax.swing.JOptionPane;

import de.tobiyas.racesandclasses.standalonegui.gui.MainGui;
import de.tobiyas.racesandclasses.standalonegui.gui.TreeView;

public class StandaloneGuiMain {

	
	public static void main(String[] args) {
		//no Display present.
		if(GraphicsEnvironment.isHeadless()) return;
		
		JOptionPane.showMessageDialog(null, "There will be a GUI here soon...");
		
		MainGui main = new MainGui();
		new TreeView(main.getFrame());
		TreeView.redraw();
		
		main.repack();
	}

}
