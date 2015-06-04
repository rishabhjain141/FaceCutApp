package com.veddis.rishabh.testapp;

/**
 * Created by rishabh on 27/5/15.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Face {
    int count=0;
//    int count1=0;
    final int MAX_FACES=10;
    int total;
    public void extract(Uri uri,ContentResolver contentResolver,int total){
        this.total=total;
        extractFaceTask extractFaceTask=new extractFaceTask(contentResolver);
        extractFaceTask.execute(uri);
    }
    private void detectFaces(Bitmap inputBitmap){
        Bitmap bmFace = null;
        if(null != inputBitmap){
            int width = inputBitmap.getWidth();
            int height = inputBitmap.getHeight();

            Bitmap bitmap565 = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            if((1==(bitmap565.getWidth()%2))){
                bitmap565 = Bitmap.createScaledBitmap(bitmap565,
                        bitmap565.getWidth()+1, bitmap565.getHeight(), false);
            }
            Paint ditherPaint = new Paint();
            Paint drawPaint = new Paint();

            //jo bhi krna hai ab bitmap565 k sath krna hai apse wo image hai main

            ditherPaint.setDither(true);
            drawPaint.setColor(Color.RED);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeWidth(2);

            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmap565);
            canvas.drawBitmap(inputBitmap, 0, 0, ditherPaint);
            FaceDetector detector = new FaceDetector(bitmap565.getWidth(), bitmap565.getHeight(),MAX_FACES);
            FaceDetector.Face[] faces =new FaceDetector.Face[MAX_FACES];
            PointF midPoint = new PointF();
            float eyeDistance = 0.0f;
            int totalFaces=detector.findFaces(bitmap565, faces);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            count++;

            Log.i("faceExtractor","faces found "+totalFaces+" count of images:"+count+"/"+total+" "+dateFormat.format(date));
            for(int count=0;count<totalFaces;count++)
            {
                try {
                    if(faces[count].confidence()>0.5089) {
//                        count1++;
                        float left;
                        float top;
                        PointF midPoints = new PointF();
                        faces[count].getMidPoint(midPoint);
                        eyeDistance = faces[count].eyesDistance();
                        left = midPoint.x - (float) (1.1 * eyeDistance);
                        top = midPoint.y - (float) (1 * eyeDistance);

                        bmFace = Bitmap.createBitmap(inputBitmap, (int) left, (int) top, (int) (2.1* eyeDistance), (int) (2.7 * eyeDistance));

//                        if (bmFace.getWidth()>200 & bmFace.getHeight()>200){
//
//                        }

//                        bmFace = Bitmap.createScaledBitmap(bmFace, 200, 200, false);
                        String filepath = Environment.getExternalStorageDirectory() + "/faces/" + faces[count].confidence() + "facedetect" + System.currentTimeMillis() + ".jpg";
                        File f = new File(Environment.getExternalStorageDirectory() + "/faces/");
                        if (!f.exists()) {
                            f.mkdir();
                        }
                        Log.i("face saved", filepath + " conf:" + faces[count].confidence()+System.currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(filepath);

                            bmFace.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                            fos.flush();
                            fos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }catch(Exception e){}

            }



        }
    }
    private class extractFaceTask extends AsyncTask<Uri, Void, String> {
        ContentResolver cr;
        public extractFaceTask(ContentResolver contentResolver){
            cr=contentResolver;
        }
        @Override
        protected String doInBackground(Uri... params) {
            Uri uri =params[0];
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                AssetFileDescriptor fileDescriptor =null;
                fileDescriptor =
                        cr.openAssetFileDescriptor( uri, "r");
                Bitmap bitmap=null;
                try {
                    bitmap = BitmapFactory.decodeFileDescriptor(
                            fileDescriptor.getFileDescriptor(), null, options);
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
//            detectFaces(bitmap);
                    detectFaces(bitmap);
                }catch(OutOfMemoryError e){
                    try {
                        bitmap.recycle();
                    }catch (Exception ex){}catch (Error e1){}
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


}
