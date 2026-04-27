// Módulo de Perfil

async function carregarPerfil() {
  const token = getToken();
  if (!token) return;

  try {
    const profile = await apiGetProfile(token);
    const stats = await apiGetUserStats(token);

    document.getElementById("profile-name").textContent = profile.name;
    document.getElementById("profile-bio").textContent = profile.bio || "Um apreciador de histórias...";
    document.getElementById("profile-email").textContent = profile.email;
    document.getElementById("profile-join-date").textContent = formatDate(profile.joinDate);

    if (profile.profileImage) {
      document.getElementById("profile-avatar").src = profile.profileImage;
    }

    document.getElementById("followers-count").textContent = stats.followers || 0;
    document.getElementById("following-count").textContent = stats.following || 0;
    document.getElementById("books-read-count").textContent = stats.booksRead || 0;
    document.getElementById("pages-read-count").textContent = stats.pagesRead || 0;

    await showUserReviews();
  } catch (err) {
    console.error("Erro ao carregar perfil:", err);
  }
}

function setupProfileImageUpload() {
  const changeBtn = document.getElementById("change-avatar-btn");
  const uploadInput = document.getElementById("avatar-upload");

  changeBtn?.addEventListener("click", () => uploadInput?.click());
  uploadInput?.addEventListener("change", async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = async (event) => {
      try {
        await apiUpdateProfileImage(getToken(), event.target.result);
        document.getElementById("profile-avatar").src = event.target.result;
        alert("Foto atualizada com sucesso!");
      } catch (err) {
        alert("Erro ao atualizar foto");
      }
    };
    reader.readAsDataURL(file);
  });
}

async function showUserReviews() {
  const token = getToken();
  const profile = await apiGetProfile(token);
  const reviews = await apiGetUserReviews(token, profile.id);

  const container = document.getElementById("profile-content");
  if (!reviews.length) {
    container.innerHTML = '<p class="info">Você ainda não escreveu nenhuma resenha.</p>';
    return;
  }

  container.innerHTML = reviews.map(review => `
    <div class="review-card">
      <h4>${review.title}</h4>
      <div class="review-rating">${'★'.repeat(review.rating)}${'☆'.repeat(5 - review.rating)}</div>
      <p>${review.comment || review.text}</p>
      <em>Sobre: ${review.book?.title || "Livro"}</em>
    </div>
  `).join("");
}

async function showFollowers() {
  const token = getToken();
  const profile = await apiGetProfile(token);
  const followers = await apiGetFollowers(token, profile.id);

  const modalList = document.getElementById("modal-list");
  modalList.innerHTML = followers.map(user => `
    <div class="modal-user-item" onclick="verPerfil(${user.id})">
      <img src="${user.profileImage || "default-avatar.svg"}" class="modal-user-avatar">
      <div><strong>${user.name}</strong></div>
    </div>
  `).join("");

  document.getElementById("modal-title").textContent = "Seguidores";
  document.getElementById("followers-modal").classList.remove("hidden");
}