import { useState, useMemo, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export type CuidadorResponse = any;

/* ── Types ── */
type Caregiver = {
  id: number;
  name: string;
  photo: string;
  specialty: string;
  rate: number;
  rating: number;
  reviews: number;
  experience: number;
  age: number;
  sex: "Femenino" | "Masculino";
  bio: string;
  skills: string[];
  certifications: string[];
  verified: boolean;
};

/* ── Data ── */
const CAREGIVERS: Caregiver[] = [
  {
    id: 1,
    name: "María Fernández",
    photo: "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=300&h=300&fit=crop&auto=format",
    specialty: "Cuidado Geriátrico",
    rate: 18,
    rating: 4.9,
    reviews: 134,
    experience: 8,
    age: 36,
    sex: "Femenino",
    bio: "Especialista en adultos mayores con condiciones crónicas. Empática, paciente y con vocación genuina por el bienestar de sus pacientes.",
    skills: ["Alzheimer", "Primeros Auxilios", "Fisioterapia básica"],
    certifications: ["Cruz Roja", "MINSA", "CPR"],
    verified: true,
  },
  {
    id: 2,
    name: "Carlos Rondón",
    photo: "https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=300&h=300&fit=crop&auto=format",
    specialty: "Enfermería Domiciliaria",
    rate: 22,
    rating: 4.8,
    reviews: 89,
    experience: 11,
    age: 42,
    sex: "Masculino",
    bio: "Enfermero titulado con más de 11 años de experiencia en cuidados domiciliarios. Especializado en post-operatorio y enfermedades cardiovasculares.",
    skills: ["Post-operatorio", "Cardiology", "Administración de medicamentos"],
    certifications: ["MINSA", "Colegio de Enfermeros", "CPR"],
    verified: true,
  },
  {
    id: 3,
    name: "Lucía Quispe",
    photo: "https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=300&h=300&fit=crop&auto=format",
    specialty: "Cuidado Paliativo",
    rate: 20,
    rating: 5.0,
    reviews: 47,
    experience: 6,
    age: 31,
    sex: "Femenino",
    bio: "Terapeuta ocupacional enfocada en cuidados paliativos. Brinda acompañamiento emocional y apoyo a familias en situaciones difíciles.",
    skills: ["Cuidado Paliativo", "Terapia Ocupacional", "Acompañamiento emocional"],
    certifications: ["Cruz Roja", "OPS"],
    verified: true,
  },
  {
    id: 4,
    name: "Andrés Vargas",
    photo: "https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=300&h=300&fit=crop&auto=format",
    specialty: "Fisioterapia Domiciliaria",
    rate: 25,
    rating: 4.7,
    reviews: 212,
    experience: 14,
    age: 45,
    sex: "Masculino",
    bio: "Fisioterapeuta con enfoque en rehabilitación geriátrica. Ayuda a adultos mayores a recuperar movilidad y autonomía en casa.",
    skills: ["Rehabilitación", "Parkinson", "Ejercicio terapéutico"],
    certifications: ["Colegio de Fisioterapeutas", "MINSA", "CPR"],
    verified: true,
  },
  {
    id: 5,
    name: "Rosa Llanos",
    photo: "https://images.unsplash.com/photo-1614608682850-e0d6ed316d47?w=300&h=300&fit=crop&auto=format",
    specialty: "Cuidado Geriátrico",
    rate: 15,
    rating: 4.6,
    reviews: 73,
    experience: 4,
    age: 28,
    sex: "Femenino",
    bio: "Cuidadora con formación técnica en gerontología. Alegre, activa y comprometida con el bienestar físico y emocional del adulto mayor.",
    skills: ["Diabetes", "Estimulación cognitiva", "Cuidado personal"],
    certifications: ["Cruz Roja", "SENATI"],
    verified: true,
  },
  {
    id: 6,
    name: "Jorge Mendoza",
    photo: "https://images.unsplash.com/photo-1582750433449-648ed127bb54?w=300&h=300&fit=crop&auto=format",
    specialty: "Técnico en Salud",
    rate: 17,
    rating: 4.5,
    reviews: 58,
    experience: 5,
    age: 33,
    sex: "Masculino",
    bio: "Técnico en enfermería especializado en cuidados a domicilio. Responsable, puntual y con excelentes referencias familiares.",
    skills: ["Demencia", "Control de signos vitales", "Higiene y nutrición"],
    certifications: ["MINSA", "CPR"],
    verified: false,
  },
  {
    id: 7,
    name: "Carmen Huanca",
    photo: "https://images.unsplash.com/photo-1607746882042-944635dfe10e?w=300&h=300&fit=crop&auto=format",
    specialty: "Cuidado Paliativo",
    rate: 19,
    rating: 4.9,
    reviews: 101,
    experience: 9,
    age: 39,
    sex: "Femenino",
    bio: "Especialista en acompañamiento a personas mayores con enfermedades terminales. Ofrece calidad de vida con dignidad y afecto.",
    skills: ["Cuidado terminal", "Aromaterapia", "Apoyo psicológico"],
    certifications: ["OPS", "Cruz Roja", "MINSA"],
    verified: true,
  },
  {
    id: 8,
    name: "Pablo Castillo",
    photo: "https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=300&h=300&fit=crop&auto=format",
    specialty: "Enfermería Domiciliaria",
    rate: 23,
    rating: 4.8,
    reviews: 155,
    experience: 12,
    age: 40,
    sex: "Masculino",
    bio: "Enfermero con amplia trayectoria en cuidados nocturnos y pacientes con movilidad reducida. Seguro, confiable y muy bien valorado.",
    skills: ["Cuidado nocturno", "Ostomías", "Primeros Auxilios"],
    certifications: ["Colegio de Enfermeros", "MINSA", "CPR"],
    verified: true,
  },
];

/* ── Helpers ── */
const Stars = ({ rating }: { rating: number }) => {
  return (
    <div className="flex gap-0.5" aria-label={`Calificación: ${rating} de 5`}>
      {[1, 2, 3, 4, 5].map((s) => (
        <svg key={s} className={`w-3.5 h-3.5 ${s <= Math.round(rating) ? "star-filled" : "star-empty"}`} fill="currentColor" viewBox="0 0 20 20">
          <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
        </svg>
      ))}
    </div>
  );
};

const VerifiedBadge = () => (
  <span className="inline-flex items-center gap-1 text-xs font-medium px-2 py-0.5 rounded-full" style={{ background: "#E0F7FC", color: "#00B4D8" }}>
    <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
      <path fillRule="evenodd" d="M6.267 3.455a3.066 3.066 0 001.745-.723 3.066 3.066 0 013.976 0 3.066 3.066 0 001.745.723 3.066 3.066 0 012.812 2.812c.051.643.304 1.254.723 1.745a3.066 3.066 0 010 3.976 3.066 3.066 0 00-.723 1.745 3.066 3.066 0 01-2.812 2.812 3.066 3.066 0 00-1.745.723 3.066 3.066 0 01-3.976 0 3.066 3.066 0 00-1.745-.723 3.066 3.066 0 01-2.812-2.812 3.066 3.066 0 00-.723-1.745 3.066 3.066 0 010-3.976 3.066 3.066 0 00.723-1.745 3.066 3.066 0 012.812-2.812zm7.44 5.252a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
    </svg>
    Verificado
  </span>
);

const CertIcon = ({ name }: { name: string }) => (
  <span className="inline-flex items-center gap-1 text-xs px-2 py-1 rounded-full border border-slate-200 text-slate-500 bg-white">
    <svg className="w-3 h-3" style={{ color: "#00B4D8" }} fill="currentColor" viewBox="0 0 20 20">
      <path d="M10.394 2.08a1 1 0 00-.788 0l-7 3a1 1 0 000 1.84L5.25 8.051a.999.999 0 01.356-.257l4-1.714a1 1 0 11.788 1.838l-2.727 1.17 1.94.831a1 1 0 00.787 0l7-3a1 1 0 000-1.838l-7-3zM3.31 9.397L5 10.12v4.102a8.969 8.969 0 00-1.05-.174 1 1 0 01-.89-.89 11.115 11.115 0 01.25-3.762zm5.99 7.176A9.026 9.026 0 007 14.935v-3.957l1.818.78a3 3 0 002.364 0l5.508-2.361a11.026 11.026 0 01.25 3.762 1 1 0 01-.89.89 8.968 8.968 0 00-5.35 2.524 1 1 0 01-1.4 0zM6 18a1 1 0 001-1v-2.065a8.935 8.935 0 00-2-.712V17a1 1 0 001 1z" />
    </svg>
    {name}
  </span>
);

/* ── Detail Modal ── */
const CaregiverModal = ({ c, onClose }: { c: Caregiver; onClose: () => void }) => {
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => { if (e.key === "Escape") onClose(); };
    document.addEventListener("keydown", onKey);
    document.body.style.overflow = "hidden";
    return () => {
      document.removeEventListener("keydown", onKey);
      document.body.style.overflow = "";
    };
  }, [onClose]);

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-4"
      style={{ background: "rgba(15, 23, 42, 0.5)", backdropFilter: "blur(4px)" }}
      onClick={(e) => { if (e.target === e.currentTarget) onClose(); }}
      role="dialog"
      aria-modal="true"
      aria-label={`Perfil de ${c.name}`}
    >
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden animate-in" style={{ animation: "modalIn 0.22s ease" }}>
        {/* Header teal */}
        <div className="relative px-6 pt-6 pb-4" style={{ background: "linear-gradient(135deg, #00B4D8 0%, #0096B4 100%)" }}>
          <button
            onClick={onClose}
            className="absolute top-4 right-4 w-8 h-8 rounded-full bg-white/20 hover:bg-white/30 flex items-center justify-center transition-colors"
            aria-label="Cerrar"
          >
            <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>

          <div className="flex items-center gap-4">
            <div className="relative shrink-0">
              <img
                src={c.photo}
                alt={`Foto de ${c.name}`}
                className="w-20 h-20 rounded-full object-cover border-4 border-white/30 bg-slate-100"
              />
              {c.verified && (
                <span className="absolute -bottom-1 -right-1 w-6 h-6 rounded-full flex items-center justify-center bg-white">
                  <svg className="w-4 h-4" style={{ color: "#00B4D8" }} fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                  </svg>
                </span>
              )}
            </div>
            <div>
              <h2 className="text-xl font-700 text-white leading-tight">{c.name}</h2>
              <p className="text-white/80 text-sm font-medium mt-0.5">{c.specialty}</p>
              <div className="flex items-center gap-2 mt-2">
                <Stars rating={c.rating} />
                <span className="text-white text-sm font-600">{c.rating}</span>
                <span className="text-white/60 text-xs">({c.reviews} reseñas)</span>
              </div>
            </div>
          </div>
        </div>

        {/* Body */}
        <div className="px-6 py-5 space-y-4">
          {/* Stats row */}
          <div className="grid grid-cols-3 gap-3">
            {[
              { label: "Experiencia", value: `${c.experience} años` },
              { label: "Edad", value: `${c.age} años` },
              { label: "Tarifa", value: `S/ ${c.rate}/hr` },
            ].map(({ label, value }) => (
              <div key={label} className="rounded-xl p-3 text-center" style={{ background: "#F8F9FA" }}>
                <p className="text-xs text-slate-400 mb-0.5">{label}</p>
                <p className="text-sm font-700 text-slate-800">{value}</p>
              </div>
            ))}
          </div>

          {/* Bio */}
          <div>
            <h4 className="text-xs font-600 text-slate-400 uppercase tracking-wider mb-2">Sobre mí</h4>
            <p className="text-sm text-slate-600 leading-relaxed">{c.bio}</p>
          </div>

          {/* Skills */}
          <div>
            <h4 className="text-xs font-600 text-slate-400 uppercase tracking-wider mb-2">Habilidades</h4>
            <div className="flex flex-wrap gap-2">
              {c.skills.map((skill) => (
                <span key={skill} className="text-xs px-3 py-1.5 rounded-full font-medium" style={{ background: "#E0F7FC", color: "#0096B4" }}>
                  {skill}
                </span>
              ))}
            </div>
          </div>

          {/* Certifications */}
          <div>
            <h4 className="text-xs font-600 text-slate-400 uppercase tracking-wider mb-2">Certificaciones</h4>
            <div className="flex flex-wrap gap-2">
              {c.certifications.map((cert) => (
                <CertIcon key={cert} name={cert} />
              ))}
            </div>
          </div>

          {/* Badges */}
          <div className="flex items-center gap-2 flex-wrap">
            {c.verified && <VerifiedBadge />}
            <span className="inline-flex items-center gap-1 text-xs font-medium px-2 py-0.5 rounded-full" style={{ background: "#FFF3EE", color: "#FF7F50" }}>
              <svg className="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clipRule="evenodd" />
              </svg>
              Disponible ahora
            </span>
            <span className="inline-flex items-center gap-1 text-xs font-medium px-2 py-0.5 rounded-full bg-slate-100 text-slate-500">
              {c.sex}
            </span>
          </div>
        </div>

        {/* Footer CTA */}
        <div className="px-6 pb-6 flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 py-3 rounded-xl text-sm font-600 border border-slate-200 text-slate-600 hover:bg-slate-50 transition-colors"
          >
            Volver
          </button>
          <button
            className="flex-1 py-3 rounded-xl text-sm font-700 text-white transition-all active:scale-95"
            style={{ background: "#FF7F50" }}
            onMouseEnter={(e) => (e.currentTarget.style.background = "#E66A3D")}
            onMouseLeave={(e) => (e.currentTarget.style.background = "#FF7F50")}
          >
            Reservar ahora
          </button>
        </div>
      </div>
    </div>
  );
};

/* ── Card ── */
const CaregiverCard = ({ c, onSelect }: { c: Caregiver; onSelect: (c: Caregiver) => void }) => (
  <article
    className="caregiver-card bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden flex flex-col cursor-pointer"
    onClick={() => onSelect(c)}
    tabIndex={0}
    role="button"
    aria-label={`Ver detalles de ${c.name}`}
    onKeyDown={(e) => { if (e.key === "Enter" || e.key === " ") onSelect(c); }}
  >
    <div className="p-5">
      <div className="flex items-start gap-4">
        {/* Photo */}
        <div className="relative shrink-0">
          <img
            src={c.photo}
            alt={`Foto de ${c.name}`}
            className="w-16 h-16 rounded-full object-cover bg-slate-100"
            loading="lazy"
          />
          {c.verified && (
            <span className="absolute -bottom-1 -right-1 w-5 h-5 rounded-full flex items-center justify-center" style={{ background: "#00B4D8" }}>
              <svg className="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
              </svg>
            </span>
          )}
        </div>

        {/* Info */}
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-2">
            <div>
              <h3 className="font-600 text-slate-800 text-base leading-tight truncate">{c.name}</h3>
              <p className="text-sm font-medium mt-0.5" style={{ color: "#00B4D8" }}>{c.specialty}</p>
            </div>
            {c.verified && <VerifiedBadge />}
          </div>

          <div className="flex items-center gap-2 mt-2">
            <Stars rating={c.rating} />
            <span className="text-xs font-600 text-slate-700">{c.rating}</span>
            <span className="text-xs text-slate-400">({c.reviews} reseñas)</span>
          </div>

          <div className="flex items-center justify-between mt-3">
            <div className="flex items-center gap-1.5">
              <svg className="w-4 h-4 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span className="text-xs text-slate-500">{c.experience} años exp.</span>
            </div>
            <div className="text-right">
              <span className="text-lg font-700" style={{ color: "#1E293B" }}>S/ {c.rate}</span>
              <span className="text-xs text-slate-400">/hr</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    {/* Ver detalles hint */}
    <div className="px-5 pb-4 pt-0">
      <div className="flex items-center gap-1.5 text-xs font-medium" style={{ color: "#00B4D8" }}>
        <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
        </svg>
        Ver perfil completo
      </div>
    </div>
  </article>
);

/* ── Filter Chip ── */
const FilterChip = ({
  label,
  active,
  onClick,
}: {
  label: string;
  active: boolean;
  onClick: () => void;
}) => (
  <button
    onClick={onClick}
    className="filter-chip text-sm font-medium px-4 py-1.5 rounded-full border whitespace-nowrap"
    style={
      active
        ? { background: "#00B4D8", color: "#fff", borderColor: "#00B4D8" }
        : { background: "#fff", color: "#64748B", borderColor: "#E2E8F0" }
    }
    aria-pressed={active}
  >
    {label}
  </button>
);

/* ── Sidebar Filter Section ── */
const FilterSection = ({
  title,
  children,
}: {
  title: string;
  children: React.ReactNode;
}) => (
  <div className="mb-6">
    <h4 className="text-xs font-600 text-slate-400 uppercase tracking-wider mb-3">{title}</h4>
    {children}
  </div>
);

/* ── Main App ── */
export default function Home() {
  const { user, isAuthenticated, logout } = useAuth();
  const [selected, setSelected] = useState<Caregiver | null>(null);
  const [search, setSearch] = useState("");
  const [sexFilter, setSexFilter] = useState<string | null>(null);
  const [minRating, setMinRating] = useState<number | null>(null);
  const [minExp, setMinExp] = useState<number | null>(null);
  const [specialty, setSpecialty] = useState<string | null>(null);
  const [maxRate, setMaxRate] = useState<number | null>(null);
  const [sortBy, setSortBy] = useState<"rating" | "rate" | "experience">("rating");

  const specialties = [...new Set(CAREGIVERS.map((c) => c.specialty))];

  const filtered = useMemo(() => {
    return CAREGIVERS.filter((c) => {
      if (search && !c.name.toLowerCase().includes(search.toLowerCase()) && !c.specialty.toLowerCase().includes(search.toLowerCase())) return false;
      if (sexFilter && c.sex !== sexFilter) return false;
      if (minRating && c.rating < minRating) return false;
      if (minExp && c.experience < minExp) return false;
      if (specialty && c.specialty !== specialty) return false;
      if (maxRate && c.rate > maxRate) return false;
      return true;
    }).sort((a, b) => {
      if (sortBy === "rating") return b.rating - a.rating;
      if (sortBy === "rate") return a.rate - b.rate;
      return b.experience - a.experience;
    });
  }, [search, sexFilter, minRating, minExp, specialty, maxRate, sortBy]);

  const clearAll = () => {
    setSearch("");
    setSexFilter(null);
    setMinRating(null);
    setMinExp(null);
    setSpecialty(null);
    setMaxRate(null);
  };

  const activeFilterCount = [sexFilter, minRating, minExp, specialty, maxRate].filter(Boolean).length;

  return (
    <>
    {selected && <CaregiverModal c={selected} onClose={() => setSelected(null)} />}
    <div className="min-h-full bg-[#F8F9FA]">
      {/* ── NAV ── */}
      <header className="sticky top-0 z-50 bg-white border-b border-slate-100 shadow-sm">
        <div className="max-w-[1440px] mx-auto px-6 h-16 flex items-center gap-6">
          {/* Logo */}
          <div className="flex items-center gap-2 shrink-0">
            <div className="w-8 h-8 rounded-xl flex items-center justify-center" style={{ background: "#00B4D8" }}>
              <svg className="w-5 h-5 text-white" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z" />
              </svg>
            </div>
            <span className="text-xl font-700 text-slate-800">
              Cuid<span style={{ color: "#00B4D8" }}>AR</span>
            </span>
          </div>

          {/* Search */}
          <div className="flex-1 max-w-xl relative">
            <svg className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            <input
              type="search"
              placeholder="Buscar cuidador o especialidad..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 bg-[#F8F9FA] text-sm text-slate-700 placeholder-slate-400 focus:outline-none focus:border-[#00B4D8] focus:ring-2 focus:ring-[#00B4D8]/20"
            />
          </div>

          {/* Nav right */}
          <div className="flex items-center gap-3 ml-auto shrink-0">
            <button className="text-sm font-medium text-slate-500 hover:text-slate-700 px-3 py-2 rounded-lg hover:bg-slate-50">
              Para familias
            </button>
            <button className="text-sm font-medium text-slate-500 hover:text-slate-700 px-3 py-2 rounded-lg hover:bg-slate-50">
              ¿Cómo funciona?
            </button>
            {isAuthenticated ? (
              <>
                <span className="text-sm font-medium text-slate-700 ml-4 border-l pl-4 border-slate-200">
                  Hola, {user?.nombre}
                </span>
                <Link to="/dashboard-familia" className="text-sm font-medium px-4 py-2 rounded-xl bg-indigo-50 text-indigo-600 hover:bg-indigo-100 transition-colors">
                  Dashboard Familia
                </Link>
                <Link to="/dashboard-cuidador" className="text-sm font-medium px-4 py-2 rounded-xl bg-teal-50 text-teal-600 hover:bg-teal-100 transition-colors">
                  Dashboard Cuidador
                </Link>
                <button 
                  onClick={logout}
                  className="text-sm font-medium px-4 py-2 rounded-xl border border-red-100 text-red-500 hover:bg-red-50 transition-colors"
                >
                  Cerrar sesión
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="text-sm font-medium px-4 py-2 rounded-xl border border-[#00B4D8] hover:bg-[#E0F7FC] transition-colors" style={{ color: "#00B4D8", display: "inline-block" }}>
                  Iniciar sesión
                </Link>
                <Link to="/registro" className="text-sm font-600 px-4 py-2 rounded-xl text-white transition-all hover:opacity-90 active:scale-95" style={{ background: "#FF7F50", display: "inline-block" }}>
                  Registrarse
                </Link>
              </>
            )}
          </div>
        </div>
      </header>

      <div className="max-w-[1440px] mx-auto px-6 py-8 flex gap-8">
        {/* ── SIDEBAR ── */}
        <aside className="w-64 shrink-0">
          <div className="sticky top-24">
            <div className="bg-white rounded-2xl shadow-sm border border-slate-100 p-5">
              <div className="flex items-center justify-between mb-5">
                <h3 className="font-600 text-slate-800 text-sm flex items-center gap-2">
                  <svg className="w-4 h-4" style={{ color: "#00B4D8" }} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                  </svg>
                  Filtros
                </h3>
                {activeFilterCount > 0 && (
                  <button onClick={clearAll} className="text-xs font-medium hover:underline" style={{ color: "#FF7F50" }}>
                    Limpiar ({activeFilterCount})
                  </button>
                )}
              </div>

              {/* Sex */}
              <FilterSection title="Sexo">
                <div className="flex flex-wrap gap-2">
                  {["Femenino", "Masculino"].map((s) => (
                    <FilterChip
                      key={s}
                      label={s}
                      active={sexFilter === s}
                      onClick={() => setSexFilter(sexFilter === s ? null : s)}
                    />
                  ))}
                </div>
              </FilterSection>

              {/* Rating */}
              <FilterSection title="Calificación mínima">
                <div className="flex flex-wrap gap-2">
                  {[4, 4.5, 4.8].map((r) => (
                    <FilterChip
                      key={r}
                      label={`★ ${r}+`}
                      active={minRating === r}
                      onClick={() => setMinRating(minRating === r ? null : r)}
                    />
                  ))}
                </div>
              </FilterSection>

              {/* Experience */}
              <FilterSection title="Experiencia mínima">
                <div className="flex flex-wrap gap-2">
                  {[{ label: "2+ años", val: 2 }, { label: "5+ años", val: 5 }, { label: "10+ años", val: 10 }].map(({ label, val }) => (
                    <FilterChip
                      key={val}
                      label={label}
                      active={minExp === val}
                      onClick={() => setMinExp(minExp === val ? null : val)}
                    />
                  ))}
                </div>
              </FilterSection>

              {/* Specialty */}
              <FilterSection title="Especialidad">
                <div className="flex flex-col gap-2">
                  {specialties.map((sp) => (
                    <FilterChip
                      key={sp}
                      label={sp}
                      active={specialty === sp}
                      onClick={() => setSpecialty(specialty === sp ? null : sp)}
                    />
                  ))}
                </div>
              </FilterSection>

              {/* Rate */}
              <FilterSection title="Tarifa máxima (S//hr)">
                <div className="flex flex-wrap gap-2">
                  {[{ label: "Hasta S/15", val: 15 }, { label: "Hasta S/20", val: 20 }, { label: "Hasta S/25", val: 25 }].map(({ label, val }) => (
                    <FilterChip
                      key={val}
                      label={label}
                      active={maxRate === val}
                      onClick={() => setMaxRate(maxRate === val ? null : val)}
                    />
                  ))}
                </div>
              </FilterSection>

              {/* CTA sidebar */}
              <div className="mt-2 p-4 rounded-xl" style={{ background: "#E0F7FC" }}>
                <p className="text-xs font-600 text-slate-700 mb-1">¿Eres cuidador/a?</p>
                <p className="text-xs text-slate-500 mb-3">Únete a nuestra red y encuentra familias que te necesitan.</p>
                <button className="w-full text-xs font-600 py-2 rounded-lg text-white" style={{ background: "#00B4D8" }}>
                  Registrarme como cuidador
                </button>
              </div>
            </div>
          </div>
        </aside>

        {/* ── MAIN CONTENT ── */}
        <main className="flex-1 min-w-0">
          {/* Hero strip */}
          <div className="rounded-2xl overflow-hidden mb-6 relative" style={{ background: "linear-gradient(135deg, #00B4D8 0%, #0096B4 100%)" }}>
            <div className="px-8 py-7 relative z-10">
              <p className="text-white/70 text-sm font-medium mb-1">Encuentra hoy mismo</p>
              <h1 className="text-2xl font-700 text-white mb-2">Cuidadores verificados para tu familiar</h1>
              <p className="text-white/80 text-sm max-w-lg">Profesionales de confianza, disponibles en tu ciudad, con calificaciones reales de familias como la tuya.</p>
              <div className="flex items-center gap-6 mt-5">
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center">
                    <svg className="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clipRule="evenodd" />
                    </svg>
                  </div>
                  <span className="text-white text-sm font-medium">+1,200 cuidadores</span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center">
                    <svg className="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M6.267 3.455a3.066 3.066 0 001.745-.723 3.066 3.066 0 013.976 0 3.066 3.066 0 001.745.723 3.066 3.066 0 012.812 2.812c.051.643.304 1.254.723 1.745a3.066 3.066 0 010 3.976 3.066 3.066 0 00-.723 1.745 3.066 3.066 0 01-2.812 2.812 3.066 3.066 0 00-1.745.723 3.066 3.066 0 01-3.976 0 3.066 3.066 0 00-1.745-.723 3.066 3.066 0 01-2.812-2.812 3.066 3.066 0 00-.723-1.745 3.066 3.066 0 010-3.976 3.066 3.066 0 00.723-1.745 3.066 3.066 0 012.812-2.812zm7.44 5.252a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                    </svg>
                  </div>
                  <span className="text-white text-sm font-medium">100% verificados</span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center">
                    <svg className="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                  </div>
                  <span className="text-white text-sm font-medium">4.8 promedio</span>
                </div>
              </div>
            </div>
            {/* Decorative circles */}
            <div className="absolute right-0 top-0 w-48 h-48 rounded-full opacity-10" style={{ background: "#fff", transform: "translate(30%, -30%)" }} />
            <div className="absolute right-24 bottom-0 w-32 h-32 rounded-full opacity-10" style={{ background: "#fff", transform: "translateY(40%)" }} />
          </div>

          {/* Toolbar */}
          <div className="flex items-center justify-between mb-5">
            <p className="text-sm text-slate-500">
              <span className="font-600 text-slate-700">{filtered.length}</span> cuidadores disponibles
            </p>
            <div className="flex items-center gap-2">
              <span className="text-xs text-slate-400 font-medium">Ordenar por:</span>
              {(["rating", "rate", "experience"] as const).map((opt) => (
                <FilterChip
                  key={opt}
                  label={opt === "rating" ? "Mejor valorado" : opt === "rate" ? "Menor tarifa" : "Más experiencia"}
                  active={sortBy === opt}
                  onClick={() => setSortBy(opt)}
                />
              ))}
            </div>
          </div>

          {/* Grid */}
          {filtered.length > 0 ? (
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">
              {filtered.map((c) => (
                <CaregiverCard key={c.id} c={c} onSelect={setSelected} />
              ))}
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center py-20 text-center">
              <div className="w-16 h-16 rounded-full flex items-center justify-center mb-4" style={{ background: "#E0F7FC" }}>
                <svg className="w-8 h-8" style={{ color: "#00B4D8" }} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </div>
              <h3 className="font-600 text-slate-700 mb-1">Sin resultados</h3>
              <p className="text-sm text-slate-400 mb-4">Intenta ajustar los filtros para encontrar cuidadores disponibles.</p>
              <button onClick={clearAll} className="text-sm font-600 px-5 py-2 rounded-xl text-white" style={{ background: "#FF7F50" }}>
                Limpiar filtros
              </button>
            </div>
          )}
        </main>
      </div>
    </div>
    </>
  );
}
