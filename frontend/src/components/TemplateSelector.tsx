import React from 'react';
import { FormControl, InputLabel, Select, MenuItem } from '@mui/material';
import type { SelectChangeEvent } from '@mui/material';
import type { Template } from '../types/template.types';

interface TemplateSelectorProps {
    templates: Template[];
    selectedId: number | null;
    onChange: (id: number) => void;
    disabled?: boolean;
}

const TemplateSelector: React.FC<TemplateSelectorProps> = ({
                                                               templates,
                                                               selectedId,
                                                               onChange,
                                                               disabled = false,
                                                           }) => {
    const handleChange = (e: SelectChangeEvent<number>) => {
        onChange(Number(e.target.value));
    };

    return (
        <FormControl fullWidth>
            <InputLabel>Template</InputLabel>
            <Select
                value={selectedId ?? ''}
                label="Template"
                onChange={handleChange}
                disabled={disabled}
            >
                {templates.map((template) => (
                    <MenuItem key={template.id} value={template.id}>
                        {template.name}
                    </MenuItem>
                ))}
            </Select>
        </FormControl>
    );
};

export default TemplateSelector;
