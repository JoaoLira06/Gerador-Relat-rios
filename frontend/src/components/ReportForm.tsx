import React, { useState } from 'react';
import {
    Box, Button, TextField, IconButton,
    Typography, Paper
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import type { Template } from '../types/template.types';

interface ReportFormProps {
    template: Template;
    onSubmit: (records: Record<string, unknown>[]) => void;
    loading: boolean;
}

const ReportForm: React.FC<ReportFormProps> = ({ template, onSubmit, loading }) => {
    // Cria uma linha vazia baseada nos campos do schema
    const createEmptyRow = (): Record<string, string> => {
        const row: Record<string, string> = {};
        template.schema?.fields?.forEach(field => {
            row[field.name] = '';
        });
        return row;
    };

    const [rows, setRows] = useState<Record<string, string>[]>([createEmptyRow()]);

    // Atualiza o valor de um campo em uma linha específica
    const handleChange = (rowIndex: number, fieldName: string, value: string) => {
        const updated = [...rows];
        updated[rowIndex] = { ...updated[rowIndex], [fieldName]: value };
        setRows(updated);
    };

    // Adiciona uma nova linha vazia
    const addRow = () => setRows([...rows, createEmptyRow()]);

    // Remove uma linha
    const removeRow = (index: number) => {
        if (rows.length > 1) {
            setRows(rows.filter((_, i) => i !== index));
        }
    };

    const handleSubmit = () => {
        // Converte strings para os tipos corretos baseado no schema
        const records = rows.map(row => {
            const record: Record<string, unknown> = {};
            template.schema?.fields?.forEach(field => {
                const value = row[field.name];
                if (field.type === 'NUMBER' || field.type === 'CURRENCY') {
                    record[field.name] = value ? parseFloat(value) : 0;
                } else {
                    record[field.name] = value;
                }
            });
            return record;
        });
        onSubmit(records);
    };

    // Retorna o tipo de input HTML baseado no tipo do campo
    const getInputType = (fieldType: string): string => {
        switch (fieldType) {
            case 'DATE': return 'date';
            case 'NUMBER':
            case 'CURRENCY': return 'number';
            default: return 'text';
        }
    };

    const fields = template.schema?.fields ?? [];

    return (
        <Box>
            <Typography variant="subtitle1" gutterBottom>
                Dados do Relatório
            </Typography>

            {rows.map((row, rowIndex) => (
                <Paper key={rowIndex} variant="outlined" sx={{ p: 2, mb: 2 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                        <Typography variant="body2" color="text.secondary">
                            Linha {rowIndex + 1}
                        </Typography>
                        {rows.length > 1 && (
                            <IconButton size="small" color="error" onClick={() => removeRow(rowIndex)}>
                                <DeleteIcon fontSize="small" />
                            </IconButton>
                        )}
                    </Box>

                    <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 2 }}>
                        {fields.map(field => (
                            <TextField
                                key={field.name}
                                label={field.name}
                                type={getInputType(field.type)}
                                value={row[field.name] ?? ''}
                                onChange={(e) => handleChange(rowIndex, field.name, e.target.value)}
                                required={field.required}
                                disabled={loading}
                                size="small"
                                slotProps={field.type === 'DATE' ? { inputLabel: { shrink: true } } : {}}

                            />
                        ))}
                    </Box>
                </Paper>
            ))}

            <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
                <Button
                    startIcon={<AddIcon />}
                    onClick={addRow}
                    disabled={loading}
                    variant="outlined"
                >
                    Adicionar Linha
                </Button>

                <Button
                    variant="contained"
                    onClick={handleSubmit}
                    disabled={loading}
                    sx={{ flexGrow: 1 }}
                >
                    Gerar Relatório
                </Button>
            </Box>
        </Box>
    );
};

export default ReportForm;
