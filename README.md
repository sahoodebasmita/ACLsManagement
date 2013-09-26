ACL Manangement Automation
==========================


1. Giving ACL to user directory for last modified files on Windows Envirnoment. 

2. In some situations if file is modified, the defined ACL lost after synchronization. for example GoogleDrive sync.
   So ACL is a sample project to check periodically if file is modified and then assign the ACLs defined in users.txt
   settings.properties file is used to keep track of last script execution time. next time the script will run, it will check 
   with the last execution time and last updated time to find out the files which are recently updated and will apply the ACL for those file.

SET UP CONFIGURATION.

1.	Create a folder called “GoogleDrive” in user’s home directory

Paste following files in the “[user.home]/GoogleDrive” directory after extracting the archive file
•	users.txt  [Used to have all domain users for whom the ACLs will be set]
•	settings.properties [Used to keep track of last execution time]


2.	Copy following files into any preferable directory.
•	ACL.jar [the program responsible for setting ACLs on modified files since last execution]
•	ACL.bat [the batch file which will be executed periodically to run ACL program ]

3.	Open ACL.bat file in note pad and change the directory path to the appropriate directory in which the google drive synch is happening and save it.


@ECHO OFF
set CLASSPATH=.
set CLASSPATH=%CLASSPATH%;ACL.jar
set PATH=%PATH%;%JAVA_HOME%
java -jar ACL.jar "C:\\Test1


ACL.bat file execution can be configured through a job scheduler.
