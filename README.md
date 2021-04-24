# Apache Spark

Spark是一个统一的大数据处理引擎，它提供了多种语言的高级API，以及一个支持大部分
图计算的优化引擎。它同样支持丰富的高级工具，如用于结构化数据的spark SQL、
用于机器学习的MLlib、和流处理的Structured Streaming。

<https://spark.apache.org/>

[![Jenkins Build](https://amplab.cs.berkeley.edu/jenkins/job/spark-master-test-sbt-hadoop-3.2/badge/icon)](https://amplab.cs.berkeley.edu/jenkins/job/spark-master-test-sbt-hadoop-3.2)
[![AppVeyor Build](https://img.shields.io/appveyor/ci/ApacheSoftwareFoundation/spark/master.svg?style=plastic&logo=appveyor)](https://ci.appveyor.com/project/ApacheSoftwareFoundation/spark)
[![PySpark Coverage](https://img.shields.io/badge/dynamic/xml.svg?label=pyspark%20coverage&url=https%3A%2F%2Fspark-test.github.io%2Fpyspark-coverage-site&query=%2Fhtml%2Fbody%2Fdiv%5B1%5D%2Fdiv%2Fh1%2Fspan&colorB=brightgreen&style=plastic)](https://spark-test.github.io/pyspark-coverage-site)


## 在线文档

你能找到最新的spark 文档，包括[编程指导](https://spark.apache.org/documentation.html)，
这个READ文件只包含基本的安装说明。

## Building Spark

spark基于 [Apache Maven](https://maven.apache.org/)构建，
要编译或者执行example,命令如下：

    ./build/mvn -DskipTests clean package

(如果下载的是编译完成的，就不需要执行了.)

更多细节请点击
["Building Spark"](https://spark.apache.org/docs/latest/building-spark.html).

开发小建议, 如使用 IDE, 点击 ["Useful Developer Tools"](https://spark.apache.org/developer-tools.html).

## Scala Shell 交互

最简单的使用方法是使用 Scala shell:

    ./bin/spark-shell

尝试下面的命令，正确的返回结果是 1,000,000,000:

    scala> spark.range(1000 * 1000 * 1000).count()

## Python Shell 交互

或者你喜欢python:

    ./bin/pyspark

执行下边的命令返回 1,000,000,000:

    >>> spark.range(1000 * 1000 * 1000).count()

## 示例程序

spark 同样提供了几个样例程序在根目录的example中，执行下边类似的命令就能跑：

    ./bin/run-example SparkPi

将会以本地模式跑Pi 3.141595653

你能自己设置master变量来决定你的程序在什么环境里执行。spark:// URL 提交到spark集群，
mesos:// 提交到mesos集群看，local就是本地跑，local[N]就是本地拿N个线程跑，
也能使用一个缩写的类名，如果这个类在example包下边的话。如下：

    MASTER=spark://host:7077 ./bin/run-example SparkPi

许多样例程序如果没给参数，会打印使用帮助。

## 运行测试

测试前要先编译，编译方法请点击 [building Spark](#building-spark). 测试命令如下：

    ./dev/run-tests

请看详细的命令
[模块测试，单独测试](https://spark.apache.org/developer-tools.html#individual-tests).

也有K8S的集成测试，查看resource-managers/kubernetes/integration-tests/README.md

## hadoop版本建议

编译的时候版本弄成匹配的，不然可能会有问题
请戳[如何在编译的时候指定hadoop版本并能yarn模式启动](https://spark.apache.org/docs/latest/building-spark.html#specifying-the-hadoop-version-and-enabling-yarn)

## 配置

戳 [配置指导](https://spark.apache.org/docs/latest/configuration.html)