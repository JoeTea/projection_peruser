/*
 * Projections.java
 * v1.0
 * 02 May 2017
 * Joel Perren
 */

package org.geotools.projection_peruser_FINAL;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Storage class for Coordinate Reference Systems defined in WKT.
 * <p>
 * Also contains additional information on each projection.
 * @author Joel Perren
 */
public class Projections {
	static String mercator = 
			"PROJCS[\"World Mercator\"," +
			"GEOGCS[\"WGS 84\"," +
			"DATUM[\"WGS_1984\"," +
			"SPHEROID[\"WGS 84\",6378137,298.257223563," +
			"AUTHORITY[\"EPSG\",\"7030\"]]," +
			"AUTHORITY[\"EPSG\",\"6326\"]]," +
			"PRIMEM[\"Greenwich\",0," +
			"AUTHORITY[\"EPSG\",\"8901\"]]," +
			"UNIT[\"degree\",0.01745329251994328," +
			"AUTHORITY[\"EPSG\",\"9122\"]]," +
			"AUTHORITY[\"EPSG\",\"4326\"]]," +
			"UNIT[\"metre\",1," +
			"AUTHORITY[\"EPSG\",\"9001\"]]," +
			"PROJECTION[\"Mercator_1SP\"]," +
			"PARAMETER[\"central_meridian\",0]," +
			"PARAMETER[\"scale_factor\",1]," +
			"PARAMETER[\"false_easting\",0]," +
			"PARAMETER[\"false_northing\",0]," +
			"AUTHORITY[\"EPSG\",\"3395\"]," +
			"AXIS[\"Easting\",EAST]," +
			"AXIS[\"Northing\",NORTH]]";
	
	static String[][] mercatorInfo = new String[][]{
		{"Projection name", "Mercator"},
		{"Projection type", "Cylindrical Conformal"},
		{"Description", "Historically the Mercator projection was the standard map projection"
				+ " for nautical purposes as it preserves the 90° angles between lines of latitude "
				+ "and longitude. <br><br> In order to achieve this, distances between the latitude "
				+ "lines vary away from the equator. As a result, distortion of the Earth becomes more "
				+ "severe as latitude increases."},
		{"Read more", "https://en.wikipedia.org/wiki/Mercator_projection"},
	};
	
	static String plateCarree = 
			"GEOGCS[\"WGS 84\"," +
			"DATUM[\"WGS_1984\"," +
			"SPHEROID[\"WGS 84\",6378137,298.257223563," +
			"AUTHORITY[\"EPSG\",\"7030\"]]," +
			"AUTHORITY[\"EPSG\",\"6326\"]]," +
			"PRIMEM[\"Greenwich\",0," +
			"AUTHORITY[\"EPSG\",\"8901\"]]," +
			"UNIT[\"degree\",0.01745329251994328," +
			"AUTHORITY[\"EPSG\",\"9122\"]]," +
			"AUTHORITY[\"EPSG\",\"4326\"]]";
	
	static String[][] plateCarreeInfo = new String[][]{
		{"Projection name", "Plate Carrée"},
		{"Projection type", "Cylindrical Equidistant"},
		{"Description", "This projection is claimed to have been invented in AD 100. Since then it has "
				+ "become a standard for global raster datasets. <br> <br> It maps meridians to vertical "
				+ "straight lines of constant spacing and circles of latitude to horizontal straight lines "
				+ "of constant spacing. The projection is neither equal area nor conformal. As the "
				+ "standard parallel is zero, distortion increases away from the equator. At high "
				+ "latitudes areas are squashed vertically and stretched horizontally."},
		{"Read more", "https://en.wikipedia.org/wiki/Equirectangular_projection"},
	};
	
	static String gallPeters = 
			"PROJCS[\"World_Cylindrical_Equal_Area\"," +
			"GEOGCS[\"GCS_WGS_1984\"," +
			"DATUM[\"D_WGS_1984\"," +
			"SPHEROID[\"WGS_1984\",6378137.0,298.257223563]]," +
			"PRIMEM[\"Greenwich\",0.0]," +
			"UNIT[\"Degree\",0.0174532925199433]]," +
			"PROJECTION[\"Cylindrical_Equal_Area\"]," +
			"PARAMETER[\"false_easting\",0.0]," +
			"PARAMETER[\"false_northing\",0.0]," +
			"PARAMETER[\"central_meridian\",0.0]," +
			"PARAMETER[\"standard_parallel_1\",45]," + //Edited to 45 as per Gall-Peters
			"UNIT[\"Meter\",1.0]]";
	
	static String[][] gallPetersInfo = new String[][]{
		{"Projection name", "Gall-Peters"},
		{"Projection type", "Cylindrical Equal-area"},
		{"Description", "The Gall–Peters projection maps all areas such that they have the correct sizes "
				+ "relative to each other. It was the focus of political debate as Arno Peters, who "
				+ "promoted the projection in the 1970s, argued that the commonly used Mercator "
				+ "projection shrank the size of countries around the equator so that they seemed "
				+ "less significant on the map. <br><br> Gall-Peters can maintain relative area, but as "
				+ "a trade-off shape and angles are heavily distorted. The projection sets its standard "
				+ "parallels to 45° so these areas experience no distortion. Areas by the equator "
				+ "however are stretched vertically and areas by the poles are stretched horizontally."},
		{"Read more", "https://en.wikipedia.org/wiki/Gall%E2%80%93Peters_projection"},
	};
	
	static String eckert_IV = CRStoWKT("EPSG:54012");
	
	static String[][] eckertInfo = new String[][]{
		{"Projection name", "Eckert IV"},
		{"Projection type", "Pseudocylindrical Equal-area"},
		{"Description", "Pseudocylindrical projections represent the central meridian as a straight "
				+ "line segment. Other meridians are longer than the central meridian and bow outward "
				+ "away from the central meridian.  Furthermore, any point further from the equator "
				+ "than some other point has a higher latitude than the other point, preserving "
				+ "north-south relationships. This trait is useful when illustrating phenomena that "
				+ "depend on latitude, such as climate. "},
		{"Read more", "https://en.wikipedia.org/wiki/Eckert_IV_projection"},
	};
	
	static String mollweide = CRStoWKT("EPSG:54009");
	
	static String[][] mollweideInfo = new String[][]{
		{"Projection name", "Mollweide"},
		{"Projection type", "Pseudocylindrical Equal-area"},
		{"Description", "This projection trades accuracy of angle and shape for accuracy of proportions "
				+ "in area, and as such is used where that property is needed, such as maps depicting "
				+ "global distributions. The whole Earth is depicted in a proportional 2:1 ellipse and "
				+ "the proportion of the area of the ellipse between any given parallel and the equator "
				+ "is the same as the proportion of the area on the globe between that parallel and the "
				+ "equator. This comes at the expense of shape distortion, which is significant at the "
				+ "perimeter of the ellipse."},
		{"Read more", "https://en.wikipedia.org/wiki/Mollweide_projection"},
	};
	
	/**
	 * Converts EPSG_CODE formats to a WKT String.
	 * @param EPSG_CODE The EPSG_CODE which refers to a CRS.
	 * @return A WKT String which refers to a CRS.
	 */
	static public String CRStoWKT(String EPSG_CODE) {
		
		CoordinateReferenceSystem crs;
		String wkt = "";
		
		try {
			crs = CRS.decode(EPSG_CODE);
			wkt = crs.toWKT();
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		
		return wkt;
	}
}