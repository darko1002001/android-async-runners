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

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

public class ComparableFutureTask<T> extends FutureTask<T>
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
