package com.tetra.service.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.FileNameMap;
import java.net.URLConnection;

import com.google.gson.internal.$Gson$Types;

/**
 * Utility class for {@link Request}
 * @author David Mayboroda (david.ftzi@gmail.com)
 */
public class RequestUtils {

	/**
	 * NOTICE: This method borrowed from Enroscar android library and has little changes.
	 * {@link https://github.com/stanfy/enroscar}
	 * {@link http://goo.gl/epoIn} - ModelTypeToken class.
	 * 
	 * Used for serialization requests model class type.
	 * @param clazz this class;
	 * @return Generic class type;
	 */
	public static Class<?> getModelClass(final Class<?> clazz) {
		Class<?> treatedType = clazz;
		Type superclass;
			do {
			    superclass = treatedType.getGenericSuperclass();
			    treatedType = treatedType.getSuperclass();
			} while (superclass instanceof Class && treatedType != Object.class);
			if (treatedType == Object.class) { 
				throw new RuntimeException("Missing type parameter."); 
			}
		final ParameterizedType parameterized = (ParameterizedType) superclass;
		Type type = $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]); 
		return $Gson$Types.getRawType(type);
	}
	
	/**
	 * Method for stream to string convertation.
	 * @param is {@link InputStream}
	 * @return response string representation.
	 * @throws IOException
	 */
	public static String convertStreamToString(final InputStream is)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		return sb.toString();
	}
	
	/**
	 * Method that returns file mime type.
	 * @param {@link File} instance.
	 * @return {@link String} file mime type
	 * TO DO: Too slow method, need to find faster one.
	 */
	public static String getFileMimeType(final File file) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		return fileNameMap.getContentTypeFor(file.getAbsolutePath()); 
	}
	
}
