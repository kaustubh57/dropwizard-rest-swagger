package com.learning.realtime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by kaustubh on 3/21/16.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Operation {

    @JsonProperty("opType")
    private OperationType opType;

    @JsonProperty("revision")
    private Long revision;

    @JsonProperty("message")
    private String message;

    public Operation(OperationType opType, String message) {
        this.opType = opType;
        this.message = message;
    }

}
