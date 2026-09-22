import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { apiClient } from "../api/client";
import VerificacionModal from "../components/VerificacionModal";
import PerfilCuidadorView from "../components/cuidador/PerfilCuidadorView";
import ExperienciasView from "../components/cuidador/ExperienciasView";

export default function DashboardCuidador() {
  const [activeTab, setActiveTab] = useState("inicio");
  const [reservas, setReservas] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [cuidador, setCuidador] = useState<any>(null);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    fetchReservas();
    fetchCuidador();
  }, []);

  const fetchCuidador = () => {
    apiClient<any>("/cuidadores/me")
      .then(setCuidador)
      .catch(console.error);
  };

  const fetchReservas = () => {
    setLoading(true);
    apiClient<any[]>("/reservas/cuidador")
      .then(setReservas)
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  const handleAceptar = async (reservaId: number) => {
    try {
      await apiClient(`/reservas/${reservaId}/estado`, {
        method: "PATCH",
        body: JSON.stringify({ nuevo_estado: "CONFIRMADA" }),
      });
      fetchReservas();
    } catch (err) {
      console.error(err);
      alert("Error al confirmar reserva");
    }
  };

  return (
    <div className="min-h-screen bg-[#F8F9FA] pb-12">
      {showModal && (
        <VerificacionModal
          onClose={() => setShowModal(false)}
          onSuccess={() => {
            setShowModal(false);
            alert("Solicitud de verificación enviada correctamente.");
            // Actualizar cuidador para reflejar que hay algo pendiente (simulado)
          }}
        />
      )}
      {/* ── NAV ── */}
      <header className="bg-white border-b border-slate-100 shadow-sm">
        <div className="max-w-[1024px] mx-auto px-6 h-16 flex items-center justify-between">
          <Link to="/" className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-xl flex items-center justify-center" style={{ background: "#00B4D8" }}>
              <svg className="w-5 h-5 text-white" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z" />
              </svg>
            </div>
            <span className="text-xl font-700 text-slate-800">
              Cuid<span style={{ color: "#00B4D8" }}>AR</span>
            </span>
          </Link>
          <div className="flex items-center gap-4">
            <Link to="/" className="text-sm font-medium text-slate-500 hover:text-slate-800">Volver al inicio</Link>
          </div>
        </div>
      </header>

      <main className="max-w-[1024px] mx-auto px-6 pt-10 flex gap-8">
        
        {/* SIDEBAR PARA NAVEGACIÓN Y VERIFICACIONES */}
        <aside className="w-64 shrink-0 space-y-4">
          <nav className="bg-white rounded-2xl shadow-sm border border-slate-100 p-4">
            <ul className="space-y-2">
              <li>
                <button 
                  onClick={() => setActiveTab("inicio")}
                  className={`w-full flex items-center gap-3 px-4 py-2 rounded-xl font-bold transition-colors ${activeTab === 'inicio' ? 'bg-slate-50 text-[#00B4D8]' : 'text-slate-600 hover:bg-slate-50'}`}
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/></svg>
                  Inicio
                </button>
              </li>
              <li>
                <button 
                  onClick={() => setActiveTab("perfil")}
                  className={`w-full flex items-center gap-3 px-4 py-2 rounded-xl font-bold transition-colors ${activeTab === 'perfil' ? 'bg-slate-50 text-[#00B4D8]' : 'text-slate-600 hover:bg-slate-50'}`}
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/></svg>
                  Mi Perfil
                </button>
              </li>
              <li>
                <button 
                  onClick={() => setActiveTab("experiencias")}
                  className={`w-full flex items-center gap-3 px-4 py-2 rounded-xl font-bold transition-colors ${activeTab === 'experiencias' ? 'bg-slate-50 text-[#00B4D8]' : 'text-slate-600 hover:bg-slate-50'}`}
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/></svg>
                  Mis Experiencias
                </button>
              </li>
            </ul>
          </nav>

          <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-5">
            <h3 className="font-bold text-slate-800 text-sm mb-2">Estado</h3>
            
            {cuidador?.estado_verificacion === "VERIFICADO" ? (
              <div className="bg-emerald-50 text-emerald-700 p-3 rounded-lg text-xs font-bold border border-emerald-100 flex items-center gap-2 mb-4">
                <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                </svg>
                Perfil Verificado
              </div>
            ) : cuidador?.estado_verificacion === "PENDIENTE" ? (
              <div className="bg-amber-50 text-amber-700 p-3 rounded-lg text-xs font-bold border border-amber-100 mb-4">
                Verificación en proceso
              </div>
            ) : (
              <>
                <p className="text-xs text-slate-500 mb-4">Mantén tus documentos al día para brindar confianza a las familias.</p>
                <button 
                  onClick={() => setShowModal(true)}
                  className="w-full py-2 bg-[#E0F7FC] text-[#00B4D8] rounded-lg text-sm font-bold hover:bg-blue-50 transition-colors"
                >
                  Subir Documentos
                </button>
              </>
            )}
          </div>
        </aside>

        {/* MAIN CONTENT */}
        <div className="flex-1 bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
          {activeTab === "inicio" && (
            <>
              <h2 className="text-lg font-bold text-slate-800 mb-6">Solicitudes y Reservas</h2>
              
              {loading ? (
                <p className="text-slate-500 text-sm">Cargando...</p>
              ) : reservas.length === 0 ? (
                <div className="text-center py-10">
              <div className="w-16 h-16 rounded-full bg-slate-50 flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm14 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z" />
                </svg>
              </div>
              <p className="text-slate-500 font-medium">Aún no tienes solicitudes.</p>
            </div>
          ) : (
            <div className="space-y-4">
              {reservas.map(r => (
                <div key={r.id} className="border border-slate-100 rounded-xl p-5 flex items-center justify-between">
                  <div>
                    <div className="flex items-center gap-3 mb-1">
                      <h3 className="font-bold text-slate-800">Solicitud de Reserva #{r.id}</h3>
                      <span className={`text-xs px-2 py-0.5 rounded-full font-bold ${
                        r.estado === "SOLICITADA" ? "bg-amber-100 text-amber-700" :
                        r.estado === "CONFIRMADA" ? "bg-emerald-100 text-emerald-700" :
                        "bg-slate-100 text-slate-700"
                      }`}>
                        {r.estado}
                      </span>
                    </div>
                    <p className="text-sm text-slate-500">
                      Fechas: {new Date(r.fecha_inicio).toLocaleDateString()} a {new Date(r.fecha_fin).toLocaleDateString()}
                    </p>
                    <p className="text-sm text-slate-600 mt-2 font-medium">Tarifa total esperada referencial: S/{r.tarifa_acordada}/hr</p>
                  </div>
                  
                  <div className="flex gap-2">
                    {r.estado === "SOLICITADA" && (
                      <>
                        <button 
                          className="text-sm font-medium px-4 py-2 border border-slate-200 rounded-lg text-slate-600 hover:bg-slate-50"
                        >
                          Rechazar
                        </button>
                        <button 
                          onClick={() => handleAceptar(r.id)}
                          className="text-sm font-bold px-4 py-2 rounded-lg text-white transition-all active:scale-95" 
                          style={{ background: "#00B4D8" }}
                        >
                          Aceptar
                        </button>
                      </>
                    )}
                    {r.estado === "CONFIRMADA" && (
                      <button 
                        className="text-sm font-bold px-4 py-2 rounded-lg text-white transition-all active:scale-95" 
                        style={{ background: "#FF7F50" }}
                      >
                        Subir Reporte Diario
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
            </>
          )}
          {activeTab === "perfil" && <PerfilCuidadorView />}
          {activeTab === "experiencias" && <ExperienciasView />}
        </div>
      </main>
    </div>
  );
}
