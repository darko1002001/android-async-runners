package com.example.asyncrunners;

import android.app.Application;

import com.araneaapps.android.libs.asyncrunners.models.AsyncRunners;

public class SimpleApp extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    AsyncRunners.init(this);
  }
}
