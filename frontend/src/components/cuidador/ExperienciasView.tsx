import { useState, useEffect } from "react";
import { apiClient } from "../../api/client";

export default function ExperienciasView() {
  const [experiencias, setExperiencias] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [saving, setSaving] = useState(false);

  const [empresa, setEmpresa] = useState("");
  const [cargo, setCargo] = useState("");
  const [fechaInicio, setFechaInicio] = useState("");
  const [fechaFin, setFechaFin] = useState("");
  const [descripcion, setDescripcion] = useState("");

  useEffect(() => {
    fetchExperiencias();
  }, []);

  const fetchExperiencias = async () => {
    setLoading(true);
    try {
      const data = await apiClient<any[]>("/cuidadores/me/experiencias");
      setExperiencias(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleAdd = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      const nueva = await apiClient<any>("/cuidadores/me/experiencias", {
        method: "POST",
        body: JSON.stringify({
          empresa,
          cargo,
          fecha_inicio: fechaInicio,
          fecha_fin: fechaFin || null,
          descripcion
        })
      });
      setExperiencias([...experiencias, nueva]);
      setShowForm(false);
      // Reset form
      setEmpresa("");
      setCargo("");
      setFechaInicio("");
      setFechaFin("");
      setDescripcion("");
    } catch (err) {
      console.error(err);
      alert("Error al agregar experiencia");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <p className="text-slate-500">Cargando experiencias...</p>;

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-lg font-bold text-slate-800">Mis Experiencias Laborales</h2>
        {!showForm && (
          <button 
            onClick={() => setShowForm(true)}
            className="px-4 py-2 bg-[#00B4D8] text-white text-sm font-bold rounded-lg hover:bg-blue-600"
          >
            + Agregar Experiencia
          </button>
        )}
      </div>

      {showForm && (
        <form onSubmit={handleAdd} className="bg-slate-50 p-5 rounded-xl border border-slate-200 mb-6 space-y-4 max-w-xl">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-1">Empresa / Lugar</label>
              <input required value={empresa} onChange={e => setEmpresa(e.target.value)} className="w-full px-3 py-2 border border-slate-200 rounded-lg" />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-1">Cargo</label>
              <input required value={cargo} onChange={e => setCargo(e.target.value)} className="w-full px-3 py-2 border border-slate-200 rounded-lg" />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-1">Fecha Inicio</label>
              <input type="date" required value={fechaInicio} onChange={e => setFechaInicio(e.target.value)} className="w-full px-3 py-2 border border-slate-200 rounded-lg" />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-1">Fecha Fin (Opcional)</label>
              <input type="date" value={fechaFin} onChange={e => setFechaFin(e.target.value)} className="w-full px-3 py-2 border border-slate-200 rounded-lg" />
            </div>
          </div>
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Descripción</label>
            <textarea required value={descripcion} onChange={e => setDescripcion(e.target.value)} rows={3} className="w-full px-3 py-2 border border-slate-200 rounded-lg" />
          </div>
          <div className="flex gap-2 justify-end">
            <button type="button" onClick={() => setShowForm(false)} className="px-4 py-2 text-slate-600 hover:bg-slate-200 rounded-lg font-medium text-sm">Cancelar</button>
            <button type="submit" disabled={saving} className="px-4 py-2 bg-[#00B4D8] text-white rounded-lg font-bold text-sm">Guardar</button>
          </div>
        </form>
      )}

      {experiencias.length === 0 ? (
        <p className="text-slate-500 font-medium">Aún no has registrado experiencias.</p>
      ) : (
        <div className="space-y-4">
          {experiencias.map(exp => (
            <div key={exp.id} className="border border-slate-100 p-4 rounded-xl">
              <h3 className="font-bold text-slate-800 text-lg">{exp.cargo} <span className="text-slate-500 font-medium text-sm ml-2">en {exp.empresa}</span></h3>
              <p className="text-xs text-slate-500 mb-2">
                {new Date(exp.fecha_inicio).toLocaleDateString()} - {exp.fecha_fin ? new Date(exp.fecha_fin).toLocaleDateString() : 'Actualidad'}
              </p>
              <p className="text-sm text-slate-600">{exp.descripcion}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
