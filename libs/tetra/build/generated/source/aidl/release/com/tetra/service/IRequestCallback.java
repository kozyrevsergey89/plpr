/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/sergey/Development/Plpt/libs/tetra/src/main/aidl/com/tetra/service/IRequestCallback.aidl
 */
package com.tetra.service;
/**
* AIDL representation for response callback.
* @author David Mayboroda (david.ftzi@gmail.com).
*/
public interface IRequestCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.tetra.service.IRequestCallback
{
private static final java.lang.String DESCRIPTOR = "com.tetra.service.IRequestCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.tetra.service.IRequestCallback interface,
 * generating a proxy if needed.
 */
public static com.tetra.service.IRequestCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.tetra.service.IRequestCallback))) {
return ((com.tetra.service.IRequestCallback)iin);
}
return new com.tetra.service.IRequestCallback.Stub.Proxy(obj);
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
case TRANSACTION_onResponse:
{
data.enforceInterface(DESCRIPTOR);
com.tetra.service.rest.Response _arg0;
if ((0!=data.readInt())) {
_arg0 = com.tetra.service.rest.Response.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onResponse(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.tetra.service.IRequestCallback
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
@Override public void onResponse(com.tetra.service.rest.Response response) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((response!=null)) {
_data.writeInt(1);
response.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onResponse, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onResponse = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void onResponse(com.tetra.service.rest.Response response) throws android.os.RemoteException;
}