// api.js
const API_URL = "http://127.0.0.1:8080";
 // depois: https://api.SEUNOME.dns

// api.js - Padronizar tratamento de erro
async function handleResponse(res) {
    const data = await res.json();
    if (!res.ok) {
        // Extrair mensagem de erro do backend
        const message = data.message || data.error || JSON.stringify(data);
        throw new Error(message);
    }
    return data;
}

// Exemplo em apiLogin
async function apiLogin(email, password) {
    const res = await fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });
    return handleResponse(res);
}
async function fetchWithAuth(url, options = {}) {
    const res = await fetch(url, options);
    
    if (res.status === 401) {
        // Token expirado ou inválido
        localStorage.removeItem("token");
        alert("Sua sessão expirou. Faça login novamente.");
        window.location.reload();
        throw new Error("Não autorizado");
    }
    return res;
}
async function apiLogin(email, password) {
  const res = await fetch(`${API_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  const data = await res.json();

  if (!res.ok) {
    throw new Error(data.message || JSON.stringify(data) || "Erro ao fazer login");
  }
  return data;
}

// REGISTER
async function apiRegister(name, email, password) {
  console.log("=== API REGISTER CHAMADA ===");
  console.log("URL:", `${API_URL}/auth/register`);
  console.log("Body:", { name, email, password });
  
  try {
    console.log("Iniciando fetch...");
    const res = await fetch(`${API_URL}/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, email, password }),
    });

    console.log("Fetch completado, status:", res.status);
    console.log("Response headers:", res.headers);

    const data = await res.json();
    console.log("JSON parseado:", data);

    if (!res.ok) {
      throw new Error(data.message || JSON.stringify(data) || "Erro ao registrar");
    }
    return data;
  } catch (err) {
    console.error("=== ERRO EM API REGISTER ===");
    console.error("Mensagem:", err.message);
    console.error("Stack completo:", err.stack);
    throw err;
  }
}

// LIVROS
async function apiGetBooks(token) {
  const res = await fetch(`${API_URL}/books`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error("Erro ao carregar livros");
  return res.json(); // espera array de livros
}

// MINHA BIBLIOTECA (livros que marquei como lido/quer ler)
async function apiGetMyLibrary(token) {
  const res = await fetch(`${API_URL}/library/me`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error("Erro ao carregar sua biblioteca");
  return res.json();
}

// FEED
async function apiGetFeed(token) {
  const res = await fetch(`${API_URL}/feed`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error("Erro ao carregar feed");
  return res.json();
}

// CRIAR POST NO FEED
async function apiCreatePost(token, content) {
  const res = await fetch(`${API_URL}/feed`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ content }),
  });

  if (!res.ok) throw new Error("Erro ao criar post");
  return res.json();
}

// RESENHAS
async function apiGetReviews(token) {
  const res = await fetch(`${API_URL}/reviews`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error("Erro ao carregar resenhas");
  return res.json();
}

// CRIAR RESENHA
async function apiCreateReview(token, payload) {
  const res = await fetch(`${API_URL}/reviews`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  if (!res.ok) throw new Error("Erro ao criar resenha");
  return res.json();
}

// PERFIL
async function apiGetProfile(token) {
  const res = await fetch(`${API_URL}/users/me`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error("Erro ao carregar perfil");
  return res.json();
}

// PERFIL DE OUTRO USUÁRIO (público)
async function apiGetUserProfile(token, userId) {
  const res = await fetch(`${API_URL}/users/${userId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error("Erro ao carregar perfil do usuário");
  return res.json();
}

// BUSCAR USUÁRIOS
async function apiSearchUsers(token, query) {
  const res = await fetch(`${API_URL}/search?query=${encodeURIComponent(query)}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error("Erro ao buscar usuários");
  const data = await res.json();
  // SearchController returns { booksFound, usersFound }
  return data.usersFound || [];
}

// LISTAR TODOS OS USUÁRIOS
async function apiGetAllUsers(token) {
  const res = await fetch(`${API_URL}/users`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error("Erro ao carregar usuários");
  return res.json();
}

// RESENHAS DE UM USUÁRIO
async function apiGetUserReviews(token, userId) {
  const res = await fetch(`${API_URL}/reviews/user/${userId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error("Erro ao carregar resenhas do usuário");
  return res.json();
}

// Definir status de livro na minha biblioteca
// Definir status de livro na minha biblioteca
async function apiSetBookStatus(token, bookId, status) {
  console.log("=== apiSetBookStatus CHAMADA ===");
  console.log("Book:", bookId, "Status:", status);

  try {
    const url = `${API_URL}/library/books/${bookId}/status?status=${encodeURIComponent(status)}`;
    const res = await fetch(url, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json"
      },
    });

    console.log('apiSetBookStatus response status:', res.status);

    if (res.status === 204 || res.status === 200) {
      // No Content or OK
      // If 200, backend may return the updated object – parse if present
      if (res.status === 200) {
        try {
          const body = await res.json();
          console.log('apiSetBookStatus response body:', body);
          return body;
        } catch (e) {
          return { ok: true };
        }
      }
      return { ok: true };
    }

    // Try to parse error body for better message
    let errorText = `Status ${res.status}`;
    try {
      const data = await res.json();
      console.error('apiSetBookStatus error body:', data);
      errorText = data?.message || JSON.stringify(data) || errorText;
    } catch (e) {
      const text = await res.text();
      console.error('apiSetBookStatus error text:', text);
      errorText = text || errorText;
    }

    throw new Error(errorText);
  } catch (err) {
    console.error("ERRO apiSetBookStatus:", err);
    throw err;
  }
}
// NOVA: Apenas para enviar o progresso de páginas (Diário)
async function apiUpdateBookProgress(token, bookId, currentPage, totalPages) {
  try {
    // Note que a URL muda um pouco: /progress em vez de /status
    const url = `${API_URL}/library/books/${bookId}/progress`; 
    
    const res = await fetch(url, {
      method: "PUT", // PATCH é o ideal para atualizações parciais
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
    paginasLidas: parseInt(currentPage),   
    paginasTotais: parseInt(totalPages)   
})
    });

    if (!res.ok) throw new Error("Erro ao salvar páginas no diário.");
    return { ok: true };
  } catch (err) {
    console.error("ERRO apiUpdateBookProgress:", err);
    throw err;
  }
}

// Seguir / deixar de seguir
async function apiFollowUser(token, userId) {
  const res = await fetch(`${API_URL}/users/${userId}/follow`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  if (!res.ok) throw new Error("Erro ao seguir usuário");
  return res.json();
}

async function apiUnfollowUser(token, userId) {
  const res = await fetch(`${API_URL}/users/${userId}/unfollow`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  if (!res.ok) throw new Error("Erro ao deixar de seguir usuário");
  return res.json();
}
