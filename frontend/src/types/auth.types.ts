// Tipos relacionados à autenticação

/**
 * Dados enviados no login
 */
export interface AuthenticationRequest {
  username: string;
  password: string;
}

/**
 * Resposta do backend após login bem-sucedido
 */
export interface AuthenticationResponse {
  token: string;
  username: string;
  role: 'USER' | 'ADMIN';
}

/**
 * Estado do contexto de autenticação
 */
export interface AuthContextType {
  user: AuthenticationResponse | null;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
}
