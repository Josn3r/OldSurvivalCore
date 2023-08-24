package club.josn3rdev.pl.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Crypto {

	@SuppressWarnings("deprecation")
	public double getCurrency(String type) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		JsonParser parser = new JsonParser();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.coingecko.com/api/v3/simple/price?ids=" + type + "&vs_currencies=usd&include_24hr_change=true&precision=2")).build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		JsonObject obj = new JsonObject();
		obj = parser.parse(response.body()).getAsJsonObject();
		Double value = obj.get(type).getAsJsonObject().get("usd").getAsDouble();
		return value;
	}
	
	@SuppressWarnings("deprecation")
	public double getChange(String type) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		JsonParser parser = new JsonParser();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.coingecko.com/api/v3/simple/price?ids=" + type + "&vs_currencies=usd&include_24hr_change=true&precision=2")).build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		JsonObject obj = new JsonObject();
		obj = parser.parse(response.body()).getAsJsonObject();
		Double value = obj.get(type).getAsJsonObject().get("usd_24h_change").getAsDouble();
		return value;
	}
	
}
