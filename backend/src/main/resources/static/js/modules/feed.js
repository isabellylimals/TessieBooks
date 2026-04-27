// Módulo do Feed

async function carregarFeed() {
  const token = getToken();
  if (!token) return;

  const container = document.getElementById("feed-list");
  if (!container) return;

  container.innerHTML = "<p>Carregando feed...</p>";

  try {
    const posts = await apiGetFeed(token);

    if (!posts || posts.length === 0) {
      container.innerHTML = "<p class='info'>O salão está silencioso... nenhuma resenha ainda.</p>";
      return;
    }

    container.innerHTML = "";
    posts.forEach((post) => {
      const isOwnPost = post.user?.id === JSON.parse(atob(getToken().split(".")[1]))?.sub;
      container.innerHTML += `
        <div class="feed-card">
          <div class="item-header">
            <h3 class="item-title">${escapeHtml(post.title || "Resenha sem título")}</h3>
            <span class="item-meta">👤 ${escapeHtml(post.user?.name || "Leitor misterioso")}</span>
          </div>
          ${post.rating ? `<div class="item-rating">${"★".repeat(post.rating)}${"☆".repeat(5 - post.rating)}</div>` : ""}
          <div class="item-body">${escapeHtml(post.text || post.comment || post.content || "")}</div>
          ${post.imageUrl ? `<img src="${post.imageUrl}" alt="Imagem" style="max-width: 100%; border-radius: 8px; margin-top: 10px;">` : ""}
          <div class="item-footer">📖 ${escapeHtml(post.book?.title || "Livro")}</div>
          ${isOwnPost ? `<button class="button small-btn" onclick="deletarPost(${post.id})" style="margin-top: 10px;">🗑️ Deletar</button>` : ""}
        </div>`;
    });
  } catch (err) {
    console.error(err);
    container.innerHTML = "<p class='error'>Erro ao carregar feed.</p>";
  }
}

async function criarPost(content) {
  const token = getToken();
  if (!token) return;

  if (!content.trim()) {
    alert("Escreva algo para compartilhar.");
    return false;
  }

  try {
    await apiCreatePost(token, content);
    await carregarFeed();
    return true;
  } catch (err) {
    alert("Erro ao publicar post: " + err.message);
    return false;
  }
}

async function deletarPost(postId) {
  const token = getToken();
  if (!confirm("Tem certeza que deseja deletar este post?")) return;

  try {
    await apiDeletePost(token, postId);
    await carregarFeed();
  } catch (err) {
    alert("Erro ao deletar post: " + err.message);
  }
}

function setupPostForm() {
  const form = document.getElementById("post-form");
  const textarea = document.getElementById("post-content");

  form?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const success = await criarPost(textarea?.value);
    if (success && textarea) textarea.value = "";
  });
}