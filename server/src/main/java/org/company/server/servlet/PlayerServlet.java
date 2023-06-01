package org.company.server.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.company.server.servlet.dto.CreatePlayerRequest;
import org.company.server.service.PlayerService;

import java.io.IOException;

public class PlayerServlet extends HttpServlet implements Bean {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private PlayerService playerService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final var players = playerService.getAll();
        final var json = objectMapper.writeValueAsString(players);

        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final var createPlayerRequest = objectMapper.readValue(request.getReader(), CreatePlayerRequest.class);
        final var player = playerService.create(createPlayerRequest);

        response.setContentType("application/json");
        response.setStatus(player == null ? 400 : 201);

        if (player != null) {
            final var json = objectMapper.writeValueAsString(player);
            response.getWriter().write(json);
        }
    }

    @Override
    public void init(ApplicationContext context) {
        playerService = context.getBean(PlayerService.class);
    }
}
