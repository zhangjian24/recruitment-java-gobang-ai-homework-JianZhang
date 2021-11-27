package com.jianzhang.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jianzhang.dto.PositionInfo;
import com.jianzhang.entity.Game;
import com.jianzhang.entity.Position;
import com.jianzhang.gobang.Gobang;
import com.jianzhang.repository.GameRepository;
import com.jianzhang.repository.PositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameServiceImpl.class);

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Map newGame() {
        Game game = new Game();
        game.setComplete(NO);
        game.setWidth(WITH);
        gameRepository.save(game);

        Map result = new HashMap(2);
        result.put("gameId", game.getId());
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public Map placePosition(Long gameId, Integer x, Integer y) {
        if (Objects.isNull(gameId)) {
            throw new RuntimeException("gameId must not be null");
        }
        if (Objects.isNull(x) || x < 0 || x > WITH) {
            throw new RuntimeException("invalid x ");
        }
        if (Objects.isNull(y) || y < 0 || y > WITH) {
            throw new RuntimeException("invalid y ");
        }

        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (!gameOpt.isPresent()) {
            throw new RuntimeException("game not found");
        }
        Game game = gameOpt.get();
        if (YES.equals(game.getComplete())) {
            throw new RuntimeException("game is complete");
        }
        //游戏加锁
        entityManager.lock(game, LockModeType.PESSIMISTIC_WRITE);
        entityManager.flush();

        Position params = new Position();
        params.setGameId(gameId);
        List<Position> positions = positionRepository.findAll(Example.of(params));

        //构建棋谱
        Gobang gobang = new Gobang(WITH);
        String winner = NONE;
        boolean complete = false;
        List<Position> sortedPos = positions.stream().sorted(Comparator.comparing(Position::getId)).collect(Collectors.toList());
        for (Position sortedPo : sortedPos) {
            if (USER.equals(sortedPo.getFrom())) {
                complete = gobang.placeUserPosition(sortedPo.getX(), sortedPo.getY());
                if (complete) {
                    winner = USER;
                }
            } else {
                complete = gobang.placeAIPosition(sortedPo.getX(), sortedPo.getY());
                if (complete) {
                    winner = AI;
                }
            }
        }
        if (complete) {
            Map<String, Object> result = new HashMap<>();
            result.put("complete", complete);
            result.put("winner", winner);
            return result;
        }

        //用户落子结果
        complete = gobang.placeUserPosition(x, y);
        if (complete) {
            winner = USER;

            Position position = new Position();
            position.setGameId(gameId);
            position.setFrom(USER);
            position.setX(x);
            position.setY(y);
            positionRepository.save(position);

            game.setComplete(YES);
            game.setWinner(winner);
            gameRepository.save(game);

            Map<String, Object> result = new HashMap<>();
            result.put("complete", complete);
            result.put("winner", winner);
            return result;
        } else {
            Position position = new Position();
            position.setGameId(gameId);
            position.setFrom(USER);
            position.setX(x);
            position.setY(y);
            positionRepository.save(position);
        }

        //AI落子情况
        PositionInfo positionInfo = gobang.estimateAI();
        if (Objects.nonNull(positionInfo)) {
            complete = gobang.placeAIPosition(positionInfo.getX(), positionInfo.getY());
            Map<String, Object> result = new HashMap<>();
            if (complete) {
                winner = AI;

                Position position = new Position();
                position.setGameId(gameId);
                position.setFrom(AI);
                position.setX(positionInfo.getX());
                position.setY(positionInfo.getY());
                positionRepository.save(position);

                game.setComplete(YES);
                game.setWinner(winner);
                gameRepository.save(game);

                result.put("winner", winner);
            } else {
                Position position = new Position();
                position.setGameId(gameId);
                position.setFrom(AI);
                position.setX(positionInfo.getX());
                position.setY(positionInfo.getY());
                positionRepository.save(position);
            }
            result.put("complete", complete);
            result.put("aiNextPosition", positionInfo);
            return result;
        } else {
            complete = true;
            Map<String, Object> result = new HashMap<>();
            result.put("complete", complete);
            result.put("winner", winner);
            return result;
        }
    }

    @Override
    public Map gameList() {
        List<Game> games = gameRepository.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("games", games.stream().map(g -> {
            Map<String, Object> info = new HashMap<>();
            info.put("id", g.getId());
            info.put("complete", YES.equals(g.getComplete()));
            info.put("winner", g.getWinner());
            return info;
        }).collect(Collectors.toList()));
        return result;
    }

    @Override
    public Map gameInfo(Long gameId) {

        Optional<Game> game = gameRepository.findById(gameId);
        Map<String, Object> result = new HashMap<>();
        if (game.isPresent()) {
            Position param = new Position();
            param.setGameId(gameId);
            List<Position> positions = positionRepository.findAll(Example.of(param));

            result.put("gameId", gameId);
            result.put("complete", YES.equals(game.get().getComplete()));
            result.put("winner", game.get().getWinner());
            result.put("positions", positions.stream().map(p -> {
                Map<String, Object> pinfo = new HashMap<>();
                pinfo.put("from", p.getFrom());
                pinfo.put("position", new PositionInfo(p.getX(), p.getY()));
                return pinfo;
            }).collect(Collectors.toList()));
        }
        return result;
    }
}
