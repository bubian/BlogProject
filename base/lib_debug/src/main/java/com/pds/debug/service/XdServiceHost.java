package com.pds.debug.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class XdServiceHost extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
