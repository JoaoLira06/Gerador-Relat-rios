import React from 'react';
import { ThemeProvider, CssBaseline } from '@mui/material';
import { AuthProvider } from './context/AuthContext';
import AppRouter from './routes/AppRouter';
import theme from './theme';

const App: React.FC = () => {
    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <AuthProvider>
                <AppRouter />
            </AuthProvider>
        </ThemeProvider>
    );
};

export default App;
