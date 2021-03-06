package com.clevertap.pushtemplates;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.clevertap.android.sdk.CTPushNotificationReceiver;
import com.clevertap.android.sdk.CleverTapAPI;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.NOTIFICATION_SERVICE;

public class PushTemplateReceiver extends BroadcastReceiver {
    boolean clicked1=true,clicked2=true,clicked3=true,clicked4=true,clicked5 = true, img1=false,img2=false,img3=false, buynow=true, bigimage=true, cta1=true,cta2=true,cta3=true,cta4=true,cta5=true,close=true;
    private RemoteViews contentViewBig, contentViewSmall, contentViewCarousel, contentViewRating,contentFiveCTAs;
    private String pt_id;
    private TemplateType templateType;
    private String pt_title;
    private String pt_msg;
    private String pt_img_small;
    private String pt_img_big;
    private String pt_title_clr,pt_msg_clr;
    private ArrayList<String> imageList = new ArrayList<>();
    private ArrayList<String> ctaList = new ArrayList<>();
    private ArrayList<String> deepLinkList = new ArrayList<>();
    private ArrayList<String> bigTextList = new ArrayList<>();
    private ArrayList<String> smallTextList = new ArrayList<>();
    private String pt_bg;
    private String channelId;
    private int smallIcon = 0;
    private boolean requiresChannelId;
    private NotificationManager notificationManager ;
    private CleverTapAPI cleverTapAPI;


    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getExtras()!=null) {
            Bundle extras = intent.getExtras();
            pt_id = intent.getStringExtra(Constants.PT_ID);
            pt_msg = extras.getString(Constants.PT_MSG);
            pt_msg_clr = extras.getString(Constants.PT_MSG_COLOR);
            pt_title = extras.getString(Constants.PT_TITLE);
            pt_title_clr =extras.getString(Constants.PT_TITLE_COLOR);
            pt_bg = extras.getString(Constants.PT_BG);
            pt_img_big = extras.getString(Constants.PT_BIG_IMG);
            pt_img_small = extras.getString(Constants.PT_SMALL_IMG);
            imageList = Utils.getImageListFromExtras(extras);
            ctaList = Utils.getCTAListFromExtras(extras);
            deepLinkList = Utils.getDeepLinkListFromExtras(extras);
            bigTextList = Utils.getBigTextFromExtras(extras);
            smallTextList = Utils.getSmallTextFromExtras(extras);

            notificationManager =(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            cleverTapAPI = CleverTapAPI.getDefaultInstance(context);
            channelId = extras.getString(Constants.WZRK_CHANNEL_ID, "");

            requiresChannelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelIdError = null;
                if (channelId.isEmpty()) {
                    channelIdError = "Unable to render notification, channelId is required but not provided in the notification payload: " + extras.toString();
                } else if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                    channelIdError = "Unable to render notification, channelId: " + channelId + " not registered by the app.";
                }
                if (channelIdError != null) {
                    PTLog.error(channelIdError);
                    return;
                }
            }

            if (pt_id != null) {
                templateType = TemplateType.fromString(pt_id);
            }
            if (templateType != null) {
                switch (templateType){
                    case RATING:
                        handleRatingNotification(context, extras);
                    case FIVE_ICONS:
                        handleFiveCTANotification(context,extras);
                    case PRODUCT_DISPLAY:
                        handleProductDisplayNotification(context,extras);
                    break;
                }
            }
        }
    }


    private void handleRatingNotification(Context context, Bundle extras){

        try{
            //Set RemoteViews again
            contentViewRating = new RemoteViews(context.getPackageName(), R.layout.rating);
            contentViewSmall = new RemoteViews(context.getPackageName(), R.layout.image_only_small);

            if(pt_title!=null && !pt_title.isEmpty()) {
                contentViewRating.setTextViewText(R.id.title, pt_title);
                contentViewSmall.setTextViewText(R.id.title, pt_title);
            }

            if(pt_msg!=null && !pt_msg.isEmpty()) {
                contentViewRating.setTextViewText(R.id.msg, pt_msg);
                contentViewSmall.setTextViewText(R.id.msg, pt_msg);
            }

            if(pt_title_clr != null && !pt_title_clr.isEmpty()){
                contentViewRating.setTextColor(R.id.title, Color.parseColor(pt_title_clr));
                contentViewSmall.setTextColor(R.id.title,Color.parseColor(pt_title_clr));
            }

            if(pt_msg_clr != null && !pt_msg_clr.isEmpty()){
                contentViewRating.setTextColor(R.id.msg,Color.parseColor(pt_msg_clr));
                contentViewSmall.setTextColor(R.id.msg,Color.parseColor(pt_msg_clr));
            }
            HashMap<String,Object> map = new HashMap<String,Object>();
            if(clicked1 == extras.getBoolean("click1",false)) {
                contentViewRating.setImageViewResource(R.id.star1, R.drawable.filled_star_1);
                map.put("Campaign", extras.getString("wzrk_id"));
                map.put("Rating",1);
                cleverTapAPI.pushEvent("Rated",map);
                clicked1 = false;
            }else{
                contentViewRating.setImageViewResource(R.id.star1, R.drawable.outline_star_1);
            }
            if(clicked2 == extras.getBoolean("click2",false)) {
                contentViewRating.setImageViewResource(R.id.star1, R.drawable.filled_star_1);
                contentViewRating.setImageViewResource(R.id.star2, R.drawable.filled_star_1);
                map.put("Campaign", extras.getString("wzrk_id"));
                map.put("Rating",2);
                cleverTapAPI.pushEvent("Rated",map);
                clicked2 = false;
            }else{
                contentViewRating.setImageViewResource(R.id.star2, R.drawable.outline_star_1);
            }
            if(clicked3 == extras.getBoolean("click3",false)) {
                contentViewRating.setImageViewResource(R.id.star1, R.drawable.filled_star_1);
                contentViewRating.setImageViewResource(R.id.star2, R.drawable.filled_star_1);
                contentViewRating.setImageViewResource(R.id.star3, R.drawable.filled_star_1);
                map.put("Campaign", extras.getString("wzrk_id"));
                map.put("Rating",3);
                cleverTapAPI.pushEvent("Rated",map);
                clicked3 = false;
            }else{
                contentViewRating.setImageViewResource(R.id.star3, R.drawable.outline_star_1);
            }
            if(clicked4 == extras.getBoolean("click4",false)) {
                contentViewRating.setImageViewResource(R.id.star1, R.drawable.filled_star_1);
                contentViewRating.setImageViewResource(R.id.star2, R.drawable.filled_star_1);
                contentViewRating.setImageViewResource(R.id.star3, R.drawable.filled_star_1);
                contentViewRating.setImageViewResource(R.id.star4, R.drawable.filled_star_1);
                map.put("Campaign", extras.getString("wzrk_id"));
                map.put("Rating",4);
                cleverTapAPI.pushEvent("Rated",map);
                clicked4 = false;
            }else{
                contentViewRating.setImageViewResource(R.id.star4, R.drawable.outline_star_1);
            }
            if(clicked5 == extras.getBoolean("click5",false)) {
                contentViewRating.setImageViewResource(R.id.star1, R.drawable.filled_star_1);
                contentViewRating.setImageViewResource(R.id.star2, R.drawable.filled_star_1);
                contentViewRating.setImageViewResource(R.id.star3, R.drawable.filled_star_1);
                contentViewRating.setImageViewResource(R.id.star4, R.drawable.filled_star_1);
                contentViewRating.setImageViewResource(R.id.star5, R.drawable.filled_star_1);
                map.put("Campaign", extras.getString("wzrk_id"));
                map.put("Rating",5);
                cleverTapAPI.pushEvent("Rated",map);
                clicked5 = false;
            }else{
                contentViewRating.setImageViewResource(R.id.star5, R.drawable.outline_star_1);
            }

            Intent launchIntent = new Intent(context, CTPushNotificationReceiver.class);
            launchIntent.putExtras(extras);
            launchIntent.removeExtra(Constants.WZRK_ACTIONS);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(),
                    launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Bundle metaData;
            try {
                PackageManager pm = context.getPackageManager();
                ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                metaData = ai.metaData;
                String x = Utils._getManifestStringValueForKey(metaData,Constants.LABEL_NOTIFICATION_ICON);
                if (x == null) throw new IllegalArgumentException();
                smallIcon = context.getResources().getIdentifier(x, "drawable", context.getPackageName());
                if (smallIcon == 0) throw new IllegalArgumentException();
            } catch (Throwable t) {
                smallIcon = Utils.getAppIconAsIntId(context);
            }

            NotificationCompat.Builder notificationBuilder;
            if(requiresChannelId) {
                notificationBuilder = new NotificationCompat.Builder(context, channelId);
            }else{
                notificationBuilder = new NotificationCompat.Builder(context);
            }

            if (notificationManager != null) {
                //Use the Builder to build notification
                notificationBuilder.setSmallIcon(smallIcon)
                        .setCustomContentView(contentViewSmall)
                        .setCustomBigContentView(contentViewRating)
                        .setContentTitle("Custom Notification")
                        .setContentIntent(pIntent)
                        .setAutoCancel(true);

                notificationManager.notify(1, notificationBuilder.build());
                Thread.sleep(1000);
                notificationManager.cancel(1);
                Toast.makeText(context,"Thank you for your feedback",Toast.LENGTH_SHORT).show();
            }

        }catch (Throwable t){
            PTLog.error("Error creating rating notification ",t);
        }


    }


    private void handleProductDisplayNotification(Context context, Bundle extras) {
        try {


            //Set RemoteViews again
            contentViewBig = new RemoteViews(context.getPackageName(), R.layout.product_display_template);
            contentViewSmall = new RemoteViews(context.getPackageName(), R.layout.image_only_small);


            contentViewBig.setImageViewBitmap(R.id.small_image1, ImageCache.getBitmap("0"));

            contentViewBig.setImageViewBitmap(R.id.small_image2, ImageCache.getBitmap("1"));

            contentViewBig.setImageViewBitmap(R.id.small_image3, ImageCache.getBitmap("2"));



            if (pt_title != null && !pt_title.isEmpty()) {
                contentViewBig.setTextViewText(R.id.title, pt_title);
                contentViewSmall.setTextViewText(R.id.title, pt_title);
            }

            if (pt_msg != null && !pt_msg.isEmpty()) {
                contentViewBig.setTextViewText(R.id.msg, pt_msg);
                contentViewSmall.setTextViewText(R.id.msg, pt_msg);
            }

            if (pt_title_clr != null && !pt_title_clr.isEmpty()) {
                contentViewBig.setTextColor(R.id.title, Color.parseColor(pt_title_clr));
                contentViewSmall.setTextColor(R.id.title, Color.parseColor(pt_title_clr));
            }

            if (pt_msg_clr != null && !pt_msg_clr.isEmpty()) {
                contentViewBig.setTextColor(R.id.msg, Color.parseColor(pt_msg_clr));
                contentViewSmall.setTextColor(R.id.msg, Color.parseColor(pt_msg_clr));
            }


            if (img1 != extras.getBoolean("img1", false)) {
                contentViewBig.setImageViewBitmap(R.id.big_image, ImageCache.getBitmap("0"));
                if (!bigTextList.isEmpty()) {
                    contentViewBig.setTextViewText(R.id.big_text, bigTextList.get(0));
                    contentViewBig.setTextViewText(R.id.small_text, smallTextList.get(0));
                }
                img1 = false;
            }
            if (img2 != extras.getBoolean("img2", false)) {
                contentViewBig.setImageViewBitmap(R.id.big_image, ImageCache.getBitmap("1"));
                if (!bigTextList.isEmpty()) {
                    contentViewBig.setTextViewText(R.id.big_text, bigTextList.get(1));
                    contentViewBig.setTextViewText(R.id.small_text, smallTextList.get(1));
                }
                img2 = false;
            }
            if (img3 != extras.getBoolean("img3", false)) {
                contentViewBig.setImageViewBitmap(R.id.big_image, ImageCache.getBitmap("2"));
                if (!bigTextList.isEmpty()) {
                    contentViewBig.setTextViewText(R.id.big_text, bigTextList.get(2));
                    contentViewBig.setTextViewText(R.id.small_text, smallTextList.get(2));
                }

                img3 = false;
            }
            if (buynow == extras.getBoolean("buynow", false)) {
                buynow = false;
            }
            if (bigimage == extras.getBoolean("bigimage", false)) {
                bigimage = false;
            }



            int notificationId = 9987;

            Intent notificationIntent1 = new Intent(context, PushTemplateReceiver.class);
            notificationIntent1.putExtra("img1",true);
            notificationIntent1.putExtra("notif_id",notificationId);
            notificationIntent1.putExtra("pt_dl",deepLinkList.get(0));
            notificationIntent1.putExtras(extras);
            PendingIntent contentIntent1 = PendingIntent.getBroadcast(context, 6, notificationIntent1, 0);
            contentViewBig.setOnClickPendingIntent(R.id.small_image1, contentIntent1);

            Intent notificationIntent2 = new Intent(context, PushTemplateReceiver.class);
            notificationIntent2.putExtra("img2",true);
            notificationIntent2.putExtra("notif_id",notificationId);
            notificationIntent1.putExtra("pt_dl",deepLinkList.get(1));
            notificationIntent2.putExtras(extras);
            PendingIntent contentIntent2 = PendingIntent.getBroadcast(context, 7, notificationIntent2, 0);
            contentViewBig.setOnClickPendingIntent(R.id.small_image2, contentIntent2);

            Intent notificationIntent3 = new Intent(context, PushTemplateReceiver.class);
            notificationIntent3.putExtra("img3",true);
            notificationIntent3.putExtra("notif_id",notificationId);
            notificationIntent1.putExtra("pt_dl",deepLinkList.get(2));
            notificationIntent3.putExtras(extras);
            PendingIntent contentIntent3 = PendingIntent.getBroadcast(context, 8, notificationIntent3, 0);
            contentViewBig.setOnClickPendingIntent(R.id.small_image3, contentIntent3);



            Intent launchIntent = new Intent(context, CTPushNotificationReceiver.class);
            launchIntent.putExtra("wzrk_dl", extras.getString("pt_dl"));
            launchIntent.putExtras(extras);

            launchIntent.removeExtra(Constants.WZRK_ACTIONS);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(),
                    launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Bundle metaData;
            try {
                PackageManager pm = context.getPackageManager();
                ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                metaData = ai.metaData;
                String x = Utils._getManifestStringValueForKey(metaData, Constants.LABEL_NOTIFICATION_ICON);
                if (x == null) throw new IllegalArgumentException();
                smallIcon = context.getResources().getIdentifier(x, "drawable", context.getPackageName());
                if (smallIcon == 0) throw new IllegalArgumentException();
            } catch (Throwable t) {
                smallIcon = Utils.getAppIconAsIntId(context);
            }


            NotificationCompat.Builder notificationBuilder;
            if (requiresChannelId) {
                notificationBuilder = new NotificationCompat.Builder(context, channelId);
            } else {
                notificationBuilder = new NotificationCompat.Builder(context);
            }

            if (notificationManager != null) {
                //Use the Builder to build notification
                notificationBuilder.setSmallIcon(smallIcon)
                        .setCustomContentView(contentViewBig)
                        .setCustomBigContentView(contentViewBig)
                        .setContentTitle("Custom Notification")
                        .setContentIntent(pIntent)
                        .setAutoCancel(true);

                notificationManager.notify(notificationId, notificationBuilder.build());
            }

        }catch(Throwable t){
            PTLog.error("Error creating rating notification ", t);
        }
    }

    private void handleFiveCTANotification(Context context, Bundle extras) {
        String dl = null;
        Intent actionLaunchIntent;

        if (cta1 == extras.getBoolean("cta1")){
            dl = deepLinkList.get(0);
        }
        if (cta2 == extras.getBoolean("cta2")){
            dl = deepLinkList.get(1);
        }
        if (cta3 == extras.getBoolean("cta3")){
            dl = deepLinkList.get(2);
        }
        if (cta4 == extras.getBoolean("cta4")){
            dl = deepLinkList.get(3);
        }
        if (cta5 == extras.getBoolean("cta5")){
            dl = deepLinkList.get(4);
        }
        if (close == extras.getBoolean("close")){
            notificationManager.cancel(9986);

        }

    }


}
