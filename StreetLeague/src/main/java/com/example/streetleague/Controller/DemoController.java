package com.example.streetleague.Controller;

import org.springframework.web.bind.annotation.*;

/**
 * Étape 14 : DemoController — Endpoints protégés par rôle
 * Permet de tester le contrôle d'accès basé sur les rôles (RBAC)
 *
 * /student/hello → accessible uniquement avec ROLE_STUDENT
 * /teacher/hello → accessible uniquement avec ROLE_TEACHER
 *
 * Ces restrictions sont configurées dans SecurityConfig.
 */
@RestController
@CrossOrigin("*")
@RequestMapping("")
public class DemoController {


    @GetMapping("/student/hello")
    public String etu() {
        return "Hello STUDENT";
    }


    @GetMapping("/teacher/hello")
    public String ens() {
        return "Hello TEACHER";
    }
}

