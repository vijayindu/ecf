echo off
setlocal
cd %~dp0
set RP=..\..\..\plugins
set ECF=%RP%\org.eclipse.ecf_0.8.9\ecf.jar
set UI=%RP%\org.eclipse.ecf.ui_0.8.9\ui.jar
set SDO=%RP%\org.eclipse.ecf.sdo_0.8.9\ecf.sdo.jar
set DS=%RP%\org.eclipse.ecf.datashare_0.8.9\datashare.jar
set DSP=%RP%\org.eclipse.ecf.provider.datashare_0.8.9\dsprovider.jar
set FS=%RP%\org.eclipse.ecf.fileshare_0.8.9\fileshare.jar
set FSP=%RP%\org.eclipse.ecf.provider.fileshare_0.8.9\fsprovider.jar
set PROVIDER=%RP%\org.eclipse.ecf.provider_0.8.9\provider.jar
set PRESENCE=%RP%\org.eclipse.ecf.presence_0.8.9\presence.jar
set GED=%RP%\org.eclipse.ecf.example.sdo.gefeditor_0.8.9\editor.jar
set ED=%RP%\org.eclipse.ecf.example.sdo.editor_0.8.9\editor.jar
set LIBRARY=%RP%\org.eclipse.ecf.example.sdo.library_0.8.9\runtime\org.eclipse.ecf.example.library.jar
set DISCOVERY=%RP%\org.eclipse.ecf.discovery_0.8.9\discovery.jar
set HELLO=%RP%\org.eclipse.ecf.example.hello_0.8.9\hello.jar
set COLLAB=%RP%\org.eclipse.ecf.example.collab_0.8.9\client.jar
set CED=%RP%\org.eclipse.ecf.example.collab.editor_0.8.9\collabeditor.jar

set CP="..\lib\core.jar;..\lib\runtime.jar;..\lib\osgi.jar;%ECF%;%UI%;%SDO%;%PROVIDER%;%PRESENCE%;%GED%;%ED%;%LIBRARY%;%HELLO%;%DS%;%DSP%;%FS%;%FsSP%;%DISCOVERY%;%COLLAB%;%CED%;."

set TRACE=-Dorg.eclipse.ecf.Trace -Dorg.eclipse.ecf.provider.Trace

set OPTIONS=%TRACE%

set MAINCLASS=org.eclipse.ecf.provider.app.ServerApplication
set ARGS=-c ..\conf\server.xml %*

rem Start server
echo "Starting server with options: %OPTIONS% and args: %ARGS%"
java -cp %CP% %OPTIONS% %MAINCLASS% %ARGS% 

endlocal