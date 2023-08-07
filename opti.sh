#!/bin/sh

for f in $(find src/main/resources/assets/immersiveintelligence/textures -name '*.png'); do ./oxipng $f --zc 9 -f 0-5 --nc --strip all; done