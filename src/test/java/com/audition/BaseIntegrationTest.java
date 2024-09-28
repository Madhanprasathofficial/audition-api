package com.audition;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class BaseIntegrationTest {


    @Autowired
    protected transient MockMvc mvc;

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    protected static final String POSTS = "/posts";
    protected static final String COMMENTS = "/comments";

    @SneakyThrows
    protected void verifyGet(final String path, final MediaType mediaType, final HttpStatus expected) {
        final var result = mvc.perform(MockMvcRequestBuilders
                        .get(path)
                        .accept(mediaType))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(expected.value());
    }
}
