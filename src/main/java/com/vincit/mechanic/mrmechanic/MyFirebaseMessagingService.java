package com.vincit.mechanic.mrmechanic;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Vincit on 4/6/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        final String type = remoteMessage.getData().get("type");
       // remoteMessage.
        // Toast.makeText(this,"hello",Toast.LENGTH_LONG).show();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {


                try{
                   //Toast.makeText(getApplicationContext(),type,Toast.LENGTH_LONG).show();
                }catch (Exception e){

                }

            }
        });


        String status = remoteMessage.getData().get("message");

        if(!status.equalsIgnoreCase("")) {
            if (type.equals("vendor")) {
                String service = remoteMessage.getData().get("service");
                String REQUEST_ID = remoteMessage.getData().get("rid");
                String MyPREFERENCES = "CREDENTIALS";

                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("REQUEST_ID", REQUEST_ID);
                editor.commit();

                //CreateNotification1("Team Mechanic","Request for service from "+ status,REQUEST_ID);
                CreateNotification1("Team Mechanic","You have service request for : " + service,REQUEST_ID);
            }else if (type.equalsIgnoreCase("customer")){
                String RESPONSE_ID = remoteMessage.getData().get("rid");
                String MyPREFERENCES = "CREDENTIALS";

                SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                String mname = remoteMessage.getData().get("name");
                editor.putString("RESPONSE_ID", RESPONSE_ID);
                editor.putString("AMOUNT", status);
                editor.putString("MNAME", mname);
                editor.commit();
                CreateNotification2("Team Mechanic","Mechanic Name : "+mname+"\n"+ "Available for "+ status + " Rs.");
            }else if(type.equalsIgnoreCase("cs")){
                CreateNotificationForStatus("Team Mechanic","Your Account is "+status);
            }else if(type.equalsIgnoreCase("vs")){
                CreateNotificationForStatus("Team Mechanic","Your Account is "+status);
            }
        }
        if(type.equalsIgnoreCase("vendorA")) {
            CreateNotificationForStatus("Team Mechanic", "Purposal Accepted");
        }
    }
    NotificationManager notificationManager;
    private void startNotification(){
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(ns);

        Notification notification = new Notification(R.drawable.icon, null,
                System.currentTimeMillis());


        RemoteViews notificationView = new RemoteViews(getPackageName(),
                R.layout.vendor_notification);


        //the intent that is started when the notification is clicked (works)
        Intent notificationIntent = new Intent(this, Login.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        notification.contentView = notificationView;
        notification.contentIntent = pendingNotificationIntent;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        //this is the intent that is supposed to be called when the
        //button is clicked

        Intent switchIntent = new Intent(this, Accept.class);
       // Intent closeIntent = new Intent(this, Cancel.class);


        PendingIntent pendingSwitchIntent = PendingIntent.getActivity(this, 0,
                switchIntent, 0);
//        PendingIntent pendingSwitchIntent1 = PendingIntent.getService(this, 0,
//                closeIntent, 0);

        notificationView.setOnClickPendingIntent(R.id.accept,
                pendingSwitchIntent);
//        notificationView.setOnClickPendingIntent(R.id.cancel,
//                pendingSwitchIntent1);

        notificationManager.notify(1, notification);
    }



    public static final String BROADCAST = "com.msg";
    static NotificationManager notifyMgr;
    static int notificationId = 01;
    public void CreateNotification1(String Title,String Msg,String REQUEST_ID){

        RemoteViews mContentView = new RemoteViews(getPackageName(), R.layout.vendor_notification);
        mContentView.setTextViewText(R.id.title, Title);
        mContentView.setTextViewText(R.id.subtitle, Msg);

        Intent i = new Intent();
        i.setAction(BROADCAST);


        Intent switchIntent = new Intent(this,Accept.class);
        switchIntent.putExtra("REQUEST_ID",REQUEST_ID);
       // PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, i, 0);
        PendingIntent pendingSwitchIntent = PendingIntent.getActivity(this, 0, switchIntent, 0);

        Intent switchIntentCancel = new Intent(this,Dummy.class);
        PendingIntent pendingSwitchIntentCancel = PendingIntent.getActivity(this, 0, switchIntentCancel, 0);

        mContentView.setOnClickPendingIntent(R.id.accept, pendingSwitchIntent);
        mContentView.setOnClickPendingIntent(R.id.cancel, pendingSwitchIntentCancel);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.icon)
                .setCustomBigContentView(mContentView)
                .setCustomContentView(mContentView);
        nBuilder.setVibrate(new long[] { 500, 500 });

        Notification notification = nBuilder.build();
        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.notify(notificationId, nBuilder.build());



    }

    public void CreateNotification2(String Title,String Msg){


        RemoteViews mContentView = new RemoteViews(getPackageName(), R.layout.vendor_notification);
        mContentView.setTextViewText(R.id.title, Title);
        mContentView.setTextViewText(R.id.subtitle, Msg);

        Intent i = new Intent();
        i.setAction(BROADCAST);



        Intent switchIntent = new Intent(this,Accept2.class);
        // PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, i, 0);
        PendingIntent pendingSwitchIntent = PendingIntent.getActivity(this, 0, switchIntent, 0);


        Intent switchIntentCancel = new Intent(this,Dummy.class);
        PendingIntent pendingSwitchIntentCancel = PendingIntent.getActivity(this, 0, switchIntentCancel, 0);
        mContentView.setOnClickPendingIntent(R.id.accept, pendingSwitchIntent);
        mContentView.setOnClickPendingIntent(R.id.cancel, pendingSwitchIntentCancel);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.icon)
                .setCustomBigContentView(mContentView)
                .setCustomContentView(mContentView);
        nBuilder.setVibrate(new long[] { 500, 500 });

        Notification notification = nBuilder.build();
        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.notify(notificationId, nBuilder.build());



    }

    public void CreateNotification3(String Title,String Msg){


        RemoteViews mContentView = new RemoteViews(getPackageName(), R.layout.vendor_notification);
        mContentView.setTextViewText(R.id.title, Title);
        mContentView.setTextViewText(R.id.subtitle, Msg);

        Intent i = new Intent();
        i.setAction(BROADCAST);



//        Intent switchIntent = new Intent(this,Accept2.class);
//        // PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, i, 0);
//        PendingIntent pendingSwitchIntent = PendingIntent.getActivity(this, 0, switchIntent, 0);
//
//
//        Intent switchIntentCancel = new Intent(this,Dummy.class);
//        PendingIntent pendingSwitchIntentCancel = PendingIntent.getActivity(this, 0, switchIntentCancel, 0);
//        mContentView.setOnClickPendingIntent(R.id.accept, pendingSwitchIntent);
//        mContentView.setOnClickPendingIntent(R.id.cancel, pendingSwitchIntentCancel);


        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.icon)
                .setCustomBigContentView(mContentView)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setCustomContentView(mContentView);
        nBuilder.setVibrate(new long[] { 500, 500 });

        Notification notification = nBuilder.build();
        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.notify(notificationId, nBuilder.build());



    }


    public void CreateNotificationForStatus(String Title,String Msg){


        RemoteViews mContentView = new RemoteViews(getPackageName(), R.layout.status_notification);
        mContentView.setTextViewText(R.id.title, Title);
        mContentView.setTextViewText(R.id.subtitle, Msg);

        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.icon)
                .setCustomBigContentView(mContentView)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setCustomContentView(mContentView);
        nBuilder.setVibrate(new long[] { 500, 500 });


        Notification notification = nBuilder.build();
        notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.notify(notificationId, nBuilder.build());



    }


    public static class SwitchButtonListener extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context,"Acceptance Sent",Toast.LENGTH_LONG).show();
//            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            context.sendBroadcast(it);




        }


    }
}
