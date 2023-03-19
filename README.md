# Tomcat ENVironment Property Source File

In Tomcats `server.xml` möchte ich keine Passwörter hinterlegen
(in SSLHostConfig->Certificate e.g., oder bei Datenbank DataSource Resourcen).

Um Werte aus Umgebungsvariablen zu nutzen, kann ich `org.apache.tomcat.util.digester.EnvironmentPropertySource`
aus `tomcat-util-scan` verwenden. Passwörter in Umgebungsvariablen zu hinterlegen ist aber auch nicht so schön.
Es besteht immer die Gefahr, dass alle Umgebungsvariablen irgendwo ausgegeben werden (somit auch die Passwörter).

Im Docker-Kontext gibt es das "Konzept" von "secrets". Das bedeutet, dass Passwörter in "Dateien" in `/run/secrets`
zur Verfügung gestellt werden. Der Pfad wird in Umgebungsvariablen definiert, e.g. `DB_PASSWORD_FILE`.
In der Umgebungsvariable `DB_PASSWORD_FILE` steht als Inhalt `/run/secrets/db_password_file`. Sollten alle
Umgebungsvariablen irgendwo angezeigt werden, ist das nicht weiter schlimm, da der Inhalt `/run/secrets/db_password_file`
erst einmal unkritisch ist. Das eigentliche Passwort befindet sich in dieser Datei. Diese Datei muss man jetzt noch auslesen.

`EnvironmentPropertySource` von Tomcat kann aber nur die Werte aus Umgebungsvariablen in `server.xml` (und anderen XML-Konfigurationen) zur Verfügung stellen, es kann aber nicht den Wert als Dateipfad verstehen, und die Datei auslesen.

Diese Lücke schließt diese kleine Bibliothek.

## Wie verwende ich diese Bibliothek?

- die `tomcat-environment-property-source-file.jar` in den `lib`-Ordner von Tomcat kopieren
- in `conf/catalina.properties` die jar bei `tomcat.util.scan.StandardJarScanFilter.jarsToSkip` hinzufügen (optional)
- in `conf/catalina.properties` eine Zeile hinzufügen:

```
org.apache.tomcat.util.digester.PROPERTY_SOURCE=de.schipplock.web.tepsf.EnvironmentPropertySourceFile
```

**In der `server.xml` kann man dann sowas schreiben:**

```xml
<Certificate certificateKeystoreFile="conf/localhost.jks"
             type="RSA"
             certificateKeyAlias="server"
             certificateKeystorePassword="${SSL_PASSWORD_FILE}" />
```

Die Umgebungsvariable `SSL_PASSWORD_FILE` hat den Inhalt `/run/secrets/ssl_password`. In der Datei `/run/secrets/ssl_password` steht das Passwort `changeit`.

In `server.xml` würde `${SSL_PASSWORD_FILE}` mit dem Wert `changeit` ersetzt werden. Man kann auch weiterhin einfach nur den Wert aus Umgebungsvariablen nutzen. Dazu muss die Umgebungsvariable am Ende nicht `_FILE` heißen (oder die Datei existiert nicht :P, dann ist der Fallback den Wert aus der Umgebungsvariable zu nutzen).

Die Beschreibung hier ist länger als die Implementierung.
Ich habe diese kleine Lib nur für mich und "meinen" Tomcat geschrieben.