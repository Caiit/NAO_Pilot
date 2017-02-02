# Design Document NAO Pilot
Caitlin Lagrand </br>
UvA Programmeerproject application January 2017.

## Diagram of classes and features
Main Activity: handles the network connection with the robot: connect/disconnect the robot and handle messages from the robot

Connect fragment: select your robot from the drop-down list and connect by clicking on the connect button. When connected show robot information like name, battery and stiffness status.

Speak fragment: enter what to say and set the speak settings like volume, speed and pitch.

Walk fragment: press an arrow to move in that direction and release to stop moving. Set the speed using the slider.

Camera fragment: take a picture with the robot and show it. Save the image on the phone.

Moves fragment: click on a move button to perform that move.

NetworkThread Class: create a new thread that handles the network connection between the application and the robot. This thread connects to the robot as client and repeatedly receives and sends messages when available.

RobotDiscoveryListener: discovers robots currently available on the network.

RobotResolveListener: finds the robots IP given the discovered service.

robotServer: python script that runs on the robot.
- NetworkThread Class: create a new thread that handles the network connection between the robot and the application. This thread creates a server and receives and sends messages when an client (the app) is connected.
- Robot Class: handles the messages from the application by letting the robot do what asked.

localServer: python script that runs on a laptop to test when no robot is available. Same as robotServer, but with print statements instead of robot actions.
- NetworkThread Class: create a new thread that handles the network connection between the robot and the application. This thread creates a server and receives and sends messages when an client (the app) is connected.
- FakeRobot Class: handles the messages from the application by letting the fake robot say what asked.

<!-- TODO: add new sketches -->
<img src="doc/modulesDiagram.jpg">

## Frameworks and datasources
Socket (https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html & https://docs.python.org/2/library/socket.html) </br>
NaoQi Python SDK (http://doc.aldebaran.com/2-1/dev/python/install_guide.html) </br>

Python keyframes are used for the specific moves. These files can be created using choregraphe (http://doc.aldebaran.com/1-14/software/choregraphe/). Create a timeline box and save the desired keyframes on that timeline. Export the timeline to python and delete the imports and perform parts (begin and end of the file). The files are saved as .txt files and send to the robot to perform that specific move.
