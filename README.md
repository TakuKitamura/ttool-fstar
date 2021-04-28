For users of TTool: (e.g., for users having a read-only access to the git)
------------------- 

You can clone the repository, and compile TTool as explained in the developer section below.
For more information: http://ttool.telecom-paristech.fr

Beware, if you want to use other options of TTool, please, start it with the needed command line, 
or make your own ttool.exe. See the online documentation for more information


For developers: (rw access to the git)
---------------

* Compiling the latest version of TTool:
make ttool

* Compiling the latest version of TTool + companions software:
make all

If the compilation fails with the following error: 'unmappable character for encoding ASCII', you need to do, before the compilation process:
export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8

Alternatively, if you wish to compile TTool without running tests (tests can be long to run), do:
make ttoolnotest
or
gradle build -x test

* Installing TTool once compiled
make install

This installs all necessary files in bin/


* Generating a release:
make release

* Starting TTool from the git clone
You should not modify bin/config.xml. Create your own config.xml (e.g. myconfig.xml), and create your own "ttool.exe" file (e.g., myttool.exe) 
if you need to modify the config.xml file
-> proceed as follows:
$ cp ttool.exe myttool.exe
$ cp bin/config.xml bin/myconfig.xml

Then edit myttool.exe and replace config.xml with myconfig.xml

You can now start TTool:
$ ./myttool.exe


Enjoy!!




