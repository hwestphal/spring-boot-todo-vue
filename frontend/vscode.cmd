@echo off
setlocal
set "PATH=%~dp0.\node;%~dp0.\node\yarn\dist\bin;%PATH%"
code "%~dp0."
