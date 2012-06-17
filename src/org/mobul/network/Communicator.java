package org.mobul.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.mobul.utils.StringUtils;

import android.util.Log;

public class Communicator {

	public static final String TAG = "Communicator";

	public static final int UNKNOWN = -1;
	public static final int OK = 0;
	public static final int ERROR = 1;
	public static final int FORMAT = 2;
	public static final int NO_CONNECTION = 3;
	public static final int NONE = 4;

	public static final int TIMEOUT = 20000;
	public static final int SOCKET_TIMEOUT = 20000;
	
	public static final String AUTH_COOKIE = ".ASPXAUTH";
	
	public static final String SERVER = "62.44.100.200"; //"78.90.132.184/Mob";
	public static final String BASE_URL = "https://" + SERVER + "/";
	public static final String LOGIN_URL = BASE_URL + "Auth/LogOn";
	public static final String UPDATE_OES_URL = BASE_URL + "Observation/SyncEvents";
	public static final String OBSERVATION_SUBMIT_URL = BASE_URL + "Task/SubmitReport/%TASK_ID%?service=true";
	public static final String PUBLIC_OES_URL = BASE_URL + "Observation/Search";	
	public static final String SUBSCRIBE_TO_OE_URL = BASE_URL + "Observation/Subscribe/%OE_ID%";
	public static final String UNSUBSCRIBE_FROM_OE_URL = BASE_URL + "Observation/Unsubscribe/%OE_ID%";
	
	protected HttpRequestBase httprequest;
	
	private static DefaultHttpClient httpclient;
	
	protected static String username;
	protected static String password;
	
	protected static synchronized DefaultHttpClient getHttpClient() {
		if (httpclient == null) {
			httpclient = httpClientWithSSL();
		}
		
		return httpclient;
	}
	
	private static DefaultHttpClient httpClientWithSSL() {
		//prepare for the https connection
		//call this in the constructor of the class that does the connection if
		//it's used multiple times
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		// http scheme
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		// https scheme
		schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
				
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(params, "utf8");
		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 1);  
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(1));  
		params.setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
 
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		
		DefaultHttpClient httpclient = new DefaultHttpClient(cm, params);
		httpclient.setAuthSchemes(new AuthSchemeRegistry());
		
		return httpclient;
	}
	
	protected BasicHttpContext getSSLContext() {
		// ignore that the ssl cert is self signed
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(new AuthScope(SERVER, AuthScope.ANY_PORT),
											new UsernamePasswordCredentials("mail", "pass"));
		BasicHttpContext context = new BasicHttpContext();
		context.setAttribute("http.auth.credentials-provider", credentialsProvider);
		
		return context;
	}
	
	public static void setAuth(String[] auth) {
		if (auth != null && auth.length == 2) {
			username = auth[0];
			password = auth[1];
		}
	}
	
	protected static boolean isLoggedIn() {
		for (Cookie c : getHttpClient().getCookieStore().getCookies()) {
			if (c.getName().equals(AUTH_COOKIE) && !StringUtils.isEmpty(c.getValue())) {
				return true;
			}
		}
		
		return false;
	}
	
	public Communicator() {
		httprequest = null;
	}
	
	protected HttpPost getPostRequest(String url) {
		HttpPost httppost = new HttpPost(url);
		httprequest = httppost;
		addHeaders(httppost);
		
		return httppost;
	}
	
	protected HttpGet getGetRequest(String url) {
		HttpGet httpget = new HttpGet(url);
		httprequest = httpget;
		addHeaders(httpget);
		
		return httpget;
	}

	protected void addHeaders(HttpRequestBase httprequest) {
		httprequest.setHeader("Pragma", "no-cache");
		httprequest.setHeader("Cache-Control", "no-cache");
	}
	
	public synchronized void abort() {
		if (httprequest != null) {
			httprequest.abort();
		}
	}
	
	protected String getResponse(HttpResponse response) {
		if (response == null) {
			return null;
		}
		
		HttpEntity resEntity = response.getEntity();

		if (resEntity == null) {
			return null;
		}

		String responseText = null;
		try {
			responseText = StringUtils.convertStreamToString(resEntity.getContent());
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responseText;
	}
	
	protected UrlEncodedFormEntity generateQuery(List<String[]> tokens) throws UnsupportedEncodingException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(tokens.size());
		
		for (String[] s: tokens) {
			if (s.length == 2 && !StringUtils.isEmpty(s[0])) {
				nameValuePairs.add(new BasicNameValuePair(s[0], s[1]));  
			}
		}
		
		UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(nameValuePairs);
		return uefe;
	}
	
	protected HttpResponse httpExecute(HttpUriRequest request) throws ClientProtocolException, IOException {
		return getHttpClient().execute(request);
	}
	
	public int login(String username, String password) {
		
		HttpPost httppost = getPostRequest(LOGIN_URL);
		
		List<String[]> l = new ArrayList<String[]>(2);
		l.add(new String[]{"UserName", username});
		l.add(new String[]{"Password", password});
				
		HttpResponse response = null;
		try {
			UrlEncodedFormEntity uefe = generateQuery(l);
			httppost.setEntity(uefe);
			response = httpExecute(httppost);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return FORMAT;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NO_CONNECTION;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NO_CONNECTION;
		}  
		
		boolean isLoggedIn = false;
		for (Cookie c : getHttpClient().getCookieStore().getCookies()) {
			Log.i(TAG, "Cookie: " + c.getName() + " = " + c.getValue());
			if (c.getName().equals(AUTH_COOKIE)) {
				isLoggedIn = true;
			}
		}
		
		String responseText = getResponse(response);
		
		Log.i(TAG, responseText);
		
		if (isLoggedIn) {
			Communicator.username = username;
			Communicator.password = password;
			return OK;
		} else {
			return NO_CONNECTION;
		}
	}
	
	public int login() {
		return login(username, password);
	}
	
	public static void resetAuth() {
		username = "";
		password = "";
		getHttpClient().getCookieStore().clear();
	}

}
