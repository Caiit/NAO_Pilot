# Report NAO Pilot
Caitlin Lagrand </br>
UvA Programmeerproject, January 2017.

The NAO pilot application controls a Nao robot (https://www.ald.softbankrobotics.com/en/cool-robots/nao). This application can be used for easily giving demonstrations with the Nao robot. The application connects with a Nao via WiFi, lets the Nao speak, walk, take a picture and perform specific moves like dances, kicks etc.

<img src="/doc/Connect.png" style="width: 50px;">

## Technical desgin
Two parts are important to run this application. First, the application itself, an Android application that controls the robot. Furthermore, a python script needs to run on the robot that executes the robot actions. To test the code without a robot, a script that creates a fake robot is developed.

<img src="/doc/fileStructure.jpg" style="width: 100px;">

### Application
The application consists of two main parts: the UI-thread that handles the users input and shows the results in the app, and the NetworkThread that handles the connection with the robot. Furthermore, a RobotDiscoveryListener and RobotResolveListener are created to discover robots on the network and resolve their IP-addresses.

#### UI-Thread
The UI-thread consists of one activity, the MainActivity, and several fragments, each for a specific action (connect, speak, walk etc.). The MainActivity is the most important part of the UI-thread. It handles the network connection with the robot by starting a NetworkThread when connecting to the robot and closing it when disconnected. Furthermore, it handles the messages from the robot which are added to the handler of the MainActivity by the NetworkThread (see NetworkThread). Four kind of messages exists: picture, info, disconnect and error. A picture message results in showing the picture received from the robot in the camera fragment. An info message sets the robot info in the connect fragment. A disconnect message tells the MainActivity that the connection with the robot is closed. An error message shows an error from the robot, for example that it was not able to read the message.

##### Connect fragment
*Select your robot from the drop-down list and connect by clicking on the connect button. When connected show robot information like name, battery and stiffness status.*

This fragment shows the correct robot information and connect  button. When the application is connected to a robot and the user switches to this fragment a message for information is sent to the robot. This message ask for the current robot name, battery and stiffness status. The stiffness is a checkbox. The user can switch the stiffness on and off using this checkbox.

<img src="/doc/Connect.png" style="width: 50px;">

##### Speak fragment
*Enter what to say and set the speak settings like volume, speed and pitch.*

The speak fragment lets the robot say the appropriate text with the given settings. The user can enter the text on top and change the settings with the sliders. When clicking the "Say" button an speak message is sent to the robot which lets the robot say the appropriate text.

<img src="/doc/Speak.png" style="width: 50px;">

##### Walk fragment
*Press an arrow to move in that direction and release to stop moving. Set the speed using the slider.*

The walk fragment lets the robot walk in the desired direction. The speed can be adjusted using the slider. When pressing an arrow a walk message is sent. This message contains the x-speed (forward speed), y-speed (side speed) and theta-speed (turn speed). When pressing forward the x-speed is set to the sliders speed and the y-speed and theta-speed is set to zero. Moving backwards results in a negative x-speed and zero y-speed or theta-speed. Walking to the left is a positive y-speed and to the right is negative. Also, turning left results in a positive theta-speed and turning right in a negative theta-speed.

<img src="/doc/Walk.png" style="width: 50px;">

##### Camera fragment
*Take a picture with the robot and show it. Save the image on the phone.*

The camera fragment sends a picture message to the robot. Showing the picture is handles by the handler of the MainActivity. To save a picture on the phone, the user can click on the save button. The first time, it asked the user permission to access the image gallery of the phone.

<img src="/doc/Camera.png" style="width: 50px;">

##### Moves fragment
*Click on a move button to perform that move.*

The move fragment contains buttons with specific moves like waving, bowing, kicking etc. When clicking on one of these buttons, the keyframes file containing the move (see Move files) is sent to the robot.

<img src="/doc/Moves.png" style="width: 50px;">

#### NetworkThread
The NetworkThread is a singleton class that create a new thread. This thread handles the connection between the application and the robot. It creates a client that connects to the server on the robot. When connected, it repeatedly sends and receives messages when available. The messages that need to be sent are stored in an outMessages queue. The UI-thread can add messages to this queue to send them to the robot. The received messages are added to the handler of the MainActivity, which handles the messages.


### Robot
Similar to the application, the robot consists of two main parts: the Robot class that handles the robots actions and the NetworkThread that handles the connection with the application. Both classes are in the same file ("robotServer.py"), in order that only one file needs to be run on the robot.

The NetworkThread creates a new thread that handles the network connection between the robot and the application. This thread creates a server and receives and sends messages when an client (the app) is connected. The messages that need to be sent are stored in an outMessages queue. The Robot class can add messages to this queue to send them to the application. Received messages are saved in an inMessages queue and handled by the Robot class. Depending on the kind of message, the Robot class executes the action and sends (when needed) the appropriate data to the robot.

### Fake Robot
To be able to test the application without a robot, the script "localServer.py" is created. This script is almost the same as robotServer.py, but has a FakeRobot class. This FakeRobot class prints the actions the robot needs to executes instead of executing them on a robot.

### Structure of messages
The messages send between the application and the robot are JSON objects. In front of each message, the size of that message is added, stored in eight digits. When reading a message, first the size is read. Next, the complete message is read of the correct size.

00000055{type: info, (name: nao, battery: 50, stiffness: true)}: get the robot name, battery and stiffness status

00000041{type: stiffness, part: Body, value: 1.0}: turn the robot's stiffness on (1.0) or off (0.0)

00000069{type: speak, text: hello robot, volume: 100, speed: 100, pitch: 100}: let the robot say the text with different parameters

00000056{type: walk, x_speed: 100, y_speed: 100, theta_speed: 0}: let the robot walk with the given speed

00000037{type: picture, (img: img_in_string)}: take a picture with the robots camera

00000043{type: moves, name: filename, file: file_content_in_string}: let the robot perform a specific move

00000103{type: error, kind: [picture|move|text|json], text: could not [take picture|perform move|read message]}: send an error message back to the application, so the user knows what went wrong


### Move files
Python keyframes are used for the specific moves. These files can be created using choregraphe (http://doc.aldebaran.com/1-14/software/choregraphe/). Create a timeline box and save the desired keyframes on that timeline. Export the timeline to python and delete the imports and perform parts (begin and end of the file). The files are saved as .txt files and send to the robot to perform that specific move.


## Challenges
The first challenge already arose the first week. The NaoQi SDK could not be imported into Android Studio. The whole application was going to be built using this SDK, so another solution or application needed to be found. Instead of using the SDK, sockets are now used. This has several disadvantages, because the information about the connection between the robot and the application had to be written. For small messages the communication works fine. However, when sending pictures or files, some data is lost and thus cannot be processed. One of the biggest challenges of creating this application was the socket connection between Java (Android) and python (Robot), but it currently works fine.

Sockets are not better then the existing NaoQi SDK and using them made the development of the application much harder then expected. However, I had no better option then using sockets or creating a completely different application. One of the advantages of using sockets is that the application can be used to control different kind of robots when creating a robot script for that robot.
