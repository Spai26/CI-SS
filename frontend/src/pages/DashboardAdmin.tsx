import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { apiClient } from "../api/client";
import { useAuth } from "../context/AuthContext";

type Solicitud = {
  id: number;
  cuidador_id: number;
  fecha_solicitud: string;
  estado: string;
  documentos: {
    id: number;
    tipo_documento: string;
    archivo_url: string;
  }[];
};

export default function DashboardAdmin() {
  const { user } = useAuth();
  const [solicitudes, setSolicitudes] = useState<Solicitud[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchSolicitudes();
  }, []);

  const fetchSolicitudes = () => {
    setLoading(true);
    apiClient<Solicitud[]>("/admin/verificaciones")
      .then(setSolicitudes)
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  const resolverSolicitud = async (id: number, nuevoEstado: string) => {
    try {
      await apiClient(`/admin/verificaciones/${id}/resolucion`, {
        method: "PUT",
        body: JSON.stringify({ nuevoEstado, observaciones: "" }),
      });
      fetchSolicitudes();
    } catch (err) {
      console.error(err);
      alert("Error al resolver la solicitud");
    }
  };

  return (
    <div className="min-h-screen bg-[#F8F9FA] pb-12">
      {/* ── NAV ── */}
      <header className="bg-white border-b border-slate-100 shadow-sm">
        <div className="max-w-[1024px] mx-auto px-6 h-16 flex items-center justify-between">
          <Link to="/" className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-xl flex items-center justify-center bg-slate-800">
              <svg className="w-5 h-5 text-white" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z"/>
              </svg>
            </div>
            <span className="text-xl font-700 text-slate-800">AdminPanel</span>
          </Link>
          <div className="flex items-center gap-4">
            <Link to="/" className="text-sm font-medium text-slate-500 hover:text-slate-800">Ir al portal</Link>
          </div>
        </div>
      </header>

      <main className="max-w-[1024px] mx-auto px-6 pt-10">
        <h1 className="text-2xl font-bold text-slate-800 mb-2">Panel de Administración</h1>
        <p className="text-slate-500 mb-8">Gestión de verificaciones de cuidadores.</p>

        <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
          <h2 className="text-lg font-bold text-slate-800 mb-6">Solicitudes Pendientes</h2>
          
          {loading ? (
            <p className="text-slate-500 text-sm">Cargando...</p>
          ) : solicitudes.length === 0 ? (
            <div className="text-center py-10">
              <div className="w-16 h-16 rounded-full bg-slate-50 flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <p className="text-slate-500 font-medium">No hay solicitudes pendientes.</p>
            </div>
          ) : (
            <div className="space-y-4">
              {solicitudes.map(s => (
                <div key={s.id} className="border border-slate-100 rounded-xl p-5 flex items-center justify-between">
                  <div>
                    <h3 className="font-bold text-slate-800">Solicitud #{s.id} (Cuidador ID: {s.cuidador_id})</h3>
                    <p className="text-sm text-slate-500 mb-2">
                      Recibida el: {new Date(s.fecha_solicitud).toLocaleString()}
                    </p>
                    
                    <div className="flex gap-2">
                      {s.documentos.map(doc => (
                        <a 
                          key={doc.id}
                          href={doc.archivo_url} 
                          target="_blank" 
                          rel="noreferrer"
                          className="inline-flex items-center gap-1 px-2 py-1 bg-slate-50 border border-slate-200 rounded text-xs font-medium text-slate-600 hover:bg-slate-100"
                        >
                          <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
                          </svg>
                          {doc.tipo_documento}
                        </a>
                      ))}
                    </div>
                  </div>
                  
                  <div className="flex gap-2">
                    <button 
                      onClick={() => resolverSolicitud(s.id, "RECHAZADA")}
                      className="text-sm font-medium px-4 py-2 border border-red-200 text-red-600 rounded-lg hover:bg-red-50"
                    >
                      Rechazar
                    </button>
                    <button 
                      onClick={() => resolverSolicitud(s.id, "APROBADA")}
                      className="text-sm font-bold px-4 py-2 rounded-lg text-white transition-all active:scale-95 bg-emerald-500 hover:bg-emerald-600"
                    >
                      Aprobar
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
