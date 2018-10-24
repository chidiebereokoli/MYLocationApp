//If you have the time, continue on Task 4.; Receive location updates
//https://google-developer-training.github.io/android-developer-advanced-course-practicals/unit-4-add-geo-features-to-your-apps/lesson-7-location/7-1-p-use-the-device-location/7-1-p-use-the-device-location.html#task2intro

package com.example.chidi.mylocationapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//The Reverse Geocode is handled in the FetchAddressTask class.
//FetchAddressTask included so that we can get the Address.
public class FetchAddressTask extends AsyncTask<Location, Void, String> {

    private OnTaskCompleted mListener;
    private final String TAG = FetchAddressTask.class.getSimpleName();
    private Context mContext;
    public FetchAddressTask(Context applicationContext, OnTaskCompleted listener) {
        mContext = applicationContext;
        mListener = listener;
    }

    interface OnTaskCompleted {

        void onTaskCompleted(String result);

    }



    //This is where we convert the location into an address string
    @Override
    protected String doInBackground(Location... locations) {                                //Variable arguments declaration, we can have a variable number of arguments, like an array
        //I am not sure if i need to decalare this geocoder object globally.
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());                    //handles geocoding and reverse geocoding
        Location location = locations[0];                                                   //varags argument, an array, that can
        // hold a variable number of arguments
        List<Address> addresses = null;                                                     // list will be filled with addresses
        // from Geocoder
        String resultmessage = "";                                                          //resultMessage will hold final result
        //we now begin the geocoding process
        try{
            addresses =  geocoder.getFromLocation(
                    location.getLatitude(),location.getLongitude(),
                    //We will use a single address in this example, that is why we select a maximum result of 1
                    1);
        }catch (IOException ioexception){
            //Catch the network or other I/O errors
            resultmessage = mContext.getString(R.string.service_not_available);
            Log.e(TAG, resultmessage,ioexception);
        }
        catch (IllegalArgumentException illegalArgumentException){
            //Catch the invalid latitutde and longitude values
            resultmessage  = mContext.getString(R.string.invalid_lat_long_used);
            Log.e(TAG, resultmessage + ". "+
                    "Latitude = " + location.getLatitude()+
                    ",Longitude = "+ location.getLongitude(), illegalArgumentException);
        };
        //we also include the case where the geocoder is not able to find the address for the given coordinates in the try block
        if (addresses == null || addresses.size() ==0){
            if(resultmessage.isEmpty()){
                resultmessage = mContext.getString(R.string.no_address_found);
                Log.e(TAG, resultmessage);
            }
            //if addresses != 0, then the reverse geocode was successful
        }else{
            //if the address is found, read it into the message
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<>();

            //Fetch the address lines using getAddressLines,join them, and send them to the thread.
            for(int i = 0; i<=address.getMaxAddressLineIndex(); i++){
                addressParts.add(address.getAddressLine(i));
            }

            resultmessage = TextUtils.join("\n", addressParts);
        }
        return resultmessage;
    }

    //when doInBackground method completes, the resulting message string is automatically passed into
    // the onPostExecute Method
    @Override
    protected void onPostExecute(String address) {
        mListener.onTaskCompleted(address);
        super.onPostExecute(address);
    }
}