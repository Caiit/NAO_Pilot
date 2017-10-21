# NAO Pilot
UvA Programmeerproject, January 2017.


The NAO pilot application controls a Nao robot (https://www.ald.softbankrobotics.com/en/cool-robots/nao). This application can be used for easily giving demonstrations with the Nao robot. The application connects with a Nao via WiFi, lets the Nao speak, walk, take a picture and perform specific moves like dances, kicks etc. Sockets are used to connect and control the Nao via the application. The Nao acts as a server with a Python script that needs to be run on the robot. The application acts as the client and send messages to the robot.


## Features

Connect screen: select your robot from the drop-down list and connect by clicking on the connect button. When connected show robot information like name, battery and stiffness status.

<img src="/doc/Connect.png" style="width: 50px;">

Speak screen: enter what to say and set the speak settings like volume, speed and pitch.

<img src="/doc/Speak.png" style="width: 50px;">

Walk screen: press an arrow to move in that direction and release to stop moving. Set the speed using the slider.

<img src="/doc/Walk.png" style="width: 50px;">

Camera screen: take a picture with the robot and show it. Save the image on the phone.

<img src="/doc/Camera.png" style="width: 50px;">

Moves screen: click on a move button to perform that move.

<img src="/doc/Moves.png" style="width: 50px;">

## Datasources and external components

Python keyframes are used for the specific moves. These files can be created using choregraphe (http://doc.aldebaran.com/1-14/software/choregraphe/). Create a timeline box and save the desired keyframes on that timeline. Export the timeline to python and delete the imports and perform parts (begin and end of the file). The files are saved as .txt files and send to the robot to perform that specific move.

Socket (https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html & https://docs.python.org/2/library/socket.html) </br>
NaoQi Python SDK (http://doc.aldebaran.com/2-1/dev/python/install_guide.html) </br>

## Possible problems that could arise

The biggest problem is connecting to the robot and stay connected. The NaoQi Java SDK cannot be imported correctly into Android Studio, so sockets are used to create a server on the robot and send messages from the application to the robot to control it.
Also, the application can become slow, mostly when showing camera images, due to the wireless connection with the robot. To avoid this, no live stream images are shown, but the user can take a picture instead.

## Other similar applications
A few similar applications exists: NAO control (https://play.google.com/store/apps/details?id=de.daboapps.androidnao), NAO Robot Controller (https://play.google.com/store/apps/details?id=com.robinbonnes.naorobotcontroller&hl=nl) and NAO Communicator (https://play.google.com/store/apps/details?id=de.robotik.nao.communicator&hl=nl).
These applications have similar functionalities as the application I develop. However, they only use basic control of the robot. The NAO Pilot application allows performing specific moves created with Choregraphe.
