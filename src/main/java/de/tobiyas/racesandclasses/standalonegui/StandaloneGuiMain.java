package de.tobiyas.racesandclasses.standalonegui;

import java.awt.GraphicsEnvironment;

import javax.swing.JOptionPane;

import de.tobiyas.racesandclasses.standalonegui.gui.MainFrame;
import de.tobiyas.racesandclasses.standalonegui.login.LogServerDataReader;
import de.tobiyas.racesandclasses.standalonegui.login.ServerConnectionData;

public class StandaloneGuiMain {

	
	public static void main(String[] args) {
		//no Display present.
		if(GraphicsEnvironment.isHeadless()) return;
		
		ServerConnectionData data = LogServerDataReader.readFromLogFile();
		if(data != null) {
			System.out.println("Data: " + data);
		}
		
		JOptionPane.showMessageDialog(null, "There will be a GUI here soon...");
		
		new MainFrame();
	}
	
	
	

}
