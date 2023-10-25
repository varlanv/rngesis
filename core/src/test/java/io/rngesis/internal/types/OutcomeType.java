package io.rngesis.internal.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class OutcomeType {

    private Long id;

    private String name;

    private Timestamp updateTime;

    private Map<Language, String> translations;
}
