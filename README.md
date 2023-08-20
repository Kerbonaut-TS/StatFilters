# Stat Filters

Status: WIP  

Java code to apply mathematical functions on Images and visualise statistics.  

This tool was designed around Jupyter Notebooks to interactively test image processing operations and understand how they affect the image.

![statFilters](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/98437bc3-1d31-418e-9d38-19d8a08456bc)


## Overview

This is a brief overview of the features developed so far.

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

 divide the image in 1000 tiles and apply the standard deviation to the entire image


![1000](https://github.com/Kerbonaut-TS/StatFilters/assets/122178043/bcc7075a-21ac-4b7c-b3a6-e917f9949b8c)


This helped me to visually debug and test Java code for processing and transform images in a python-like style.



### Tutorial
WIP


