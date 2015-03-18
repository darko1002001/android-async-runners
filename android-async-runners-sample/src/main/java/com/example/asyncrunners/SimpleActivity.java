package com.example.asyncrunners;

import android.app.Activity;
import android.os.Bundle;

import com.araneaapps.android.libs.asyncrunners.enums.DownloadPriority;
import com.araneaapps.android.libs.asyncrunners.models.RequestOptions;
import com.araneaapps.android.libs.asyncrunners.models.TaskStore;

public class SimpleActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);

    for (int i = 1; i < 10; i++) {
      TaskStore.get(this).queue(new MyRunnable(i), new RequestOptions.RequestOptionsBuilder().
        setPriority(DownloadPriority.NORMAL).
        setRunInSingleThread(true).build());
    }

    for (int i = 10; i < 20; i++) {
      TaskStore.get(this).queue(new MyRunnable(i), new RequestOptions.RequestOptionsBuilder().
        setPriority(DownloadPriority.CRITICAL).
        setRunInSingleThread(true).build());
    }

    TaskStore.get(this).queue(new MyRunnable(1), new RequestOptions.RequestOptionsBuilder().
      setPriority(DownloadPriority.NORMAL).
      setRunInSingleThread(false).build());
  }

}
