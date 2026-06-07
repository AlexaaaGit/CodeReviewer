import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { authApi } from '../api/authApi';
import type { AuthUser } from '../types';

/**
 * Authentication context — provides login/register/logout functionality
 * and the current user's info (including role) to all components.
 */

interface AuthContextType {
  user: AuthUser | null;
  token: string | null;
  login:    (username: string, password: string) => Promise<void>;
  register: (username: string, password: string) => Promise<void>;
  logout:   () => void;
  isJunior: boolean;
  isMentor: boolean;
  isAdmin:  boolean;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user,  setUser]  = useState<AuthUser | null>(null);
  const [token, setToken] = useState<string | null>(null);

  // On mount: restore session from localStorage
  useEffect(() => {
    const savedToken = localStorage.getItem('jwt_token');
    const savedUser  = localStorage.getItem('jwt_user');
    if (savedToken && savedUser) {
      setToken(savedToken);
      setUser(JSON.parse(savedUser));
    }
  }, []);

  const login = async (username: string, password: string) => {
    const response = await authApi.login(username, password);
    const { token: jwt, id, username: uname, role } = response.data;

    const authUser: AuthUser = { id, username: uname, role: role as AuthUser['role'] };
    setToken(jwt);
    setUser(authUser);
    localStorage.setItem('jwt_token', jwt);
    localStorage.setItem('jwt_user', JSON.stringify(authUser));
  };

  const register = async (username: string, password: string) => {
    await authApi.register(username, password);
    // After registration, auto-login
    await login(username, password);
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('jwt_user');
  };

  return (
    <AuthContext.Provider value={{
      user,
      token,
      login,
      register,
      logout,
      isJunior: user?.role === 'ROLE_JUNIOR',
      isMentor: user?.role === 'ROLE_MENTOR',
      isAdmin: user?.role === 'ROLE_ADMIN',
    }}>
      {children}
    </AuthContext.Provider>
  );
}

/** Hook to access auth context from any component. */
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside <AuthProvider>');
  return ctx;
}
