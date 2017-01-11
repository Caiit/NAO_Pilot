# Design Document NAO Pilot
Caitlin Lagrand </br>
UvA Programmeerproject application January 2017.

##a diagram of modules or classes that you’ve decided to implement, in appropriate detail and advanced sketches of your UI that clearly explain which features are connected to which underlying part of the code
Connect Activity: ask for IP and connect button, when connected show robot information like name and battery status and volume.</br>
Speak Activity: input what to say and speak settings like voice and speed. </br>
Walk Activity: arrows in which way to move or turn. Maybe joystick. Slider for speed. </br>
Camera Activity: camera image from the selected camera and save button to save either on the robot or on the phone. </br>
Specific moves Activity: buttons or images with the specific move. Add new specific moves (go to move joints activity). </br>
Move joints Activity: sliders to move specific joints and save button. </br>
Robot Class: Connect, Speak, Walk, Camera, Move, Joints </br>
FileManager Class: Read and write files </br>
Server on robot script: create a server (socket) and react to messages from the app. </br>

<img src="doc/appSketch.jpg">
<img src="doc/modulesDiagram.jpg">

##a list of APIs and frameworks or plugins that you will be using to provide functionality in your app
~~NaoQi Java SDK (http://doc.aldebaran.com/2-1/dev/java/index_java.html)~~</br>
Socket (https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html & https://docs.python.org/2/library/socket.html) </br>
NaoQi Python SDK (http://doc.aldebaran.com/2-1/dev/python/install_guide.html) </br>

##a list of data sources if you will get data from an external source
External specific moves files (probably keyframes).

##a list of database tables and fields (and their types) if you will use a database
Not applicable
