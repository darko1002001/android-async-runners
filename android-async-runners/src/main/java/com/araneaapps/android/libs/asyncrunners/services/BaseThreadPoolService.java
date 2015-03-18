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
import android.util.Log;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author darko.grozdanovski
 */
public abstract class BaseThreadPoolService extends Service {

  public static final String TAG = BaseThreadPoolService.class.getSimpleName();
  /**
   * the number of objects that will be executed simultaniously
   */
  private static final int CORE_POOL_SIZE = 3;
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
      Log.v(TAG, e.getMessage());
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


}
