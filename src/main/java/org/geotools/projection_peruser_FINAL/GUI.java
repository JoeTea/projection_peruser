/*
 * GUI.java
 * v1.0
 * 02 May 2017
 * Joel Perren
 */

package org.geotools.projection_peruser_FINAL;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.action.SafeAction;
import org.opengis.referencing.FactoryException;

/**
 * The main class of the application. Sets up the GUI and calls methods from other classes.
 * @author Joel Perren
 * @version 1.0
 */
public class GUI {
	
	/** Global variables **/
	protected static MapContent map;
	protected static JMapFrame frame;
	protected static Layer world; //Global for bounds calculations
	protected static String[][] projectionInfo = Projections.plateCarreeInfo;
	
	private boolean debug = false;
	
	public static void main(String[] args) {
		IO.setUpFiles();
		new GUI();
	}
	
	/**
	 * Sets up the main GUI window.
	 * 
	 * Defines and number of buttons and menus and assigns them action listeners.
	 */
	private GUI() {
		
		//Set up a MapContent with 3 layers: world, grid, and tissots.
		map = new MapContent();
		map.setTitle("Projection Peruser");
		
		world = new FeatureLayer(IO.worldFeatureSource, 
				Utilities.createStyle(new Color(204,204,204), 1, Color.WHITE, 0));
		
		Layer grid = new FeatureLayer(Utilities.createGrid(), 
				Utilities.createStyle(Color.WHITE, 0, new Color(153,153,153), 1));
		
		Layer tissots = new FeatureLayer(IO.tissotsFeatureSource, 
				Utilities.createStyle(new Color(255,153,0), 0.85, Color.WHITE, 0));
		
		map.addLayer(world);
		map.addLayer(grid);
		map.addLayer(tissots);
		
		//Set up a JMapFrame and enable tool/status bars
		frame = new JMapFrame(map);
		frame.setSize(800, 600);
		frame.enableStatusBar(true);
		frame.enableToolBar(true);
		if (debug) frame.enableLayerTable(true);
		
		//Set up JMenuBar for save/load options and adds to to an action listener
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File...");
		menuBar.add(fileMenu);
		
		JMenuItem save = new JMenuItem("Save current world");
		JMenuItem exit = new JMenuItem("Exit");
		
		fileMenu.add(save);
		fileMenu.add(exit);
		
		ListenForMenu lForMenu = new ListenForMenu();
		save.addActionListener(lForMenu);
		exit.addActionListener(lForMenu);
		
		//Set up projection buttons on ToolBar
		JToolBar toolbar = frame.getToolBar();
        toolbar.addSeparator();
        toolbar.add(new JButton(new TransformPlateCarree()));
        toolbar.add(new JButton(new TransformMercator()));
        toolbar.add(new JButton(new TransformGallPeters()));
        toolbar.add(new JButton(new TransformEckert_IV()));
        toolbar.add(new JButton(new TransformMollweide()));
        toolbar.addSeparator();
        toolbar.add(new JButton(new ToggleGrid()));
        toolbar.add(new JButton(new ToggleTissots()));
        toolbar.addSeparator();
        toolbar.add(new JButton(new QueryProjection()));
		
		frame.setMinimumSize(new Dimension(850, 600));
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		
	} //END OF GUI()
	
	/**
	 * Sets up the pop-up window which displays additional information on projections to the user.
	 */
	private void infoPanel(){
		//Disable main frame when infoPanel opens
		frame.setEnabled(false);
		
		JFrame infoPanel = new JFrame();
		infoPanel.setTitle(projectionInfo[0][1]);
		
		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		
		//Set to HTML to allow for simple text formatting.
		editorPane.setContentType("text/html");
		
		//Finds default theme font-family
		String fontfamily = editorPane.getFont().getFamily(); 
		String setFont = "<body style=\"font-family:" + fontfamily + "\">";
		editorPane.setText(
				"<html>" + setFont +
				"<b>" + projectionInfo[0][0] + "</b>: " + projectionInfo[0][1] + "<br><br>" +
				"<b>" + projectionInfo[1][0] + "</b>: " + projectionInfo[1][1] + "<br><br>" +
				projectionInfo[2][1] + "<br><br>" +
				"<b>" + projectionInfo[3][0] + "</b>: " + projectionInfo[3][1] +
				"</html>"
						);
			
		JScrollPane editorScrollPane = new JScrollPane(editorPane);
		editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);		
		infoPanel.getContentPane().add(editorScrollPane);
		infoPanel.setSize(new Dimension (600, 400));
		infoPanel.setVisible(true);
		infoPanel.setLocationRelativeTo(null);
		
		//Re-enable main frame on infoPanel close
		infoPanel.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				  frame.setEnabled(true);
			  }
		});
	}
	
	/** Setters and getters **/
	
	/**
	 * Returns the world Layer
	 * @return world Layer
	 */
	protected static Layer getWorld() {
		return world;
	}
	
	/**
	 * Returns the MapContent
	 * @return MapContent
	 */
	protected static MapContent getMap() {
		return map;
	}
	
	/** Internal classes for action listeners **/
	
	@SuppressWarnings("serial")
	/**
	 * An action listener for the Plate Carrée button
	 * <p>
	 * Transforms the map to the given projection and updates projectionInfo to show the right info.
	 * @author Joel Perren
	 */
	class TransformPlateCarree extends SafeAction {
		TransformPlateCarree() {
	        super("Plate Carrée");
	        putValue(Action.SHORT_DESCRIPTION, "Transform to Plate Carrée projection");
	    }
		
	    public void action(ActionEvent e) throws Throwable {
	    	projectionInfo = Projections.plateCarreeInfo;
	    	Utilities.transformWorld(Projections.plateCarree);
	    }
	}
	
	@SuppressWarnings("serial")
	/**
	 * An action listener for the Mercator button
	 * <p>
	 * Transforms the map to the given projection and updates projectionInfo to show the right info.
	 * @author Joel Perren
	 */
	class TransformMercator extends SafeAction {
		TransformMercator() {
	        super("Mercator");
	        putValue(Action.SHORT_DESCRIPTION, "Transform to mercator projection");
	    }
		
	    public void action(ActionEvent e) throws Throwable {
	    	projectionInfo = Projections.mercatorInfo;
	    	Utilities.transformWorld(Projections.mercator);
	    }
	}
	
	@SuppressWarnings("serial")
	/**
	 * An action listener for the Gall-Peters button
	 * <p>
	 * Transforms the map to the given projection and updates projectionInfo to show the right info.
	 * @author Joel Perren
	 */
	class TransformGallPeters extends SafeAction {
		TransformGallPeters() {
	        super("Gall-Peters");
	        putValue(Action.SHORT_DESCRIPTION, "Transform to gall-peters projection");
	    }
		
	    public void action(ActionEvent e) throws Throwable {
	    	projectionInfo = Projections.gallPetersInfo;
	    	Utilities.transformWorld(Projections.gallPeters);
	    }
	}
	
	@SuppressWarnings("serial")
	/**
	 * An action listener for the Eckert IV button
	 * <p>
	 * Transforms the map to the given projection and updates projectionInfo to show the right info.
	 * @author Joel Perren
	 */
	class TransformEckert_IV extends SafeAction {
		TransformEckert_IV() {
	        super("Eckert IV");
	        putValue(Action.SHORT_DESCRIPTION, "Transform to Eckert_IV projection");
	        
	    }
		
	    public void action(ActionEvent e) throws Throwable {
	    	projectionInfo = Projections.eckertInfo;
	    	Utilities.transformWorld(Projections.eckert_IV);
	    }
	}
	
	@SuppressWarnings("serial")
	/**
	 * An action listener for the Mollweide button
	 * <p>
	 * Transforms the map to the given projection and updates projectionInfo to show the right info.
	 * @author Joel Perren
	 */
	class TransformMollweide extends SafeAction {
		TransformMollweide() {
	        super("Mollweide");
	        putValue(Action.SHORT_DESCRIPTION, "Transform to Mollweide projection");
	        
	    }
		
	    public void action(ActionEvent e) throws Throwable {
	    	projectionInfo = Projections.mollweideInfo;
	    	Utilities.transformWorld(Projections.mollweide);
	    }
	}
	
	@SuppressWarnings("serial")
	/**
	 * An action listener for the Toggle grid button
	 * <p>
	 * Toggles the visibility of the grid layer on and off
	 * @author Joel Perren
	 */
	class ToggleGrid extends SafeAction {
		ToggleGrid() {
	        super("Toggle grid");
	        putValue(Action.SHORT_DESCRIPTION, "Toggle grid on and off");
	    }
		
	    public void action(ActionEvent e) throws Throwable {
	    	if (map.layers().get(1).isVisible()) {
	    		map.layers().get(1).setVisible(false);
	    	} else {
	    		map.layers().get(1).setVisible(true);
	    	}
	    }
	}
	
	@SuppressWarnings("serial")
	/**
	 * An action listener for the Toggle Tissots button
	 * <p>
	 * Toggles the visibility of the Tissots layer on and off
	 * @author Joel Perren
	 */
	class ToggleTissots extends SafeAction {
		ToggleTissots() {
	        super("Toggle Tissots");
	        putValue(Action.SHORT_DESCRIPTION, "Toggle Tissot's Indicatrix on and off");
	    }
		
	    public void action(ActionEvent e) throws Throwable {
	    	if (map.layers().get(2).isVisible()) {
	    		map.layers().get(2).setVisible(false);
	    	} else {
	    		map.layers().get(2).setVisible(true);
	    	}
	    }
	}
	
	@SuppressWarnings("serial")
	/**
	 * An action listener for the query button
	 * <p>
	 * Displays additional projection information to the user.
	 * @author Joel Perren
	 */
	class QueryProjection extends SafeAction {
		QueryProjection() {
	        super("?");
	        putValue(Action.SHORT_DESCRIPTION, "Display information on current projection");
	    }
		
	    public void action(ActionEvent e) throws Throwable {
	    	infoPanel();
	    }
	}
	
	/**
	 * An action listener for the MenuItem button
	 * <p>
	 * Allows the user to save the re-projected shapefile and exit the application
	 * @author Joel Perren
	 */
	class ListenForMenu implements ActionListener{

		public void actionPerformed(ActionEvent e){
			JMenuItem clickedMenuItem = (JMenuItem)e.getSource();
			
			if (clickedMenuItem.getText().equals("Save current world")){
				try {
					IO.saveToShapefile();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (FactoryException e1) {
					e1.printStackTrace();
				}
			}
			
			else if (clickedMenuItem.getText().equals("Exit")) {
				System.exit(0);
			}
		}
	}

} //END OF CLASS
