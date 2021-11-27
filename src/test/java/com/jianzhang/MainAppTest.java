package com.jianzhang;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jianzhang.dto.PositionInfo;
import com.jianzhang.entity.Game;
import com.jianzhang.repository.GameRepository;
import com.jianzhang.repository.PositionRepository;
import com.jianzhang.service.GameService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.JsonPathExpectationsHelper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MainAppTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainAppTest.class);

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void newGameTest() throws Exception {
        MvcResult res = mockMvc.perform(post("/games"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").exists()).andReturn();
    }

    @Test
    public void gameListTest() throws Exception {
        mockMvc.perform(get("/games"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.games.*.id").exists());
    }

    @Test
    public void gameInfoTest() throws Exception {
        mockMvc.perform(get("/games/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").exists());
    }

    @Test
    public void positionsTest() throws Exception {
        mockMvc.perform(post("/games/1/positions").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PositionInfo(13, 14))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.complete").exists());
    }
}
