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
    private val taskCallbackTable: Hashtable<T, TaskCallback<T>> = Hashtable()
    private val service: ExecutorService = Executors.newSingleThreadExecutor()
    /**
     * true working ; false
     */
    private var serviceState: Boolean = false

    private var workTask: T? = null

    open fun submit(task: T, taskCallback: TaskCallback<T> = defaultTaskCallback) {
        taskQueue.put(task)
        taskCallbackTable[task] = taskCallback
        if (!serviceState)
            execute()
    }

    /**
     * 移除Task
     */
    fun remove(task: T) =
            when (taskQueue.remove(task)) {
                false -> false
                true -> {
                    taskCallbackTable.remove(task)
                    true
                }
            }

    /**
     * 清除剩下的Task队列
     */
    fun clear() {
        taskQueue.clear()
        taskCallbackTable.clear()
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

    /**
     * convert chapter
     */
    private fun execute() {
        service.submit {
            while (true) {
                with(taskQueue.poll() ?: break) {
                    serviceState = true
                    val callBack = taskCallbackTable[this]
                    callBack?.onTaskCompleted(this, dealTask(this))
                    Unit
                }
            }
            workTask = null
            serviceState = false
        }
    }

    abstract fun dealTask(task: T): Int

    private val defaultTaskCallback: TaskCallback<T> = object : TaskCallback<T> {
        override fun onTaskCompleted(task: T, result: Int) {
        }
    }

    interface TaskCallback<in T> {
        fun onTaskCompleted(task: T, result: Int)
    }
}
