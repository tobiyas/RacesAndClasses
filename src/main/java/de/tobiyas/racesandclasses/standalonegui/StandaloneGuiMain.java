package de.tobiyas.racesandclasses.standalonegui;

import java.awt.GraphicsEnvironment;

import javax.swing.JOptionPane;

import de.tobiyas.racesandclasses.standalonegui.gui.MainFrame;

public class StandaloneGuiMain {

	
	public static void main(String[] args) {
		//no Display present.
		if(GraphicsEnvironment.isHeadless()) return;
		
		JOptionPane.showMessageDialog(null, "There will be a GUI here soon...");
		
		new MainFrame();
	}

}
