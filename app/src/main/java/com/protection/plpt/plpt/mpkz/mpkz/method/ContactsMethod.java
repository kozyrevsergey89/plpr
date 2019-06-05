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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ContactsMethod {

    final String vfile = "backup.vcf";
    ArrayList<String> vCard;
    Cursor cursor;

    public File getVcardFile(Context context) throws IOException {
        vCard = new ArrayList<String>();
        cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            int i;
            String storage_path = Environment.getExternalStorageDirectory().toString() + File.separator + vfile;
            FileOutputStream mFileOutputStream = new FileOutputStream(storage_path, false);
            cursor.moveToFirst();
            for (i = 0; i < cursor.getCount(); i++) {
                get(cursor, context);
                //Log.d("TAG", "Contact "+(i+1)+"VcF String is"+vCard.get(i));
                cursor.moveToNext();
                if (i < vCard.size()) {
                    mFileOutputStream.write(vCard.get(i).toString().getBytes());// HERE IS A PLACE, WHERE WE WRITE TO FILE - CHANGE TO SEND a FILE
                }
            }
            mFileOutputStream.close();
            cursor.close();
            return new File(Environment.getExternalStorageDirectory().toString() + File.separator + vfile);
        } else {
            Log.d("TAG", "No Contacts in Your Phone");
            return null;
        }
    }

    private void get(Cursor cursor2, Context context) {
        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
        AssetFileDescriptor fd;
        try {
            fd = context.getContentResolver().openAssetFileDescriptor(uri, "r");

            FileInputStream fis = fd.createInputStream();
            byte[] buf = new byte[(int) fd.getDeclaredLength()];
            fis.read(buf);
            String vcardstring = new String(buf);
            vCard.add(vcardstring);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
	
	public void importContacts(final Context context){
    	Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".com.protection.plpt.provider", new File(Environment.getExternalStorageDirectory().toString() + File.separator+vfile));
		intent.setDataAndType(fileUri,"text/x-vcard"); //|we have vfile="backup.vcf"|storage path is path of your vcf file and vFile is name of that file.

		List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolveInfo : resInfoList) {
			String packageName = resolveInfo.activityInfo.packageName;
			context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}

		context.startActivity(intent);
    }


}
