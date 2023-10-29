# Stat Filters

Status: v0.3  

![statFilters](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/98437bc3-1d31-418e-9d38-19d8a08456bc)

Java code to apply mathematical operations to images and visualise statistics.  
This tool was designed around Jupyter Notebooks to test interactively different sequences of image processing operations and understand how they affect the image.


## Overview
An image can be divided into tiles using ```createTiles(rows, columns)``` or alternatively ```createTiles(pixelsize)``` where pixel size is given in this format "3x3", "16x16","32x32" 

![divide](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/125bb558-ae32-4dc2-8fd6-1e8f363d6894)


Each tile can be selected either by ID or  coordinates (row,column) and local statistics can be displayed.

![get1](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/efdc9fb4-da18-4734-9c0f-0cbe9d84a3e2)

```[{ "img":"green.json", "Rank":0, "Y":0, "X":0, "height":5, "width":5, "avg.green":107, "avg.red":0, "saturation":100.0, "hue":24.0, "entropy":4.053660689688185, "brightness":41.0, "std.dev":5.044759657307769, "avg.blue":0, "mean":35.0}] ```

Tiles can be sorted by metrics:
```
Boolean ascending = true;
filter.sortTilesBy("std.dev", ascending);
```
which returns:
```
{6,19,16,17,10,2,8,23,18,3,11,9,22,1,13,24,12,14,4,5,7,21,0,15,20,}
```

Some of these metrics can be applied as "operation filters" using 

    filter.applyOperation(operation, subset) 
    
![operations](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/ff41ab45-225a-4ece-b957-23fa19d46b30)
This is a list of all the operations available: 
``` ["mean", "std.dev","log","sqrt","entropy","sobel","red","green", "blue"]   ```


An operatation can be applied only to specific tiles passing an array of IDs in  the parameter subset.  
If subset of files is not specified the opration is applied to the entire image. 

![sort](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/427c4476-afdd-41ac-8f7b-0fb300ea3da5)



## How to run on a Jupyter Notebook 

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






