import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';
import LoginPage from '../pages/LoginPage';
import ReportGeneratorPage from '../pages/ReportGeneratorPage';

/**
 * Configuração central de rotas da aplicação.
 */
const AppRouter: React.FC = () => {
    return (
        <BrowserRouter>
            <Routes>
                {/* Rota pública */}
                <Route path="/login" element={<LoginPage />} />

                {/* Rota protegida — precisa estar autenticado */}
                <Route
                    path="/"
                    element={
                        <ProtectedRoute>
                            <ReportGeneratorPage />
                        </ProtectedRoute>
                    }
                />

                {/* Redireciona qualquer rota desconhecida para / */}
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </BrowserRouter>
    );
};

export default AppRouter;
