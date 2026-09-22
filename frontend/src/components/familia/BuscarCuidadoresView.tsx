import { useState, useEffect } from "react";
import { apiClient } from "../../api/client";

export default function BuscarCuidadoresView() {
  const [cuidadores, setCuidadores] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCuidadores();
  }, []);

  const fetchCuidadores = async () => {
    setLoading(true);
    try {
      const data = await apiClient<any[]>("/cuidadores");
      setCuidadores(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSolicitar = (cuidadorId: number) => {
    alert(`Solicitud de reserva al cuidador ${cuidadorId} - ¡Próximamente modal de reserva!`);
    // Aquí iría la lógica para abrir un modal de reserva pasando el cuidadorId
  };

  return (
    <div>
      <h2 className="text-lg font-bold text-slate-800 mb-6">Buscar Cuidadores</h2>
      
      {loading ? (
        <p className="text-slate-500">Buscando cuidadores disponibles...</p>
      ) : cuidadores.length === 0 ? (
        <p className="text-slate-500 font-medium">No se encontraron cuidadores en este momento.</p>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {cuidadores.map(c => (
            <div key={c.id} className="border border-slate-100 rounded-2xl p-5 hover:border-[#00B4D8] transition-colors flex flex-col justify-between">
              <div>
                <div className="flex items-center gap-3 mb-3">
                  <div className="w-12 h-12 bg-slate-100 rounded-full flex items-center justify-center text-slate-400">
                    <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                  </div>
                  <div>
                    <h3 className="font-bold text-slate-800">Cuidador #{c.usuario_id}</h3>
                    <p className="text-sm font-medium text-[#00B4D8]">S/ {c.tarifa_referencial}/hr</p>
                  </div>
                </div>
                
                <p className="text-sm text-slate-600 mb-4 line-clamp-2">
                  {c.presentacion || "Sin presentación disponible."}
                </p>
                
                <div className="flex flex-wrap gap-2 mb-4">
                  <span className="text-xs font-bold px-2 py-1 bg-slate-50 text-slate-600 rounded">
                    {c.anos_experiencia} años exp.
                  </span>
                  {c.estado_verificacion === "VERIFICADO" && (
                    <span className="text-xs font-bold px-2 py-1 bg-emerald-50 text-emerald-700 rounded flex items-center gap-1">
                      <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20"><path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" /></svg>
                      Verificado
                    </span>
                  )}
                </div>
              </div>
              
              <button 
                onClick={() => handleSolicitar(c.id)}
                className="w-full py-2 bg-[#00B4D8] hover:bg-blue-600 text-white font-bold rounded-lg transition-colors text-sm"
              >
                Solicitar Reserva
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
