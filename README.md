# Traffic Junction Simulator

## Overview
This project revolves around the development of an application tht models real-life traffic junctions with various
adjustable parameters that can simulate real life traffic.

## Installation
### Ubuntu/Debian
Download the debian package and navigate on a terminal to the directory where it is located.
```bash 
sudo dpkg -i trafficsim_1.0_amd64.deb
```

Run the executable

```bash 
/opt/trafficsim/bin/TrafficSim
```
### Windows
Run the exe executable and follow the instructions presented.

Find the application in ``C:/Program Files/TrafficSim``
### MacOS
- Run the dmg executable and drag TrafficSim to Applications
- Navigate to the applications list in Finder to run the application 

## Usage

Run the jar file by following the installation instructions

### Simulation Tab
- Input Number of lanes for each junction
- Specify if a bus lane should exist (by default bus lanes are the leftmost lanes)
- Determine the Vehicles per Hour (VPH) for each outbound direction
- Specify if pedestrians will be crossing, for how long, and at what rate
- Results tab indicate the average and maximum wait time and max queue length for each outbound direction
- Each junction configuration is also given a unique score denoting the junction quality
- Generate more simulations through the simulations tab
- Simulations can be renamed

### Metrics Tab
- After running at least one simulation, the results are displayed as bar charts
- Simulation names are used as labels in the bar chart