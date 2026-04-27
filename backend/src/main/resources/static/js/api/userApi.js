// API de Usuário e Perfil

async function apiGetProfile(token) {
  const res = await fetch(`${API_URL}/users/me`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao carregar perfil");
  return res.json();
}

async function apiGetUserStats(token) {
  const res = await fetch(`${API_URL}/users/me/stats`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao carregar estatísticas");
  return res.json();
}

async function apiUpdateProfileImage(token, imageUrl) {
  const res = await fetch(`${API_URL}/users/me/profile-image`, {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ profileImage: imageUrl }),
  });
  if (!res.ok) throw new Error("Erro ao atualizar foto");
  return res.json();
}

async function apiFollowUser(token, userId) {
  const res = await fetch(`${API_URL}/users/${userId}/follow`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || "Erro ao seguir usuário");
  }
  return res.json();
}

async function apiUnfollowUser(token, userId) {
  const res = await fetch(`${API_URL}/users/${userId}/unfollow`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });
  if (!res.ok) {
    const error = await res.text();
    throw new Error(error || "Erro ao deixar de seguir");
  }
  return res.json();
}

async function apiGetFollowers(token, userId) {
  const res = await fetch(`${API_URL}/users/${userId}/followers`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao carregar seguidores");
  return res.json();
}

async function apiGetFollowing(token, userId) {
  const res = await fetch(`${API_URL}/users/${userId}/following`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao carregar seguindo");
  return res.json();
}

async function apiGetUserProfile(token, userId) {
  const res = await fetch(`${API_URL}/users/${userId}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error("Erro ao carregar perfil do usuário");
  return res.json();
}