# Stocks Watchlist #
* You will need to set up developer accounts with Alpha Vantage and Twilio and then insert your API keys 

* To run the application in the Spring embedded Tomcat server: 

```
mvn spring-boot:run
``` 

* To run the debugger at port `8081` from your IDE, add the required configuration to the `org.springframework.boot` plugin in your pom:
```
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <jvmArguments>
            -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8081
        </jvmArguments>
    </configuration>
</plugin>
```

* If you chose the run the application in a standalone application server, make sure to package the service as a war. In the pom.xml:
```
<packaging>war</packaging>
```