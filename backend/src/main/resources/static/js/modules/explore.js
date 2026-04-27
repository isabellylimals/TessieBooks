// Módulo de Explorar (Comunidade)

async function carregarComunidade() {
  const token = getToken();
  if (!token) return;

  const container = document.getElementById("users-list");
  if (!container) return;

  container.innerHTML = "<p>Carregando comunidade...</p>";

  try {
    const usuarios = await apiGetAllUsers(token);
    const currentUser = await apiGetProfile(token);

    if (!usuarios || usuarios.length === 0) {
      container.innerHTML = "<p class='info'>Nenhum outro leitor encontrado.</p>";
      return;
    }

    container.innerHTML = "";
    usuarios.forEach((user) => {
      if (user.id === currentUser.id) return;

      const isFollowing = currentUser.following?.some(f => f.id === user.id);

      container.innerHTML += `
        <div class="user-card">
          <div class="user-header">
            <h3 class="user-name">${escapeHtml(user.name)}</h3>
            <div>
              <button class="button secondary-btn" onclick="verPerfil(${user.id})">Ver Perfil</button>
              <button class="button follow-btn" data-user="${user.id}">${isFollowing ? "Seguindo" : "Seguir"}</button>
            </div>
          </div>
          <div class="user-bio">${escapeHtml(user.bio || "Um apreciador de histórias.")}</div>
        </div>`;
    });

    document.querySelectorAll(".follow-btn").forEach(btn => {
      btn.addEventListener("click", async () => {
        const userId = btn.dataset.user;
        const isFollowing = btn.textContent === "Seguindo";
        
        try {
          if (isFollowing) {
            await apiUnfollowUser(getToken(), userId);
            btn.textContent = "Seguir";
          } else {
            await apiFollowUser(getToken(), userId);
            btn.textContent = "Seguindo";
          }
        } catch (err) {
          alert(err.message);
        }
      });
    });
  } catch (err) {
    console.error(err);
    container.innerHTML = "<p class='error'>Erro ao carregar comunidade.</p>";
  }
}

async function buscarUsuarios(query) {
  const token = getToken();
  if (!token) return;

  const container = document.getElementById("users-list");
  if (!query) {
    await carregarComunidade();
    return;
  }

  container.innerHTML = "<p>Buscando...</p>";

  try {
    const usuarios = await apiSearchUsers(token, query);
    const currentUser = await apiGetProfile(token);

    if (!usuarios || usuarios.length === 0) {
      container.innerHTML = `<p class='info'>Nenhum leitor encontrado com "${query}".</p>`;
      return;
    }

    container.innerHTML = "";
    usuarios.forEach((user) => {
      if (user.id === currentUser.id) return;

      const isFollowing = currentUser.following?.some(f => f.id === user.id);

      container.innerHTML += `
        <div class="user-card">
          <div class="user-header">
            <h3 class="user-name">${escapeHtml(user.name)}</h3>
            <div>
              <button class="button secondary-btn" onclick="verPerfil(${user.id})">Ver Perfil</button>
              <button class="button follow-btn" data-user="${user.id}">${isFollowing ? "Seguindo" : "Seguir"}</button>
            </div>
          </div>
          <div class="user-bio">${escapeHtml(user.bio || "Um apreciador de histórias.")}</div>
        </div>`;
    });

    document.querySelectorAll(".follow-btn").forEach(btn => {
      btn.addEventListener("click", async () => {
        const userId = btn.dataset.user;
        const isFollowing = btn.textContent === "Seguindo";
        
        try {
          if (isFollowing) {
            await apiUnfollowUser(getToken(), userId);
            btn.textContent = "Seguir";
          } else {
            await apiFollowUser(getToken(), userId);
            btn.textContent = "Seguindo";
          }
        } catch (err) {
          alert(err.message);
        }
      });
    });
  } catch (err) {
    console.error(err);
    container.innerHTML = "<p class='error'>Erro ao buscar usuários.</p>";
  }
}

function setupSearchUsers() {
  const input = document.getElementById("search-users-input");
  const btn = document.getElementById("search-users-btn");

  btn?.addEventListener("click", () => buscarUsuarios(input?.value.trim()));
  input?.addEventListener("keypress", (e) => {
    if (e.key === "Enter") buscarUsuarios(input.value.trim());
  });
}