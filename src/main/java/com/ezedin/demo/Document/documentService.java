package com.ezedin.demo.Document;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.Document;

@Service
@RequiredArgsConstructor
public class documentService {
    private final documentRepository repository ;

    public void save(documentEvent newDocument) {

        repository.save(document.builder().id(newDocument.getId()).content(newDocument.getContent()).build());
    }
}
