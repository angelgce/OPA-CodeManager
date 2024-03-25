package com.pirate.arena.app.requests;

import lombok.Builder;

@Builder
public record RequestCreateCode(String email, String type) {
}
