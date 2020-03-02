package com.example.nemo1.weather21.model;
import android.os.StrictMode;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.ByteArrayBody;
//import org.apache.http.entity.mime.content.FileBody;

/**
 * Utility methods for retrieving content over HTTP using the more-supported
 * {@code java.net} classes in Android.
 */
@SuppressWarnings("deprecation")
public final class HttpUtils {
	private static final Charset charset = Charset.forName("UTF-8");
	private static final String TAG = HttpUtils.class.getSimpleName();
	private static final String USER_AGENT = "Acacy (Android)";
	private static final int CONNECTION_TIMEOUT = 20 * 1000;
	private static final long MCC_TIMEOUT = 5 * 60 * 1000; /* 5 seconds */
	private static int SOCKET_TIMEOUT = 5 * 60 * 1000;

	private HttpUtils() {
	}

	public enum ContentType {
		/** HTML-like content type, including HTML, XHTML, etc. */
		HTML,
		/** JSON content */
		JSON,
		/** XML */
		XML,
		/** Plain text content */
		TEXT,

		FILE
	}

	public static CharSequence downloadViaHttp(String uri, ContentType type) throws IOException {
		return downloadViaHttp(uri, type, Integer.MAX_VALUE);
	}

	public static CharSequence downloadViaHttp(String uri, ContentType type, int maxChars) throws IOException {
		String contentTypes;
		switch (type) {
		case HTML:
			contentTypes = "application/xhtml+xml,text/html,text/*,*/*";
			break;
		case JSON:
			contentTypes = "application/json,text/*,*/*";
			break;
		case XML:
			contentTypes = "application/xml,text/*,*/*";
			break;
		case FILE:
			contentTypes = "application/octet-stream,text/*,*/*";
			break;
		case TEXT:
		default:
			contentTypes = "text/*,*/*";
		}
		return downloadViaHttp(uri, contentTypes, maxChars);
	}

	private static CharSequence downloadViaHttp(String uri, String contentTypes, int maxChars) throws IOException {
		int redirects = 0;
		while (redirects < 5) {
			URL url = new URL(uri);
			HttpURLConnection connection = safelyOpenConnection(url);
			connection.setInstanceFollowRedirects(true); // Won't work HTTP ->
															// HTTPS or vice
															// versa
			connection.setRequestProperty("Accept", contentTypes);
			connection.setRequestProperty("Accept-Charset", "utf-8,*");
			connection.setRequestProperty("User-Agent", USER_AGENT);
			try {
				int responseCode = safelyConnect(uri, connection);
				switch (responseCode) {
				case HttpURLConnection.HTTP_OK:
				case HttpURLConnection.HTTP_ACCEPTED:
					return consume(connection, maxChars);
				case HttpURLConnection.HTTP_MOVED_TEMP:
					String location = connection.getHeaderField("Location");
					if (location != null) {
						uri = location;
						redirects++;
						continue;
					}
					throw new IOException("No Location");
				default:
					throw new IOException("Bad HTTP response: " + responseCode);
				}
			} finally {
				connection.disconnect();
			}
		}
		throw new IOException("Too many redirects");
	}

	private static String getEncoding(URLConnection connection) {
		String contentTypeHeader = connection.getHeaderField("Content-Type");
		if (contentTypeHeader != null) {
			int charsetStart = contentTypeHeader.indexOf("charset=");
			if (charsetStart >= 0) {
				return contentTypeHeader.substring(charsetStart + "charset=".length());
			}
		}
		return "UTF-8";
	}

	private static CharSequence consume(URLConnection connection, int maxChars) throws IOException {
		String encoding = getEncoding(connection);
		StringBuilder out = new StringBuilder();
		Reader in = null;
		try {
			in = new InputStreamReader(connection.getInputStream(), encoding);
			char[] buffer = new char[1024];
			int charsRead;
			while (out.length() < maxChars && (charsRead = in.read(buffer)) > 0) {
				out.append(buffer, 0, charsRead);
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ioe) {
					// continue
				} catch (NullPointerException npe) {
					// another apparent Android / Harmony bug; continue
				}
			}
		}
		return out;
	}

	public static URI unredirect(URI uri) throws IOException {
		// if (!REDIRECTOR_DOMAINS.contains(uri.getHost())) {
		// return uri;
		// }
		URL url = uri.toURL();
		HttpURLConnection connection = safelyOpenConnection(url);
		connection.setInstanceFollowRedirects(false);
		connection.setDoInput(false);
		connection.setRequestMethod("HEAD");
		connection.setRequestProperty("User-Agent", USER_AGENT);
		try {
			int responseCode = safelyConnect(uri.toString(), connection);
			switch (responseCode) {
			case HttpURLConnection.HTTP_MULT_CHOICE:
			case HttpURLConnection.HTTP_MOVED_PERM:
			case HttpURLConnection.HTTP_MOVED_TEMP:
			case HttpURLConnection.HTTP_SEE_OTHER:
			case 307: // No constant for 307 Temporary Redirect ?
				String location = connection.getHeaderField("Location");
				if (location != null) {
					try {
						return new URI(location);
					} catch (URISyntaxException e) {
						// nevermind
					}
				}
			}
			return uri;
		} finally {
			connection.disconnect();
		}
	}

	public static boolean canConnect(URL url) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		HttpURLConnection conn = null;
		boolean canConnect = false;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			canConnect = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!(conn instanceof HttpURLConnection)) {
			canConnect = false;
		}
		if (conn != null) {
			conn.disconnect();
		}
		Log.i("Internet Connect", "" + canConnect);
		return canConnect;
	}

	private static HttpURLConnection safelyOpenConnection(URL url) throws IOException {
		URLConnection conn;
		try {
			conn = url.openConnection();
		} catch (NullPointerException npe) {
			// Another strange bug in Android?
			Log.w(TAG, "Bad URI? " + url);
			throw new IOException(npe.toString());
		}
		if (!(conn instanceof HttpURLConnection)) {
			throw new IOException();
		}
		return (HttpURLConnection) conn;
	}

	private static int safelyConnect(String uri, HttpURLConnection connection) throws IOException {
		try {
			connection.connect();
		} catch (NullPointerException npe) {
			// this is an Android bug:
			// http://code.google.com/p/android/issues/detail?id=16895
			Log.w(TAG, "Bad URI? " + uri);
			throw new IOException(npe.toString());
		} catch (IllegalArgumentException iae) {
			// Also seen this in the wild, not sure what to make of it. Probably
			// a bad URL
			Log.w(TAG, "Bad URI? " + uri);
			throw new IOException(iae.toString());
		} catch (SecurityException se) {
			// due to bad VPN settings?
			Log.w(TAG, "Restricted URI? " + uri);
			throw new IOException(se.toString());
		} catch (IndexOutOfBoundsException ioobe) {
			// Another Android problem?
			// https://groups.google.com/forum/?fromgroups#!topic/google-admob-ads-sdk/U-WfmYa9or0
			Log.w(TAG, "Bad URI? " + uri);
			throw new IOException(ioobe.toString());
		}
		try {
			return connection.getResponseCode();
		} catch (NullPointerException npe) {
			// this is maybe this Android bug:
			// http://code.google.com/p/android/issues/detail?id=15554
			Log.w(TAG, "Bad URI? " + uri);
			throw new IOException(npe.toString());
		} catch (IllegalArgumentException iae) {
			// Again seen this in the wild for bad header fields in the server
			// response! or bad reads
			Log.w(TAG, "Bad server status? " + uri);
			throw new IOException(iae.toString());
		}
	}

	public static String post(String Uri, Map<String, String> paramaters) throws IOException {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		String str = null;
		BufferedReader reader = null;
		String query = "";
		HttpURLConnection conn = null;
		try {
			URL url = new URL(Uri);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
			conn.setReadTimeout(SOCKET_TIMEOUT);
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			if (paramaters != null && !paramaters.isEmpty()) {
				for (String key : paramaters.keySet()) {
					if (paramaters.get(key) != null) {
						query += key + "=" + URLEncoder.encode(paramaters.get(key), "utf-8") + "&";
					}
				}
			}
			Log.d("Http paramaters", query);
			OutputStream output = null;
			try {
				output = conn.getOutputStream();
				output.write(query.getBytes(charset));
			} catch (IOException e) {
				throw e;
			} finally {
				if (output != null)
					try {
						output.close();
					} catch (IOException logOrIgnore) {
						return null;
					}
			}
			Log.d("Http ResponseMessage", conn.getResponseMessage());
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder buf = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				buf.append(line + "\n");
			}

			str = buf.toString();

		} catch (IOException e) {
			throw e;
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return str;
	}

		private static class JsonReader extends Reader {
		private final InputStreamReader stream;

		public JsonReader(InputStream in) {
			this.stream = new InputStreamReader(in);
		}

		@Override
		public void close() throws IOException {
			stream.close();
		}

		@Override
		public int read(char[] buffer, int offset, int count) throws IOException {
			return stream.read(buffer, offset, count);
		}

	}


	public static boolean uploadFile(String uri, Map<String, String> paramaters, File file) throws IOException {
		int redirects = 0;
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		final HttpPost postRequest = new HttpPost(uri);
		FileBody sender = new FileBody(file);
		MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();

		reqEntity.addPart("uploadfile", sender);
		reqEntity.addTextBody("filename", file.getName());
		HttpParams params = postRequest.getParams();
		params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT);
		params.setLongParameter(ConnManagerPNames.TIMEOUT, MCC_TIMEOUT);

		if (paramaters != null && !paramaters.isEmpty()) {
			for (String key : paramaters.keySet()) {
				if (paramaters.get(key) != null) {
					reqEntity.addTextBody(key, paramaters.get(key).toString());
				}
			}
		}
		postRequest.setEntity(reqEntity.build());
		HttpResponse response = null;
		HttpClient client = null;
		while (redirects < 2) {
			try {
				client = new DefaultHttpClient();
				response = client.execute(postRequest);
				int responseCode = response.getStatusLine().getStatusCode();
				switch (responseCode) {
				case HttpStatus.SC_OK:
					return true;
				case HttpStatus.SC_REQUEST_TIMEOUT:
					redirects++;
					continue;
				default:
					break;
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				client.getConnectionManager().shutdown();
				redirects++;
				continue;
			} catch (ConnectTimeoutException e) {
				e.printStackTrace();
				client.getConnectionManager().shutdown();
				redirects++;
				continue;
			} catch (IOException e) {
				Log.w(TAG, e.getMessage(), e);
				throw new IOException(e.toString());
			} finally {
				client.getConnectionManager().shutdown();
			}
			redirects++;
		}
		return false;
	}

	public static boolean uploadViaHttp(String uri, Map<String, String> paramaters, byte[]... data) throws IOException {
		int redirects = 0;
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		final HttpPost postRequest = new HttpPost(uri);
		MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
		HttpParams params = postRequest.getParams();
		params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT);
		params.setLongParameter(ConnManagerPNames.TIMEOUT, MCC_TIMEOUT);
		if (data != null) {

			for (int i = 0; i < data.length; i++) {
				byte[] _data = data[i];
				// ByteArrayBody sender = new ByteArrayBody(_data, "data" +
				// i);
				reqEntity.addPart("data" + i, new ByteArrayBody(_data, "data" + i));
			}

		}

		if (paramaters != null && !paramaters.isEmpty()) {
			for (String key : paramaters.keySet()) {
				if (paramaters.get(key) != null) {
					String _text = paramaters.get(key).toString();
					reqEntity.addTextBody(key, _text);
				}
			}
		}
		postRequest.setEntity(reqEntity.build());
		HttpResponse response = null;
		HttpClient client = null;
		while (redirects < 2) {
			try {
				client = new DefaultHttpClient();
				response = client.execute(postRequest);
				int responseCode = response.getStatusLine().getStatusCode();
				switch (responseCode) {
				case HttpStatus.SC_OK:
					return true;
				case HttpStatus.SC_REQUEST_TIMEOUT:
					redirects++;
					continue;
				default:
					break;
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				client.getConnectionManager().shutdown();
				redirects++;
				continue;
			} catch (ConnectTimeoutException e) {
				e.printStackTrace();
				client.getConnectionManager().shutdown();
				redirects++;
				continue;
			} catch (IOException e) {
				Log.w(TAG, e.getMessage(), e);
				throw new IOException(e.toString());
			} finally {
				client.getConnectionManager().shutdown();
			}
			redirects++;
		}
		return false;
	}

	public static String upload(String uri, Map<String, String> paramaters, byte[]... data) throws Exception {
		int redirects = 0;
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		final HttpPost postRequest = new HttpPost(uri);
		MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
		HttpParams params = postRequest.getParams();
		params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, SOCKET_TIMEOUT);
		params.setLongParameter(ConnManagerPNames.TIMEOUT, MCC_TIMEOUT);
		if (data != null) {
			for (int i = 0; i < data.length; i++) {
				ByteArrayBody sender = new ByteArrayBody(data[i], "data" + i);
				reqEntity.addPart("data" + i, sender);
			}
		}
		if (paramaters != null && !paramaters.isEmpty()) {
			for (String key : paramaters.keySet()) {
				if (paramaters.get(key) != null) {
					reqEntity.addTextBody(key, paramaters.get(key).toString());
				}
			}
		}
		postRequest.setEntity(reqEntity.build());
		HttpResponse response = null;
		HttpClient client = null;
		while (redirects < 2) {
			try {
				client = new DefaultHttpClient();
				response = client.execute(postRequest);
				int responseCode = response.getStatusLine().getStatusCode();
				switch (responseCode) {
				case HttpStatus.SC_OK: {
					InputStream reader = null;
					reader = response.getEntity().getContent();
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					int BUF_SIZE = 8 * 1024;
					byte[] buf = new byte[BUF_SIZE];
					int len;
					while ((len = reader.read(buf, 0, BUF_SIZE)) > 0) {
						stream.write(buf, 0, len);
					}
					byte[] content = stream.toByteArray();
					stream.close();
					return new String(content, Charset.defaultCharset());
				}
				case HttpStatus.SC_REQUEST_TIMEOUT:
					redirects++;
					continue;
				default:
					Log.w("HTTP", uri);
					Log.w("HTTP", "StatusCode: " + responseCode);
					break;
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				client.getConnectionManager().shutdown();
				redirects++;
				continue;
			} catch (ConnectTimeoutException e) {
				e.printStackTrace();
				client.getConnectionManager().shutdown();
				redirects++;
				continue;
			} catch (IOException e) {
				Log.w(TAG, e.getMessage(), e);
				throw new IOException(e.toString());
			} finally {
				client.getConnectionManager().shutdown();
			}
			redirects++;
		}
		throw new Exception("ERROR");
	}
}
