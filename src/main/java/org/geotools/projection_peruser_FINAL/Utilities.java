/*
 * Utilities.java
 * v1.0
 * 02 May 2017
 * Joel Perren
 */

package org.geotools.projection_peruser_FINAL;

import java.awt.Color;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.grid.Grids;
import org.geotools.referencing.CRS;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Contains a number of utility methods used within the application.
 * @author Joel Perren
 * @version 1.0
 */
public class Utilities {

	static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
	static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);
	
	/**
	 * Creates a polygon style based on input parameters
	 * 
	 * @param fillColour The colour of the fill
	 * @param fillOpacity The opacity of the fill
	 * @param strokeColour The colour of the stroke
	 * @param strokeOpacity The opacity of the stroke
	 * @return A polygon style object
	 */
	static protected Style createStyle(Color fillColour, double fillOpacity, Color strokeColour, double strokeOpacity) {

		// Create the fill
		Fill fill = styleFactory.createFill(filterFactory.literal(fillColour), filterFactory.literal(fillOpacity));

		// Create the stroke
		Stroke stroke = styleFactory.createStroke(filterFactory.literal(strokeColour),
				filterFactory.literal(strokeOpacity), filterFactory.literal(strokeOpacity));

		// Create polygon style based on fill and stroke
		PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);
		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[] { rule });
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}

	/**
	 * Creates a grid based on the bounds of the world Layer from GUI.java
	 * @return A vector grid
	 */
	static protected SimpleFeatureSource createGrid() {

		ReferencedEnvelope gridBounds = GUI.getWorld().getBounds();
		
		// Define size of grid
		double squareWidth = 20;
		double vertexSpacing = squareWidth / 20;

		SimpleFeatureSource grid = Grids.createSquareGrid(gridBounds, squareWidth, vertexSpacing);

		return grid;
	}
	
	/**
	 * Transforms the map from the source Coordiante Reference System to a new one.
	 * @param crs The relevant Coordinate Reference System as defined in Projections.java
	 */
	static protected void transformWorld(String crs) {
		CoordinateReferenceSystem sourceCRS;
		CoordinateReferenceSystem targetCRS;
		
		sourceCRS = GUI.getMap().getViewport().getCoordinateReferenceSystem();
		targetCRS = null;
		
		try {
			targetCRS = CRS.parseWKT(crs);
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		
		GUI.getMap().getViewport().setBounds(new ReferencedEnvelope(
				-31, 75, -24, 44, sourceCRS)); // Fix for bug with Mercator projection
		GUI.getMap().getViewport().setCoordinateReferenceSystem(targetCRS);
	}

}
