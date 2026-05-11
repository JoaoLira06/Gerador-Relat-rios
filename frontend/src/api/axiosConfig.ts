import axios from 'axios';

// Cria uma instância do Axios com configurações base
const api = axios.create({
    baseURL: '/api', // usa o proxy do vite.config.ts
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor de REQUEST — adiciona o token JWT em toda requisição
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Interceptor de RESPONSE — trata erros globalmente
api.interceptors.response.use(
    (response) => response,
    (error) => {
        // Se receber 401 (não autorizado), limpa o localStorage e redireciona para login
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;
