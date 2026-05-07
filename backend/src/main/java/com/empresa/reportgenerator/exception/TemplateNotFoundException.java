package com.empresa.reportgenerator.exception;

public class TemplateNotFoundException extends BusinessException {
    public TemplateNotFoundException(Long id) {
        super("Template não encontrado: " + id);
    }
    public TemplateNotFoundException(String name) {
        super("Template não encontrado: " + name);
    }
}
