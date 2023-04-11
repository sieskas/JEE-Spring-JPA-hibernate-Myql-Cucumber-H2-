package com.example.demo.app.v1.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResource implements Serializable {
    private String username;
    private String email;
}
