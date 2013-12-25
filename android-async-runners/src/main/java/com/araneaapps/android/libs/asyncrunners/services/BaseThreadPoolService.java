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
import com.araneaapps.android.libs.asyncrunners.enums.DownloadPriority;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** @author darko.grozdanovski */
public abstract class BaseThreadPoolService extends Service {

  /** the number of objects that will be executed simultaniously */
  private static final int CORE_POOL_SIZE = 3;

  public static final String TAG = BaseThreadPoolService.class.getSimpleName();

  private ExecutorService fixedSizePoolExecutor;
  private ExecutorService singleThreadExecutorService;

  @Override
  public void onDestroy() {
    safelyShutdownService(fixedSizePoolExecutor);
    safelyShutdownService(singleThreadExecutorService);
  }

  private void safelyShutdownService(ExecutorService service) {
    try {
      service.shutdown();
    } catch (Exception e) {
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
    @SuppressWarnings("unchecked")
    final PriorityBlockingQueue<Runnable> queue = new PriorityBlockingQueue<Runnable>(10,
        new ComparePriority());
    fixedSizePoolExecutor = new ThreadPoolExecutor(getCorePoolSize(),
        getCorePoolSize(),
        50L,
        TimeUnit.SECONDS,
        queue);

    @SuppressWarnings("unchecked")
    final PriorityBlockingQueue<Runnable> singleThreadQueue = new PriorityBlockingQueue<Runnable>(
        10, new ComparePriority());
    singleThreadExecutorService = new ThreadPoolExecutor(1, 1, 50L, TimeUnit.SECONDS,
        singleThreadQueue);
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

  public ExecutorService getFixedSizePoolExecutor() {
    return fixedSizePoolExecutor;
  }

  public ExecutorService getSingleThreadExecutorService() {
    return singleThreadExecutorService;
  }

  /**
   * @author darko.grozdanovski
   * @param <T>
   */
  private static class ComparePriority<T extends WorkerPriority> implements Comparator<T> {

    @Override
    public int compare(final T o1, final T o2) {
      return Integer.valueOf((o1.getPriority().ordinal())).compareTo(
          Integer.valueOf(o2.getPriority().ordinal()));
    }
  }

  /**
   * Implement in the worker to be able to prioritize the execution
   * 
   * @author darko.grozdanovski
   */
  public interface WorkerPriority {

    public DownloadPriority getPriority();
  }
}
