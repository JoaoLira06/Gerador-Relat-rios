import api from './axiosConfig';
import type { Template } from '../types/template.types';

/**
 * Busca todos os templates disponíveis
 */
export const getTemplates = async (): Promise<Template[]> => {
    const response = await api.get<Template[]>('/templates');
    return response.data;
};
