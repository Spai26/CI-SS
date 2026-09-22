import { useState, useEffect } from "react";
import { apiClient, ApiError } from "../api/client";
import { CuidadorResponse } from "../pages/Home";

type AdultoMayor = {
  id: number;
  nombre: string;
  apellido: string;
  necesidades_especificas: string;
};

export default function BookingModal({
  cuidador,
  onClose,
}: {
  cuidador: CuidadorResponse;
  onClose: () => void;
}) {
  const [adultos, setAdultos] = useState<AdultoMayor[]>([]);
  const [selectedAdultoId, setSelectedAdultoId] = useState<number | "">("");
  const [fechaInicio, setFechaInicio] = useState("");
  const [fechaFin, setFechaFin] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    // Fetch adultos mayores para la familia actual
    apiClient<AdultoMayor[]>("/familias/me/adultos-mayores")
      .then(setAdultos)
      .catch((err) => {
        if (err instanceof ApiError && err.status === 403) {
          setError("Solo las Familias pueden reservar.");
        } else {
          setError("Error al cargar tus familiares.");
        }
      });
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    
    if (!selectedAdultoId || !fechaInicio || !fechaFin) {
      setError("Por favor completa todos los campos");
      return;
    }

    setLoading(true);
    try {
      // Usar OffsetDateTime formateado, ej: 2026-09-25T10:00:00Z
      const inicio = new Date(fechaInicio).toISOString();
      const fin = new Date(fechaFin).toISOString();

      await apiClient("/reservas", {
        method: "POST",
        body: JSON.stringify({
          cuidador_id: cuidador.id,
          adulto_mayor_id: selectedAdultoId,
          fecha_inicio: inicio,
          fecha_fin: fin,
          tarifa_acordada: cuidador.tarifa_referencial,
        }),
      });

      setSuccess(true);
      setTimeout(() => {
        onClose();
        window.location.href = "/dashboard-familia";
      }, 2000);
    } catch (err: any) {
      setError(err instanceof ApiError ? err.message : "Error al crear la reserva");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden animate-in fade-in zoom-in-95">
        <div className="px-6 py-5 border-b border-slate-100 flex items-center justify-between">
          <h2 className="text-xl font-bold text-slate-800">Solicitar Reserva</h2>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-600">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        {success ? (
          <div className="p-8 text-center">
            <div className="w-16 h-16 rounded-full bg-[#E0F7FC] flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-[#00B4D8]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h3 className="text-lg font-bold text-slate-800">¡Reserva Solicitada!</h3>
            <p className="text-sm text-slate-500 mt-2">Redirigiendo a tu panel...</p>
          </div>
        ) : (
          <form className="p-6 space-y-4" onSubmit={handleSubmit}>
            {error && (
              <div className="p-3 bg-red-50 text-red-600 text-sm rounded-lg border border-red-100">
                {error}
              </div>
            )}

            <div className="bg-slate-50 p-4 rounded-xl flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-slate-700">
                  Cuidador: {cuidador.usuario.nombre} {cuidador.usuario.apellido}
                </p>
                <p className="text-xs text-slate-500">Tarifa referencial: S/ {cuidador.tarifa_referencial}/hr</p>
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">
                ¿Para quién es el servicio?
              </label>
              <select
                value={selectedAdultoId}
                onChange={(e) => setSelectedAdultoId(Number(e.target.value))}
                className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-[#00B4D8] focus:border-[#00B4D8] outline-none"
                required
              >
                <option value="" disabled>Selecciona un familiar...</option>
                {adultos.map(a => (
                  <option key={a.id} value={a.id}>{a.nombre} {a.apellido}</option>
                ))}
              </select>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Inicio</label>
                <input
                  type="datetime-local"
                  value={fechaInicio}
                  onChange={(e) => setFechaInicio(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-[#00B4D8] focus:border-[#00B4D8] outline-none text-sm"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1">Fin</label>
                <input
                  type="datetime-local"
                  value={fechaFin}
                  onChange={(e) => setFechaFin(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:ring-2 focus:ring-[#00B4D8] focus:border-[#00B4D8] outline-none text-sm"
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full mt-6 py-2.5 rounded-xl text-white font-bold transition-colors disabled:opacity-50"
              style={{ background: "#FF7F50" }}
            >
              {loading ? "Solicitando..." : "Confirmar Solicitud"}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
