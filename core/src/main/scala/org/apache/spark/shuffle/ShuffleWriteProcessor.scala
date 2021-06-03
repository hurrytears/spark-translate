/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.shuffle

import org.apache.spark.{Partition, ShuffleDependency, SparkEnv, TaskContext}
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.scheduler.MapStatus
import org.apache.spark.util.Utils

/**
  * The interface for customizing shuffle write process. The driver create a ShuffleWriteProcessor
  * and put it into [[ShuffleDependency]], and executors use it in each ShuffleMapTask.
  */
private[spark] class ShuffleWriteProcessor extends Serializable with Logging {

    /**
      * Create a [[ShuffleWriteMetricsReporter]] from the task context. As the reporter is a
      * per-row operator, here need a careful consideration on performance.
      */
    protected def createMetricsReporter(context: TaskContext): ShuffleWriteMetricsReporter = {
        context.taskMetrics().shuffleWriteMetrics
    }

    /**
      * The write process for particular partition, it controls the life circle of [[ShuffleWriter]]
      * get from [[ShuffleManager]] and triggers rdd compute, finally return the [[MapStatus]] for
      * this task.
      */
    def write(
                 rdd: RDD[_],
                 dep: ShuffleDependency[_, _, _],
                 mapId: Long,
                 context: TaskContext,
                 partition: Partition): MapStatus = {
        var writer: ShuffleWriter[Any, Any] = null
        try {
            val manager = SparkEnv.get.shuffleManager
            writer = manager.getWriter[Any, Any](
                dep.shuffleHandle,
                mapId,
                context,
                createMetricsReporter(context))

            /**
              * 最最重要的一行代码就在这里
              * 首先调用了RDD的iterator()方法，并且传入了，当前task要处理哪个partition
              * 所以，核心的逻辑，就在RDD的iterator()方法中，在这里，就实现了针对rdd的某个partiton,执行我们自己定义的
              * 算子，或者是函数
              * 执行完了我们定义的算子，或者函数，是不是相当于是，针对rdd的partition执行了处理，那么，是不是会有返回
              * 的数据？？、
              * OK，返回的数据，都是通过shuffleWriter,经过hashPartitioner进行分区之后，写入自己对应的分区bucket
              */
            writer.write(
                rdd.iterator(partition, context).asInstanceOf[Iterator[_ <: Product2[Any, Any]]])
            /**
              * 最后，返回结果是，MapStatus
              * MapStatus里面封装了ShuffleMapTask计算后的数据，存储在哪里，其实就是BlockManager相关的信息
              * blockManager,是Spark底层的内存、数据、磁盘数据管理的组件
              * 讲完shuffle之后，我们就来剖析BlockManager
              */
            val mapStatus = writer.stop(success = true)
            if (mapStatus.isDefined) {
                // Initiate shuffle push process if push based shuffle is enabled
                // The map task only takes care of converting the shuffle data file into multiple
                // block push requests. It delegates pushing the blocks to a different thread-pool -
                // ShuffleBlockPusher.BLOCK_PUSHER_POOL.
                if (Utils.isPushBasedShuffleEnabled(SparkEnv.get.conf) && dep.getMergerLocs.nonEmpty) {
                    manager.shuffleBlockResolver match {
                        case resolver: IndexShuffleBlockResolver =>
                            val dataFile = resolver.getDataFile(dep.shuffleId, mapId)
                            new ShuffleBlockPusher(SparkEnv.get.conf)
                                .initiateBlockPush(dataFile, writer.getPartitionLengths(), dep, partition.index)
                        case _ =>
                    }
                }
            }
            mapStatus.get
        } catch {
            case e: Exception =>
                try {
                    if (writer != null) {
                        writer.stop(success = false)
                    }
                } catch {
                    case e: Exception =>
                        log.debug("Could not stop writer", e)
                }
                throw e
        }
    }
}
