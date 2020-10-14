// Copyright 2019 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import io.flutter.plugin.common.EventChannel;

class StreamHandlerImpl implements EventChannel.StreamHandler {

  private SensorEventListener sensorEventListener;
  private final SensorManager sensorManager;
  private final Sensor sensor;

  StreamHandlerImpl(SensorManager sensorManager, int sensorType) {
    this.sensorManager = sensorManager;
    sensor = sensorManager.getDefaultSensor(sensorType);
  }

  @Override
  public void onListen(Object arguments, EventChannel.EventSink events) {
    sensorEventListener = createSensorEventListener(events);
    int delay;
    if(arguments != null){
      delay = (int)arguments;
    }else{
      delay = SensorManager.SENSOR_DELAY_NORMAL;
    }
    assert arguments != null;
    sensorManager.registerListener(sensorEventListener, sensor, delay);
  }

  @Override
  public void onCancel(Object arguments) {
    sensorManager.unregisterListener(sensorEventListener);
  }

  SensorEventListener createSensorEventListener(final EventChannel.EventSink events) {
    return new SensorEventListener() {
      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {}

      @Override
      public void onSensorChanged(SensorEvent event) {
        double[] sensorValues = new double[event.values.length + 2];
        for (int i = 0; i < event.values.length; i++) {
          sensorValues[i] = event.values[i];
        }
        sensorValues[event.values.length] = event.accuracy;
        sensorValues[event.values.length + 1] = (double)event.timestamp;
        events.success(sensorValues);
      }
    };
  }
}
