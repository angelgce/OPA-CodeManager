package com.pirate.arena.app.requests;

public record RequestSendMail(String title, String body, String type, boolean isCode, String emailReceiver) {
}

