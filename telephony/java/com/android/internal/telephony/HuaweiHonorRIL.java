/*
 *
 * Please see: QualcommSharedRIL.java,
 *             HuaweiRIL.java,
 *             LGEQualcommRIL.java,
 *             SemcRIL.java,
 *             RILConstants.java
 *
 */

package com.android.internal.telephony;

import static com.android.internal.telephony.RILConstants.*;

import android.content.Context;
import android.os.AsyncResult;
import android.os.Message;
import android.os.Parcel;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

public class HuaweiHonorRIL extends QualcommSharedRIL
  implements CommandsInterface
{
  boolean RILJ_LOGD = true;
  boolean RILJ_LOGV = true;
  protected int mPinState;

  // Reviewed 2012-10-16

  public HuaweiHonorRIL(Context context, int networkMode, int cdmaSubscription)
  {
    super(context, networkMode, cdmaSubscription);
  }

  // Reviewed 2012-10-16

  public void changeIccPin(String oldPin, String newPin, Message result)
  {
    // Assuming RIL_REQUEST_CHANGE_SIM_PIN := 6

    RILRequest rr = RILRequest.obtain(RIL_REQUEST_CHANGE_SIM_PIN, result);
    if(RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));
    rr.mp.writeString(this.mAid);
    rr.mp.writeString(oldPin);
    rr.mp.writeString(newPin);
    send(rr);
  }

  // Reviewed 2012-10-16

  public void changeIccPin2(String oldPin, String newPin, Message result)
  {
    // Assuming RIL_REQUEST_CHANGE_SIM_PIN2 := 7

    RILRequest rr = RILRequest.obtain(RIL_REQUEST_CHANGE_SIM_PIN2, result);
    if(RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));
    rr.mp.writeString(this.mAid);
    rr.mp.writeString(oldPin);
    rr.mp.writeString(newPin);
    send(rr);
  }

  // Reviewed 2012-10-17

  protected DataCallState getDataCallState(Parcel p, int version)
  {
    DataCallState dcs = new DataCallState();
    String ts;

    boolean oldRil = needsOldRilFeature("datacall");

    if((!oldRil) && (version<5)) dcs = super.getDataCallState(p, version);
 
    if(!oldRil) {
      dcs.version = version;
      dcs.status = p.readInt();
      dcs.suggestedRetryTime = p.readInt();
      dcs.cid = p.readInt();
      dcs.active = p.readInt();
      dcs.type = p.readString();
      dcs.ifname = p.readString();

      if((dcs.status == DataConnection.FailCause.NONE.getErrorCode()) && (TextUtils.isEmpty(dcs.ifname)) && (dcs.active != 0)) throw new RuntimeException("getDataCallState, no ifname");

      ts = p.readString();
      if(!TextUtils.isEmpty(ts)) dcs.addresses = ts.split(" ");

      ts = p.readString();
      if(!TextUtils.isEmpty(ts)) dcs.dnses = ts.split(" ");

      ts = p.readString();
      if(!TextUtils.isEmpty(ts)) dcs.gateways = ts.split(" ");
    }
    else {
      dcs.version = 4;
      dcs.cid = p.readInt();
      dcs.active = p.readInt();
      dcs.type = p.readString();
      dcs.ifname = this.mLastDataIface[dcs.cid];

      p.readString();
      if(TextUtils.isEmpty(dcs.ifname)) dcs.ifname = this.mLastDataIface[0];

      ts = p.readString();
      if(!TextUtils.isEmpty(ts)) dcs.addresses = ts.split(" ");

      p.readInt();
      p.readInt();

      dcs.dnses = new String[2];
      dcs.dnses[0] = SystemProperties.get("net." + dcs.ifname + ".dns1");
      dcs.dnses[1] = SystemProperties.get("net." + dcs.ifname + ".dns2");
    }

   return dcs;
  }

  // Reviewed 2012-10-16
  // See LGEQualcommRIL.java

  public void getIMSI(Message result)
  {
    // Assuming RIL_REQUEST_GET_IMSI := 11

    RILRequest rr = RILRequest.obtain(RIL_REQUEST_GET_IMSI, result);

    rr.mp.writeString(this.mAid);

    if(RILJ_LOGD) riljLog(rr.serialString() + "> getIMSI:RIL_REQUEST_GET_IMSI " + RIL_REQUEST_GET_IMSI + " aid: " + this.mAid + " " + requestToString(rr.mRequest));

    send(rr);
  }

  // Reviewed 2012-10-16
  // TODO: Don't know about replaceAll
  // See LGEQualcommRIL.java

  public void iccIO(int command, int fileId, String path, int p1, int p2, int p3, String data, String pin2, Message result)
  {
    // Assuming RIL_REQUEST_SIM_IO := 28

    RILRequest rr = RILRequest.obtain(RIL_REQUEST_SIM_IO, result);

    if(this.mUSIM) path = path.replaceAll("7F20$", "7FFF");

    rr.mp.writeString(this.mAid);
    rr.mp.writeInt(command);
    rr.mp.writeInt(fileId);
    rr.mp.writeString(path);
    rr.mp.writeInt(p1);
    rr.mp.writeInt(p2);
    rr.mp.writeInt(p3);
    rr.mp.writeString(data);
    rr.mp.writeString(pin2);

    if(RILJ_LOGD) riljLog(rr.serialString() + "> iccIO: " + " aid: " + this.mAid + " " + requestToString(rr.mRequest)
                                            + " 0x" + Integer.toHexString(command)
                                            + " 0x" + Integer.toHexString(fileId) + " "
                                            + " path: " + path + ","
                                            + p1 + "," + p2 + "," + p3);

    send(rr);
  }

  // Reviewed 2012-10-16

  public void queryFacilityLock(String facility, String password, int serviceClass, Message result)
  {
    // Assuming RIL_REQUEST_QUERY_FACILITY_LOCK := 42

    RILRequest rr = RILRequest.obtain(RIL_REQUEST_QUERY_FACILITY_LOCK, result);

    if(RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest) + " aid: " + this.mAid + " facility: " + facility);

    rr.mp.writeInt(4);
    rr.mp.writeString(this.mAid);
    rr.mp.writeString(facility);
    rr.mp.writeString(password);
    rr.mp.writeString(Integer.toString(serviceClass));

    send(rr);
  }

  // Reviewed 2012-10-18
  // TODO Check status: Merged LGEQualcommRIL.java with smali

  protected Object responseIccCardStatus(Parcel p)
  {
    IccCardApplication ca;

    IccCardStatus status = new IccCardStatus();

    status.setCardState(p.readInt());
    status.setUniversalPinState(p.readInt());

    int gsmUmtsSubscriptionAppCount = p.readInt();

    for(int i=0; i<gsmUmtsSubscriptionAppCount; i++) {
      if(i==0)
        status.setGsmUmtsSubscriptionAppIndex(p.readInt());
      else
        p.readInt();
    }

    int cdmaSubscriptionAppCount = p.readInt();

    for(int i=0; i<cdmaSubscriptionAppCount; i++) {
      if(i==0)
        status.setCdmaSubscriptionAppIndex(p.readInt());
      else
        p.readInt();
    }

    int numApplications = p.readInt();

    // limit to maximum allowed applications
    // Assuming IccCardStart.CARD_MAX_APPS := 8

    if(numApplications>IccCardStatus.CARD_MAX_APPS) numApplications = IccCardStatus.CARD_MAX_APPS;

    status.setNumApplications(numApplications);

    for(int i=0; i<numApplications; i++) {
      ca = new IccCardApplication();
      ca.app_type       = ca.AppTypeFromRILInt(p.readInt());
      ca.app_state      = ca.AppStateFromRILInt(p.readInt());
      ca.perso_substate = ca.PersoSubstateFromRILInt(p.readInt());
      ca.aid            = p.readString();
      ca.app_label      = p.readString();
      ca.pin1_replaced  = p.readInt();
      ca.pin1           = ca.PinStateFromRILInt(p.readInt());
      ca.pin2           = ca.PinStateFromRILInt(p.readInt());
      status.addApplication(ca);
      p.readInt();
      p.readInt();
      p.readInt();
      p.readInt();
    }

    // int appIndex = -1;
    int appIndex;

    if (mPhoneType == RILConstants.CDMA_PHONE) {
      appIndex = status.getCdmaSubscriptionAppIndex();
      Log.d(LOG_TAG, "This is a CDMA PHONE " + appIndex);
    } else {
      appIndex = status.getGsmUmtsSubscriptionAppIndex();
      Log.d(LOG_TAG, "This is a GSM PHONE " + appIndex);

      IccCardApplication application = status.getApplication(appIndex);
      mAid = application.aid;

      this.mUSIM = true;
      this.mSetPreferredNetworkType = this.mPreferredNetworkType;

      if(TextUtils.isEmpty(this.mAid)) this.mAid = "";

      Log.d("RILJ", "mAid " + this.mAid);
    }

    IccCardApplication application = status.getApplication(appIndex);
    mAid = application.aid;
    // mPinState = (application.pin1 == IccCardStatus.PinState.PINSTATE_DISABLED || 
    //              application.pin1 == IccCardStatus.PinState.PINSTATE_UNKNOWN) ? 0 : 1;

    return status;
  }

  // Reviewed 2012-10-16
  // TODO Check if LGEQualcommRIL.java is right about lockState / lockString

  public void setFacilityLock(String facility, boolean lockState, String password, int serviceClass, Message result)
  {
    String lockString;

    // Assuming RIL_REQUEST_SET_FACILITY_LOCK := 43

    RILRequest rr = RILRequest.obtain(RIL_REQUEST_SET_FACILITY_LOCK, result);
    if(RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest) + " aid: " + this.mAid + " facility: " + facility + " lockstate: " + lockState);

    rr.mp.writeInt(5);
    rr.mp.writeString(this.mAid);
    rr.mp.writeString(facility);
    lockString = (lockState)?"1":"0";
    rr.mp.writeString(lockString);
    rr.mp.writeString(password);
    rr.mp.writeString(Integer.toString(serviceClass));

    send(rr);
  }

  // Reviewed 2012-10-16
  // TODO Check if null, -1 is okay for Huawei

  public void setNetworkSelectionModeAutomatic(Message result)
  {
    // Assuming RIL_REQUEST_SET_NETWORK_SELECTION_AUTOMATIC := 46

    RILRequest rr = RILRequest.obtain(RIL_REQUEST_SET_NETWORK_SELECTION_AUTOMATIC, result);

    if(RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));

    // SemcRIL.java

    rr.mp.writeString(null);
    rr.mp.writeInt(-1);

    send(rr);
  }

  // Reviewed 2012-10-16
  // TODO Check IP version status

  public void setupDataCall(String radioTechnology, String profile, String apn, String user, String password, String authType, String ipVersion, Message result)
  {
    // Assuming RIL_REQUEST_SETUP_DATA_CALL := 27

    RILRequest rr = RILRequest.obtain(RIL_REQUEST_SETUP_DATA_CALL, result);

    rr.mp.writeInt(7);
    rr.mp.writeString(radioTechnology);
    rr.mp.writeString(profile);
    rr.mp.writeString(apn);
    rr.mp.writeString(user);
    rr.mp.writeString(password);
    rr.mp.writeString(authType);
    rr.mp.writeString(ipVersion);
    // LGEQualcommRIL.java: rr.mp.writeString("IP"); // ipVersion

    if(RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest) + " " + radioTechnology + " " + profile + " " + apn + " " + user + " " + password + " " + authType + " " + ipVersion);

    send(rr);
  }

  // Reviewed 2012-10-16

  public void supplyIccPin(String pin, Message result)
  {
    // Assuming RIL_REQUEST_ENTER_SIM_PIN := 2

    RILRequest rr = RILRequest.obtain(RIL_REQUEST_ENTER_SIM_PIN, result);

    if(RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));

    rr.mp.writeString(this.mAid);
    rr.mp.writeString(pin);

    send(rr);
  }

  // Reviewed 2012-10-16

  public void supplyIccPin2(String pin, Message result)
  {
    // Assuming RIL_REQUEST_ENTER_SIM_PIN2 := 4

    RILRequest rr = RILRequest.obtain(RIL_REQUEST_ENTER_SIM_PIN2, result);

    if(RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));

    rr.mp.writeString(this.mAid);
    rr.mp.writeString(pin);

    send(rr);
  }

  // Reviewed 2012-10-16

  public void supplyIccPuk(String puk, String newPin, Message result)
  {
    // Assuming RIL_REQUEST_ENTER_SIM_PUK := 3

    RILRequest rr = RILRequest.obtain(RIL_REQUEST_ENTER_SIM_PUK, result);

    if(RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));

    rr.mp.writeString(this.mAid);
    rr.mp.writeString(puk);
    rr.mp.writeString(newPin);

    send(rr);
  }

  // Reviewed 2012-10-16

  public void supplyIccPuk2(String puk, String newPin, Message result)
  {
    // Assuming RIL_REQUEST_ENTER_SIM_PUK2 := 5

    RILRequest rr = RILRequest.obtain(RIL_REQUEST_ENTER_SIM_PUK2, result);

    if(RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));

    rr.mp.writeString(this.mAid);
    rr.mp.writeString(puk);
    rr.mp.writeString(newPin);

    send(rr);
  }

  // Reviewed 2012-10-16

  public void supplyNetworkDepersonalization(String netPin, Message result)
  {
    // Assuming RIL_REQUEST_ENTER_NETWORK_DEPERSONALIZATION := 8

    RILRequest rr = RILRequest.obtain(RIL_REQUEST_ENTER_NETWORK_DEPERSONALIZATION, result);

    if(RILJ_LOGD) riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));

    rr.mp.writeInt(3);
    rr.mp.writeString(netPin);

    send(rr);
  }

  // Reviewed 2012-11-14

  protected Object responseOperatorInfos(Parcel p)
  {
    String strings[] = (String [])responseStrings(p);
    ArrayList<OperatorInfo> ret;

    if(strings.length % 4 != 0)
    {
     // Dump strings

     for(int i=0; i<strings.length; i++)
     {
      if(RILJ_LOGD) riljLog(strings[i]);
     }

     // Throw exception

     throw new RuntimeException("RIL_REQUEST_QUERY_AVAILABLE_NETWORKS: invalid response. Got "
                                + strings.length + " strings, but expected multiple of 4");
    }

    ret = new ArrayList<OperatorInfo>(strings.length / 4);

    for(int i=0; i<strings.length; i+=4)
    {
     ret.add(new OperatorInfo(strings[i+0],
                              strings[i+1],
                              strings[i+2],
                              strings[i+3]));
    }

    return ret;
  }

}

