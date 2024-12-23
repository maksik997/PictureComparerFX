#!/bin/bash
# You should run this script from this Directory
# Additionally, you should have installed maven, jdk with jpackage.
# TODO: MAKE IT IDIOT-PROOF

cd ../..

mvn clean package

mkdir -p ./build/MacOS
mkdir -p ./build/temp

cp target/PictureComparerFX-0.7.0-SNAPSHOT-SHADED.jar ./build/temp
cp -r target/generated-resources/* ./build/temp

jpackage --name "PictureComparerFX" \
--input build/temp \
--main-jar PictureComparerFX-0.7.0-SNAPSHOT-SHADED.jar \
--type app-image \
--icon images/MacOS/thumbnail.icns \
--app-version 1.0 \
--main-class pl.magzik.picture_comparer_fx.Main \
--dest build/MacOS

rm -Rf build/temp
