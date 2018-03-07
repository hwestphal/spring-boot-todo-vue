@echo off
setlocal
set "PATH=%~dp0.\node;%PATH%"
"%~dp0.\node\yarn\dist\bin\yarn" %*
