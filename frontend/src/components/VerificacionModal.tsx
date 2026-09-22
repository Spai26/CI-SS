import { useState } from "react";
import { apiClient, ApiError } from "../api/client";

export default function VerificacionModal({
  onClose,
  onSuccess,
}: {
  onClose: () => void;
  onSuccess: () => void;
}) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [documentos, setDocumentos] = useState({
    IDENTIDAD: null as File | null,
    ANTECEDENTES: null as File | null,
    CV: null as File | null,
  });

  const handleFileChange = (tipo: string, file: File | null) => {
    setDocumentos((prev) => ({ ...prev, [tipo]: file }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!documentos.IDENTIDAD || !documentos.ANTECEDENTES || !documentos.CV) {
      setError("Por favor sube todos los documentos requeridos.");
      return;
    }

    setLoading(true);
    try {
      // En un entorno real, aquí subirías los archivos a S3 u otro storage 
      // y obtendrías las URLs. Por ahora simulamos las URLs.
      const payload = {
        documentos: [
          { tipoDocumento: "IDENTIDAD", archivoUrl: "https://storage.example.com/mock-id.pdf" },
          { tipoDocumento: "ANTECEDENTES", archivoUrl: "https://storage.example.com/mock-antecedentes.pdf" },
          { tipoDocumento: "CV", archivoUrl: "https://storage.example.com/mock-cv.pdf" }
        ]
      };

      await apiClient("/cuidadores/me/verificacion", {
        method: "POST",
        body: JSON.stringify(payload),
      });

      onSuccess();
    } catch (err: any) {
      setError(err instanceof ApiError ? err.message : "Error al enviar la solicitud.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden animate-in fade-in zoom-in-95">
        <div className="px-6 py-5 border-b border-slate-100 flex items-center justify-between">
          <h2 className="text-xl font-bold text-slate-800">Verificación de Identidad</h2>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-600">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <form className="p-6 space-y-4" onSubmit={handleSubmit}>
          {error && (
            <div className="p-3 bg-red-50 text-red-600 text-sm rounded-lg border border-red-100">
              {error}
            </div>
          )}

          <p className="text-sm text-slate-500 mb-4">
            Para ofrecer seguridad a las familias, necesitamos verificar tu identidad y experiencia. Por favor sube los siguientes documentos (PDF, JPG, PNG).
          </p>

          {/* DNI */}
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Documento de Identidad (DNI/Pasaporte)</label>
            <input
              type="file"
              accept=".pdf,.jpg,.jpeg,.png"
              onChange={(e) => handleFileChange("IDENTIDAD", e.target.files?.[0] || null)}
              className="w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-[#E0F7FC] file:text-[#00B4D8] hover:file:bg-blue-50"
            />
          </div>

          {/* Antecedentes */}
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Certificado de Antecedentes Policiales</label>
            <input
              type="file"
              accept=".pdf,.jpg,.jpeg,.png"
              onChange={(e) => handleFileChange("ANTECEDENTES", e.target.files?.[0] || null)}
              className="w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-[#E0F7FC] file:text-[#00B4D8] hover:file:bg-blue-50"
            />
          </div>

          {/* CV */}
          <div>
            <label className="block text-sm font-bold text-slate-700 mb-1">Curriculum Vitae (CV)</label>
            <input
              type="file"
              accept=".pdf,.jpg,.jpeg,.png"
              onChange={(e) => handleFileChange("CV", e.target.files?.[0] || null)}
              className="w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-[#E0F7FC] file:text-[#00B4D8] hover:file:bg-blue-50"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full mt-6 py-2.5 rounded-xl text-white font-bold transition-colors disabled:opacity-50"
            style={{ background: "#FF7F50" }}
          >
            {loading ? "Enviando..." : "Enviar para Revisión"}
          </button>
        </form>
      </div>
    </div>
  );
}
