package com.syncstate.go.cardinal.inside.ShiftKrew.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class JsonDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate localDate, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Date utcDate = this.localDateToUtcDate(localDate, ZoneId.systemDefault());
        gen.writeString(new SimpleDateFormat("yyyy-MM-dd").format(utcDate));
    }


    public static Date localDateToUtcDate(LocalDate localDate, ZoneId zoneId) {
        Date date;
        if (localDate == null || zoneId == null) {
            date = null;
        } else {
            date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        return date;
    }
}