package com.ezedin.demo.Document;

public record DocumentSummary(Long id, boolean publicAccess) {
    public static DocumentSummary from(document doc) {
        return new DocumentSummary(doc.getId(), doc.isPublicAccess());
    }
}
