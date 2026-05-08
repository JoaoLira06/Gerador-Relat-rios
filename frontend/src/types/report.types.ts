// Tipos relacionados à geração de relatórios

/**
 * Formato de saída do relatório
 */
export type OutputFormat = 'PDF' | 'EXCEL';

/**
 * Dados que preenchem o relatório
 * Cada registro é um objeto com chave-valor flexível
 */
export interface ReportData {
  records: Record<string, unknown>[];
  metadata?: Record<string, unknown>;
}

/**
 * Requisição para gerar um relatório
 */
export interface ReportRequest {
  templateId: number;
  outputFormat: OutputFormat;
  data: ReportData;
}

/**
 * Resposta de erro padronizada da API
 */
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: string[];
}
