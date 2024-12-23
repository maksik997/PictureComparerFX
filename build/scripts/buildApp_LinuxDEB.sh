#!/bin/bash
# You should run this script from this Directory (i.e. {PROJECT_DIR}/build/scripts).
# Additionally, you should have installed maven, and jdk.

cd ../..

mvn clean package

mkdir -p ./build/Linux
mkdir -p ./build/temp

cp ./target/PictureComparerFX-0.7.0-SNAPSHOT-SHADED.jar ./build/temp
cp -r ./target/generated-resources/* ./build/temp

jpackage --name "PictureComparerFX" \
--input ./build/temp \
--main-jar PictureComparerFX-0.7.0-SNAPSHOT-SHADED.jar \
--type deb \
--icon images/thumbnail.png \
--main-class pl.magzik.picture_comparer_fx.Main \
--dest ./build/Linux

rm -Rf build/temp