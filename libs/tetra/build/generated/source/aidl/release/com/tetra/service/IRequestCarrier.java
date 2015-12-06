/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/sergey/Development/Plpt/libs/tetra/src/main/aidl/com/tetra/service/IRequestCarrier.aidl
 */
package com.tetra.service;
/**
* AIDL representation for service binder.
* @author David Mayboroda (david.ftzi@gmail.com).
*/
public interface IRequestCarrier extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.tetra.service.IRequestCarrier
{
private static final java.lang.String DESCRIPTOR = "com.tetra.service.IRequestCarrier";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.tetra.service.IRequestCarrier interface,
 * generating a proxy if needed.
 */
public static com.tetra.service.IRequestCarrier asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.tetra.service.IRequestCarrier))) {
return ((com.tetra.service.IRequestCarrier)iin);
}
return new com.tetra.service.IRequestCarrier.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_doRequest:
{
data.enforceInterface(DESCRIPTOR);
com.tetra.service.rest.Carrier _arg0;
if ((0!=data.readInt())) {
_arg0 = com.tetra.service.rest.Carrier.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
com.tetra.service.IRequestCallback _arg1;
_arg1 = com.tetra.service.IRequestCallback.Stub.asInterface(data.readStrongBinder());
this.doRequest(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.tetra.service.IRequestCarrier
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/** 
	* Method for request fetching from activity.
	* @param Carrier with request.
	* @param IRequestCallback callback for response handling.
	*/
@Override public void doRequest(com.tetra.service.rest.Carrier carrier, com.tetra.service.IRequestCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((carrier!=null)) {
_data.writeInt(1);
carrier.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_doRequest, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_doRequest = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
/** 
	* Method for request fetching from activity.
	* @param Carrier with request.
	* @param IRequestCallback callback for response handling.
	*/
public void doRequest(com.tetra.service.rest.Carrier carrier, com.tetra.service.IRequestCallback callback) throws android.os.RemoteException;
}
