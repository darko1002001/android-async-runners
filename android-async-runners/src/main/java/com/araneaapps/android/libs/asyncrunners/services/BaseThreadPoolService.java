/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Aranea D.O.O. Skopje
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 *
 */
package com.araneaapps.android.libs.asyncrunners.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.araneaapps.android.libs.logger.ALog;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author darko.grozdanovski
 */
public abstract class BaseThreadPoolService extends Service {

  /**
   * the number of objects that will be executed simultaniously
   */
  private static final int CORE_POOL_SIZE = 3;

  public static final String TAG = BaseThreadPoolService.class.getSimpleName();

  private PriorityExecutor fixedSizePoolExecutor;
  private PriorityExecutor singleThreadExecutorService;

  @Override
  public void onDestroy() {
    safelyShutdownService(fixedSizePoolExecutor);
    safelyShutdownService(singleThreadExecutorService);
  }

  private void safelyShutdownService(ThreadPoolExecutor service) {
    try {
      service.shutdown();
    } catch (Exception e) {
      ALog.v(e.getMessage());
    }
  }

  @Override
  public IBinder onBind(final Intent intent) {
    return null;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void onCreate() {
    super.onCreate();
    fixedSizePoolExecutor = PriorityExecutor.newFixedThreadPool(getCorePoolSize());
    singleThreadExecutorService = PriorityExecutor.newFixedThreadPool(1);
  }

  @Override
  public int onStartCommand(final Intent intent, final int flags, final int startId) {
    handleIntent(intent);
    return START_NOT_STICKY;
  }

  /**
   * This method should be implemented to handle the execution of the background
   * threads it runs in the UI thread, so don't do processor heavy operations
   */
  public abstract void handleIntent(Intent intent);

  protected int getCorePoolSize() {
    return CORE_POOL_SIZE;
  }

  public PriorityExecutor getFixedSizePoolExecutor() {
    return fixedSizePoolExecutor;
  }

  public PriorityExecutor getSingleThreadExecutorService() {
    return singleThreadExecutorService;
  }


  static class PriorityExecutor extends ThreadPoolExecutor {

    public PriorityExecutor(int corePoolSize, int maximumPoolSize,
                            long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
      super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }
    //Utitlity method to create thread pool easily

    public static PriorityExecutor newFixedThreadPool(int nThreads) {
      return new PriorityExecutor(nThreads, nThreads, 60L,
          TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>());
    }
    //Submit with New comparable task

    public Future<?> submit(Runnable task, int priority) {
      return super.submit(new ComparableFutureTask(task, null, priority));
    }
    //execute with New comparable task

    public void execute(Runnable command, int priority) {
      super.execute(new ComparableFutureTask(command, null, priority));
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
      return (RunnableFuture<T>) callable;
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
      return (RunnableFuture<T>) runnable;
    }
  }

  static class ComparableFutureTask<T> extends FutureTask<T>
      implements Comparable<ComparableFutureTask<T>> {

    volatile int priority = 0;
    static final AtomicLong SEQUENCE_GENERATOR = new AtomicLong(0);
    final long sequenceNumber;

    public ComparableFutureTask(Runnable runnable, T result, int priority) {
      super(runnable, result);
      this.priority = priority;
      sequenceNumber = SEQUENCE_GENERATOR.getAndIncrement();
    }

    public ComparableFutureTask(Callable<T> callable, int priority) {
      super(callable);
      this.priority = priority;
      sequenceNumber = SEQUENCE_GENERATOR.getAndIncrement();
    }

    @Override
    public int compareTo(ComparableFutureTask<T> o) {
      return o.priority > priority ? 1
          : (o.priority < priority ? -1
          : (sequenceNumber > o.sequenceNumber ? 1
          : (sequenceNumber < o.sequenceNumber ? -1 : 0)));
    }
  }
}
