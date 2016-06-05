# VS-Praktikum-Maven

# Für Aufgabe 1

## JAR Dateien

Befinden sich unter dem Pfad: **out/artifacts/`modulname`_jar/`modulname`.jar**

### Module starten

#### Modul Printer

| TCP  | UDP |
|---|---|
|```java -jar Printer.jar tcp://ip:port```|```java -jar Printer.jar udp://ip:port```|

#### Modul Material

| TCP  | UDP |
|---|---|
|```java -jar Material.jar tcp://ip:port fill-level=10```|```java -jar Material.jar udp://ip:port fill-level=10```|

`fill-level` beschreibt den Füllstand der Behälter beim Start des Moduls und gleichzeitig auch den maximal möglichen Füllstand

#### Modul ControlPanel

| TCP  | UDP |
|---|---|
|```java -jar ControlPanel.jar printer=tcp://ip:port material=tcp://ip:port```|```java -jar ControlPanel.jar printer=udp://ip:port material=udp://ip:port```|

#### Beispiel TCP

```
java -jar Printer.jar tcp://127.0.0.1:5555
java -jar Material.jar tcp://127.0.0.1:6666
java -jar ControlPanel.jar printer=tcp://127.0.0.1:5555 material=tcp://127.0.0.1:6666
```

#### Beispiel UDP

```
java -jar Printer.jar udp://127.0.0.1:5555
java -jar Material.jar udp://127.0.0.1:6666
java -jar ControlPanel.jar printer=udp://127.0.0.1:5555 material=udp://127.0.0.1:6666
```

### Performancetest

Bei jedem Modul das Argument `performancetest` anhängen.
Der Füllstand bei Material wird damit automatisch auf 1 Milliarde festgesetzt.

### Beispiel

```
java -jar Printer.jar tcp://127.0.0.1:5555 performancetest
java -jar Material.jar tcp://127.0.0.1:6666 performancetest
java -jar ControlPanel.jar printer=tcp://127.0.0.1:5555 material=tcp://127.0.0.1:6666 performancetest
```

# Für Aufgabe 2

## Applikationstest

Befinden sich unter dem Pfad: **out/artifacts/`modulname`_jar/`modulname`.jar**

### Fabrik starten
java -jar Fabric.jar

### Dashboard starten
java -jar Dashboard.jar test