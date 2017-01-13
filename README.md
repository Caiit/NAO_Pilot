# NAO Pilot
Caitlin Lagrand </br>
UvA Programmeerproject application January 2017. 


The NAO pilot application controls a NAO robot (https://www.ald.softbankrobotics.com/en/cool-robots/nao). This application can for example be used for easily creating a dataset or giving demonstrations. The application connects with a NAO via WiFi,  lets the NAO speak, walk, perform and create specific moves (dances, kicks etc.) and gets the camera output. External specific moves files (probably keyframes) can be added and created to personalise it. ~~The NaoQi Java SDK (http://doc.aldebaran.com/2-1/dev/java/index_java.html) will be used to connect and control the NAO.~~ Sockets are used to connect and control the NAO via the app. The NAO will act as a server with a Python script that needs to be run on the robot. The app will act as the client and send messages to the robot. The biggest problem is connecting to the robot and stay connected. With the NaoQi SDK this should be done easily, but you never know with robots. Also, the application can become slow, mostly when showing camera output, due to the wireless connection with the robot.


## Features

<img src="/doc/appSketch.jpg">

Connect screen: ask for IP and connect button, maybe home screen, when connected show robot information like name and battery status and volume. </br>
Speak screen: input what to say and speak settings like voice and speed. </br>
Walk screen: arrows in which way to move or turn. Maybe joystick. Slider for speed. </br>
Camera screen: camera image from the selected camera and save button to save either on the robot or on the phone. </br>
Specific moves screen: buttons or images with the specific move. Add new specific moves. </br>
Move joints screen: sliders to move specific joints and save button. </br>

# Datasources and external components

External specific moves files (probably keyframes) will be used for the specific moves. These files can be created using choregraphe (http://doc.aldebaran.com/1-14/software/choregraphe/) or with the app. 

~~The NaoQi Java SDK (http://doc.aldebaran.com/2-1/dev/java/index_java.html) will be used.~~ </br>
Socket (https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html & https://docs.python.org/2/library/socket.html) </br>
NaoQi Python SDK (http://doc.aldebaran.com/2-1/dev/python/install_guide.html) </br>

## Possible problems that could arise 

The biggest problem is connecting to the robot and stay connected. ~~With the NaoQi SDK this should be done easily, but you never know with robots.~~ The NaoQi Java SDK can not be imported correctly within Android Studio, so sockets will be used to create a server on the robot and send messages from the app to the robot to control it.
Also, the app can become slow, mostly when showing camera images, due to the wireless connection with the robot.

## Other similar applications
A few similar applications exists: NAO control (https://play.google.com/store/apps/details?id=de.daboapps.androidnao), NAO Robot Controller (https://play.google.com/store/apps/details?id=com.robinbonnes.naorobotcontroller&hl=nl) and NAO Communicator (https://play.google.com/store/apps/details?id=de.robotik.nao.communicator&hl=nl).
These apps have similar functionalities as the application I develop. However, they only use basic control of the robot. The NAO pilot application will be able to allow creating and adding specific moves.
