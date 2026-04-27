// API de Livros e Biblioteca

async function apiGetBooks(token) {
  const res = await fetch(`${API_URL}/books`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao carregar livros");
  return res.json();
}

async function apiGetMyLibrary(token) {
  const res = await fetch(`${API_URL}/library/me`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao carregar sua biblioteca");
  return res.json();
}

async function apiSetBookStatus(token, bookId, status) {
  const url = `${API_URL}/library/books/${bookId}/status?status=${encodeURIComponent(status)}`;
  const res = await fetch(url, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });

  if (!res.ok) {
    let errorText = `Status ${res.status}`;
    try {
      const data = await res.json();
      errorText = data?.message || JSON.stringify(data) || errorText;
    } catch (e) {
      const text = await res.text();
      errorText = text || errorText;
    }
    throw new Error(errorText);
  }
  return res.status === 200 ? await res.json() : { ok: true };
}

async function apiUpdateBookProgress(token, bookId, paginasLidas, paginasTotais) {
  const res = await fetch(`${API_URL}/library/books/${bookId}/progress`, {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      paginasLidas: parseInt(paginasLidas),
      paginasTotais: parseInt(paginasTotais),
    }),
  });
  if (!res.ok) throw new Error("Erro ao salvar páginas no diário.");
  return { ok: true };
}