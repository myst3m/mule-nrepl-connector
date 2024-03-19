# nREPL - Extension

This extension is nREPL in Clojure for Mule Application.

The nREPL is implemented as a Listener and can configure a listen port.
You can put this listener to your Mule application .
In this version, the listener does not run the processes. 
But to avoid error during build, you should put some processor like Log .

Add this dependency to your application pom.xml

```
<groupId>theorems</groupId>
<artifactId>nrepl-connector</artifactId>
<version>0.0.1</version>
<classifier>mule-plugin</classifier>
```
