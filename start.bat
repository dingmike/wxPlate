echo 'Start to build project'
@echo off

cd P3-Biz-commonweixin
call mvn install package -Dmaven.test.skip=true

cd ..\P3-Biz-jiugongge
call mvn install package -Dmaven.test.skip=true

cd ..\P3-Biz-shaketicket
call mvn install package -Dmaven.test.skip=true

cd ..\P3-Web
set MAVEN_OPTS=%MAVEN_OPTS% -Xms1024M -Xmx1024M -XX:PermSize=256M -XX:MaxPermSize=256M
call mvn tomcat:run

@echo on
echo 'Build project successfully!'

pause