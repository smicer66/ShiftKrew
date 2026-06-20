package com.syncstate.go.cardinal.inside.ShiftKrew.models.responses;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AutoGraphResponse {
    public int status;
    public String statusMessage;
    public Object responseData;

    public AutoGraphResponse()
    {

    }

    public AutoGraphResponse(int status, String statusMessage, Object responseData)
    {
        this.status = status;
        this.statusMessage = statusMessage;
        this.responseData = responseData;
    }
}
