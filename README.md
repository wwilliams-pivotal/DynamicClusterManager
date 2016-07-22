# Dynamic Server Management via gfsh

Creates server regions dynamically by invoking the same code that is used by GFSH.
This permits the regions to be stored into the cluster configuration service.

Requirements: You will need to bring up a locator on localhost[10334] before running. For convenience I have provided the grid subdirectory. Launch startall.sh in the grid subdirectory.

Warning: This uses internal gemfire calls. If the implementation changes in the future, consult Geode CommandResult for the upgraded code to use.
