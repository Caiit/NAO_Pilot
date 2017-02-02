# day 1
Write a proposal of the app.

# day 2
Tried importing the NaoQi Java SDK to Android Studio. The SDK as .jar file needs to be added to the libs folder of the project. The code compiles, but on runtime it crashes with the following error:

**Caused by: java.lang.UnsatisfiedLinkError: No implementation found for long com.aldebaran.qimessaging.Session.qiSessionCreate()**

After googling and trying different possible solutions, it still didn't work. Probably the .jar is missing some important code. Another method to connect with the robot is the best option to still be able to create the app.

Some things that were tried: 
- https://community.ald.softbankrobotics.com/en/forum/controll-nao-android-application-2189
- https://github.com/ghostwan/jnao-example
- https://community.ald.softbankrobotics.com/en/forum/qimessaging-android-873
- https://github.com/aldebaran/libqi-java
- http://aidanjones.info/?page_id=17

# day 3
Because the NaoQi Java SDK doesn't work, socket is now used to create a server on the robot and send messages from the app to the server.
Test scripts outside Android Studio are written to create the connection. A Python script that creates a server runs on the robot and a Java script that sends a message runs on the laptop. This works fine.

# day 4
Implemented the script that creates a client within Android Studio. This needs to be on a separate thread, because it uses a network connection. The thread starts when an IP is given and the connect button is clicked. A client is created and a continuous connection with the server is available. Messages to the server are saved in a queue. When the user wants to send a message, it is added to the queue. The networkthread handles this queue by taking the first item and sending it to the server. 

# day 5
Tested the code with the server on the robot. The robot says the message out loud. </br>
Created connect and speak fragments.

# day 6
Extended the speak fragment, the user can now change the volume, speed and pitch of the text. Created JSON objects of the data sent between the server and client. The following structure is used: </br>

{type: speak, text: blub, volume: 100, speed: 100, pitch: 100} </br>
{type: stiffness, part: Body, value: 1.0} </br>
{type: walk, xspeed: 100, yspeed: 100} </br>

Started on the walk fragment. The Networkthread is now a singleton. This caused some problems when restarting, because the thread was already closed, but existed. Currently, the thread is set to null when closed and when an instance is created, a new networkthread is created if the thread is null.

# day 7
The robot walks when the user presses the arrow and stops on release. However, when the press is really short, it does not correctly send the release command (because they are too close after each other). This can be fixed by also using a separate thread on the robot for handling the connection. 

# day 8
A thread on the robot is implemented. The robot can now walk and turn without any problems and the speed can be adjusted. 

# day 9
Started with camera input. Problems: the server on the robot is blocking: it waits for a new incoming message. This results in not sending the data immediately. The user has to press first a take picture then a show picture button, because the data needs to be obtained from the messages from the thread. Would be nice if a on data change for a queue exists.

# day 10
The camera input is fixed. When clicking the take picture button, a picture is shown. The server on the robot is now non-blocking. The app uses a handler and messages to handle the messages of the networkthread on the UI-thread (http://codetheory.in/android-handlers-runnables-loopers-messagequeue-handlerthread/). 

# day 11
The special moves via files is fixed. The app reads in a file, stored in the assets directory, and sends it to the robot. This file is python code containing three lists: times, joint names and joint values. 

# day 12
To get more "real time" (it is still with a delay) images instead of clicking a button to get an image, the app now sends a message to obtain images when the user switches to the camera tab and sends a message to stop when switching to another tab. The robot sends camera images every cycle. Images are often still not correctly send (data is missing). To avoid this, I want to send the size of a message in front of the message string, so the receiver knows how much to read. 

# day 13
After many attempts, sending the size of the message in front of the message works. Now the structure is as follow:


00000055{type: info, (name: nao, battery: 50, stiffness: true)}: get the robot name, battery and stiffness status </br>
00000041{type: stiffness, part: Body, value: 1.0}: turn the robot's stiffness on (1.0) or off (0.0) </br>
00000069{type: speak, text: hello robot, volume: 100, speed: 100, pitch: 100}: let the robot say the text with different parameters </br>
00000056{type: walk, x_speed: 100, y_speed: 100, theta_speed: 0}: let the robot walk with the given speed </br>
00000037{type: picture, (img: img_in_string)}: take a picture with the robots camera </br>
00000043{type: moves, file: file_content_in_string}: let the robot perform a specific move </br>

# day 14
Cleaned the code. Most functions of a specific part are now in their own fragment instead of in the main activity. Only connecting with the robot and handling the messages from the robot are still in the main activity, because the handler is part of the main activity.

# day 15
Cleaned the code again. During the presentation the camera didn't work. Instead of video, the user can now only take a picture to reduce the amount of data send. This works fine. New moves are added. 

TODOs:
- ~~fix connect button: oncreate the connect button says disconnect while there is no connection~~
- ~~add app logo~~
- ~~save picture~~
- ~~add more dances~~
- ~~set name and battery and stiffness visible / invisible~~
- ~~show toast when connected~~
- ~~move connect button down a little bit~~
- ~~move say button to bottom~~
- ~~keyboard down when button clicked~~
- ~~moves in gridview~~
- ~~better picture on camera fragment~~
- ~~don't allow landscape view~~
- ~~nicer buttons: create style and use on all buttons~~
- ~~add scrollview to moves~~
- ~~toast when saving picture, maybe sound when taking picture~~
- ~~dropdown list style ~~
- ~~test dropdown list with robot name~~

Bugs:
- dropdown doesn't work on start

# day 16
Created a dropdown list of robots on the network. This only works on a real device and not with the emulator. Fixed some minor bugs and todos (day 15) and cleaned the code.

# day 17
Fixed some minor bugs and todos (day 15) and cleaned the code.

# day 18 
Talked to RobotWise that wants to use my app. Fixed some more bugs and todos and cleaned the code again. Python code is now pep8 correct. 

# day 19
Fixed some bugs and todos (day 15). Created server without robot, to test without a robot. 
