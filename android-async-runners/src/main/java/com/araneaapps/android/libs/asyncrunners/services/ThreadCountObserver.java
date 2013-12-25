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

import com.araneaapps.android.libs.logger.ALog;

import java.util.LinkedList;

/** @author darko.grozdanovski */
public abstract class ThreadCountObserver {

  public static final String TAG = ThreadCountObserver.class.getSimpleName();

  public abstract void onThreadsFinished();

  public abstract void newRunnableRegistered();

  LinkedList<Runnable> observerList = new LinkedList<Runnable>();

  public void registerRunnable(final Runnable r) {
    synchronized (observerList) {
      observerList.add(r);
    }
    newRunnableRegistered();
    ALog.d(TAG, "observer list size on register: " + observerList.size());
  }

  public void unregisterRunnable(final Runnable r) {
    synchronized (observerList) {
      observerList.remove(r);
    }
    ALog.d(TAG, "observer list size on unregister: " + observerList.size());
    if (observerList.size() == 0) {
      ALog.d(TAG, "observer calling finish method");
      onThreadsFinished();
    }
  }
}
