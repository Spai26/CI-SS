import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import DashboardFamilia from "./pages/DashboardFamilia";
import DashboardCuidador from "./pages/DashboardCuidador";
import DashboardAdmin from "./pages/DashboardAdmin";

function ProtectedRoute({ children, role }: { children: React.ReactNode, role?: string }) {
  const { isAuthenticated, user } = useAuth();
  
  if (!isAuthenticated) return <Navigate to="/login" />;
  // Temporary: remove role check to allow any active user to see the dashboard while roles are implemented
  // if (role && user?.estado !== role) return <Navigate to="/" />;
  
  return children;
}

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/registro" element={<Register />} />
          <Route path="/dashboard-familia" element={
            <ProtectedRoute role="F">
              <DashboardFamilia />
            </ProtectedRoute>
          } />
          <Route path="/dashboard-cuidador" element={
            <ProtectedRoute role="C">
              <DashboardCuidador />
            </ProtectedRoute>
          } />
          <Route path="/dashboard-admin" element={
            <ProtectedRoute role="A">
              <DashboardAdmin />
            </ProtectedRoute>
          } />
        </Routes>
      </Router>
    </AuthProvider>
  );
}
