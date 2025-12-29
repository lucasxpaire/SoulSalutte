package com.soulsalutte.soulsalutte.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosWebhookWhatsApp(
        String object,
        Entry[] entry
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Entry(
            String id,
            Change[] changes
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Change(
            String field,
            Value value
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Value(
            String messaging_product,
            Message[] messages,
            Contact[] contacts
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(
            String from,
            String id,
            String timestamp,
            Text text
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Text(
            String body
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Contact(
            String wa_id,
            Profile profile
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Profile(
            String name
    ) {
    }
}

