package com.meng.hui.android.xiezuo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.google.gson.Gson;
import com.meng.hui.android.xiezuo.entity.ResponseData;
import com.meng.hui.android.xiezuo.entity.VersionCheckEntity;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author menghui
 */
public class HttpHandler extends AsyncTask<String, Integer, String>
{

	private final static String TAG = "HttpHandler";
	private final static String encoding = "UTF-8";

	private String url;
	private HttpCallBack callBack;
	private Map<String, String> map;
	private String subvalue;

	public final static ExecutorService pool_service = Executors.newFixedThreadPool(20);

	public HttpHandler(String url, Context context)
	{
		this.url = url;
		this.map = new HashMap<String, String>();
	}

	public HttpHandler addValue(String key, String value)
	{
		this.map.put(key, value);
		return this;
	}

	public HttpHandler addValue(String key, int value)
	{
		String v = String.valueOf(value);
		return addValue(key, v);
	}

	public HttpHandler addValue(String key, long value)
	{
		String v = String.valueOf(value);
		return addValue(key, v);
	}

	public HttpHandler addValue(String key, double value)
	{
		String v = String.valueOf(value);
		return addValue(key, v);
	}

	@SuppressLint("NewApi")
	public void request(HttpCallBack callBack)
	{
		this.callBack = callBack;
        StringBuffer sb = new StringBuffer();
        Set<String> keyset = map.keySet();
        Iterator<String> iterator = keyset.iterator();
        boolean notFirst = false;
        while(iterator.hasNext())
        {
            String key = iterator.next();
            String value = map.get(key);
            if (notFirst)
            {
                sb.append("&");
            }
            sb.append(key + "="+ value);
            notFirst = true;
        }
        subvalue = sb.toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && pool_service!=null)
		{
			executeOnExecutor(pool_service);
		} else
		{
			execute();
		}
	}

	@Override
	protected String doInBackground(String... params)
	{
//		if (!isNetWorkAvaliable(context))
//		{
//			XiezuoDebug.v(TAG, "没有网络连接");
//			return null;
//		}
		OutputStream os = null;
		InputStream is = null;
		InputStreamReader isr = null;
		try
		{
			XiezuoDebug.v(TAG, "URL=" + url);
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(2000);
			conn.setReadTimeout(5000);
			conn.connect();
			XiezuoDebug.v(TAG, "请求方式为=" + conn.getRequestMethod());
			if (subvalue != null && !"".equals(subvalue))
			{
				XiezuoDebug.v(TAG, "请求的参数:" + subvalue);
				byte[] bf = subvalue.getBytes("UTF-8");
				os = conn.getOutputStream();
				os.write(bf);
			}
			is = conn.getInputStream();
			isr = new InputStreamReader(is, encoding);
			int len;
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[1024];
			while ((len = isr.read(buffer)) != -1)
			{
				sb.append(buffer, 0, len);
			}
			return sb.toString();
		} catch (Exception e)
		{
			XiezuoDebug.e(TAG, "MalformedURLException:", e);
		}finally
		{
			if (isr != null)
			{
				try
				{
					isr.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (is != null)
            {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null)
            {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result)
	{
		super.onPostExecute(result);
		if (result!=null && !"".equals(result))
		{
            try
            {
                JSONObject resuobj = new JSONObject(result);
                int code = resuobj.getInt("code");
                String info = resuobj.getString("info");
                String data = resuobj.getString("data");
                ResponseData responseData = new ResponseData(code, info, data);
                callBack.onCallBack(responseData);
            }catch(Exception e)
            {
                XiezuoDebug.e(TAG, "", e);
                callBack.onCallBack(new ResponseData(101, "解析错误", null));
            }
        }else
        {
            callBack.onCallBack(new ResponseData(100, "网络错误", null));
        }
	}

    public interface HttpCallBack
    {
        public void onCallBack(ResponseData data);
    }

	/**
	 * 判断网络是否连接
	 * 
	 * @param mContext
	 * @return
	 *//*
	private static boolean isNetWorkAvaliable(Context mContext)
	{
		try
		{
			boolean isNet = (isWifiContected(mContext) || isNetContected(mContext));
			return isNet;
		} catch (Exception e)
		{
			return false;
		}
		
	}

	*//**
	 * WIFI是否连接
	 * 
	 * @param context
	 * @return
	 *//*
	private static boolean isWifiContected(Context context)
	{
		if (context!=null)
		{
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetworkInfo!=null && wifiNetworkInfo.isConnected())
			{
				return true;
			}
		}
		return false;
	}

	*//**
	 * 移动网络是否连接
	 * 
	 * @param context
	 * @return
	 *//*
	private static boolean isNetContected(Context context)
	{
		if (context!=null)
		{
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mobileNetworkInfo!=null && mobileNetworkInfo.isConnected())
			{
				return true;
			}
		}
		return false;

	}*/
/*
	*//** DES3加密种子 *//*
	private final static String secretKey = "038bcd079067797996c78200a6060524";
	*//** DES3加密向量 *//*
	private final static String iv = "76540123";

	*//**
	 * DES3加密
	 *
	 * @param plainText
	 *            普通文本
	 * @return
	 *//*
	@SuppressLint("TrulyRandom")
	public static String des3Encode(String plainText)
	{
		try
		{
			Key deskey = null;
			DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
			SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
			deskey = keyfactory.generateSecret(spec);

			Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
			IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
			byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
			return BASE64Encoder(encryptData);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	*//** Base64编码种子 *//*
	private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
	        .toCharArray();

	*//**
	 * Base64转码
	 *
	 * @param data
	 * @return
	 *//*
	public static String BASE64Encoder(byte[] data)
	{
		int start = 0;
		int len = data.length;
		StringBuffer buf = new StringBuffer(data.length * 3 / 2);

		int end = len - 3;
		int index = start;
		int num = 0;

		while (index <= end)
		{
			int d = ((((int) data[index]) & 0x0ff) << 16) | ((((int) data[index + 1]) & 0x0ff) << 8)
			        | (((int) data[index + 2]) & 0x0ff);

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append(legalChars[(d >> 6) & 63]);
			buf.append(legalChars[d & 63]);

			index += 3;

			if (num++ >= 14)
			{
				num = 0;
				buf.append(" ");
			}
		}

		if (index == start + len - 2)
		{
			int de = ((((int) data[index]) & 0x0ff) << 16) | ((((int) data[index + 1]) & 255) << 8);

			buf.append(legalChars[(de >> 18) & 63]);
			buf.append(legalChars[(de >> 12) & 63]);
			buf.append(legalChars[(de >> 6) & 63]);
			buf.append("=");
		} else if (index == start + len - 1)
		{
			int d = (((int) data[index]) & 0x0ff) << 16;

			buf.append(legalChars[(d >> 18) & 63]);
			buf.append(legalChars[(d >> 12) & 63]);
			buf.append("==");
		}

		return buf.toString();
	}
	public static String doMD5Encode(String plainText) {

		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(plainText.getBytes("utf-8"));
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			} else {
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
		}
//		return md5StrBuff.toString().toUpperCase(Locale.US);
		return md5StrBuff.toString();
	}*/
}