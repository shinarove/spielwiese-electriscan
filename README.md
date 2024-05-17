# team4-RTFM-projekt2-electriscan
IT23aWin-PM2-2024

Welcome to the electriscan by: <br>
Team 4, RTFM <br>
Jakub Baranec, Nghia Dao, Nino Frei, Nardi Teesselink

### How to get started
To start the application, open a terminal in the application directory: team4-RTFM-projekt2-electriscan and
execute the command ```./gradlew.run``` or ```.\gradlew run``` in the terminal.

This will open the application where you can see your households on the left. 
First you will have to load a household by clicking on the "Haushalt laden" button.
You can have multiple households and can switch between them.
Then you can see the details of the household on the right.
There you can see all the rooms and the devices in the rooms.
The details of the devices or rooms can be seen by clicking on them.
The devices and rooms can be added, edited or deleted.
It is also possible to add solar panels to the household.
For that you have to switch to the "Solaranlagen" tab.
When you go to the "Kostenübersicht" tab you can see the costs of the household.
There you can also see the cost per room or per device category.
You can also see how much energy the solar panels produce.

#### Location of the JSON and Documents
The location of the JSON Files are in [team4-RTFM-projekt2-electriscan\JSON](https://github.zhaw.ch/PM2-IT23aWIN-fame-wahl-kars/team4-RTFM-projekt2-electriscan/tree/main/json) and the location of the Documents Folder are here [team4-RTFM-projekt2-electriscan\Documents](https://github.zhaw.ch/PM2-IT23aWIN-fame-wahl-kars/team4-RTFM-projekt2-electriscan/tree/main/documentation)


### Dependencies
For this Project we have used [Mockito](https://mvnrepository.com/artifact/org.mockito/mockito-core/5.11.0) and its dependencies,
[JSON](https://mvnrepository.com/artifact/org.json/json/20240303) and JUnit5

## Documents
### Class diagram
[Klassendiagramm_Electriscan](https://github.zhaw.ch/PM2-IT23aWIN-fame-wahl-kars/team4-RTFM-projekt2-electriscan/tree/main/documentation/Klassendiagramm-ElectriScan.pdf)
#### Level of detail in the class diagramm
With regard to the level of detail of the class diagram, we have decided not to display private methods, inner classes and property enums. For the data fields, we mainly limit ourselves to primitive types. Lists, maps or sets are not shown, instead we show the methods that indicate that they are being interacted with. Whether a list or a map is used for storage is an implementation detail. To reduce the number of setters or getters in the class diagram, we have declared the primitive data types as public or marked them {readonly}. To clarify our approach to property changes, we have highlighted certain arrows and data fields in color. These indicate (where the arrow points) that a class is a listener of the class with the PropertyChangeSupport.

### Test concept with equivalence classes
[Testkonzept-Electriscan](https://github.zhaw.ch/PM2-IT23aWIN-fame-wahl-kars/team4-RTFM-projekt2-electriscan/tree/main/documentation/Testkonzept-ElectriScan.pdf)

## Project strategy
### Branch strategy
In our branching model, we have 3 main distinctions, these are the main, develop and feature branches:
- The main branch always contains a working implementation of the game.
- The develop branch also contains a working implementation, with the addition that some features are still experimental.
- Active issues are implemented in the feature branches, be it bug fixes, improvements, clean code or documentation.

A separate feature branch is created for each issue. To minimise conflict, only one team member should be working on a branch. 
When a implementation in a feature branch is finished, the assigned team member creates a pull request to merge his branch into the develop branch. 
Another team member, will review this pull request, if its approved the feature will be merged into the develop branch.

### Work separation and assignment of tasks
For the project, we divided the tasks by classes. Each team member was responsible for specific class.
The classes were divided so that the team members could work mostly independent of each other.

### Test strategy
Regarding testing, our focus was on three classes, as they were integral to the program and comprised a significant amount of logic and calculations from other classes.
- CostCalculator.java 
- FileHandler.java/JsonHandler.java
- SolarCalculator <br>

With regard to other classes that are dependent on this one, we also conducted tests but placed less emphasis on them.

### Kanban board

We used the [kanban board](https://github.zhaw.ch/PM2-IT23aWIN-fame-wahl-kars/team4-RTFM-projekt2-electriscan/projects/1) in GitHub to display the status of the tickets.

### Drafting
Every time a team member started working on a new feature, he created a draft pull request.
This way the team could see what the team member was working on and could give feedback.
When the feature was finished, the draft was converted to a pull request and the team could review the code.
This way the team could ensure that the code was reviewed by at least one other team member before merging it into the develop branch.<br>
[Example 1](https://github.zhaw.ch/PM2-IT23aWIN-fame-wahl-kars/team4-RTFM-projekt2-electriscan/pull/75)<br>
[Example 2](https://github.zhaw.ch/PM2-IT23aWIN-fame-wahl-kars/team4-RTFM-projekt2-electriscan/pull/96)
