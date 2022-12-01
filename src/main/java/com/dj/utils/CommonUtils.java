package com.dj.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
public class CommonUtils {

    public static Collection<?> getData(Object value) {
        if (value instanceof String) {
            return Arrays.stream(((String) value).split(" ")).collect(Collectors.toList());
        } else if (value instanceof Collection) {
            return (Collection<?>) value;
        } else {
            return Collections.emptyList();
        }
    }

    public static void printOutputStream(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        OutputStream out = httpServletResponse.getOutputStream();
        out.write(HttpStatus.UNAUTHORIZED.toString().getBytes());
        out.flush();
    }
}
