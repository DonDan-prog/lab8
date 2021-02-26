@jar -xf ArgumentParser.jar ArgumentParser/
@javac *.java
@echo Compiled files
@jar -cvfe WebCrawler.jar App *.class ArgumentParser/*.class
@echo Executable made!
@del *.class
@rmdir ArgumentParser /s /q
@echo.
@echo.
@echo To launch compiled program use:
@echo java -jar WebCrawler.jar url depth [threads] [logname]
@echo.
@pause