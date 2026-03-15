package com.ezedin.demo.controller;

import com.ezedin.demo.Document.DocumentCreateRequest;
import com.ezedin.demo.Document.DocumentResponse;
import com.ezedin.demo.Document.document;
import com.ezedin.demo.Document.documentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class documentController {

    private final documentService service;

    @PostMapping
    public DocumentResponse createDocument(@RequestBody(required = false) DocumentCreateRequest request,
                                           Authentication authentication) {
        String content = request == null ? "" : request.content();
        boolean publicAccess = request != null && Boolean.TRUE.equals(request.publicAccess());
        document created = service.createForUser(authentication.getName(), content, publicAccess);
        return DocumentResponse.from(created);
    }

    @GetMapping("/{id}")
    public DocumentResponse getDocument(@PathVariable Long id, Authentication authentication) {
        document doc = service.getAccessibleDocument(id, authentication.getName());
        return DocumentResponse.from(doc);
    }
}
