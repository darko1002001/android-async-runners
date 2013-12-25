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

package com.araneaapps.android.libs.asyncrunners.services;

import android.content.Intent;
import com.araneaapps.android.libs.asyncrunners.models.RequestOptions;
import com.araneaapps.android.libs.asyncrunners.models.TaskDecorator;
import com.araneaapps.android.libs.asyncrunners.models.TaskStore;
import com.araneaapps.android.libs.logger.ALog;

public class ExecutorService extends BaseObservableThreadPoolServiceService {

  @Override
  public void handleIntent(Intent intent) {
    TaskStore taskStore = TaskStore.get(getApplicationContext());
      TaskDecorator task = taskStore.poll();
      if (task == null) {
        ALog.d(TAG, TaskDecorator.class.getSimpleName() + " is null");
        return;
      }
      RequestOptions options = task.getOptions();
      WorkerThread worker = new WorkerThread(options.getPriority(), task.getRunnable());
      ALog.d("Pool id " + task.getRunnable().toString());
      if (options.shouldRunInSingleThread() == false) {
        getFixedSizePoolExecutor().execute(
            worker);
      } else {
        // Handle according to options
        getSingleThreadExecutorService().execute(
            worker);
      }
    }
}
