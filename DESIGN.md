# Design Document NAO Pilot
Caitlin Lagrand </br>
UvA Programmeerproject, January 2017.

## Diagram of classes and features
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

## Frameworks and datasources
~~NaoQi Java SDK (http://doc.aldebaran.com/2-1/dev/java/index_java.html)~~</br>
Socket (https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html & https://docs.python.org/2/library/socket.html) </br>
NaoQi Python SDK (http://doc.aldebaran.com/2-1/dev/python/install_guide.html) </br>

External specific moves files (probably keyframes). </br>
