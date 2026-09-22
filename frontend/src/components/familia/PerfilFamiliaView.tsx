import { useState, useEffect } from "react";
import { apiClient } from "../../api/client";

export default function PerfilFamiliaView() {
  const [perfil, setPerfil] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  
  const [direccion, setDireccion] = useState("");
  const [preferenciasBusqueda, setPreferenciasBusqueda] = useState("");

  useEffect(() => {
    fetchPerfil();
  }, []);

  const fetchPerfil = async () => {
    setLoading(true);
    try {
      const data = await apiClient<any>("/familias/me");
      setPerfil(data);
      if (data) {
        setDireccion(data.direccion || "");
        setPreferenciasBusqueda(data.preferencias_busqueda || "");
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
      const updated = await apiClient<any>("/familias/me", {
        method: "PUT",
        body: JSON.stringify({
          direccion,
          preferencias_busqueda: preferenciasBusqueda
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
      <h2 className="text-lg font-bold text-slate-800 mb-6">Perfil de la Familia</h2>
      
      {perfil ? (
        <form onSubmit={handleSave} className="space-y-4 max-w-lg">
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Dirección Principal</label>
            <input 
              value={direccion}
              onChange={e => setDireccion(e.target.value)}
              className="w-full px-4 py-2 border border-slate-200 rounded-lg focus:outline-none focus:border-[#00B4D8]"
            />
          </div>
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Preferencias de Búsqueda</label>
            <textarea 
              value={preferenciasBusqueda}
              onChange={e => setPreferenciasBusqueda(e.target.value)}
              className="w-full px-4 py-2 border border-slate-200 rounded-lg focus:outline-none focus:border-[#00B4D8]"
              rows={4}
              placeholder="Ej. Buscamos a alguien con paciencia, experiencia con Alzheimer..."
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
        <p className="text-amber-600 font-medium">No se encontró el perfil de familia.</p>
      )}
    </div>
  );
}
