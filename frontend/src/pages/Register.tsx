import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

export default function Register() {
  const [formData, setFormData] = useState({
    nombre: "",
    apellido: "",
    email: "",
    password: "",
    telefono: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await fetch("/api/v1/auth/registro", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.detail || "Error al registrar usuario");
      }

      // Auto-verificación en entorno de desarrollo
      const devToken = response.headers.get("X-Dev-Verification-Token");
      if (devToken) {
        await fetch(`/api/v1/auth/verificar-email?token=${devToken}`, { method: "POST" });
      }

      setSuccess(true);
      setTimeout(() => {
        navigate("/login");
      }, 3000);
    } catch (err: any) {
      setError(err.message || "Ocurrió un error inesperado");
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="min-h-screen bg-[#F8F9FA] flex flex-col justify-center py-12 sm:px-6 lg:px-8">
        <div className="sm:mx-auto sm:w-full sm:max-w-md bg-white p-8 rounded-xl shadow text-center">
          <div className="w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4" style={{ background: "#E0F7FC" }}>
            <svg className="w-8 h-8" style={{ color: "#00B4D8" }} fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h2 className="text-2xl font-bold text-gray-900 mb-2">¡Registro exitoso!</h2>
          <p className="text-gray-600 mb-4">Te hemos enviado un correo para verificar tu cuenta. Serás redirigido al login en breve.</p>
          <Link to="/login" className="text-[#00B4D8] font-medium hover:underline">
            Ir al inicio de sesión
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#F8F9FA] flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center items-center gap-2">
          <div className="w-10 h-10 rounded-xl flex items-center justify-center" style={{ background: "#00B4D8" }}>
            <svg className="w-6 h-6 text-white" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z" />
            </svg>
          </div>
          <span className="text-3xl font-700 text-slate-800">
            Cuid<span style={{ color: "#00B4D8" }}>AR</span>
          </span>
        </div>
        <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Crea tu cuenta
        </h2>
        <p className="mt-2 text-center text-sm text-gray-600">
          ¿Ya tienes cuenta?{" "}
          <Link to="/login" className="font-medium text-[#00B4D8] hover:text-[#0096B4]">
            Inicia sesión aquí
          </Link>
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
          <form className="space-y-6" onSubmit={handleSubmit}>
            {error && (
              <div className="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-md text-sm">
                {error}
              </div>
            )}
            
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label htmlFor="nombre" className="block text-sm font-medium text-gray-700">Nombre</label>
                <input
                  id="nombre" name="nombre" type="text" required
                  value={formData.nombre} onChange={handleChange}
                  className="mt-1 appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#00B4D8] focus:border-[#00B4D8] sm:text-sm"
                />
              </div>
              <div>
                <label htmlFor="apellido" className="block text-sm font-medium text-gray-700">Apellido</label>
                <input
                  id="apellido" name="apellido" type="text" required
                  value={formData.apellido} onChange={handleChange}
                  className="mt-1 appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#00B4D8] focus:border-[#00B4D8] sm:text-sm"
                />
              </div>
            </div>

            <div>
              <label htmlFor="telefono" className="block text-sm font-medium text-gray-700">Teléfono</label>
              <input
                id="telefono" name="telefono" type="tel" required
                value={formData.telefono} onChange={handleChange}
                className="mt-1 appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#00B4D8] focus:border-[#00B4D8] sm:text-sm"
              />
            </div>

            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700">Correo electrónico</label>
              <input
                id="email" name="email" type="email" required
                value={formData.email} onChange={handleChange}
                className="mt-1 appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#00B4D8] focus:border-[#00B4D8] sm:text-sm"
              />
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700">Contraseña</label>
              <input
                id="password" name="password" type="password" required minLength={8}
                value={formData.password} onChange={handleChange}
                className="mt-1 appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#00B4D8] focus:border-[#00B4D8] sm:text-sm"
              />
            </div>

            <div>
              <button
                type="submit" disabled={loading}
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white hover:opacity-90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#00B4D8] disabled:opacity-50"
                style={{ background: "#FF7F50" }}
              >
                {loading ? "Registrando..." : "Crear cuenta"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
