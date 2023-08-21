# Stat Filters

Status: WIP  

Java code to apply mathematical functions on Images and visualise statistics.  

This tool was designed around Jupyter Notebooks to interactively test image processing operations and understand how they affect the image.

![statFilters](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/98437bc3-1d31-418e-9d38-19d8a08456bc)


## Overview

An image can be divided into tiles

![divide](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/88b69a8b-f439-4c40-9c45-8b2f131f6279)

Each tile can be selected by ID and local statistics can be displayed.



![get1](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/fa3c985d-8cb8-4848-9314-ec9b7400f08e)

Tiles can be sorted by different metrics: ```red```,```green```,```blue``` ```mean```, ```std.dev```, ```entropy```...

```
Boolean ascending = true;
f.sortTilesBy("std.dev", ascending);
```
```
{6,19,16,17,10,2,8,23,18,3,11,9,22,1,13,24,12,14,4,5,7,21,0,15,20,}
```

These same metrics can be used as "filters" and applied to a subset of Tiles. 

For instance, keeping only the ```Red``` channel in those tiles that have the highest standard deviation

![sort](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/9fa7a848-882a-4c1f-b673-5a6217918ace)


Or apply a ```mean```  to those tiles that have the highest amount of green

```
Boolean ascending = true;
f.sortTilesBy("green",ascending);
```

```
{24,19,0,1,15,23,14,20,5,21,22,10,3,4,18,9,16,2,6,17,11,8,13,12,7,}
```

![green](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/f4a4c286-77c5-4aca-8d84-f668b5af4bde)


This is useful when done at scale, for instance:

Divide the image in 1000 tiles and apply the standard deviation to the entire image

![1000](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/1edfd412-d021-4ab0-b1ce-cddd08d7966c)


This helped me to visually test Java code for processing and transform images, using an interactive process similar to python.



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






