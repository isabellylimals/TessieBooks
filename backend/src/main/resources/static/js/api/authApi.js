// API de Autenticação
const API_URL = "http://127.0.0.1:8080";

async function handleResponse(res) {
  const data = await res.json();
  if (!res.ok) {
    const message = data.message || data.error || JSON.stringify(data);
    throw new Error(message);
  }
  return data;
}

async function apiLogin(email, password) {
  const res = await fetch(`${API_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });
  return handleResponse(res);
}

async function apiRegister(name, email, password) {
  const res = await fetch(`${API_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, email, password }),
  });
  return handleResponse(res);
}