package apiTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.websocket.Session;

import org.json.JSONObject;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

public class JokerSlackBot {
	SlackSession session;
	SlackChannel channel;
	LocalTime startTime;

	public static void call_me_JSON() throws Exception {
		URL url = new URL("http://httpbin.org/post");
		Map<String, String> params = new LinkedHashMap<>();
		params.put("name", "Jinu Jawad");
		params.put("email", "helloworld@gmail.com");
		params.put("CODE", "1111");
		params.put("message", "Hello Post Test success");

		StringBuilder postData = new StringBuilder();
		for (Entry<String, String> param : params.entrySet()) {
			if (postData.length() != 0)
				postData.append('&');
			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(param.getValue(), "UTF-8"));
		}
		byte[] postDataBytes = postData.toString().getBytes("UTF-8");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(postDataBytes);

		Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (int c; (c = in.read()) >= 0;)
			sb.append((char) c);
		String response = sb.toString();
		System.out.println(response);
		JSONObject myResponse = new JSONObject(response.toString());
		System.out.println("result after Reading JSON Response");
		System.out.println("origin- " + myResponse.getString("origin"));
		System.out.println("url- " + myResponse.getString("url"));

		JSONObject form_data = myResponse.getJSONObject("form");
		System.out.println("CODE- " + form_data.getString("CODE"));
		System.out.println("email- " + form_data.getString("email"));
		System.out.println("message- " + form_data.getString("message"));
		System.out.println("name" + form_data.getString("name"));
	}

	public static void call_me() throws Exception {
		String url = "http://httpbin.org/ip";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		// add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print in String
		System.out.println(response.toString());

		// Read JSON response and print
		JSONObject myResponse = new JSONObject(response.toString());
		System.out.println("result after Reading JSON Response");
		System.out.println("origin- " + myResponse.getString("origin"));
	}

	public String get_me_a_joke() throws Exception {
		String url = "https://icanhazdadjoke.com/";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		// add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept", "application/json");
//		int responseCode = con.getResponseCode();
//		System.out.println("\nSending 'GET' request to URL : " + url);
//		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print in String
		System.out.println(response.toString());

		// Read JSON response and print
		JSONObject myResponse = new JSONObject(response.toString());
		System.out.println("result after Reading JSON Response");
		System.out.println("joke: " + myResponse.getString("joke"));
		return myResponse.getString("joke");
	}

	public void setupSlack() {
		session = SlackSessionFactory
				.createWebSocketSlackSession("xoxb-395896150887-396361959346-j0zoTQKxb8CDnQzBcv034Y95");

		try {
			session.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		channel = session.findChannelByName("general"); // make sure bot is a member of the channel.
	}

	public static void main(String[] args) throws IOException {
		JokerSlackBot j = new JokerSlackBot();
		j.setupSlack();
		String joke = "Couldn't get a joke :(";
		try {
			while(true) {
				if(Math.random()<0.2) {
					joke = j.get_me_a_joke();
					j.session.sendMessage(j.channel, joke);
				}
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			j.session.disconnect();
		}
	}

}