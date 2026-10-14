# Library-Management-System

javac -cp "lib/*" -d bin src/Main.java
javac -d bin src\Main.java src\config\EnvConfig.java src\frames\LoginFrame.java src\frames\DashboardFrame.java src\config\FileCreation.java src\service\BooksService.java
javac -d bin src\Main.java src\config\EnvConfig.java src\frames\LoginFrame.java src\frames\DashboardFrame.java src\config\FileCreation.java src\service\BooksService.java src\frames\BookFrame.java src\frames\MemberFrame.java

java -cp "lib/*;bin" Main

javac -d . *.java

Open the Services manager by pressing Win + R, typing services.msc, and pressing Enter.