CLASSES = \
	ClientConnector.java \
	SocketHandler.java \
	Server.java

default: classes

classes:
	mkdir -p bin
	cp Server/*.java .
	javac *.java
	rm *.java
	cp Client/*.java .
	javac -nowarn *.java
	rm *.java
	mv *.class bin

clean:
	$(RM) bin/*.class
