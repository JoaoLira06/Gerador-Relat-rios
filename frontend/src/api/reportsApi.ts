import api from './axiosConfig';
import type { ReportRequest } from '../types/report.types';

/**
 * Gera um relatório e retorna os bytes do arquivo
 * responseType: 'blob' indica que a resposta é um arquivo binário
 */
export const generateReport = async (request: ReportRequest): Promise<Blob> => {
    const response = await api.post('/reports/generate', request, {
        responseType: 'blob', // importante para receber arquivos!
    });
    return response.data;
};
