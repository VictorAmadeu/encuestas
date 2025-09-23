package com.acme.encuestas.dto;

public record RegisterRequest(String email, String password, String role) { }