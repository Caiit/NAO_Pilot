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
