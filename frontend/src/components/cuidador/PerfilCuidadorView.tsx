import { useState, useEffect } from "react";
import { apiClient } from "../../api/client";

export default function PerfilCuidadorView() {
  const [perfil, setPerfil] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  
  // form state
  const [presentacion, setPresentacion] = useState("");
  const [anosExperiencia, setAnosExperiencia] = useState(0);
  const [tarifaReferencial, setTarifaReferencial] = useState(0);

  useEffect(() => {
    fetchPerfil();
  }, []);

  const fetchPerfil = async () => {
    setLoading(true);
    try {
      const data = await apiClient<any>("/cuidadores/me");
      setPerfil(data);
      if (data) {
        setPresentacion(data.presentacion || "");
        setAnosExperiencia(data.anos_experiencia || 0);
        setTarifaReferencial(data.tarifa_referencial || 0);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      const updated = await apiClient<any>("/cuidadores/me", {
        method: "PUT",
        body: JSON.stringify({
          presentacion,
          anos_experiencia: anosExperiencia,
          tarifa_referencial: tarifaReferencial
        })
      });
      setPerfil(updated);
      alert("Perfil actualizado exitosamente");
    } catch (err) {
      console.error(err);
      alert("Error al actualizar perfil");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <p className="text-slate-500">Cargando perfil...</p>;

  return (
    <div>
      <h2 className="text-lg font-bold text-slate-800 mb-6">Mi Perfil</h2>
      
      {perfil ? (
        <form onSubmit={handleSave} className="space-y-4 max-w-lg">
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Presentación</label>
            <textarea 
              value={presentacion}
              onChange={e => setPresentacion(e.target.value)}
              className="w-full px-4 py-2 border border-slate-200 rounded-lg focus:outline-none focus:border-[#00B4D8]"
              rows={4}
              placeholder="Cuéntale a las familias sobre ti..."
            />
          </div>
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Años de Experiencia</label>
            <input 
              type="number" 
              value={anosExperiencia}
              onChange={e => setAnosExperiencia(Number(e.target.value))}
              className="w-full px-4 py-2 border border-slate-200 rounded-lg focus:outline-none focus:border-[#00B4D8]"
            />
          </div>
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Tarifa Referencial (S/ por hora)</label>
            <input 
              type="number" 
              step="0.1"
              value={tarifaReferencial}
              onChange={e => setTarifaReferencial(Number(e.target.value))}
              className="w-full px-4 py-2 border border-slate-200 rounded-lg focus:outline-none focus:border-[#00B4D8]"
            />
          </div>
          
          <button 
            type="submit" 
            disabled={saving}
            className="px-6 py-2 bg-[#00B4D8] text-white font-bold rounded-lg hover:bg-blue-600 disabled:opacity-50"
          >
            {saving ? "Guardando..." : "Guardar Cambios"}
          </button>
        </form>
      ) : (
        <p className="text-amber-600 font-medium">No se encontró el perfil de cuidador.</p>
      )}
    </div>
  );
}
