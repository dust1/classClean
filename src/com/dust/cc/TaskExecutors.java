package com.dust.cc;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用于执行任务的线程池
 */
public class TaskExecutors {

    private Set<String> referenceSets;

    private LinkedBlockingQueue<ClassTaskFunction> taskQueue;

    private List<Worker> workers;

    private volatile boolean working = true;

    public static TaskExecutors create(int poolSize, int queueSize) {
        return new TaskExecutors(poolSize, queueSize);
    }

    private TaskExecutors(int poolSize, int queueSize) {
        this.taskQueue = new LinkedBlockingQueue<>(queueSize);
        this.workers = Collections.synchronizedList(new LinkedList<>());

        this.referenceSets = new ConcurrentSkipListSet<>();

        initWork(poolSize);
    }

    private void initWork(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            Worker w = new Worker(this);
            this.workers.add(w);
            w.start();
        }
    }

    public void submit(ClassTaskFunction task) {
        this.taskQueue.offer(task);
    }

    public void shutDown() {
        if (this.working) {
            this.working = false;

            for (Thread t : workers) {
                if (t.getState().equals(Thread.State.BLOCKED)
                    || t.getState().equals(Thread.State.WAITING)) {
                    t.interrupt();
                }
            }
        }
    }

    public Set<String> getCleanList() {
        return referenceSets;
    }

    public void addClean(String path) {
        referenceSets.add(path);
    }

    public void addCleans(List<String> path) {
        referenceSets.addAll(path);
    }

    /**
     * 工作县城
     */
    static class Worker extends Thread {

        private TaskExecutors pool;

        public Worker(TaskExecutors executors) {
            super();
            this.pool = executors;
        }

        @Override
        public void run() {
            while (this.pool.working || this.pool.taskQueue.size() > 0) {
                ClassTaskFunction task = null;
                try {
                    if (this.pool.working) {
                        //如果线程池还在进行，且队列为空，则阻塞
                        task = this.pool.taskQueue.take();
                    } else {
                        //否则返null
                        task = this.pool.taskQueue.poll();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (task != null) {
                    try {
                        List<String> result = task.run();
                        this.pool.addCleans(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
