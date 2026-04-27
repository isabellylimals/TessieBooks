// Módulo de Resenhas

let reviewStarsElements = [];
let reviewRatingValue = 5;

async function carregarResenhas() {
  const token = getToken();
  if (!token) return;

  const container = document.getElementById("reviews-list");
  if (!container) return;

  container.innerHTML = "<p>Carregando resenhas...</p>";

  try {
    const reviews = await apiGetReviews(token);

    if (!reviews || reviews.length === 0) {
      container.innerHTML = "<p class='info'>Ainda não há resenhas. Seja o primeiro a escrever.</p>";
      return;
    }

    container.innerHTML = "";
    reviews.forEach((review) => {
      container.innerHTML += `
        <div class="review-card">
          <h4>${escapeHtml(review.title || "Resenha sem título")}</h4>
          <div class="review-rating">${"★".repeat(review.rating)}${"☆".repeat(5 - review.rating)}</div>
          <p>${escapeHtml(review.comment || review.text || "")}</p>
          <em>Sobre: ${escapeHtml(review.book?.title || "Livro")}</em>
          <div class="review-author">👤 ${escapeHtml(review.user?.name || "Leitor misterioso")}</div>
          <button class="button small-btn like-btn" data-review="${review.id}">❤️ ${review.likes?.length || 0} curtidas</button>
        </div>`;
    });

    document.querySelectorAll(".like-btn").forEach(btn => {
      btn.addEventListener("click", async (e) => {
        const reviewId = btn.dataset.review;
        await apiLikeReview(getToken(), reviewId);
        await carregarResenhas();
      });
    });
  } catch (err) {
    console.error(err);
    container.innerHTML = "<p class='error'>Erro ao carregar resenhas.</p>";
  }
}

async function criarResenha(bookId, title, text, rating, imageFile) {
  const token = getToken();

  if (!bookId || !title || !text) {
    alert("Preencha todos os campos da resenha.");
    return false;
  }

  try {
    let imageUrl = null;
    if (imageFile) {
      const reader = new FileReader();
      imageUrl = await new Promise((resolve, reject) => {
        reader.onload = () => resolve(reader.result);
        reader.onerror = reject;
        reader.readAsDataURL(imageFile);
      });
    }

    await apiCreateReview(token, {
      bookId: Number(bookId),
      title,
      text,
      rating,
      imageUrl,
    });
    await carregarResenhas();
    return true;
  } catch (err) {
    alert("Erro ao publicar resenha: " + err.message);
    return false;
  }
}

function setupReviewStars() {
  const stars = document.querySelectorAll(".stars-container .star");
  const ratingInput = document.getElementById("review-rating");

  stars.forEach((star) => {
    star.addEventListener("click", () => {
      const value = parseInt(star.dataset.value);
      ratingInput.value = value;
      stars.forEach((s) => {
        if (parseInt(s.dataset.value) <= value) {
          s.classList.add("active");
        } else {
          s.classList.remove("active");
        }
      });
    });
  });

  // Inicializar com 5 estrelas
  if (ratingInput) ratingInput.value = 5;
  stars.forEach((s) => {
    if (parseInt(s.dataset.value) <= 5) s.classList.add("active");
  });
}

function setupReviewForm() {
  const form = document.getElementById("review-form");
  const bookIdInput = document.getElementById("review-book-id");
  const titleInput = document.getElementById("review-title");
  const textInput = document.getElementById("review-text");
  const ratingInput = document.getElementById("review-rating");
  const imageInput = document.getElementById("review-image");

  form?.addEventListener("submit", async (e) => {
    e.preventDefault();
    const success = await criarResenha(
      bookIdInput?.value,
      titleInput?.value,
      textInput?.value,
      parseInt(ratingInput?.value || 5),
      imageInput?.files[0]
    );
    if (success && form) form.reset();
    if (ratingInput) ratingInput.value = 5;
    document.querySelectorAll(".stars-container .star").forEach((s, i) => {
      if (i < 5) s.classList.add("active");
      else s.classList.remove("active");
    });
  });
}