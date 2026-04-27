// API de Resenhas

async function apiGetReviews(token) {
  const res = await fetch(`${API_URL}/reviews`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao carregar resenhas");
  return res.json();
}

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

async function apiDeleteReview(token, reviewId) {
  const res = await fetch(`${API_URL}/reviews/${reviewId}`, {
    method: "DELETE",
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao deletar resenha");
  return res.json();
}

async function apiLikeReview(token, reviewId) {
  const res = await fetch(`${API_URL}/reviews/${reviewId}/like`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao curtir resenha");
  return res.json();
}

async function apiGetUserReviews(token, userId) {
  const res = await fetch(`${API_URL}/reviews/user/${userId}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao carregar resenhas do usuário");
  return res.json();
}

async function apiGetBookReviews(token, bookId) {
  const res = await fetch(`${API_URL}/reviews/book/${bookId}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao carregar resenhas do livro");
  return res.json();
}