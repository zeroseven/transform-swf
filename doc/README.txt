------------------
  Transform SWF
------------------
The Transform SWF framework is a collection of classes for each of the data
structures and tags that make up the Flash File Format Specification from
Macromedia. The classes provide a completely object-oriented API to encode
and decode Flash (.swf) files. Transform SWF supports the latest published 
version from Adobe - Flash 9.

The framework also contains classes that provide a higher level interface
supporting the addition of shapes, images, sounds, fonts and text from
external file formats.

The documentation that accompanied this release contains a description of the
each of the classes along with examples on how they may be used to decode,
process and encode Flash (.swf) files. Flagstone Software's web site,
www.flagstonesoftware.com also contains more detailed information and advanced
examples.

-------------
  Licensing
-------------
Transform SWF is made available under the terms of the Berkeley Software
Distribution (BSD) license. This allow you complete freedom to use and
distribute the code in source and/or binary form as long as you respect
the original copyright. Please see the LICENCE.txt file for exact terms.

-----------------
  Contributions
-----------------
The framework includes the excellent ImageInfo class, developed by Marco Schmidt
and contributed to the Public Domain. For more information please see,
http://schmidt.devlib.org/image-info/

Contributions by other authors are also available from the Flagstone web site.
Unless otherwise stated in the contribution's documentation, each is licensed
for use and redistribution under the terms of the Berkeley Software Distribution
(BSD) license.

----------------
  Requirements
----------------
To use Transform SWF you must have at least:

    Java 6 Standard Edition

--------------------------
  Building the Framework
--------------------------
The ANT build file, build.xml contains the following major targets:

    build   compile with optimisation all the classes in the
            transform and transform utilities and package them
            in transform.jar (examples and test classes are not
            included).

Minor targets provide more control over the compiling and packaging
of classes, allowing for example a JAR file to be created containing
the framework classes and the examples. Targets include:

    clean    remove all the compiled classes and jars.
    classes  compile the framework classes for production.
    debug    compile the framework classes for development.
    jar      package all the compiled classes in a JAR.

-----------------------
  Using the Framework
-----------------------
Add the JAR file to your classpath, e.g.:

Windows:  CLASSPATH=...;C:\Program Files\Flagstone\java\transform.jar
Unix:     CLASSPATH=...:/usr/local/java/transform.jar

To use the classes, place the following import statement in your code:

    com.flagstone.transform.*;

--------------------------
  Additional Information
--------------------------
For Further Information please contact:

Stuart MacKay

Flagstone Software Ltd.
92 High Street
Wick, Caithness KW1 4LY
Scotland

www.flagstonesoftware.com
