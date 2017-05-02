# Projection Peruser
Projection Peruser is a Java app which allow the viewing of a selection of map projections in order to show how they each distort the Earth in different ways. Built using GeoTools for GEOG5790: Programming for Geographical Information Analysis: Advanced Skills.

## Installation
The app can be run by executing the Projection_Peruser.jar. Running it within Ecplise requires the Eclipse Maven plugin. More information can be found at http://docs.geotools.org/stable/userguide.old/tutorial/quickstart/eclipse.html.

## Use
The app loads two shapefiles in order to run - world.shp and tissots.shp. These are included in this directory. These should be automatically loaded by the app on startup, but if they are not then a file dialogue will appear and ask you to navigate to the location of the two shapefiles.

Once the shapefiles are loaded a GUI will appear. On the GUI the user is able to convert the projection to one of 5 map projections: Plate Carree, Mercator, Gall-Peters, Eckert IV, and Mollwide. The world shapefile is accomanied by a grid and a set of Tissot's Intricacies which help to demonstrate the distortion present in each projection. The grid and Tissot's Intricacies can be toggled on and off with their respective buttons. 

At any time the user can export a new shapefile in the projection which they are currently viewing through the File menu. Finally, additional information on each projection can be viewed by pressing the query (?) button in the toolbar.
