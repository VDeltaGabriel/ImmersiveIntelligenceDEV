#!/bin/sh

chmod +x ./oxipng
chmod +x ./ffprobe
chmod +x ./ffmpeg

processAudio () {
    echo "Processing $1";
    bitrate=$(./ffprobe -v 0 -select_streams a:0 -show_entries stream=bit_rate -of compact=p=0:nk=1 "$1");
    if [ "$bitrate" -le "120000" ]
    then
        echo "Bitrate is under 120kb/s no need to compress";
        return;
    fi

    touch $1.tmp
    ./ffmpeg -y -i $1 -f ogg -c:a libvorbis -b:a 120k $1.tmp &
    wait
    rm $1
    mv $1.tmp $1
}

while getopts "ia" arg; do
    case $arg in
        i)
            echo "Processing images..."
            for f in $(find src/main/resources/assets/immersiveintelligence/textures -name '*.png'); do ./oxipng $f --zc 9 -f 0-5 --nc --strip all; done
            ;;
        a)
            echo "Processing audio..."
            for f in $(find src/main/resources/assets/immersiveintelligence/sounds -name '*.ogg'); do processAudio $f; done
            ;;
    esac
done