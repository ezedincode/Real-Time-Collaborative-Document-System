package com.ezedin.demo.Document;

import com.ezedin.demo.user.AppUser;
import com.ezedin.demo.user.AppUserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class documentService {
    private final documentRepository repository ;
    private final AppUserRepository appUserRepository;

    public document createForUser(String username, String initialContent, boolean publicAccess) {
        AppUser owner = findUserByUsername(username);

        document newDocument = document.builder()
                .content(initialContent == null ? "" : initialContent)
                .publicAccess(publicAccess)
                .owner(owner)
                .build();

        return repository.save(newDocument);
    }

    public document getAccessibleDocument(Long documentId, String username) {
        document found = repository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

        if (!canAccess(found, username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this document");
        }

        return found;
    }

    public List<DocumentSummary> listForUser(String username) {
        AppUser owner = findUserByUsername(username);

        return repository.findAllByOwnerIdOrderByIdDesc(owner.getId())
                .stream()
                .map(DocumentSummary::from)
                .toList();
    }

    public documentEvent save(documentEvent newDocument, String username) {
        if (newDocument.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document id is required");
        }

        document existing = repository.findById(newDocument.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

        if (!canEdit(existing, username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this document");
        }

        existing.setContent(newDocument.getContent());
        repository.save(existing);

        newDocument.setId(existing.getId());
        return newDocument;
    }

    public boolean isOwner(Long documentId, String username) {
        AppUser owner = appUserRepository.findByUsername(username).orElse(null);
        if (owner == null) {
            return false;
        }
        return repository.existsByIdAndOwnerId(documentId, owner.getId());
    }

    public boolean canAccess(Long documentId, String username) {
        document found = repository.findById(documentId).orElse(null);
        return found != null && canAccess(found, username);
    }

    private boolean canAccess(document doc, String username) {
        if (doc.isPublicAccess()) {
            return true;
        }
        return isOwnerDocument(doc, username);
    }

    private boolean canEdit(document doc, String username) {
        // Public documents are collaborative: any authenticated user can edit.
        if (doc.isPublicAccess()) {
            return true;
        }
        return isOwnerDocument(doc, username);
    }

    private boolean isOwnerDocument(document doc, String username) {
        return doc.getOwner() != null && username != null && username.equals(doc.getOwner().getUsername());
    }

    private AppUser findUserByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
