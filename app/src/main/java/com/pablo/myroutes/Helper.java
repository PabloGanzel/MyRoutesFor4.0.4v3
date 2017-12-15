package com.pablo.myroutes;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Paul on 24.07.2017.
 */

public class Helper {

    static final String DEFAULT_POINT_ADDRESS = "Ломоносова, 183 к.1";

    static String getTimeNow(){
        final Calendar calendar = Calendar.getInstance();

        int minutes = calendar.get((Calendar.MINUTE));
        switch (minutes % 5 ){
            case 1:
            case 2:
                minutes -= minutes % 5 ;
                break;
            case 3:
                minutes += 2;
                break;
            case 4:
                minutes +=  1;
                break;
        }
        calendar.set(Calendar.MINUTE, minutes);

        return new SimpleDateFormat("HH:mm").format(calendar.getTime());
    }

    static String getDate(){
        final Calendar c = Calendar.getInstance();
        return String.valueOf(c.get(Calendar.DAY_OF_MONTH))+" "+c.getDisplayName(Calendar.MONTH,Calendar.LONG, new Locale("ru"))+" "+String.valueOf(c.get(Calendar.YEAR));
    }

    // TODO: 23.11.2017
    static String getAddress(){
        return "Ломоносова 10";
    }

    static String getTimeDifference(String firstTime, String lastTime){

        Calendar calendar = Calendar.getInstance();
        calendar.set(2017,01,1,Integer.parseInt(firstTime.substring(0,2)),Integer.parseInt(firstTime.substring(3,5)));
        long _firstTime = calendar.getTime().getTime();
        calendar.set(2017,01,1,Integer.parseInt(lastTime.substring(0,2)),Integer.parseInt(lastTime.substring(3,5)));
        long _lastTime = calendar.getTime().getTime();

        //long difference = ((_lastTime - _firstTime)/1000)/60;

        return String.valueOf(((_lastTime - _firstTime)/1000)/60);
    }

    static Object getObjectByTag(String tag, Context context) throws Exception {
        //FileInputStream ffis = new FileInputStream(tag);
        FileInputStream fis = context.openFileInput(tag);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        ois.close();
        fis.close();
        return object;
    }

    static void saveObject(Object object, String tag, Context context) throws Exception {
        FileOutputStream fos = context.openFileOutput(tag, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }
}

