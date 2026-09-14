@echo off
setlocal
call "%~dp0.tools\apache-maven-3.9.9\bin\mvn.cmd" "-Dmaven.repo.local=%~dp0.tools\repository" %*
exit /b %errorlevel%
