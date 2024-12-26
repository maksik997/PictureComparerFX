#!/bin/bash
set -e
set -o pipefail

check_command() {
  command -v "$1" > /dev/null 2>&1 || {
    echo >&2 "&1 is required but not installed. Aborting"
    exit 1
  }
}

check_command mvn
check_command java
check_command jpackage

if [ ! -f "pom.xml" ] ; then
  echo "Error: Run this script from the project root directory (where pom.xml is located)."
  exit 1
fi

mvn clean package || {
  echo "Maven build failed. Aborting."
  exit 1
}

mkdir -p ./build/Linux
mkdir -p ./build/temp

JAR_FILE=$(find target -name "*SHADED.jar" | head -n 1)

if [ -z "$JAR_FILE" ] ; then
    echo "Error: No SHADED JAR file found. Maven build might have failed."
    exit 1
fi

cp "$JAR_FILE" ./build/temp

if [ -d "target/generated-resources" ] ; then
    cp -r target/generated-resources/* ./build/temp
else
    echo "Warning: No generated resources found. Proceeding without them."
fi

jpackage --name "PictureComparerFX" \
         --input ./build/temp \
         --main-jar "$(basename "$JAR_FILE")" \
         --type rpm \
         --icon images/thumbnail.png \
         --main-class pl.magzik.picture_comparer_fx.Main \
         --dest ./build/Linux || {
           echo "jpackage failed. Aborting."
           exit 1
         }

rm -Rf build/temp

echo "Build completed successfully."