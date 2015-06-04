package com.veddis.rishabh.testapp;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;

public class MyFaceService extends Service {
    public MyFaceService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, START_STICKY, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Cursor cursor=getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        Face faceExtractor=new Face();
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
//            String path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String id = cursor.getString(0);
//            long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
//            fmt = outData.format(new Date(time)).toString


//            MediaStore.Images.Media.

            faceExtractor.extract(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id),this.getContentResolver(),cursor.getCount());
        }
        cursor.close();
    }
}
