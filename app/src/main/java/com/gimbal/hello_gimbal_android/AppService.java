package com.gimbal.hello_gimbal_android;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.gimbal.android.Beacon;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Communication;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Push;
import com.gimbal.android.Visit;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class AppService extends Service {

    public static final String APPSERVICE_STARTED_ACTION = "appservice_started";
    private static final int MAX_NUM_EVENTS = 100;

    private PlaceEventListener placeEventListener;
    private CommunicationListener communicationListener;
    private LinkedList<String> events;

    @Override
    public void onCreate(){
        events = new LinkedList<>(GimbalDAO.getEvents(getApplicationContext()));

        Gimbal.setApiKey(this.getApplication(), "d900be54-f79e-47a7-8bc2-96b89e77c3cf");
        setupGimbalPlaceManager();
        setupGimbalCommunicationManager();
        Gimbal.start();
    }

    private void setupGimbalCommunicationManager() {
        communicationListener = new CommunicationListener() {
            @Override
            public Notification.Builder prepareCommunicationForDisplay(Communication communication, Visit visit, int notificationId) {
                addEvent(String.format( "Communication Delivered : %s", communication.getTitle()));
                // If you want a custom notification create and return it here
                return null;
            }

            @Override
            public Notification.Builder prepareCommunicationForDisplay(Communication communication, Push push, int notificationId) {
                addEvent(String.format( "Push Communication Delivered : %s", communication.getTitle()));
                // If you want a custom notification create and return it here
                return null;
            }

            @Override
            public void onNotificationClicked(List<Communication> communications) {
                for (Communication communication : communications) {
                    if(communication != null) {
                        addEvent("Communication Clicked");
                    }
                }
            }
        };
        CommunicationManager.getInstance().addListener(communicationListener);
    }

    private void setupGimbalPlaceManager() {
        placeEventListener = new PlaceEventListener() {
//            @Override
//            public void onVisitStart(Visit visit) {
//                addEvent(String.format("Start Visit for %s", visit.getPlace().getName()));
//            }
            @Override
            public void onBeaconSighting(BeaconSighting sighting, List<Visit> places) {
                Beacon beacon = sighting.getBeacon();
                addEvent("Beacon identified: " + beacon.getIdentifier());
            }
            @Override
            public void locationDetected(Location location) {
                addEvent(String.format(Locale.getDefault(), "Location = %.3f Lat, %.3f Lng", location.getLatitude(), location.getLongitude()));
            }
        };
        PlaceManager.getInstance().addListener(placeEventListener);
    }

    private void addEvent(String event) {
        while (events.size() >= MAX_NUM_EVENTS) {
            events.removeLast();
        }
        events.add(0, event);
        GimbalDAO.setEvents(getApplicationContext(), events);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        notifyServiceStarted();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        PlaceManager.getInstance().removeListener(placeEventListener);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notifyServiceStarted() {
        Intent intent = new Intent(APPSERVICE_STARTED_ACTION);
        sendBroadcast(intent);
    }
}
