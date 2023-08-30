# Stat Filters

Status: v0.2  

Java code to apply mathematical functions on images and visualise statistics.  

This tool was designed around Jupyter Notebooks to test interactively different image processing operations and understand how they affect the image.

![statFilters](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/98437bc3-1d31-418e-9d38-19d8a08456bc)


## Overview

An image can be divided into tiles using ```createTiles(rows, columns)``` or alternatively ```createTiles(pixelsize)``` where pixel size is given in this format "3x3", "16x16","32x32" 

![divide](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/125bb558-ae32-4dc2-8fd6-1e8f363d6894)


Each tile can be selected either by ID or (row,column) coordinates  and local statistics can be displayed.

![get1](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/efdc9fb4-da18-4734-9c0f-0cbe9d84a3e2)


Tile IDs can be sorted by different metrics: ```red```,```green```,```blue``` ```mean```, ```std.dev```, ```entropy```...

```
Boolean ascending = true;
f.sortTilesBy("std.dev", ascending);
```
```
{6,19,16,17,10,2,8,23,18,3,11,9,22,1,13,24,12,14,4,5,7,21,0,15,20,}
```

These same metrics can be applied as "filters" using ```applyOperation(operation, subset)``` specifying a subset of tiles. 

Example: keeping only the ```red``` channel in those tiles that have the highest standard deviation

![sort](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/427c4476-afdd-41ac-8f7b-0fb300ea3da5)

Or apply a ```mean```  to those tiles that have the highest amount of green

```
Boolean ascending = true;
f.sortTilesBy("green",ascending);
```

```
{24,19,0,1,15,23,14,20,5,21,22,10,3,4,18,9,16,2,6,17,11,8,13,12,7,}
```

![green](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/f4a4c286-77c5-4aca-8d84-f668b5af4bde)

If subset is not specified the opration is applied to the entire image. 

Example: Divide the image in "5x5" tiles and apply the standard deviation to the entire image

![1000](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/1edfd412-d021-4ab0-b1ce-cddd08d7966c)

This helped me in visually testing Java code for processing and transform images, following an interactive process similar to python.



## Tutorial

### Step 1
Install [IJava](https://github.com/SpencerPark/IJava) a Jupyer kernel for executing Java code developed by [SpencerPark](https://github.com/SpencerPark)

### Step 2
Download the latest version of the jar file and save it in your project folder

### Step 3
Open a new Notebook in the same folder where the jar file is and execute this code in the first cell:

```
%jars ./StatFilters.jar
import filters.*;
import java.awt.image.BufferedImage;
```






