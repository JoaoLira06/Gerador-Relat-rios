import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Box, Card, CardContent, TextField, Button,
    Typography, Alert, CircularProgress, InputAdornment,
    IconButton, Divider
} from '@mui/material';
import {
    PersonOutlined as PersonOutlineIcon,
    LockOutlined as LockOutlinedIcon,
    Visibility,
    VisibilityOff,
    Assessment as AssessmentIcon
} from '@mui/icons-material';

import { useAuth } from '../hooks/useAuth';

const LoginPage: React.FC = () => {
    const navigate = useNavigate();
    const { login } = useAuth();

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        try {
            await login(username, password);
            navigate('/');
        } catch {
            setError('Usuário ou senha inválidos. Tente novamente.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box sx={{
            minHeight: '100vh',
            display: 'flex',
            background: 'linear-gradient(135deg, #1565C0 0%, #0D47A1 50%, #00897B 100%)',
        }}>
            {/* Lado esquerdo — branding */}
            <Box sx={{
                flex: 1,
                display: { xs: 'none', md: 'flex' },
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                p: 6,
                color: 'white',
            }}>
                <AssessmentIcon sx={{ fontSize: 80, mb: 3, opacity: 0.9 }} />
                <Typography variant="h3" gutterBottom sx = {{fontWeight: 'bold'}} >
                    ReportGen
                </Typography>
                <Typography variant="h6" sx={{ opacity: 0.85, textAlign: 'center', maxWidth: 360 }}>
                    Gerador de Relatórios Empresariais
                </Typography>
                <Divider sx={{ width: 60, borderColor: 'rgba(255,255,255,0.4)', my: 3 }} />
                <Typography variant="body1" sx={{ opacity: 0.75, textAlign: 'center', maxWidth: 320 }}>
                    Gere relatórios profissionais em PDF e Excel com apenas alguns cliques.
                </Typography>
            </Box>

            {/* Lado direito — formulário */}
            <Box sx={{
                flex: { xs: 1, md: '0 0 460px' },
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                p: 3,
                backgroundColor: 'background.default',
                borderRadius: { md: '24px 0 0 24px' },
            }}>
                <Card sx={{ width: '100%', maxWidth: 400, p: 2 }}>
                    <CardContent>
                        {/* Header mobile */}
                        <Box sx={{ display: { xs: 'flex', md: 'none' }, justifyContent: 'center', mb: 3 }}>
                            <AssessmentIcon sx={{ fontSize: 48, color: 'primary.main' }} />
                        </Box>

                        <Typography variant="h5"  gutterBottom sx = {{ fontWeight:'bold'}}>
                            Bem-vindo de volta!
                        </Typography>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                            Faça login para acessar o sistema
                        </Typography>

                        {error && (
                            <Alert severity="error" sx={{ mb: 2, borderRadius: 2 }}>
                                {error}
                            </Alert>
                        )}

                        <form onSubmit={handleSubmit}>
                            <TextField
                                label="Usuário"
                                fullWidth
                                margin="normal"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                disabled={loading}
                                required
                                slotProps={{
                                    input: {
                                        startAdornment: (
                                            <InputAdornment position="start">
                                                <PersonOutlineIcon color="action" />
                                            </InputAdornment>
                                        ),
                                    }
                                }}
                            />
                            <TextField
                                label="Senha"
                                type={showPassword ? 'text' : 'password'}
                                fullWidth
                                margin="normal"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                disabled={loading}
                                required
                                slotProps={{
                                    input: {
                                        startAdornment: (
                                            <InputAdornment position="start">
                                                <LockOutlinedIcon color="action" />
                                            </InputAdornment>
                                        ),
                                        endAdornment: (
                                            <InputAdornment position="end">
                                                <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                                                    {showPassword ? <VisibilityOff /> : <Visibility />}
                                                </IconButton>
                                            </InputAdornment>
                                        ),
                                    }
                                }}
                            />
                            <Button
                                type="submit"
                                variant="contained"
                                fullWidth
                                size="large"
                                sx={{ mt: 3, py: 1.5 }}
                                disabled={loading}
                            >
                                {loading ? <CircularProgress size={24} color="inherit" /> : 'Entrar'}
                            </Button>
                        </form>
                    </CardContent>
                </Card>
            </Box>
        </Box>
    );
};

export default LoginPage;
