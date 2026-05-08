import React, { createContext, useState, useEffect } from 'react';
import {AuthContextType, AuthenticationResponse} from "../types/auth.types";
import {login as loginApi, logout as logoutApi} from "../api/authApi";

export const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider: React.FC<{children: React.ReactNode}> = ({ children }) => {

    const [user, setUser] = useState<AuthenticationResponse | null>(null);

    useEffect(() =>{
        const savedUSer  = localStorage.getItem("user");
        if (savedUSer) {
            setUser(JSON.parse(savedUSer));
        }
    }, [])};
// Função de login
const login = async (username: string, password: string) => {
    const response = await loginApi({ username, password });
    setUser(response);
    localStorage.setItem('user', JSON.stringify(response));
    localStorage.setItem('token', response.token);
};

// Função de logout
const logout = () => {
    const token = localStorage.getItem('token');
    if (token) {
        logoutApi(token).catch(() => {}); // ignora erro no logout
    }
    setUser(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
};

return (
    <AuthContext.Provider value={{
        user,
        isAuthenticated: !!user,
        login,
        logout
    }}>
        {children}
    </AuthContext.Provider>
);
};