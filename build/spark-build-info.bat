#!/usr/bin/env bash


                  <arg line="${project.basedir}/../build/spark-build-info.bat"/>
                  <arg line="${project.build.directory}/extra-resources"/>
                  <arg line="${project.version}"/>

:: This script generates the build info for spark and places it into the spark-version-info.properties file.
:: Arguments:
::   build_tgt_directory - The target directory where properties file would be created. [./core/target/extra-resources]
::   spark_version - The current version of spark
@echo off
set RESOURCE_DIR=%1
mkdir %RESOURCE_DIR%
set SPARK_BUILD_INFO=%RESOURCE_DIR%/spark-version-info.properties
CALL :echo_build_properties %2 > %SPARK_BUILD_INFO%

:echo_build_properties
@echo version=%2
@echo user=sosog
@echo revision=94e0822f5539eec2c7b581b9898a6a83405117d2
@echo branch=master
@echo date=2021-04-2116:15:14
@echo url=git@gitee.com:sosog_zhang/spark.git
goto:eof
