package de.tobiyas.racesandclasses.standalonegui.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import de.tobiyas.racesandclasses.standalonegui.data.GuiLoader;

public class MainGui {

	/**
	 * The Main Frame
	 */
	private final JFrame frame;
	
	
	public MainGui() {
		this.frame = new JFrame();
		
		JButton reload = new JButton("Load");
		reload.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GuiLoader.openBaseFileSelection();
				TreeView.redraw();
			}
		});
		this.frame.add(reload);
		
	}
	
	
	
	/**
	 * Repacks the GUI.
	 */
	public void repack(){
		this.frame.pack();
		this.frame.setVisible(true);
	}
	
	
	public JFrame getFrame() {
		return frame;
	}
	
}
