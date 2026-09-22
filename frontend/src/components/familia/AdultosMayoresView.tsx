import { useState, useEffect } from "react";
import { apiClient } from "../../api/client";

export default function AdultosMayoresView() {
  const [adultos, setAdultos] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [saving, setSaving] = useState(false);

  const [nombre, setNombre] = useState("");
  const [apellido, setApellido] = useState("");
  const [fechaNacimiento, setFechaNacimiento] = useState("");
  const [genero, setGenero] = useState("M");
  const [condicionSalud, setCondicionSalud] = useState("");
  const [notas, setNotas] = useState("");

  useEffect(() => {
    fetchAdultos();
  }, []);

  const fetchAdultos = async () => {
    setLoading(true);
    try {
      const data = await apiClient<any[]>("/familias/me/adultos-mayores");
      setAdultos(data);
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
      const nuevo = await apiClient<any>("/familias/me/adultos-mayores", {
        method: "POST",
        body: JSON.stringify({
          nombre,
          apellido,
          fecha_nacimiento: fechaNacimiento,
          genero,
          condicion_salud: condicionSalud,
          notas
        })
      });
      setAdultos([...adultos, nuevo]);
      setShowForm(false);
      // Reset
      setNombre("");
      setApellido("");
      setFechaNacimiento("");
      setGenero("M");
      setCondicionSalud("");
      setNotas("");
    } catch (err) {
      console.error(err);
      alert("Error al registrar familiar");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <p className="text-slate-500">Cargando...</p>;

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-lg font-bold text-slate-800">Familiares Registrados</h2>
        {!showForm && (
          <button 
            onClick={() => setShowForm(true)}
            className="px-4 py-2 bg-[#00B4D8] text-white text-sm font-bold rounded-lg hover:bg-blue-600"
          >
            + Agregar Familiar
          </button>
        )}
      </div>

      {showForm && (
        <form onSubmit={handleAdd} className="bg-slate-50 p-5 rounded-xl border border-slate-200 mb-6 space-y-4 max-w-xl">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-1">Nombre</label>
              <input required value={nombre} onChange={e => setNombre(e.target.value)} className="w-full px-3 py-2 border border-slate-200 rounded-lg" />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-1">Apellido</label>
              <input required value={apellido} onChange={e => setApellido(e.target.value)} className="w-full px-3 py-2 border border-slate-200 rounded-lg" />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-1">Fecha Nacimiento</label>
              <input type="date" required value={fechaNacimiento} onChange={e => setFechaNacimiento(e.target.value)} className="w-full px-3 py-2 border border-slate-200 rounded-lg" />
            </div>
            <div>
              <label className="block text-sm font-bold text-slate-700 mb-1">Género</label>
              <select value={genero} onChange={e => setGenero(e.target.value)} className="w-full px-3 py-2 border border-slate-200 rounded-lg">
                <option value="M">Masculino</option>
                <option value="F">Femenino</option>
                <option value="O">Otro</option>
              </select>
            </div>
          </div>
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Condiciones de Salud</label>
            <input value={condicionSalud} onChange={e => setCondicionSalud(e.target.value)} placeholder="Ej. Hipertensión, Diabetes..." className="w-full px-3 py-2 border border-slate-200 rounded-lg" />
          </div>
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Notas Adicionales</label>
            <textarea value={notas} onChange={e => setNotas(e.target.value)} rows={2} className="w-full px-3 py-2 border border-slate-200 rounded-lg" />
          </div>
          <div className="flex gap-2 justify-end">
            <button type="button" onClick={() => setShowForm(false)} className="px-4 py-2 text-slate-600 hover:bg-slate-200 rounded-lg font-medium text-sm">Cancelar</button>
            <button type="submit" disabled={saving} className="px-4 py-2 bg-[#00B4D8] text-white rounded-lg font-bold text-sm">Guardar</button>
          </div>
        </form>
      )}

      {adultos.length === 0 ? (
        <p className="text-slate-500 font-medium">Aún no has registrado familiares.</p>
      ) : (
        <div className="space-y-4">
          {adultos.map(a => (
            <div key={a.id} className="border border-slate-100 p-4 rounded-xl">
              <h3 className="font-bold text-slate-800 text-lg">{a.nombre} {a.apellido}</h3>
              <p className="text-sm text-slate-500 mb-2">
                Nacimiento: {a.fecha_nacimiento} | Género: {a.genero}
              </p>
              {a.condicion_salud && (
                <p className="text-sm text-slate-600"><span className="font-bold">Salud:</span> {a.condicion_salud}</p>
              )}
              {a.notas && (
                <p className="text-sm text-slate-600"><span className="font-bold">Notas:</span> {a.notas}</p>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
