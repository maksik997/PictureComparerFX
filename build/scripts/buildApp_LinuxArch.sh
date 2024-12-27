#!/bin/bash
set -e
set -o pipefail

check_command() {
  command -v "$1" > /dev/null 2>&1 || {
    echo >&2 "$1 is required but not installed. Aborting"
    exit 1
  }
}

check_command mvn
check_command java
check_command jpackage
check_command makepkg

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
mkdir -p ./build/arch

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
         --type app-image \
         --icon images/thumbnail.png \
         --main-class pl.magzik.picture_comparer_fx.Main \
         --dest ./build/Linux || {
          echo "jpackage failed. Aborting."
          exit 1
        }

cat > ./build/Linux/PictureComparerFX/PictureComparerFX.desktop <<EOF
[Desktop Entry]
Type=Application
Name=PictureComparerFX
Exec=/usr/bin/picturecomparerfx
Icon=/opt/PictureComparerFX/lib/PictureComparerFX.png
Terminal=false
Categories=Utility;
EOF

tar -czvf ./build/arch/PictureComparerFX-Arch.tar.gz -C ./build/Linux PictureComparerFX

cat > ./build/arch/PKGBUILD <<EOF
pkgname=picturecomparerfx
pkgver=1.0
pkgrel=1
arch=('x86_64')
pkgdesc="Picture Comparer FX - JavaFX application for comparing images"
url="https://github.com/maksik997/ThousandPictureComapre"
license=('MIT')
depends=('java-runtime' 'java-environment')
source=("PictureComparerFX-Arch.tar.gz")
sha256sums=($(sha256sum ./build/arch/PictureComparerFX-Arch.tar.gz | cut -d ' ' -f 1))

package() {
  mkdir -p "\${pkgdir}/opt/PictureComparerFX"
  cp -r "\${srcdir}/PictureComparerFX/"* "\${pkgdir}/opt/PictureComparerFX/"

  mkdir -p "\${pkgdir}/usr/bin"
  ln -s "/opt/PictureComparerFX/bin/PictureComparerFX" "\${pkgdir}/usr/bin/picturecomparerfx"

  install -Dm644 "\${srcdir}/PictureComparerFX/PictureComparerFX.desktop" "\${pkgdir}/usr/share/applications/PictureComparerFX.desktop"
}
EOF

echo "PKGBUILD and .desktop files generated in ./build/arch"

rm -Rf ./build/temp

echo "Build completed successfully. Run command 'makepkg -si' from 'build/arch' directory, to install the app."
