@echo off
echo.
set apppath="oxipng.exe"
for /R "src\main\resources\assets\immersiveintelligence\textures" %%f in (*.png) do %apppath% %%f --zc 9 -f 0-5 --nc --strip all