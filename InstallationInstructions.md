# Installation Instructions #

## Requirements ##
MouseClient requires a Java 6 runtime environment to be able run.  I develop and test MouseClient using the Oracle Java runtime, though others may also work.

Windows, Solaris and Linux users can get the latest Oracle JRE here:
http://java.com/en/download/index.jsp

Currently MacOSX ships with its own Java runtime, though in the future this is going to change.

Ubuntu instructions can be found here:
https://help.ubuntu.com/community/Java

## Instructions ##
Unpack the MouseClient.jar file from the zip file.
Launch the application by running:
```
java -jar MouseClient.jar
```

## Usage ##
When MouseClient launches a dialog will be displayed to select the Mobile Mouse Server that you would like to connect to.  Either select from the list or enter IP address and port number manually.

Once logged in the MouseClient window displays no further controls.  Clicking on the window will cause further mouse and keyboard events to be sent to the server machine.  Pressing "Ctrl + ~", will return control to your local machine.