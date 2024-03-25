package com.pirate.arena.app.requests;

public record RequestSendMail(String subject,String title, String body, String type, boolean isCode, String email) {
}

