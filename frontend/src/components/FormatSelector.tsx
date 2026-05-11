import React from 'react';
import { FormControl, FormLabel, RadioGroup, FormControlLabel, Radio } from '@mui/material';
import type {OutputFormat} from '../types/report.types';

interface FormatSelectorProps {
    value: OutputFormat;
    onChange: (format: OutputFormat) => void;
    disabled?: boolean;
}

const FormatSelector: React.FC<FormatSelectorProps> = ({ value, onChange, disabled = false }) => {
    return (
        <FormControl>
            <FormLabel>Formato de Saída</FormLabel>
            <RadioGroup
                row
                value={value}
                onChange={(e) => onChange(e.target.value as OutputFormat)}
            >
                <FormControlLabel value="PDF" control={<Radio />} label="PDF" disabled={disabled} />
                <FormControlLabel value="EXCEL" control={<Radio />} label="Excel" disabled={disabled} />
            </RadioGroup>
        </FormControl>
    );
};

export default FormatSelector;
