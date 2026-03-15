package com.ezedin.demo.Document;

public record DocumentResponse(Long id, String content, boolean publicAccess) {
    public static DocumentResponse from(document doc) {
        return new DocumentResponse(doc.getId(), doc.getContent(), doc.isPublicAccess());
    }
}
