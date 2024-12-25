![logo](images/thumbnail.png)
# Picture Comparer FX
#### Version: 1.0

## Introduction:
This application is designed to help you automatically find and manage redundant images in your collection.
Whether you're dealing with duplicate photos or just want a more organized image library,
this tool simplifies the process.

### Key features:
- **Duplicates Removal** - Automatically detects duplicate and deletes duplicate images from your collection.
- **Comparer** - Scans folders to identify duplicate images and allows you to either delete or move them based on your preferences.
- **Image Viewer** - Open and view selected images directly in your system's default viewer.
- **Batch Renaming** - Rename selected images according to a customizable naming pattern.
- **Bulk Deletion** - Permanently deleted selected images from you disk with ease.
- **Customizable Duplicate Search** - This application offers flexible options for duplicate detection. Users can:
  - Enable or disable specific search algorithms to tailor the detection process.
  - Activate recursive scanning to search for duplicates across multiple directories and subfolders.

This is my first project developed using a JavaFX Framework and a custom-built image processing library.
Although the app may still have occasional bugs, I am dedicated to improving its performance and enhancing user experience over time.

## Requirements:

### System requirements:

- **Java:** 21
- **Operating System:**
  - Windows 10 (or newer)
  - macOS 11 (Big Sur) (or newer)
  - Linux:
    - Debian/Ubuntu: 20.04 (or newer) (DEB)
    - Fedora/RHEL: version 35 (or newer) (RPM)
    - Arch Linux/Manjaro (Arch)

### Additional requirements (when building from source):

- **Apache Maven** - to compile and build project.
- **Internet access** - to download all dependencies from Maven Central or Jitpack.io.

## Dependencies:

- **Runtime dependencies:**
  - **PictureComparer:** 0.7.0
  - **JavaFX:** 21.0.5
  - **Ikonli:** 12.3.1
  - **SLF4J:** 2.0.13
  - **Logback:** 1.5.15
  - **Jetbrains Annotations:** 26.0.1

## Installation:

### Multi-platform:

To run the application on any system with Java 21+ installed, use the `.jar` file.

#### Requirements: 
- **Java 21** or higher (JDK or JRE).

#### Installation and Launch:
1. Download the latest `PictureComparerFX.jar` from the [Releases](https://github.com/maksik997/PictureComparerFX/releases) section on GitHub.
2. Open a terminal or a file explorer and navigate to the folder containing the `.jar` file.
3. Launch the application: Either using the command below, or double-clicking the file.
```bash
java -jar PictureComparerFX.jar
```

#### Uninstallation:
1. Remove the `.jar` file.
2. Remove the configuration containing directory:

_Windows:_
```bash
C:\Users\[user]\AppData\Roaming\PictureComparerFX
```
_MacOS:_
```bash
~/Library/Application\ Support/PictureComparerFX/
```
_Linux:_
```bash
~/.config/PictureComparerFX/
```

### Windows:
[//]: # (TODO: FULFiLL)
TBD

### MacOS:

The application is provided as `.app` file for easy installation and launch.

#### Installation:
1. Download `PictureComparerFX.app` from the [Releases](https://github.com/maksik997/PictureComparerFX/releases) section.
2. Move the file to the **Applications** folder.
3. Double-click to launch the application.
4. If needed, right-click and select **Open** during the first launch to bypass Gatekeeper warnings.

#### Uninstallation:
1. Open the **Application** folder.
2. Locate the `PictureComparerFX.app` file.
3. Right-click and select **Move to Trash**, or drag the application to the Trash.
4. To remove configuration files, open **Finder** and navigate to the following path:
```bash
~/Library/Application\ Support/PictureComparerFX/
```
5. Delete the `PictureComparerFX` folder.

### Linux:

#### Arch Linux / Manjaro (Arch-based)

##### Installation:
1. Download the `PictureComparerFX-Arch.tar.gz` file from the [Releases](https://github.com/maksik997/PictureComparerFX/releases) section.
2. Open a terminal and navigate to the directory with the file.
3. Extract the file:
```bash
tar -xzvf PictureComparerFX-Arch.tar.gz
```
4. Move to the extracted directory:
```bash
cd PictureComparerFX-Arch 
```
5. Run the following command:
```bash
makepkg -si
```
6. After installation, launch the application from the menu or terminal
```bash
picturecomparerfx 
```

##### Uninstallation:
1. Open a terminal.
2. Uninstall the application:
```bash
sudo pacman -Rns picturecomparerfx 
```
3. To remove the configuration files, delete the `PictureComparerFX` folder:
```bash
rm -Rf ~/.config/PictureComparerFX/ 
```

#### Debian / Ubuntu (DEB)

##### Installation:
1. Download the `.deb` package from the [Releases](https://github.com/maksik997/PictureComparerFX/releases) section.
2. Open a terminal and navigate to the directory containing the file.
3. Install the application:
```bash
sudo dpkg -i PictureComparerFX.deb
sudo apt --fix-broken install # In case of dependency issues 
```
4. Launch the application:
```bash
picturecomparerfx 
```

##### Uninstallation:
1. Open a terminal.
2. Uninstall the application:
```bash
sudo apt remove picturecomparerfx 
```
3. To remove configuration files, delete the `PictureComparerFX` folder:
```bash
rm -Rf ~/.config/PictureComparerFX/
```

#### Fedora / RHEL (RPM)

##### Installation:
1. Download the `.rpm` package from the [Releases](https://github.com/maksik997/PictureComparerFX/releases) section.
2. Install the application:
```bash
sudo dnf install PictureComparerFX.rpm
```
3. Launch the application:
```bash
picturecomparerfx 
```

##### Uninstallation:
1. Open a terminal.
2. Uninstall the application:
```bash
sudo dnf remove picturecomparerfx
```
3. To remove the configuration files, delete the `PictureComparerFX` folder:
```bash
rm -Rf ~/.config/PictureComparerFX/ 
```

### Building from source:
...

## Usage:
...

## Information:
...

## Contribution:

We welcome contributions! If you'd like to contribute, 
please fork the repository, create a new branch, 
and submit a pull request with your changes.

## License:

This project is licensed under the MIT License. See the `LICENSE.txt` file for details.

### v DEPRECATED v ###

## Description: 
**Warning!
This version of this app contains some bugs.**
However, if you want to use it, you should know that your data is safe.
If you don't trust comparer, you could always move your images instead of removing them (Gallery feature).

If you want to find or remove redundant images, and you want to do it somehow automatic, then this app is for you.
App features with: 

- Comparer which will help you find those images that are duplicates of existing ones,
- Gallery which will help you in a kind of comfortable way to manage your images.

Both of them are easy to use, and they're relatively fast. 

**Comparer** features with comfortable view of all loaded images and duplicates that were found.
Additionally, you can see how many of both were found.
There is an option to use recursive search for images,
which could be handy if you use many directories where you store your images.

**Gallery** features with a list of your loaded images, with some options: Distinct, Unify, Open. Distinct will let you find any duplicates in selection and will ask if you want to remove them or move. Unify will let you standardize your image names in format: `tp_img_[n]_[timestamp].[extension]` (you can change prefix in settings). And last but not the least Open button, which let you open your images in your system image viewer. Additionally, new feature is here: tag system. New system will let you tag your images basing on your own preference.  

This is my first bigger project, and I'm thrilled how it looks.
It's a buggy from time to time,
but I hope that with some more time I will be able to remove all bugs and create a great user experience.

And for those that read the source code: Yes, there are a lot of todo comments there :P

## Instruction of usage: 
1. Run the app.
2. You can choose either to open a Comparer or Gallery or Settings.

### Comparer
1. You can pick directory where you want to search for images,
2. Then you're able to click **Load & compare** button, which will lock most Comparer functions for time it's working.
3. After waiting for some time, you will see that **Total** and **Duplicates** values will change, and you will be able to see that **Loaded originals** and **Duplicates found** changed (Duplicates only if any duplicate was found).
4. Now you can perform **Move** action which will move those duplicates to the directory you specified.
5. Then, if you want to use this Comparer instance, again you have to click **Reset** button.

### Gallery 
You will see a table and five buttons.
To add images, you should click **Add** button, and pick there any file you want to add
(the app will check if this is a valid picture, where valid means that image is supported by Picture Comparer).
To remove image, you should click **Remove** button, which will remove all the selected images in table.
To remove duplicates, you should click **Distinct** button, which require at least two pictures selected.
Next there is **Unify** button that will rename all of your loaded images with specified format:
`tp_img_[n]_[timestamp].[extension]`.
And lastly, there is **Open** button, which will open your image in your system image viewer.

### Settings
Here you can change your destination for your Comparer output (for both Comparer and Gallery),
and you can pick if you want to recursively search for images (only for Comparer).
Additionally, you can change your language and select theme.
Also, there are options to disable comparing using P-Hash and Pixel-by-Pixel algorithms.
And in the case of Gallery's settings,
you can change prefix for name unification function and change if you want your file extensions lowercase.
Remember after any change if you want to save your settings you should click **Save settings** button.

## Running app:
1. Click .exe file.
2. End...

## Notes:
- Project build system: IntelliJ IDEA
- Jdk version: 22
- Versions with d letter suffix are un-tested

Author: [GitHub](https://github.com/maksik997)
