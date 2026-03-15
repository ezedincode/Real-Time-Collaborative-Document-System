package com.ezedin.demo.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class documentEvent {
    private Long id;
    private String content;
    // Identify which client produced this event so that
    // the same client can ignore its own echoed messages.
    private String clientId;
}
