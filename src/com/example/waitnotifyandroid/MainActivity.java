package com.example.waitnotifyandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	MonitorObject mSync = new MonitorObject();
	Thread t;
	int threadNameCounter = 0;
	Runnable work;
	Handler handler = new Handler();
	Button btnGoNextActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnGoNextActivity = (Button) findViewById(R.id.btnGoNextActivity);
		btnGoNextActivity.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this, NextActivity.class));
			}
		});

		work = new Runnable() {
			boolean myRunning;

			@Override
			public void run() {
				// TODO Auto-generated method stub
				synchronized (mSync) {
					myRunning = mSync.running;
				}
				while (myRunning) {

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							synchronized (mSync) {
								if (mSync.mustBePost) {
									Toast.makeText(MainActivity.this,
											mSync.message, Toast.LENGTH_SHORT)
											.show();
									mSync.mustBePost = false;
									if (mSync.message
											.equals("Main Activity is going to pause")) {
										mSync.running = false;
									}
								}
								Toast.makeText(getApplicationContext(),
										"Thread is running", Toast.LENGTH_SHORT)
										.show();

							}
						}
					});

					synchronized (mSync) {
						myRunning = mSync.running;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		t = new Thread(work, "My name is " + String.valueOf(threadNameCounter));
		t.start();
		// handler.postDelayed(work, 0);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		synchronized (mSync) {
			mSync.mustBePost = true;
			mSync.message = "Main Activity is going to pause";
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		threadNameCounter++;
		synchronized (mSync) {
			mSync.running = true;
			mSync.mustBePost = true;
			mSync.message = "Main Activity is going to resume";
		}
		t = new Thread(work, "My name is " + String.valueOf(threadNameCounter));
		t.start();

	}

	public class MonitorObject {
		public boolean running = true;
		public String message = "";
		public boolean mustBePost = true;
	}

}
