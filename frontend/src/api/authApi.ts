import api from './axiosConfig';
import type { AuthenticationRequest, AuthenticationResponse } from '../types/auth.types';

/**
 * Realiza o login do usuário
 */
export const login = async (request: AuthenticationRequest): Promise<AuthenticationResponse> => {
    const response = await api.post<AuthenticationResponse>('/auth/login', request);
    return response.data;
};

/**
 * Realiza o logout do usuário
 */
export const logout = async (token: string): Promise<void> => {
    await api.post('/auth/logout', null, {
        headers: { Authorization: `Bearer ${token}` }
    });
};
