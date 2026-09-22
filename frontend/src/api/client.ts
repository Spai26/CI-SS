export class ApiError extends Error {
  status: number;
  data: any;

  constructor(status: number, message: string, data?: any) {
    super(message);
    this.status = status;
    this.data = data;
  }
}

export async function apiClient<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const token = localStorage.getItem("jwt_token");
  
  const headers = new Headers(options.headers || {});
  
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }
  
  if (!(options.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }

  const config: RequestInit = {
    ...options,
    headers,
  };

  const response = await fetch(`/api/v1${endpoint}`, config);

  if (!response.ok) {
    let errorData;
    try {
      errorData = await response.json();
    } catch {
      errorData = null;
    }

    if (response.status === 401) {
      // Si el token expiró o es inválido, limpiamos localStorage
      localStorage.removeItem("jwt_token");
      localStorage.removeItem("user_data");
      window.location.href = "/login"; // Redirigir usando window para forzar recarga de estado
    }

    const message = errorData?.detail || errorData?.message || "Ocurrió un error en la solicitud";
    throw new ApiError(response.status, message, errorData);
  }

  if (response.status === 204) {
    return null as unknown as T;
  }

  return response.json();
}
