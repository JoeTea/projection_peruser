/*
 * IO.java
 * v1.0
 * 02 May 2017
 * Joel Perren
 */

package org.geotools.projection_peruser_FINAL;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Contains methods relating to reading and writing files.
 * @author Joel Perren
 */
public class IO {
	
	/** Shapefile paths in project directory **/
	static File worldFile = new File("./shapefile/world.shp");
	static File tissotsFile = new File("./shapefile/tissots.shp");
	static SimpleFeatureSource worldFeatureSource; // For saveToShapefile()
	static SimpleFeatureSource tissotsFeatureSource;
	
	/**
	 * Sets up the global variables for this class.
	 * <p>
	 * If the relevant shapefiles cannot be found from their path, a file dialogue is used to locate them.
	 */
	protected static void setUpFiles() {
		if (!worldFile.exists()) {
			int dialogResult = JOptionPane.showConfirmDialog(null, 
					"Could not find " + worldFile.getName() + ". Would you like to manually locate it?", 
					"Load shapefile error", JOptionPane.YES_NO_OPTION);
			if (dialogResult == JOptionPane.YES_OPTION) {
				worldFile = findFile();
			} else {
				System.exit(0);
			}
		}
		
		if (!tissotsFile.exists()) {
			int dialogResult = JOptionPane.showConfirmDialog(null, 
					"Could not find " + tissotsFile.getName() + ". Would you like to manually locate it?", 
					"Load shapefile error", JOptionPane.YES_NO_OPTION);
			if (dialogResult == JOptionPane.YES_OPTION) {
				tissotsFile = findFile();
			} else {
				System.exit(0);
			}
		}
		
		try {
			worldFeatureSource = FileDataStoreFinder.getDataStore(worldFile).getFeatureSource();
			tissotsFeatureSource = FileDataStoreFinder.getDataStore(tissotsFile).getFeatureSource();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens a file dialogue to locate shapefiles.
	 * @return A chosen File.
	 */
	private static File findFile() {
		File file = JFileDataStoreChooser.showOpenFile(".shp", null);
		if (file == null) {
			System.exit(0);
			return null;
		} else {
			return file;
		}
	}

	/**
	 * Saves a shapefile based on the current CRS of the MapContent
	 * @throws IOException
	 * @throws FactoryException Thrown when a factory can't create an instance of the requested object.
	 */
	protected static void saveToShapefile() throws IOException, FactoryException {
		
		SimpleFeatureType schema = worldFeatureSource.getSchema();
		
		JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
		chooser.setDialogTitle("Save reprojected shapefile");
		chooser.setSaveFile(worldFile);
		
		// Close window if save is not approved
		int returnVal = chooser.showSaveDialog(null);
		if (returnVal != JFileDataStoreChooser.APPROVE_OPTION) {
			return;
		}
		
		File file = chooser.getSelectedFile();
		
		// Ensure original world.shp cannot be overwritten
		if (file.equals(worldFile)) {
			JOptionPane.showMessageDialog(null, "Cannot replace " + file);
			return;
		}
		
		// Get the CRS for worldFeatureSource and the current map and set up the MathTransform
		CoordinateReferenceSystem dataCRS = schema.getCoordinateReferenceSystem();
		CoordinateReferenceSystem worldCRS = GUI.map.getCoordinateReferenceSystem();
		boolean lenient = true; // Allow for some errors due to different datums
		MathTransform transform = CRS.findMathTransform(dataCRS, worldCRS, lenient);

		// Get all the features from world.shp
		SimpleFeatureCollection featureCollection = worldFeatureSource.getFeatures();

		// Finds geometry type and sets up feature store
		DataStoreFactorySpi factory = new ShapefileDataStoreFactory();
		Map<String, Serializable> create = new HashMap<String, Serializable>();
		create.put("url", file.toURI().toURL());
		create.put("create spatial index", Boolean.TRUE);
		DataStore dataStore = factory.createNewDataStore(create);
		SimpleFeatureType featureType = SimpleFeatureTypeBuilder.retype(schema, worldCRS);
		dataStore.createSchema(featureType);

		// Get the name of the new Shapefile, which will be used to open the FeatureWriter
		String createdName = dataStore.getTypeNames()[0];

		Transaction transaction = new DefaultTransaction("Reproject");
		try {
			FeatureWriter<SimpleFeatureType, SimpleFeature> writer = dataStore.getFeatureWriterAppend(createdName, transaction); 
					
			// Set up iterator to loop through features
			SimpleFeatureIterator iterator = featureCollection.features();
			
			// Copy the contents of each feature and transform the geometry
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				SimpleFeature copy = writer.next();
				copy.setAttributes(feature.getAttributes());

				Geometry geometry = (Geometry) feature.getDefaultGeometry();
				Geometry geometry2 = JTS.transform(geometry, transform);

				copy.setDefaultGeometry(geometry2);
				writer.write();
			}
			
			// Close feature writer iterator and commit transformation
			writer.close();
			iterator.close();
			transaction.commit();
			JOptionPane.showMessageDialog(null, "Export to shapefile complete");
		} catch (Exception problem) {
			problem.printStackTrace();
			transaction.rollback();
			JOptionPane.showMessageDialog(null, "Export to shapefile failed");
		} finally {
			transaction.close();
		}
	}

} // END OF CLASS
