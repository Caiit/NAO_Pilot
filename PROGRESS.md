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
