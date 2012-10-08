/**
 * code from the book Arduino + Android Projects for the Evil Genius
 * <br>Copyright 2011 Simon Monk
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation (see COPYING).
 * 
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.caltech.app.duinodroid;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


import android.hardware.usb.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;


public class OpenAccessoryTest extends Activity implements Runnable{

  //private EditText mByteField;
  //private EditText mResponseField;
  //private Button mSendButton;

  private static final String TAG = "OpenAcc";
  private static final String ACTION_USB = "com.caltech.app.action.USB_ACTION";
  private PendingIntent mPermissionIntent;
  private boolean mPermissionRequestPending;
  private WaveformView waveformView;

  private UsbManager mUsbManager;
  private UsbAccessory mAccessory;
  private ParcelFileDescriptor mFileDescriptor;
  private FileInputStream mInputStream;
  private FileOutputStream mOutputStream;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    //mByteField = (EditText) findViewById(R.id.messagebyte);
    //mResponseField = (EditText) findViewById(R.id.arduinoresponse);
    waveformView = (WaveformView) findViewById(R.id.waveformView);
    //mSendButton = (Button) findViewById(R.id.sendButton);
    //mSendButton.setOnClickListener(new OnClickListener() {
    //  public void onClick(View v) {
        //sendMessageToArduino();
    //  }
    //});
    
    
	waveformView.postInvalidate();  	
    log("OnCreating");
    //Thread thread = new Thread(null, this, "OpenAccessoryTest");
    //thread.start();
    setupAccessory();
  }
  
//  public Object onRetainNonConfigurationInstance() {
//		if (mAccessory != null) {
//			return mAccessory;
//		} else {
//			return super.onRetainNonConfigurationInstance();
//		}
//	}
  
  //@Override
  public void onResume() {
    log("Resuming");
    super.onResume();

    log("mInputStream: " + mInputStream);
    log("mOutputStream: " + mOutputStream);
    if (mInputStream != null && mOutputStream != null) {
      log("Resuming: streams were not null");
    } else {
      log("Resuming: streams were null");
      establishPermissionsAndOpenAccessory();
    }
  }

  private void establishPermissionsAndOpenAccessory() {
    UsbAccessory[] accessories = mUsbManager.getAccessoryList();
    UsbAccessory accessory = (accessories == null ? null : accessories[0]);
    if (accessory != null) {
      if (mUsbManager.hasPermission(accessory)) {
        openAccessory(accessory);
      } else {
        log("In establishPermissionsAndOpenAccessory: accessory didnt have permission");
        synchronized (mUsbReceiver) {
          if (!mPermissionRequestPending) {
            mUsbManager.requestPermission(accessory, mPermissionIntent);
            mPermissionRequestPending = true;
          }
        }
      }
    } else {
      log("onResume:mAccessory is null");
    }
  }

  @Override
  public void onPause() {
    log("Pausing");
    super.onPause();
  }

  @Override
  public void onDestroy() {
    log("Destroying");
    unregisterReceiver(mUsbReceiver);
    super.onDestroy();
  }

  private void setupAccessory() {
    log("In setupAccessory");
    mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    log("In setupAccessory1");
    mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
        ACTION_USB), 0);
    log("In setupAccessory2");
    IntentFilter filter = new IntentFilter(ACTION_USB);
    log("In setupAccessory3");
    filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
    log("In setupAccessory4");
    registerReceiver(mUsbReceiver, filter);
    log("In setupAccessory5");
//    if (getLastNonConfigurationInstance() != null) {
//    	log("In setupAccessory6");	
//      mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
//      log("In setupAccessory7");
//      openAccessory(mAccessory);
//    }
  }

  private void openAccessory(UsbAccessory accessory) {
	Log.i(TAG, "*****************In Open Accessory*******************************");

    log("In openAccessory");
    mFileDescriptor = mUsbManager.openAccessory(accessory);
    if (mFileDescriptor != null) {
      mAccessory = accessory;
      FileDescriptor fd = mFileDescriptor.getFileDescriptor();
      mInputStream = new FileInputStream(fd);
      mOutputStream = new FileOutputStream(fd);
      Thread thread = new Thread(null, this, "OpenAccessoryTest");
      thread.start();
      alert("openAccessory: Accessory openned");
      log("Attached");
    } else {
      log("openAccessory: accessory open failed");
    }
  }

  private void closeAccessory() {
    log("In closeAccessory");
    try {
      if (mFileDescriptor != null) {
        mFileDescriptor.close();
      }
    } catch (IOException e) {
    } finally {
      mFileDescriptor = null;
      mAccessory = null;
      mInputStream = null; // srm
      mOutputStream = null; // srm
    }
  }

  public void run1() {
	  log("On Running");
//	  Timer timer1 = new Timer();
//	  timer1.scheduleAtFixedRate(new Task(5), 500, 1000);
	  int i = 0;
	  while (true) {
		  
	  
	  float sensorValues[] = new float[15];
		sensorValues[0] = (float)(1.0 + i * 0.1);
		sensorValues[1] = (float) 273.0 + i;
		sensorValues[2] = (float) 98506.0 + i;
		//sensorValues[3] = (float) 42.5;
		sensorValues[3] = (float) 3 + i;
		sensorValues[4] = (float) 36.0 + i;
		sensorValues[5] = (float) 0.20167;
		sensorValues[6] = (float) 1.021875;
		sensorValues[7] = (float) 49.0;
		sensorValues[8] = (float) 51.0;
		sensorValues[9] = (float) 136.0;
		sensorValues[10] = (float) 138.0;
		sensorValues[11] = (float) 161.0;
		sensorValues[12] = (float) 14629.0;
		ValueMsg sensorValueMsg = new ValueMsg(sensorValues);
		ArrayList<ValueMsg> sensorValueMsgs = new ArrayList<ValueMsg> ();
		sensorValueMsgs.add(sensorValueMsg);
		
		Log.i(TAG, "in running sensor value msg size: " + sensorValueMsgs.size());
		waveformView.addBatchData(sensorValueMsgs);
		i++;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
  }
  
  public void run() {
		// TODO Auto-generated method stub
		//Use this method to convert the bytes of data obtained from Arduino into different datatypes
		
		int ret = 0, intbits = 0;
		byte[] buffer = new byte[16384];
		ArrayList<ValueMsg> sensorValueMsgs = new ArrayList<ValueMsg> ();
		Log.i(TAG, "*****************In Running*******************************");
		while (true) { // keep reading messages forever. There are prob lots of messages in the buffer, each 4 bytes
			Log.i(TAG, "***********Before Reading*************************");
			try {
				ret = mInputStream.read(buffer);				
			} catch (IOException e) {
				Log.i(TAG, "*****************In IO Exception*******************************");
				break;
			}

			Log.i(TAG, "*****************In while********* ret: " + ret);
			float sensorValues[] = new float[15];
			//Checks if it recieves the 4 bytes of data and converts it back into into using bit wise operation
			if(ret == 60){
				for (int i = 0; i < 60; i+= 4) {
					intbits = (buffer[i+3] << 24) | ((buffer[i+2] & 0xff) << 16) | ((buffer[i+1] & 0xff) << 8) | (buffer[i] & 0xff);
					float t = Float.intBitsToFloat(intbits); //This displays the standard deviation values on the TextView using a separate thread
					sensorValues[(int)(i/4)] = t;
					Log.i(TAG, "float: " + t);
				}
			}
			ValueMsg sensorValueMsg = new ValueMsg(sensorValues);
			sensorValueMsgs.add(sensorValueMsg);
			if (sensorValueMsgs.size() == 2) {
				waveformView.addBatchData(sensorValueMsgs);
				sensorValueMsgs.clear();
			}
			Log.i(TAG, "**********End of while");
		}
	}

//  public void sendMessageToArduino() {
//    String valueStr = mByteField.getText().toString();
//    byte val;
//    try {
//      val = Byte.parseByte(valueStr);
//      log("Sending to Arduino: " + val);
//      sendCommand(val);
//    } catch (NumberFormatException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//      alert("The Byte should be a number between 0 and 255");
//    }
//
//  }
//
//  public void sendCommand(byte value) {
//    byte[] buffer = new byte[1];
//    buffer[0] = (byte) value;
//    if (mOutputStream != null) {
//      try {
//        mOutputStream.write(buffer);
//      } catch (IOException e) {
//        log("Send failed: " + e.getMessage());
//      }
//    } else {
//      log("Send failed: mOutStream was null");
//    }
//  }

  private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
	@Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      log("here in mUsbReceiver action: " + action);
      if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
        //if (accessory != null && accessory.equals(mAccessory)) {
          log("Detached");
          closeAccessory();
        //}
      }
      log("here after detached");
      log("action: " + action);
      if (ACTION_USB.equals(action)) {
    	  log("open accessory");
    	  mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    	  mAccessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
    	  if (mAccessory != null) {
    		  log("here after detaching at opening");
    		  openAccessory(mAccessory);
    		  
    	  }
    		  
      }
    }
  };

  private void log(String string) {
    //String contents = mResponseField.getText().toString();
    //mResponseField.setText(string + "\n" + contents);
  }

  public void alert(String message) {
    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle("Alert");
    alertDialog.setMessage(message);
    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        return;
      }
    });
    alertDialog.show();
  }
  
  private class Task extends TimerTask {
	  private float num;
	  public Task(float n) {
		  log("Inside timer construct");
		  num = n;
	  }
	  
	  public void run() {
		  log("inside timer run");
			float sensorValues[] = new float[15];
			sensorValues[0] = (float) 1.0;
			sensorValues[1] = (float) 2.0;
			sensorValues[2] = (float) 3.0;
			sensorValues[3] = (float) 4.0;
			sensorValues[4] = (float) 5.0;
			sensorValues[5] = (float) 6.0;
			ValueMsg sensorValueMsg = new ValueMsg(sensorValues);
			ArrayList<ValueMsg> sensorValueMsgs = new ArrayList<ValueMsg> ();
			sensorValueMsgs.add(sensorValueMsg);
			waveformView.addBatchData(sensorValueMsgs);
	  }
  }
}