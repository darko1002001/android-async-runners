package com.example.asyncrunners;

import android.app.Activity;
import android.os.Bundle;
import com.araneaapps.android.libs.asyncrunners.models.TaskStore;
import com.araneaapps.android.libs.logger.ALog;

public class SimpleActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);
    for(int i= 0; i< 50; i++ ){
      TaskStore.get(this).queue(new MyRunnable(i));
    }
  }

  private static class MyRunnable implements Runnable {
    private final int i;

    public MyRunnable(int i) {
      this.i = i;
    }

    @Override
    public void run() {
      ALog.e("Task " + i);
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
      }
    }

    @Override
    public String toString() {
      return "Order " + this.i;
    }
  }
}
