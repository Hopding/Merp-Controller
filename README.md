# MERP Desktop Controller
The MERP Desktop Controller is a desktop interface for remote controlling [MERP](https://github.com/Hopding/MERP) via a WiFi connection.

![MERP Desktop Controller Demo](http://hopding.com/img/merp-desktop-controller-demo.gif)

After entering MERP's IP and connecting via WiFi, the Desktop Controller can send various motor control signals to the Raspberry Pi on MERP, using both the speed controls and the directional controls.

### Desktop Controller On Startup/Disconnected
![MERP Desktop Controller Startup](http://hopding.com/img/merp-controller-screenshot-0.PNG)

### Desktop Controller On Successful Connection
![MERP Desktop Controller Successful Connection](http://hopding.com/img/merp-controller-screenshot-1.PNG)

### Desktop Controller After Successful Connection
![MERP Desktop Controller After Successful Connection](http://hopding.com/img/merp-controller-screenshot-3.PNG)

### Desktop Controller On Successful Disconnect
![MERP Desktop Controller Successful Disconnect](http://hopding.com/img/merp-controller-screenshot-2.PNG)

### Desktop Controller On Failed Connection
![MERP Desktop Controller Failed Connection](http://hopding.com/img/merp-controller-screenshot-4.PNG)

# Building the Project
The code for the Desktop Controller is structured as a Gradle project, with a task for building a runnable JAR file. To build and run the JAR (for both Windows Powershell and Linux):

**NOTE:** [Releases](https://github.com/Hopding/Merp-Controller/releases) are available with pre-built JARs, if you would prefer to skip the cloning and building steps, and get straight to running the code.

* **Clone** this repo: 
```
$ git clone https://github.com/Hopding/Merp-Controller.git
```
* **CD** into the project directory: 
```
$ cd Merp-Controller
```
* **Execute** the Gradle task to create the JAR (The JAR will be created in the `build/libs` directory): 
```
$ ./gradlew runnableJAR
```
* **Execute** the JAR with the following command:
```
$ java -jar "build/libs/MERP Controller-1.0.jar"
```
