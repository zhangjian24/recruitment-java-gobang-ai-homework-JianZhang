package com.jianzhang.controller;

import com.jianzhang.dto.PositionInfo;
import com.jianzhang.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class GameController {

    @Autowired
    GameService gameService;

    /**
     * 创建一轮棋局
     * @return
     */
    @PostMapping(value = "/games")
    @ResponseBody
    public ResponseEntity<Map> newGame(){
        return ResponseEntity.ok(gameService.newGame());
    }

    /**
     * 放置棋子
     * @param gameId
     * @param positionInfo
     * @return
     */
    @PostMapping(value = "/games/{gameId}/positions")
    @ResponseBody
    public ResponseEntity<Map> setPosition(@PathVariable(name = "gameId") Long gameId, @RequestBody PositionInfo positionInfo){
        return ResponseEntity.ok(gameService.placePosition(gameId,positionInfo.getX(),positionInfo.getY()));
    }

    /**
     * 棋局列表
     * @return
     */
    @GetMapping(value = "/games")
    @ResponseBody
    public ResponseEntity<Map> gameList(){
        return ResponseEntity.ok(gameService.gameList());
    }

    /**
     * 棋局详情
     * @param gameId
     * @return
     */
    @GetMapping(value = "/games/{gameId}")
    @ResponseBody
    public ResponseEntity<Map> gameInfo(@PathVariable(name = "gameId")Long gameId){
        return ResponseEntity.ok(gameService.gameInfo(gameId));
    }
}
