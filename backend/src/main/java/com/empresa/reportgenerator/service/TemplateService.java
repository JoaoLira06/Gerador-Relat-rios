package com.empresa.reportgenerator.service;

import com.empresa.reportgenerator.dto.template.CreateTemplateRequest;
import com.empresa.reportgenerator.dto.template.Template;
import com.empresa.reportgenerator.dto.template.UpdateTemplateRequest;
import com.empresa.reportgenerator.entity.TemplateEntity;
import com.empresa.reportgenerator.entity.enums.TemplateType;
import com.empresa.reportgenerator.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateParser templateParser;
    private final ValidationService validationService;

    @Cacheable(value = "templates", key = "#id")
    public Template getTemplate(Long id) {
        TemplateEntity entity = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template não encontrado" + id));

        return templateParser.parse(entity);
    }

    public List<Template> listTemplatesType(TemplateType type) {
            return templateRepository.findByType(type)
                    .stream()
                    .map(templateParser::parse)
                    .collect(Collectors.toList());
    }

    public Template createTemplate(CreateTemplateRequest request) {
        if (!templateParser.isValidSchemaJson(request.getSchemaDefinition())) {
            throw new IllegalArgumentException("Schema do template é inválido");
        }

        if(templateRepository.findByName(request.getName()).isPresent()){
            throw new IllegalArgumentException(
                    "Nome do template já existe " + request.getName());

        }

        String createdBy = SecurityContextHolder.getContext().
                getAuthentication().getName();

        TemplateEntity entity =  TemplateEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .schemaDefinition(request.getSchemaDefinition())
                .layoutDefinition(request.getLayoutDefinition())
                .createdBy(createdBy)
                .build();


        TemplateEntity saved = templateRepository.save(entity);


        return templateParser.parse(saved);

    }

    @CacheEvict(value = "templates", key = "#id")
    public Template updateTemplate(Long id, UpdateTemplateRequest request) {
        // 1. Busca o template existente
        TemplateEntity entity = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template não encontrado: " + id));

        // 2. Atualiza apenas os campos fornecidos (campos nulos são ignorados)
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getSchemaDefinition() != null) {
            if (!templateParser.isValidSchemaJson(request.getSchemaDefinition())) {
                throw new IllegalArgumentException("Schema do template é inválido");
            }
            entity.setSchemaDefinition(request.getSchemaDefinition());
        }
        if (request.getLayoutDefinition() != null) {
            entity.setLayoutDefinition(request.getLayoutDefinition());
        }

        // 3. Salva as alterações
        TemplateEntity updated = templateRepository.save(entity);

        // 4. Retorna o domain model atualizado
        return templateParser.parse(updated);
    }

    /**
     * Deleta um template pelo ID.
     * Apenas ADMIN pode deletar templates.
     * Invalida o cache do template deletado.
     *
     * @param id ID do template a ser deletado
     * @throws RuntimeException se o template não for encontrado
     */
    @Transactional
    @CacheEvict(value = "templates", key = "#id")
    public void deleteTemplate(Long id) {
        // Verifica se o template existe antes de deletar
        if (!templateRepository.existsById(id)) {
            throw new RuntimeException("Template não encontrado: " + id);
        }

        templateRepository.deleteById(id);
    }

}
