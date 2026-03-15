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
}
