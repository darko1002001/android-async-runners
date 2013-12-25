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

package com.araneaapps.android.libs.asyncrunners.models;

import android.content.Context;
import android.content.Intent;
import com.araneaapps.android.libs.asyncrunners.services.ExecutorService;
import com.araneaapps.android.libs.logger.ALog;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Darko.Grozdanovski
 */
public class TaskStore {

  public static final String TAG = TaskStore.class.getSimpleName();

  private static Queue<TaskDecorator> queue = new ConcurrentLinkedQueue<TaskDecorator>();
  private static Class<?> executorServiceClass = ExecutorService.class;
  private final Context context;
  private static TaskStore instance;

  private TaskStore(final Context context) {
    this.context = context;
  }

  public static TaskStore get(final Context context) {
    if (instance == null) {
      ALog.e("USE THE APPLICATION CLASS AND CALL " + TAG + ".init(context) to wire the singleton to the App Class loader");
      instance = new TaskStore(context.getApplicationContext());
    }
    return instance;
  }

  /*
   * Use this in the Application class to wire the singleton to the Application Classloader
   */
  public static void init(Context context){
    if (instance == null) {
      instance = new TaskStore(context.getApplicationContext());
    }
  }
  
  public static void setExecutorClass(final Class<?> executorServiceClass) {
    TaskStore.executorServiceClass = executorServiceClass;
  }

  private void addTask(final TaskDecorator task) {
    queue.add(task);
  }

  public void removeTask(final TaskDecorator task) {
    queue.remove(task);
  }

  public TaskDecorator poll(){
    return queue.poll();
  }

  public TaskDecorator peek(){
    return queue.peek();
  }

  public boolean hasTasks() {
    return queue.size() > 0;
  }

  public TaskDecorator queue(final Runnable runnable) {
    return queue(runnable, null);
  }

  public TaskDecorator queue(final Runnable runnable, RequestOptions options) {
    if (executorServiceClass == null) {
      throw new RuntimeException(
          "Initialize the Executor service class in a class extending application by calling " + AsyncRunners.class.getSimpleName() + ".init(context)");
    }
    final Intent service = new Intent(context, executorServiceClass);
    final TaskDecorator wrapper = new TaskDecorator(runnable, options);
    addTask(wrapper);
    context.startService(service);
    return wrapper;
  }

}
