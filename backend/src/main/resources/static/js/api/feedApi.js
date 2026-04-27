// API de Feed e Posts

async function apiGetFeed(token) {
  const res = await fetch(`${API_URL}/feed`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao carregar feed");
  return res.json();
}

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

async function apiDeletePost(token, postId) {
  const res = await fetch(`${API_URL}/feed/${postId}`, {
    method: "DELETE",
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao deletar post");
  return res.json();
}