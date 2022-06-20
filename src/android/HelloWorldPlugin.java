package io.electrosoft.helloworld;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager;


import android.os.Build;
import android.telephony.TelephonyManager;

import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import android.Manifest;
import android.telephony.CellSignalStrengthLte;
import android.content.Context;


import android.content.Intent;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.app.ActivityManager;

import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.LocationFetchService;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class HelloWorldPlugin extends CordovaPlugin { 
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    try {
      if (action.equals("getAppName")) {  
        //   // RtlsMainActivity rtlsMainActivity = null;
        //   // cordova.getActivity().runOnUiThread(new Runnable() {
        //   //     public void run() {  
        //   //       rtlsMainActivity = new RtlsMainActivity();
        //   //     }
        //   // });
         
        //   // rtlsMainActivity.startWifiScan(this.cordova.getActivity().getApplicationContext());
        //   // final Handler handler = new Handler();
        //   // final int delay = 15000;
        //   // handler.postDelayed(new Runnable() {
        //   //     public void run() {
        //   //         List<ScanResult> mList = rtlsMainActivity.scanSuccess();
        //   //         for(ScanResult scanResult : mList) {
        //   //             Log.d("Wifi", scanResult.SSID);
        //   //         }
        //   //     }
        //   // }, delay);
        //   // callbackContext.success(); // Thread-safe.
        //   Class mainActivity;
        //   Context context = this.cordova.getActivity().getApplicationContext();
        //   String  packageName = context.getPackageName();
        //   Intent  launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        //   String  className = launchIntent.getComponent().getClassName();

        //   mainActivity = Class.forName(className);
        //   Intent intent = new Intent(context, mainActivity);
        //   intent.setAction("android.intent.action.MAIN");
        //   intent.addCategory("android.intent.category.LAUNCHER");
        // //  RtlsMainActivity.foregroundIntent = intent;

        //  // this.handlePermissions();
        //   this.rtlsMainActivity = new RtlsMainActivity();
        //  // this.rtlsMainActivity.setRandomizedID(false);

          // JSONObject jsonMainBody = createV2JsonObject();
          stopService();
          bindService(this.cordova.getActivity());
          callbackContext.success(jsonMainBody.toString());        
         return true;

      }
      if (action.equals("getPackageName")) {
        callbackContext.success(this.cordova.getActivity().getPackageName());
        return true;
      }
      if (action.equals("getVersionNumber")) {
        PackageManager packageManager = this.cordova.getActivity().getPackageManager();
        callbackContext.success(packageManager.getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionName);
      return true;
      }
      if (action.equals("getVersionCode")) {
        PackageManager packageManager = this.cordova.getActivity().getPackageManager();
        callbackContext.success(packageManager.getPackageInfo(this.cordova.getActivity().getPackageName(), 0).versionCode);
      return true;
      }
      return false;
    } catch (Exception e) {
      callbackContext.success("N/A");
      return true;
    }
  }

//   private void openNewActivity(Context context) {
//     Intent intent = new Intent(context, RtlsMainActivity.class);
//     this.cordova.getActivity().startActivity(intent);
// }
 
  public JSONObject createV2JsonObject() {
    Context mContext = this.cordova.getActivity().getApplicationContext();
    TelephonyManager m_telephonyManager = (TelephonyManager) mContext.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return null;
    }
    JSONObject jsonCellsBody = new JSONObject();
    List<CellInfo> cellLocation = m_telephonyManager.getAllCellInfo();
    JSONArray jsonLteArray = new JSONArray();
    try {
      if (cellLocation != null) {
        for (CellInfo info : cellLocation) {
          if (info instanceof CellInfoLte) {
            final CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
            final CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
            JSONObject jsonPlainCellTowerData = new JSONObject();
            if (identityLte.getCi() > 0) {
              // MCC
              int mcc = identityLte.getMcc();
              if (mcc > 9 & mcc < 1000)
                jsonPlainCellTowerData.put("mcc", mcc);
              else
                continue;
              // MNC
              int mnc = identityLte.getMnc();
              if (mnc > 9 & mnc < 1000)
                jsonPlainCellTowerData.put("mnc", mnc);
              else
                continue;
              // Tac
              int tac = identityLte.getTac();
              if (tac >= 0 && tac <= 65536) {
                jsonPlainCellTowerData.put("tac", tac);
              } else {
                continue;
              }
              // CellId
              int cellId = identityLte.getCi();
              jsonPlainCellTowerData.put("cellId", cellId);
              // RSSI
              int rssi = lte.getDbm();
              if (rssi >= -150 && rssi <= 0)
                jsonPlainCellTowerData.put("rssi", rssi);
              else
                continue;
              // Frequency
              int frequency = identityLte.getEarfcn();
              if (frequency >= 1 && frequency <= 99999999)
                jsonPlainCellTowerData.put("frequency", frequency);
              else
                continue;
              jsonLteArray.put(jsonPlainCellTowerData);
            }
          }
        }
      }
      jsonCellsBody.put("ltecells", jsonLteArray);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return jsonCellsBody;
  }

  public void stopService() {
    if (isMyServiceRunning(LocationFetchService.class, this)) {
        Intent myService = new Intent(this, LocationFetchService.class);
        stopService(myService);
    }
}

public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
  ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
  for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
          return true;
      }
  }
  return false;
}

public static void bindService(Context context) {
  Log.d("JLS", "service status=" + isMyServiceRunning(LocationFetchService.class, context));
  if (!isMyServiceRunning(LocationFetchService.class, context)) {
      Log.d("JLS", "Service  started");
      Intent startIntent = new Intent(context, LocationFetchService.class);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          ContextCompat.startForegroundService(context, startIntent);
      } else {
          context.startService(startIntent);
      }
  }
}


// public void startBluetoothDiscovery() {

//     Intent intent = new Intent(this, RtlsMainActivity.class);

//     this.cordova.getActivity().startActivity(intent);

//   }

  // public static String uuid;

  //   public HelloWorldPlugin() {
  //   }


  //   public void initialize(CordovaInterface cordova, CordovaWebView webView) {
  //       super.initialize(cordova, webView);
  //       HelloWorldPlugin.uuid = getUuid();
  //   }

  //   public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
  //       if ("getDeviceInfo".equals(action)) {
  //           JSONObject r = new JSONObject();
  //           r.put("uuid", HelloWorldPlugin.uuid);
  //           callbackContext.success(r);
  //       }
  //       else {
  //           return false;
  //       }
  //       return true;
  //   }



  //  public String getUuid() {
  //       String uuid = Settings.Secure.getString(this.cordova.getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
  //       return "pragati12154352435yadav";
  //   }



    // public boolean execute(
    //                        String action,
    //                        JSONArray args,
    //                        CallbackContext callbackContext
    //                       ) throws JSONException {

    //    if(action.equals("nativeToast")){
    //        nativeToast();
    //         return true;
    //        }
    //    return false;
    // }

// public int nativeToast(){
//   return 10+20;
//       // Toast.makeText(
//       //                 webView.getContext(),
//       //                 "Hello World Cordova Plugin",
//       //                 Toast.LENGTH_SHORT)
//       //                 .show();
//    }
}
