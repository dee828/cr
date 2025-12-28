package com.example.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class HelloControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    public void testHello() throws Exception {
       // 谁谁谁.帮我打开浏览器输入/,验证浏览器显示的hello
        mockMvc.perform(get("/"))
                .andExpect(content().string("hello"))
        ;
    }

    @Test
    public void testHi() throws Exception {
        // 谁谁谁.帮我打开浏览器输入/,验证浏览器显示的hello
        mockMvc.perform(get("/hi"))
                .andExpect(content().string("hi"))
        ;
    }
}
