# NAO Pilot
Caitlin Lagrand </br>
UvA Programmeerproject application January 2017. 


The NAO pilot application controls a NAO robot (https://www.ald.softbankrobotics.com/en/cool-robots/nao). This application can for example be used for easily creating a dataset or giving demonstrations. The application connects with a NAO via WiFi,  lets the NAO speak, walk, perform and create specific moves (dances, kicks etc.) and gets the camera output. External specific moves files (probably keyframes) can be added and created to personalise it. The NaoQi Java SDK (http://doc.aldebaran.com/2-1/dev/java/index_java.html) will be used to connect and control the NAO. The biggest problem is connecting to the robot and stay connected. With the NaoQi SDK this should be done easily, but you never know with robots. Also, the application can become slow, mostly when showing camera output, due to the wireless connection with the robot.



##what problem will be solved for the user

The NAO pilot application can control a NAO robot (https://www.ald.softbankrobotics.com/en/cool-robots/nao).

##what features will be available to solve the problem

Connect to the robot. </br>
Let the robot speak. </br>
Move the robot forwards, backwards, left, right, turn. </br>
Get the camera images of the robot, choose between top and bottom. </br>
Let the robot perform specific moves (dances, kicks etc.). </br>
Move specific joints and save the configurations to be able to create specific moves. </br>

##a visual sketch of what the application will look like for the user; if you envision the application to have multiple screens, sketch these all out; not in full detail though

<img src="/doc/appSketch.jpg">

Connect screen: ask for IP and connect button, maybe home screen, when connected show robot information like name and battery status and volume. </br>
Speak screen: input what to say and speak settings like voice and speed. </br>
Walk screen: arrows in which way to move or turn. Maybe joystick. Slider for speed. </br>
Camera screen: camera image from the selected camera and save button to save either on the robot or on the phone. </br>
Specific moves screen: buttons or images with the specific move. Add new specific moves. </br>
Move joints screen: sliders to move specific joints and save button. </br>

##what data sets and data sources will you need, how you will get the data into the right form for your app

External specific moves files (probably keyframes). 

##what separate parts of the application can be defined (decomposing the problem) and how these should work together

The different control parts (move, speak, camera) can be separated.

##what external components (APIs) you probably need to make certain features possible

The NaoQi Java SDK (http://doc.aldebaran.com/2-1/dev/java/index_java.html) will be used.

##technical problems or limitations that could arise during development and what possibilities you have to overcome these

The biggest problem is connecting to the robot and stay connected. With the NaoQi SDK this should be done easily, but you never know with robots.
Also, the app can become slow, mostly when showing camera images, due to the wireless connection with the robot.

##a review of similar applications or visualizations in terms of features and technical aspects (what do they offer? how have they implemented it?)
A few similar applications exists: NAO control (https://play.google.com/store/apps/details?id=de.daboapps.androidnao), NAO Robot Controller (https://play.google.com/store/apps/details?id=com.robinbonnes.naorobotcontroller&hl=nl) and NAO Communicator (https://play.google.com/store/apps/details?id=de.robotik.nao.communicator&hl=nl).
These apps have similar functionalities as the application I want to develop. However, they only use basic control of the robot. The NAO pilot application will be able to allow creating and adding specific moves.
