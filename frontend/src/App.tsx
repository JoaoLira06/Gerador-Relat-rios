import React from 'react';
import { AuthProvider } from './context/AuthContext';
import AppRouter from './routes/AppRouter';

/**
 * Componente raiz da aplicação.
 * AuthProvider envolve tudo para que qualquer componente
 * possa acessar o contexto de autenticação.
 */
const App: React.FC = () => {
  return (
    <AuthProvider>
      <AppRouter />
    </AuthProvider>
  );
};

export default App;
