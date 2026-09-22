import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { apiClient } from "../api/client";
import { useAuth } from "../context/AuthContext";
import BuscarCuidadoresView from "../components/familia/BuscarCuidadoresView";
import PerfilFamiliaView from "../components/familia/PerfilFamiliaView";
import AdultosMayoresView from "../components/familia/AdultosMayoresView";

export default function DashboardFamilia() {
  const [activeTab, setActiveTab] = useState("inicio");
  const { user } = useAuth();
  const [reservas, setReservas] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient<any[]>("/reservas/familia")
      .then(setReservas)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="min-h-screen bg-[#F8F9FA] pb-12">
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
        {/* SIDEBAR PARA NAVEGACIÓN */}
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
                  onClick={() => setActiveTab("buscar")}
                  className={`w-full flex items-center gap-3 px-4 py-2 rounded-xl font-bold transition-colors ${activeTab === 'buscar' ? 'bg-slate-50 text-[#00B4D8]' : 'text-slate-600 hover:bg-slate-50'}`}
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/></svg>
                  Buscar Cuidadores
                </button>
              </li>
              <li>
                <button 
                  onClick={() => setActiveTab("perfil")}
                  className={`w-full flex items-center gap-3 px-4 py-2 rounded-xl font-bold transition-colors ${activeTab === 'perfil' ? 'bg-slate-50 text-[#00B4D8]' : 'text-slate-600 hover:bg-slate-50'}`}
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/></svg>
                  Perfil de Familia
                </button>
              </li>
              <li>
                <button 
                  onClick={() => setActiveTab("adultos")}
                  className={`w-full flex items-center gap-3 px-4 py-2 rounded-xl font-bold transition-colors ${activeTab === 'adultos' ? 'bg-slate-50 text-[#00B4D8]' : 'text-slate-600 hover:bg-slate-50'}`}
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"/></svg>
                  Familiares (Adultos Mayores)
                </button>
              </li>
            </ul>
          </nav>
        </aside>

        {/* MAIN CONTENT */}
        <div className="flex-1 bg-white rounded-2xl shadow-sm border border-slate-100 p-6">
          {activeTab === "inicio" && (
            <>
              <h2 className="text-lg font-bold text-slate-800 mb-6">Tus Reservas</h2>
          
          {loading ? (
            <p className="text-slate-500 text-sm">Cargando...</p>
          ) : reservas.length === 0 ? (
            <div className="text-center py-10">
              <div className="w-16 h-16 rounded-full bg-slate-50 flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-slate-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
              <p className="text-slate-500 font-medium">Aún no tienes reservas.</p>
              <Link to="/" className="text-[#00B4D8] text-sm mt-2 inline-block font-bold hover:underline">
                Buscar un cuidador
              </Link>
            </div>
          ) : (
            <div className="space-y-4">
              {reservas.map(r => (
                <div key={r.id} className="border border-slate-100 rounded-xl p-5 flex items-center justify-between hover:border-[#00B4D8] transition-colors">
                  <div>
                    <div className="flex items-center gap-3 mb-1">
                      <h3 className="font-bold text-slate-800">Reserva #{r.id}</h3>
                      <span className={`text-xs px-2 py-0.5 rounded-full font-bold ${
                        r.estado === "SOLICITADA" ? "bg-amber-100 text-amber-700" :
                        r.estado === "CONFIRMADA" ? "bg-emerald-100 text-emerald-700" :
                        r.estado === "CANCELADA" ? "bg-red-100 text-red-700" :
                        "bg-slate-100 text-slate-700"
                      }`}>
                        {r.estado}
                      </span>
                    </div>
                    <p className="text-sm text-slate-500">
                      Inicio: {new Date(r.fecha_inicio).toLocaleString()}
                      <span className="mx-2">|</span>
                      Fin: {new Date(r.fecha_fin).toLocaleString()}
                    </p>
                    <p className="text-sm text-slate-600 mt-2 font-medium">Tarifa: S/{r.tarifa_acordada}/hr</p>
                  </div>
                  
                  <div className="flex gap-2">
                    <button 
                      onClick={() => alert("Reportes - Proximamente")}
                      className="text-sm font-medium px-4 py-2 border border-slate-200 rounded-lg text-slate-600 hover:bg-slate-50"
                    >
                      Ver Reportes
                    </button>
                    {r.estado === "CONFIRMADA" && (
                      <button 
                        onClick={() => alert("Dejar Reseña - Proximamente")}
                        className="text-sm font-medium px-4 py-2 rounded-lg text-white" style={{ background: "#FF7F50" }}
                      >
                        Dejar Reseña
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
            </>
          )}
          {activeTab === "buscar" && <BuscarCuidadoresView />}
          {activeTab === "perfil" && <PerfilFamiliaView />}
          {activeTab === "adultos" && <AdultosMayoresView />}
        </div>
      </main>
    </div>
  );
}
