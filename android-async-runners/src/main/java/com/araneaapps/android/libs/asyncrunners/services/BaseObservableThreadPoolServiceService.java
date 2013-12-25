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

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import com.araneaapps.android.libs.logger.ALog;

import java.util.concurrent.TimeUnit;

/**
 * @author darko.grozdanovski
 */
public abstract class BaseObservableThreadPoolServiceService extends
    BaseThreadPoolService {

  public static final String TAG = BaseObservableThreadPoolServiceService.class
      .getSimpleName();

  /**
   * Use to shut down the service when done, register the worker when its
   * started, unregister when its completed
   */
  public ThreadCountObserver observer = new ThreadCountObserver() {

    private final Handler shutdownHandler = new Handler();
    private final Runnable runnable = new Runnable() {

      @Override
      public void run() {
        ALog.d("Shutting down " + TAG);
        stopSelf();
      }
    };

    @Override
    public void onThreadsFinished() {
      shutdownHandler.postDelayed(runnable, TimeUnit.SECONDS.toMillis(60L));
    }

    @Override
    public void newRunnableRegistered() {
      shutdownHandler.removeCallbacks(runnable);
    }
  };

  @Override
  public IBinder onBind(final Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }

  /**
   * Used to submit prioritized tasks to the Queue for the file download
   *
   * @author darko.grozdanovski
   */
  public class WorkerThread implements Runnable {

    private Runnable runnable;

    public WorkerThread(final Runnable runnable) {
      this.runnable = runnable;
    }

    @Override
    public void run() {
      observer.registerRunnable(this);
      runnable.run();
      observer.unregisterRunnable(this);
    }
  }
}
