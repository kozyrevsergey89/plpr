package com.protection.plpt.plpt.mpkz.mpkz.method;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactsMethod {

    private static final String TAG = ContactsMethod.class.getSimpleName();

    private static final String vfile = "backup.vcf";

    public File getVcardFile(Context context) throws IOException {
        ArrayList<String> vCardsList = new ArrayList<String>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            int i;
            String storage_path = Environment.getExternalStorageDirectory().toString() + File.separator + vfile;
            FileOutputStream mFileOutputStream = new FileOutputStream(storage_path, false);
            cursor.moveToFirst();
            for (i = 0; i < cursor.getCount(); i++) {
                String vCardString = processContact(cursor, context);
                if (vCardString != null) {
                    vCardsList.add(vCardString);
                }
                cursor.moveToNext();
                if (i < vCardsList.size()) {
                    // write to our local file before sending
                    mFileOutputStream.write(vCardsList.get(i).getBytes());
                }
            }
            mFileOutputStream.close();
            cursor.close();
            return new File(Environment.getExternalStorageDirectory().toString() + File.separator + vfile);
        } else {
            Log.d(TAG, "No Contacts in Your Phone");
            return null;
        }
    }

    private String processContact(Cursor cursor, Context context) {
        String vCardResult = null;
        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
        Log.d(TAG, "processContact AssetFileDescriptor is not null, adding vCard");
        AssetFileDescriptor fd;
        try {
            fd = context.getContentResolver().openAssetFileDescriptor(uri, "r");
            FileInputStream fis;
            if (fd != null) {
                Log.d(TAG, "processContact AssetFileDescriptor is not null, adding vCard");
                fis = fd.createInputStream();
                byte[] buf = readBytes(fis);
                vCardResult = new String(buf);
            } else {
                Log.d(TAG, "processContact AssetFileDescriptor is null, not adding vCard");
            }
        } catch (Exception e1) {
            Log.d(TAG, "processContact exception happened with message: " + e1.getMessage());
        }
        return vCardResult;
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public void importContacts(final Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".com.protection.plpt.provider", new File(Environment.getExternalStorageDirectory().toString() + File.separator + vfile));
        intent.setDataAndType(fileUri, "text/x-vcard"); //|we have vfile="backup.vcf"|storage path is path of your vcf file and vFile is name of that file.

        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        context.startActivity(intent);
    }


}
