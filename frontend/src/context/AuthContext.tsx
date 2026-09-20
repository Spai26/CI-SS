import { createContext, useContext, useState, useEffect, ReactNode } from "react";

export type User = {
  id: number;
  email: string;
  nombre: string;
  apellido: string;
  telefono: string;
  estado: string;
  emailVerificado: boolean;
};

type AuthContextType = {
  user: User | null;
  token: string | null;
  login: (token: string, user: User) => void;
  logout: () => void;
  isAuthenticated: boolean;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);

  useEffect(() => {
    const storedToken = localStorage.getItem("jwt_token");
    const storedUser = localStorage.getItem("user_data");
    
    if (storedToken && storedUser) {
      try {
        setToken(storedToken);
        setUser(JSON.parse(storedUser));
      } catch (e) {
        console.error("Error parsing stored user data", e);
        logout();
      }
    }
  }, []);

  const login = (newToken: string, newUser: User) => {
    localStorage.setItem("jwt_token", newToken);
    localStorage.setItem("user_data", JSON.stringify(newUser));
    setToken(newToken);
    setUser(newUser);
  };

  const logout = () => {
    localStorage.removeItem("jwt_token");
    localStorage.removeItem("user_data");
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
