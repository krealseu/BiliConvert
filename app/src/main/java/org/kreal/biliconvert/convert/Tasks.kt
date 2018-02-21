package org.kreal.biliconvert.convert

import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by lthee on 2018/1/26.
 * 任务
 */
abstract class Tasks<T> {
    private val taskQueue: LinkedBlockingQueue<T> = LinkedBlockingQueue()
    private val callBackTable: Hashtable<T, CallBack<T>> = Hashtable()
    private val service: ExecutorService = Executors.newSingleThreadExecutor()
    /**
     * true working ; false
     */
    private var serviceState: Boolean = false

    private var workTask: T? = null

    open fun submit(task: T, callBack: CallBack<T> = defaultCallBack) {
        taskQueue.put(task)
        callBackTable[task] = callBack
        if (!serviceState)
            execute()
    }

    fun remove(task: T) =
            when (taskQueue.remove(task)) {
                false -> false
                true -> {
                    callBackTable.remove(task)
                    true
                }
            }


    fun clear() {
        taskQueue.clear()
        callBackTable.clear()
    }

    fun list() = taskQueue.toList()

    /**
     * @param  task state in task
     * @return -1 no in task, 0 in taskQueue and wait deal, 1 is dealing
     */
    fun getState(task: T) =
            when (task) {
                workTask -> 1
                else -> if (taskQueue.contains(task)) 0 else -1
            }

    abstract fun dealTask(task: T): Int

    /**
     * convert chapter
     */
    private fun execute() {
        service.submit {
            while (true) {
                workTask = taskQueue.poll()
                workTask?.also {
                    serviceState = true
                    val callBack = callBackTable[it]
                    callBack?.done(it, dealTask(it))
                } ?: break
            }
            workTask = null
            serviceState = false
        }
    }

    val defaultCallBack: CallBack<T> = object : CallBack<T> {
        override fun done(task: T, result: Int) {
        }
    }

    interface CallBack<T> {
        fun done(task: T, result: Int)
    }
}
