package controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import view.ContinueView;
import org.eclipse.swt.widgets.Display;

public class ContinueController {
	private ContinueView view;

	private final static String apiKey = "";// please use your own key for deep-seek
	private static final String API_URL = "https://api.deepseek.com/chat/completions";

	public ContinueController(ContinueView view) {
		this.view = view;
	}

	public void sendMessage(String message) {
		if (!message.isEmpty()) {
			// 在UI线程中显示用户输入
			Display.getDefault().asyncExec(() -> {
				view.receiveMessage("User:\n" + message);
			});
			
			// 在后台线程中执行网络请求
			new Thread(() -> {
				try {
					String response = sendChatRequest(message);
					String content = extractContent(response);
					// 在UI线程中更新界面
					Display.getDefault().asyncExec(() -> {
						receiveApiResponse(content);
					});
				} catch (Exception e) {
					e.printStackTrace();
					// 在UI线程中显示错误信息
					Display.getDefault().asyncExec(() -> {
						view.receiveMessage("Error: Unable to get response from API");
					});
				}
			}).start();
		}
	}

	public void receiveApiResponse(String response) {
		view.receiveMessage("AI:\n" + response);
	}

	public static String sendChatRequest(String content) throws Exception {
		HttpClient client = HttpClient.newHttpClient();

		// 构建JSON请求体
		JsonObject message = new JsonObject();
		message.addProperty("role", "user");
		message.addProperty("content", content);

		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("model", "deepseek-chat");
		requestBody.add("messages", new Gson().toJsonTree(new JsonObject[] { message }));
		requestBody.addProperty("stream", false);

		// 创建HTTP请求
		HttpRequest request = HttpRequest.newBuilder().uri(new URI(API_URL)).header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + apiKey)
				.POST(HttpRequest.BodyPublishers.ofString(requestBody.toString())).build();

		// 发送请求并获取响应
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		return response.body();
	}

	public static String extractContent(String jsonString) {
		try {
			JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
			JsonObject message = jsonObject.getAsJsonArray("choices").get(0).getAsJsonObject()
					.getAsJsonObject("message");
			return message.get("content").getAsString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Error: Unable to extract content";
		}
	}
}
