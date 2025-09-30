package com.kshrd.jpahibernate02_homework.controller;

import com.kshrd.jpahibernate02_homework.service.ShellScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ScriptController {

    @Autowired
    private ShellScriptService shellScriptService;

    @PostMapping("/execute")
    public String executeScript(@RequestBody ScriptRequest request) {
        return shellScriptService.executeScript(request.getScriptName());
    }
}

class ScriptRequest {
    private String scriptName;

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }
}