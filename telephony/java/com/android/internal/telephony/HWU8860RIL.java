package com.android.internal.telephony;

import android.content.Context;
import android.os.Message;
import android.os.Parcel;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

public class HWU8860RIL extends QualcommSharedRIL
  implements CommandsInterface
{
  boolean RILJ_LOGD = true;
  boolean RILJ_LOGV = true;
  protected int mPinState;

  public HWU8860RIL(Context paramContext, int paramInt1, int paramInt2)
  {
    super(paramContext, paramInt1, paramInt2);
  }

  public void changeIccPin(String paramString1, String paramString2, Message paramMessage)
  {
    RILRequest localRILRequest = RILRequest.obtain(6, paramMessage);
    if (this.RILJ_LOGD)
      riljLog(localRILRequest.serialString() + "> " + requestToString(localRILRequest.mRequest));
    localRILRequest.mp.writeString(this.mAid);
    localRILRequest.mp.writeString(paramString1);
    localRILRequest.mp.writeString(paramString2);
    send(localRILRequest);
  }

  public void changeIccPin2(String paramString1, String paramString2, Message paramMessage)
  {
    RILRequest localRILRequest = RILRequest.obtain(7, paramMessage);
    if (this.RILJ_LOGD)
      riljLog(localRILRequest.serialString() + "> " + requestToString(localRILRequest.mRequest));
    localRILRequest.mp.writeString(this.mAid);
    localRILRequest.mp.writeString(paramString1);
    localRILRequest.mp.writeString(paramString2);
    send(localRILRequest);
  }

  protected DataCallState getDataCallState(Parcel paramParcel, int paramInt)
  {
    DataCallState localDataCallState = new DataCallState();
    boolean bool = needsOldRilFeature("datacall");
    if ((!bool) && (paramInt < 5))
      localDataCallState = super.getDataCallState(paramParcel, paramInt);
    while (true)
    {
      return localDataCallState;
      if (!bool)
      {
        localDataCallState.version = paramInt;
        localDataCallState.status = paramParcel.readInt();
        localDataCallState.suggestedRetryTime = paramParcel.readInt();
        localDataCallState.cid = paramParcel.readInt();
        localDataCallState.active = paramParcel.readInt();
        localDataCallState.type = paramParcel.readString();
        localDataCallState.ifname = paramParcel.readString();
        if ((localDataCallState.status == DataConnection.FailCause.NONE.getErrorCode()) && (TextUtils.isEmpty(localDataCallState.ifname)) && (localDataCallState.active != 0))
          throw new RuntimeException("getDataCallState, no ifname");
        String str2 = paramParcel.readString();
        if (!TextUtils.isEmpty(str2))
          localDataCallState.addresses = str2.split(" ");
        String str3 = paramParcel.readString();
        if (!TextUtils.isEmpty(str3))
          localDataCallState.dnses = str3.split(" ");
        String str4 = paramParcel.readString();
        if (!TextUtils.isEmpty(str4))
          localDataCallState.gateways = str4.split(" ");
      }
      else
      {
        localDataCallState.version = 4;
        localDataCallState.cid = paramParcel.readInt();
        localDataCallState.active = paramParcel.readInt();
        localDataCallState.type = paramParcel.readString();
        localDataCallState.ifname = this.mLastDataIface[localDataCallState.cid];
        paramParcel.readString();
        if (TextUtils.isEmpty(localDataCallState.ifname))
          localDataCallState.ifname = this.mLastDataIface[0];
        String str1 = paramParcel.readString();
        if (!TextUtils.isEmpty(str1))
          localDataCallState.addresses = str1.split(" ");
        paramParcel.readInt();
        paramParcel.readInt();
        localDataCallState.dnses = new String[2];
        localDataCallState.dnses[0] = SystemProperties.get("net." + localDataCallState.ifname + ".dns1");
        localDataCallState.dnses[1] = SystemProperties.get("net." + localDataCallState.ifname + ".dns2");
      }
    }
  }

  public void getIMSI(Message paramMessage)
  {
    RILRequest localRILRequest = RILRequest.obtain(11, paramMessage);
    localRILRequest.mp.writeString(this.mAid);
    if (this.RILJ_LOGD)
      riljLog(localRILRequest.serialString() + "> getIMSI:RIL_REQUEST_GET_IMSI " + 11 + " aid: " + this.mAid + " " + requestToString(localRILRequest.mRequest));
    send(localRILRequest);
  }

  public void iccIO(int paramInt1, int paramInt2, String paramString1, int paramInt3, int paramInt4, int paramInt5, String paramString2, String paramString3, Message paramMessage)
  {
    RILRequest localRILRequest = RILRequest.obtain(28, paramMessage);
    if (this.mUSIM)
      paramString1 = paramString1.replaceAll("7F20$", "7FFF");
    localRILRequest.mp.writeString(this.mAid);
    localRILRequest.mp.writeInt(paramInt1);
    localRILRequest.mp.writeInt(paramInt2);
    localRILRequest.mp.writeString(paramString1);
    localRILRequest.mp.writeInt(paramInt3);
    localRILRequest.mp.writeInt(paramInt4);
    localRILRequest.mp.writeInt(paramInt5);
    localRILRequest.mp.writeString(paramString2);
    localRILRequest.mp.writeString(paramString3);
    if (this.RILJ_LOGD)
      riljLog(localRILRequest.serialString() + "> iccIO: " + " aid: " + this.mAid + " " + requestToString(localRILRequest.mRequest) + " 0x" + Integer.toHexString(paramInt1) + " 0x" + Integer.toHexString(paramInt2) + " " + " path: " + paramString1 + "," + paramInt3 + "," + paramInt4 + "," + paramInt5);
    send(localRILRequest);
  }

  public void queryFacilityLock(String paramString1, String paramString2, int paramInt, Message paramMessage)
  {
    RILRequest localRILRequest = RILRequest.obtain(42, paramMessage);
    if (this.RILJ_LOGD)
      riljLog(localRILRequest.serialString() + "> " + requestToString(localRILRequest.mRequest) + " aid: " + this.mAid + " facility: " + paramString1);
    localRILRequest.mp.writeInt(4);
    localRILRequest.mp.writeString(this.mAid);
    localRILRequest.mp.writeString(paramString1);
    localRILRequest.mp.writeString(paramString2);
    localRILRequest.mp.writeString(Integer.toString(paramInt));
    send(localRILRequest);
  }

  protected Object responseIccCardStatus(Parcel paramParcel)
  {
    IccCardStatus localIccCardStatus = new IccCardStatus();
    localIccCardStatus.setCardState(paramParcel.readInt());
    localIccCardStatus.setUniversalPinState(paramParcel.readInt());
    int i = paramParcel.readInt();
    int j = 0;
    if (j < i)
    {
      if (j == 0)
        localIccCardStatus.setGsmUmtsSubscriptionAppIndex(paramParcel.readInt());
      while (true)
      {
        j++;
        break;
        paramParcel.readInt();
      }
    }
    int k = paramParcel.readInt();
    int m = 0;
    if (m < k)
    {
      if (m == 0)
        localIccCardStatus.setCdmaSubscriptionAppIndex(paramParcel.readInt());
      while (true)
      {
        m++;
        break;
        paramParcel.readInt();
      }
    }
    int n = paramParcel.readInt();
    if (n > 8)
      n = 8;
    localIccCardStatus.setNumApplications(n);
    for (int i1 = 0; i1 < n; i1++)
    {
      IccCardApplication localIccCardApplication1 = new IccCardApplication();
      localIccCardApplication1.app_type = localIccCardApplication1.AppTypeFromRILInt(paramParcel.readInt());
      localIccCardApplication1.app_state = localIccCardApplication1.AppStateFromRILInt(paramParcel.readInt());
      localIccCardApplication1.perso_substate = localIccCardApplication1.PersoSubstateFromRILInt(paramParcel.readInt());
      localIccCardApplication1.aid = paramParcel.readString();
      localIccCardApplication1.app_label = paramParcel.readString();
      localIccCardApplication1.pin1_replaced = paramParcel.readInt();
      localIccCardApplication1.pin1 = localIccCardApplication1.PinStateFromRILInt(paramParcel.readInt());
      localIccCardApplication1.pin2 = localIccCardApplication1.PinStateFromRILInt(paramParcel.readInt());
      localIccCardStatus.addApplication(localIccCardApplication1);
      paramParcel.readInt();
      paramParcel.readInt();
      paramParcel.readInt();
      paramParcel.readInt();
    }
    int i2;
    if (this.mPhoneType == 2)
    {
      i2 = localIccCardStatus.getCdmaSubscriptionAppIndex();
      Log.d("RILJ", "This is a CDMA PHONE " + i2);
      if (n > 0)
      {
        IccCardApplication localIccCardApplication2 = localIccCardStatus.getApplication(i2);
        this.mAid = localIccCardApplication2.aid;
        if (localIccCardApplication2.app_type != IccCardApplication.AppType.APPTYPE_USIM)
          break label457;
      }
    }
    label457: for (boolean bool = true; ; bool = false)
    {
      this.mUSIM = bool;
      this.mSetPreferredNetworkType = this.mPreferredNetworkType;
      if (TextUtils.isEmpty(this.mAid))
        this.mAid = "";
      Log.d("RILJ", "mAid " + this.mAid);
      return localIccCardStatus;
      i2 = localIccCardStatus.getGsmUmtsSubscriptionAppIndex();
      Log.d("RILJ", "This is a GSM PHONE " + i2);
      break;
    }
  }

  protected Object responseSignalStrength(Parcel paramParcel)
  {
    boolean bool = needsOldRilFeature("signalstrength");
    int i = 0;
    int[] arrayOfInt = new int[12];
    int j = 0;
    if (j < 12)
    {
      if (((bool) || (i != 0)) && (j > 6) && (j < 12))
        arrayOfInt[j] = -1;
      while (true)
      {
        if ((j == 7) && (arrayOfInt[j] == 99))
        {
          arrayOfInt[j] = -1;
          i = 1;
        }
        if ((j == 8) && (i == 0) && (!bool))
          arrayOfInt[j] = (-1 * arrayOfInt[j]);
        j++;
        break;
        arrayOfInt[j] = paramParcel.readInt();
      }
    }
    return arrayOfInt;
  }

  public void setFacilityLock(String paramString1, boolean paramBoolean, String paramString2, int paramInt, Message paramMessage)
  {
    RILRequest localRILRequest = RILRequest.obtain(43, paramMessage);
    if (this.RILJ_LOGD)
      riljLog(localRILRequest.serialString() + "> " + requestToString(localRILRequest.mRequest) + " aid: " + this.mAid + " facility: " + paramString1 + " lockstate: " + paramBoolean);
    localRILRequest.mp.writeInt(5);
    localRILRequest.mp.writeString(this.mAid);
    localRILRequest.mp.writeString(paramString1);
    if (paramBoolean);
    for (String str = "1"; ; str = "0")
    {
      localRILRequest.mp.writeString(str);
      localRILRequest.mp.writeString(paramString2);
      localRILRequest.mp.writeString(Integer.toString(paramInt));
      send(localRILRequest);
      return;
    }
  }

  public void setNetworkSelectionModeAutomatic(Message paramMessage)
  {
    RILRequest localRILRequest = RILRequest.obtain(46, paramMessage);
    if (this.RILJ_LOGD)
      riljLog(localRILRequest.serialString() + "> " + requestToString(localRILRequest.mRequest));
    send(localRILRequest);
  }

  public void setupDataCall(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, Message paramMessage)
  {
    RILRequest localRILRequest = RILRequest.obtain(27, paramMessage);
    localRILRequest.mp.writeInt(7);
    localRILRequest.mp.writeString(paramString1);
    localRILRequest.mp.writeString(paramString2);
    localRILRequest.mp.writeString(paramString3);
    localRILRequest.mp.writeString(paramString4);
    localRILRequest.mp.writeString(paramString5);
    localRILRequest.mp.writeString(paramString6);
    localRILRequest.mp.writeString(paramString7);
    if (this.RILJ_LOGD)
      riljLog(localRILRequest.serialString() + "> " + requestToString(localRILRequest.mRequest) + " " + paramString1 + " " + paramString2 + " " + paramString3 + " " + paramString4 + " " + paramString5 + " " + paramString6 + " " + paramString7);
    send(localRILRequest);
  }

  public void supplyIccPin(String paramString, Message paramMessage)
  {
    RILRequest localRILRequest = RILRequest.obtain(2, paramMessage);
    if (this.RILJ_LOGD)
      riljLog(localRILRequest.serialString() + "> " + requestToString(localRILRequest.mRequest));
    localRILRequest.mp.writeString(this.mAid);
    localRILRequest.mp.writeString(paramString);
    send(localRILRequest);
  }

  public void supplyIccPin2(String paramString, Message paramMessage)
  {
    RILRequest localRILRequest = RILRequest.obtain(4, paramMessage);
    if (this.RILJ_LOGD)
      riljLog(localRILRequest.serialString() + "> " + requestToString(localRILRequest.mRequest));
    localRILRequest.mp.writeString(this.mAid);
    localRILRequest.mp.writeString(paramString);
    send(localRILRequest);
  }

  public void supplyIccPuk(String paramString1, String paramString2, Message paramMessage)
  {
    RILRequest localRILRequest = RILRequest.obtain(3, paramMessage);
    if (this.RILJ_LOGD)
      riljLog(localRILRequest.serialString() + "> " + requestToString(localRILRequest.mRequest));
    localRILRequest.mp.writeString(this.mAid);
    localRILRequest.mp.writeString(paramString1);
    localRILRequest.mp.writeString(paramString2);
    send(localRILRequest);
  }

  public void supplyIccPuk2(String paramString1, String paramString2, Message paramMessage)
  {
    RILRequest localRILRequest = RILRequest.obtain(5, paramMessage);
    if (this.RILJ_LOGD)
      riljLog(localRILRequest.serialString() + "> " + requestToString(localRILRequest.mRequest));
    localRILRequest.mp.writeString(this.mAid);
    localRILRequest.mp.writeString(paramString1);
    localRILRequest.mp.writeString(paramString2);
    send(localRILRequest);
  }

  public void supplyNetworkDepersonalization(String paramString, Message paramMessage)
  {
    RILRequest localRILRequest = RILRequest.obtain(8, paramMessage);
    if (this.RILJ_LOGD)
      riljLog(localRILRequest.serialString() + "> " + requestToString(localRILRequest.mRequest));
    localRILRequest.mp.writeInt(3);
    localRILRequest.mp.writeString(paramString);
    send(localRILRequest);
  }
}

/* Location:           /tmp/classes-dex2jar.jar
 * Qualified Name:     com.android.internal.telephony.HWU8860RIL
 * JD-Core Version:    0.6.1
 */