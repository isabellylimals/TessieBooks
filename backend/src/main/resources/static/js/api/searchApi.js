// API de Busca

async function apiSearchUsers(token, query) {
  const res = await fetch(`${API_URL}/search?query=${encodeURIComponent(query)}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao buscar usuários");
  const data = await res.json();
  return data.usersFound || [];
}

async function apiSearchBooks(token, query) {
  const res = await fetch(`${API_URL}/search?query=${encodeURIComponent(query)}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao buscar livros");
  const data = await res.json();
  return data.booksFound || [];
}

async function apiGetAllUsers(token) {
  const res = await fetch(`${API_URL}/users`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao carregar usuários");
  return res.json();
}