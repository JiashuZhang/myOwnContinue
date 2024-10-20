package controller;

import view.ContinueView;

public class ContinueController {
    private ContinueView view;

    public ContinueController(ContinueView view) {
        this.view = view;
    }

    public void sendMessage(String message) {
        if (!message.isEmpty()) {
            sendToApi(message);
            view.clearInputText();
        }
    }

    private void sendToApi(String message) {
        // TODO: 实现API调用逻辑
        System.out.println("Sending to API: " + message);
        // 模拟API响应
        receiveApiResponse("API response: Message received - " + message);
    }

    public void receiveApiResponse(String response) {
        view.receiveMessage(response);
    }
}