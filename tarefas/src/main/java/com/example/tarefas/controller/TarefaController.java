package com.example.tarefas.controller;

import com.example.tarefas.model.Tarefa;
import com.example.tarefas.service.TarefaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import javax.validation.Valid;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.validation.FieldError;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    private final TarefaService service;

    public TarefaController(TarefaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody Tarefa tarefa, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, Object> response = new HashMap<>();
            StringBuilder errorMessage = new StringBuilder("Dados inválidos: ");
            
            for (FieldError error : result.getFieldErrors()) {
                errorMessage.append(String.format("Campo '%s' %s. ", error.getField(), error.getDefaultMessage()));
            }

            response.put("result", null);
            response.put("error", errorMessage.toString());
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Tarefa novaTarefa = service.salvar(tarefa);
            Map<String, Object> response = new HashMap<>();
            response.put("result", novaTarefa);
            response.put("error", null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("result", null);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable Long id, @Valid @RequestBody Tarefa novaTarefa, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, Object> response = new HashMap<>();
            StringBuilder errorMessage = new StringBuilder("Dados inválidos: ");
            
            for (FieldError error : result.getFieldErrors()) {
                errorMessage.append(String.format("Campo '%s' %s. ", error.getField(), error.getDefaultMessage()));
            }

            response.put("result", null);
            response.put("error", errorMessage.toString());
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Tarefa tarefaExistente = service.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));
            tarefaExistente.setNome(novaTarefa.getNome());
            tarefaExistente.setDataEntrega(novaTarefa.getDataEntrega());
            tarefaExistente.setResponsavel(novaTarefa.getResponsavel());

            Tarefa tarefaAtualizada = service.salvar(tarefaExistente);
            Map<String, Object> response = new HashMap<>();
            response.put("result", tarefaAtualizada);
            response.put("error", null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("result", null);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Método para listar todas as tarefas
    @GetMapping
    public ResponseEntity<Map<String, Object>> listarTodas() {
        try {
            List<Tarefa> tarefas = service.listarTodas();
            Map<String, Object> response = new HashMap<>();
            response.put("result", tarefas);
            response.put("error", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("result", null);
            response.put("error", "Erro ao listar tarefas.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) {
        try {
            Tarefa tarefa = service.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));
            Map<String, Object> response = new HashMap<>();
            response.put("result", tarefa);
            response.put("error", null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("result", null);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletar(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!service.buscarPorId(id).isPresent()) {
                response.put("result", null);
                response.put("error", "Tarefa não encontrada");
                return ResponseEntity.badRequest().body(response);
            }

            service.deletar(id);
            
            response.put("result", "Tarefa deletada com sucesso");
            response.put("error", null);
            return ResponseEntity.ok().body(response);
            
        } catch (IllegalArgumentException e) {
            response.put("result", null);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("result", null);
        response.put("error", "Erro: Método não permitido para este endpoint.");
        return ResponseEntity.status(405).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleRequestBodyMissing(HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("result", null);
        response.put("error", "Erro: Corpo da requisição está faltando ou está mal formado.");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleEndpointNotFound(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("result", null);
        response.put("error", "Erro: O endpoint solicitado não existe.");
        return ResponseEntity.status(404).body(response);
    }
}
