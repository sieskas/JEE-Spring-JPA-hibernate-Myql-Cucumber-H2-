package com.example.demo.app.v1.resources;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserResource implements Serializable {
    private String username;
    private String email;
}
