// Tipos relacionados aos templates de relatório

/**
 * Tipos de campo disponíveis no schema do template
 */
export type FieldType = 'STRING' | 'NUMBER' | 'CURRENCY' | 'DATE' | 'BOOLEAN';

/**
 * Definição de um campo no schema do template
 */
export interface FieldDefinition {
  name: string;
  type: FieldType;
  required: boolean;
  description?: string;
}

/**
 * Schema do template — define quais campos o template espera
 */
export interface TemplateSchema {
  fields: FieldDefinition[];
}

/**
 * Template de relatório retornado pela API
 */
export interface Template {
  id: number;
  name: string;
  description?: string;
  type: 'PREDEFINED' | 'CUSTOM';
  schema: TemplateSchema;
  layout: Record<string, unknown>;
}
