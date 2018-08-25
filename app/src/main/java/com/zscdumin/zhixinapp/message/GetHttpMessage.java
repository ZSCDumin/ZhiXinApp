package com.zscdumin.zhixinapp.message;

import com.google.gson.Gson;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

public class GetHttpMessage {
	/*public static void main(String[] args) throws IllegalStateException,
			IOException {
		String ss = new TestCase().testGetRequest("你爸爸是谁");
		System.err.println(ss);
	}*/

	public String testGetRequest(String info) throws IllegalStateException,
			IOException {
		info = URLEncoder.encode(info, "UTF-8");
		HttpClient client = new HttpClient();
		StringBuilder sb = new StringBuilder();
		InputStream ins = null;
		// Create a method instance.
		GetMethod method = new GetMethod(
				"http://www.tuling123.com/openapi/api?key=3e33b5a662e94daca699f26301f8a529&info="
						+ info + "");
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);
			System.out.println(statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				ins = method.getResponseBodyAsStream();
				byte[] b = new byte[1024];
				int r_len = 0;
				while ((r_len = ins.read(b)) > 0) {
					sb.append(new String(b, 0, r_len, method
							.getResponseCharSet()));
				}
			} else {
				System.err.println("Response Code: " + statusCode);
			}
		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
		} finally {
			method.releaseConnection();
			if (ins != null) {
				ins.close();
			}
		}
		System.out.println(sb.toString());
		String ss = sb.toString();
		Msg person = GsonUtil.parseJsonWithGson(ss, Msg.class);
		ss = person.getText();
		return (ss);
	}

	static class GsonUtil {
		// 将Json数据解析成相应的映射对象
		public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
			Gson gson = new Gson();
			T result = gson.fromJson(jsonData, type);
			return result;
		}
	}
}
