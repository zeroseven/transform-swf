Transform SWF
=============

Transform SWF is a collection of classes for each of the data structures and tags that make up the Flash File Format Specification from Adobe. The classes provide a completely object-oriented API to encode and decode Flash (.swf) files. Transform SWF supports the latest published version - Flash 10.

The library also contains classes that provide a higher level interface supporting the addition of shapes, images, sounds, fonts and text from external file formats.

The documentation that accompanied this release contains a description of the each of the classes along with examples on how they may be used to decode, process and encode Flash (.swf) files. Flagstone Software's web site, http://www.flagstonesoftware.com also contains more detailed information and advanced examples.

Key Features
------------

* Full support for Flash 10.
* Access to all of Flash giving full control of the Player.
* Generate Flash files for any version of the desktop Flash Player.
* Generate Flash Lite files for mobile phones and devices.
* Easy to use API allows you to edit any flash file.
* Comprehensive 2-D API to draw shapes from arbitrary complex paths.
* Use OpenType or TrueType fonts to display text.
* Direct support for generating images using JPEG, PNG and BMP files.
* Add Event and streaming sounds from WAV and MP3 files.
* Plugin architecture for adding decoders for new image and sound formats.
* Java JDK integration adds support for BufferedImage, AWT Fonts and ImageIO readers.
* Open Source, BSD licence is free for commercial use.

An Example
----------

Here is an example, in Java, showing how to use Transform to generate a flash file that displays a text field. For more examples, detailed information on Flash concepts and How-Tos take a look at the [Cookbook](http://www.flagstonesoftware.com/cookbook/ "Cookbook").

	int uid = 1;
	int layer = 1;
	
	final String str = "The quick, brown fox jumped over the lazy dog."
	final Color color = WebPalette.BLACK.color();
	
	final String fontName = "Arial";
	final int fontSize = 24;
	final int fontStyle = java.awt.Font.PLAIN;
	
	// Load the AWT font.
	final AWTDecoder fontDecoder = new AWTDecoder();
	fontDecoder.read(new java.awt.Font(fontName, fontStyle, fontSize));
	final Font font = fontDecoder.getFonts().get(0);
	
	// Create a table of the characters displayed.
	final CharacterSet set = new CharacterSet();
	set.add(str);
	
	// Define the font containing only the characters displayed.
	DefineFont2 fontDef = font.defineFont(uid++, set.getCharacters());
	
	// Generate the text field used for the button text.
	final TextTable textGenerator = new TextTable(fontDef, fontSize * 20);
	DefineText2 text = textGenerator.defineText(uid++, str, color);
	
	// Set the screen size to match the text with padding so the
	// text does not touch the edge of the screen.
	int padding = 1000;
	int screenWidth = text.getBounds().getWidth() + padding;
	int screenHeight = text.getBounds().getHeight() + padding;
	
	// Position the text in the center of the screen.
	final int xpos = padding / 2;
	final int ypos = screenHeight / 2;
	
	MovieHeader header = new MovieHeader();
	header.setFrameRate(1.0f);
	header.setFrameSize(new Bounds(0, 0, screenWidth, screenHeight));
	
	// Add all the objects together to create the movie.
	Movie movie = new Movie();
	movie.add(header);
	movie.add(new Background(WebPalette.LIGHT_BLUE.color()));
	movie.add(fontDef);
	movie.add(text);
	movie.add(Place2.show(text.getIdentifier(), layer++, xpos, ypos));
	movie.add(ShowFrame.getInstance());
	
	movie.encodeToFile("example.swf");

Licensing
---------

Transform SWF is made available under the terms of the Berkeley Software Distribution (BSD) license. This allow you complete freedom to use and distribute the code in source and/or binary form as long as you respect the original copyright. Please see the LICENCE.txt file for exact terms.

Contributions
-------------

The library includes the excellent ImageInfo class, developed by Marco Schmidt and contributed to the Public Domain. For more information please see, http://schmidt.devlib.org/image-info/

Contributions by other authors are also available from the Flagstone web site. Unless otherwise stated in the contribution's documentation, each is licensed for use and redistribution under the terms of the Berkeley Software Distribution (BSD) license.

Requirements
------------

To use Transform SWF you must have at least:

* Java 6 Standard Edition

Building the library
--------------------

The ANT build file, build.xml contains the following major targets:

    build   compile with optimisation all the classes and package them in transform.jar.

Minor targets provide more control over the compiling and packaging of classes, allowing for example a JAR file to be created. Targets include:

    clean    remove all the compiled classes and jars.
    classes  compile the classes for production.
    debug    compile the classes for development.
    jar      package all the compiled classes in a JAR.

Using the library
-----------------

Add the JAR file to your classpath, e.g.:

Windows:  
    CLASSPATH=...;C:\Program Files\Flagstone\java\transform.jar
Unix:     
    CLASSPATH=...:/usr/local/java/transform.jar

To use the classes, place the following import statement in your code:

    com.flagstone.transform.*;

Additional Information
----------------------

For Further Information please contact:

Stuart MacKay

Flagstone Software Ltd.
92 High Street
Wick, Caithness KW1 4LY
Scotland

http://www.flagstonesoftware.com
