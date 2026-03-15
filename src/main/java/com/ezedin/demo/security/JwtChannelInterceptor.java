package com.ezedin.demo.security;

import com.ezedin.demo.Document.documentService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final Pattern DOCUMENT_SUBSCRIBE_PATTERN = Pattern.compile("^/topic/document/(\\d+)$");

    private final JwtService jwtService;
    private final documentService documentService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authorization = accessor.getFirstNativeHeader("Authorization");
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new AccessDeniedException("Missing Authorization header");
            }

            String token = authorization.substring(7);
            String username = jwtService.extractUsername(token);
            if (!jwtService.isTokenValid(token, username)) {
                throw new AccessDeniedException("Invalid token");
            }

            accessor.setUser(new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            ));
        }

        if ((StompCommand.SUBSCRIBE.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand()))
                && accessor.getUser() == null) {
            throw new AccessDeniedException("Unauthenticated websocket operation");
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            if (destination != null) {
                Matcher matcher = DOCUMENT_SUBSCRIBE_PATTERN.matcher(destination);
                if (matcher.matches()) {
                    Long documentId = Long.parseLong(matcher.group(1));
                    String username = accessor.getUser().getName();
                    boolean canAccess = documentService.canAccess(documentId, username);
                    if (!canAccess) {
                        throw new AccessDeniedException("Forbidden subscription for this document");
                    }
                }
            }
        }

        return message;
    }
}
