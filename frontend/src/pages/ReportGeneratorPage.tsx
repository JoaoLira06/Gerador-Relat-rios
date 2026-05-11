import React, { useState, useEffect } from 'react';
import {
    Box, AppBar, Toolbar, Typography, Button,
    Container, Paper, Stack, Alert, Snackbar,
    CircularProgress, Divider
} from '@mui/material';
import ReportForm from '../components/ReportForm';
import LogoutIcon from '@mui/icons-material/Logout';
import { useAuth } from '../hooks/useAuth';
import { getTemplates } from '../api/templatesApi';
import { generateReport } from '../api/reportsApi';
import { downloadFile } from '../utils/downloadFile';
import TemplateSelector from '../components/TemplateSelector';
import FormatSelector from '../components/FormatSelector';
import type { Template } from '../types/template.types';
import type { OutputFormat } from '../types/report.types';

const ReportGeneratorPage: React.FC = () => {
    const { user, logout } = useAuth();

    // Estado dos templates
    const [templates, setTemplates] = useState<Template[]>([]);
    const [selectedTemplateId, setSelectedTemplateId] = useState<number | null>(null);
    const [outputFormat, setOutputFormat] = useState<OutputFormat>('PDF');

    // Estado da geração
    const [loading, setLoading] = useState(false);
    const [loadingTemplates, setLoadingTemplates] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [success, setSuccess] = useState(false);



    // Carrega os templates ao montar a página
    useEffect(() => {
        const fetchTemplates = async () => {
            try {
                const data = await getTemplates();
                setTemplates(data);
                if (data.length > 0) {
                    setSelectedTemplateId(data[0].id);
                }
            } catch {
                setError('Erro ao carregar templates.');
            } finally {
                setLoadingTemplates(false);
            }
        };
        fetchTemplates();
    }, []);

    const handleGenerate = async (records: Record<string, unknown>[]) => {
        if (!selectedTemplateId) return;

        setLoading(true);
        setError(null);

        try {
            const blob = await generateReport({
                templateId: selectedTemplateId,
                outputFormat,
                data: { records },
            });

            const template = templates.find(t => t.id === selectedTemplateId);
            const ext = outputFormat === 'PDF' ? '.pdf' : '.xlsx';
            const filename = `relatorio-${template?.name ?? 'report'}${ext}`;

            downloadFile(blob, filename);
            setSuccess(true);
        } catch {
            setError('Erro ao gerar relatório. Tente novamente.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box sx={{ minHeight: '100vh', backgroundColor: '#f5f5f5' }}>
            {/* AppBar — barra superior */}
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6" sx={{ flexGrow: 1 }}>
                        Gerador de Relatórios
                    </Typography>
                    <Typography variant="body2" sx={{ mr: 2 }}>
                        {user?.username} ({user?.role})
                    </Typography>
                    <Button color="inherit" startIcon={<LogoutIcon />} onClick={logout}>
                        Sair
                    </Button>
                </Toolbar>
            </AppBar>

            {/* Conteúdo principal */}
            <Container maxWidth="md" sx={{ mt: 4 }}>
                <Paper sx={{ p: 4 }}>
                    <Typography variant="h5" gutterBottom>
                        Gerar Relatório
                    </Typography>
                    <Divider sx={{ mb: 3 }} />

                    {loadingTemplates ? (
                        <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
                            <CircularProgress />
                        </Box>
                    ) : (
                        <Stack spacing={3}>
                            {/* Seleção de template */}
                            <TemplateSelector
                                templates={templates}
                                selectedId={selectedTemplateId}
                                onChange={setSelectedTemplateId}
                                disabled={loading}
                            />

                            {/* Seleção de formato */}
                            <FormatSelector
                                value={outputFormat}
                                onChange={setOutputFormat}
                                disabled={loading}
                            />

                            {/* Dados do relatório */}
                            {error && <Alert severity="error">{error}</Alert>}

                            {selectedTemplateId && templates.find(t => t.id === selectedTemplateId) && (
                                <ReportForm
                                    template={templates.find(t => t.id === selectedTemplateId)!}
                                    onSubmit={handleGenerate}
                                    loading={loading}
                                />
                            )}

                        </Stack>
                    )}
                </Paper>
            </Container>

            {/* Snackbar de sucesso */}
            <Snackbar
                open={success}
                autoHideDuration={4000}
                onClose={() => setSuccess(false)}
                message="Relatório gerado com sucesso!"
            />
        </Box>
    );
};

export default ReportGeneratorPage;
