@echo off
javac -cp "..\lib\mysql-connector-j-9.5.0.jar;..\lib\jcalendar-1.4.jar;." Driver.java
java -cp "..\lib\mysql-connector-j-9.5.0.jar;..\lib\jcalendar-1.4.jar;." Driver
:: IMPORTANT TO RESET CLASSPATH AFTER USAGE
set classpath=
del *.class /f /q