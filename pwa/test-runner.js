const BASE = window.BASE_API || 'http://localhost:8080';
const logEl = document.getElementById('log');
function log(...args){
  const t = new Date().toLocaleTimeString();
  logEl.innerText += `[${t}] ` + args.map(a => typeof a === 'object' ? JSON.stringify(a,null,2) : String(a)).join(' ') + '\n\n';
  logEl.scrollTop = logEl.scrollHeight;
}

function clearLog(){ logEl.innerText = ''; }

async function req(path, opts={}){
  const url = BASE + path;
  try{
    const res = await fetch(url, opts);
    const text = await res.text();
    try{ const json = JSON.parse(text); return {ok: res.ok, status: res.status, body: json}; } catch(e){ return {ok: res.ok, status: res.status, body: text}; }
  } catch(e){ return {ok:false, status:0, body: String(e)} }
}

async function registerRandom(){
  const ts = Date.now();
  const name = `test_user_${ts}`;
  const email = `test_${ts}@example.com`;
  const body = {name, email, password: 'senha123'};
  log('Registering', body.email);
  const r = await req('/auth/register', {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)});
  log('/auth/register', r);
  return {name, email, password:'senha123', registerResp: r};
}

async function login(email,password){
  const r = await req('/auth/login', {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({email,password})});
  log('/auth/login', r);
  if (r.ok && r.body && r.body.token) return r.body; 
  return null;
}

async function getBooks(){ const r = await req('/books'); log('/books', r); return r; }
async function getMyLibrary(token){ const r = await req('/library/me', {headers:{Authorization:`Bearer ${token}`}}); log('/library/me', r); return r; }
async function setStatus(token, bookId, status){ const r = await req(`/library/books/${bookId}/status?status=${status}`, {method:'POST', headers:{Authorization:`Bearer ${token}`}}); log('setStatus', r); return r; }
async function createReview(token, bookId){ const body = {bookId, title:'Teste UI', text:'Resenha via test-runner', rating:5}; const r = await req('/reviews', {method:'POST', headers:{'Content-Type':'application/json', Authorization:`Bearer ${token}`}, body: JSON.stringify(body)}); log('/reviews POST', r); return r; }
async function getReviews(){ const r = await req('/reviews'); log('/reviews GET', r); return r; }
async function getUsers(){ const r = await req('/users'); log('/users GET', r); return r; }
async function searchUsers(q){ const r = await req(`/search?query=${encodeURIComponent(q)}`); log('/search', r); return r; }
async function followUser(token, id){ const r = await req(`/users/${id}/follow`, {method:'POST', headers:{Authorization:`Bearer ${token}`}}); log(`/users/${id}/follow`, r); return r; }
async function unfollowUser(token, id){ const r = await req(`/users/${id}/unfollow`, {method:'POST', headers:{Authorization:`Bearer ${token}`}}); log(`/users/${id}/unfollow`, r); return r; }
async function getFeed(){ const r = await req('/feed'); log('/feed GET', r); return r; }
async function createPost(token, content){ const r = await req('/feed', {method:'POST', headers:{'Content-Type':'application/json', Authorization:`Bearer ${token}`}, body: JSON.stringify({content})}); log('/feed POST', r); return r; }

document.addEventListener('DOMContentLoaded', ()=>{
  document.getElementById('btn-clear').onclick = clearLog;
  document.getElementById('btn-register').onclick = async ()=>{ const u = await registerRandom(); window._lastRegistered = u; };
  document.getElementById('btn-login').onclick = async ()=>{ if (!window._lastRegistered) { log('Crie um usuário primeiro (Registrar).'); return; } const data = await login(window._lastRegistered.email, window._lastRegistered.password); window._token = data ? data.token : null; window._me = data || null; };
  document.getElementById('btn-get-books').onclick = getBooks;
  document.getElementById('btn-my-library').onclick = ()=>{ if (!window._token) { log('Faça login primeiro.'); return; } getMyLibrary(window._token); };
  document.getElementById('btn-set-status').onclick = async ()=>{ if (!window._token) { log('Faça login primeiro.'); return; } const b = await getBooks(); const bookId = Array.isArray(b.body) && b.body.length>0 ? b.body[0].id : 1; await setStatus(window._token, bookId, 'TO_READ'); };
  document.getElementById('btn-create-review').onclick = async ()=>{ if (!window._token) { log('Faça login primeiro.'); return; } const b = await getBooks(); const bookId = Array.isArray(b.body) && b.body.length>0 ? b.body[0].id : 1; await createReview(window._token, bookId); };
  document.getElementById('btn-get-reviews').onclick = getReviews;
  document.getElementById('btn-get-users').onclick = getUsers;
  document.getElementById('btn-search-users').onclick = async ()=>{ const q = window._lastRegistered ? window._lastRegistered.name : 'test'; await searchUsers(q); };
  document.getElementById('btn-follow').onclick = async ()=>{
    if (!window._token) { log('Faça login primeiro.'); return; }
    const users = await getUsers();
    if (!users || !users.body || users.body.length<2) { log('Menos de 2 usuários no sistema — crie mais.'); return; }
    const meId = window._me ? window._me.id : null;
    const target = users.body.find(u=>u.id !== meId) || users.body[0];
    await followUser(window._token, target.id);
    await unfollowUser(window._token, target.id);
  };
  document.getElementById('btn-get-feed').onclick = getFeed;
  document.getElementById('btn-create-post').onclick = async ()=>{ if (!window._token) { log('Faça login primeiro.'); return; } await createPost(window._token, 'Post via test-runner ' + new Date().toISOString()); };

  document.getElementById('btn-run-all').onclick = async ()=>{
    clearLog();
    log('Iniciando execução completa.');
    const u = await registerRandom();
    window._lastRegistered = u;
    const me = await login(u.email, u.password);
    if (me) { window._token = me.token; window._me = me; }
    await getBooks();
    if (window._token) await getMyLibrary(window._token);
    if (window._token) {
      const b = await getBooks(); const bookId = Array.isArray(b.body)&&b.body.length?b.body[0].id:1;
      await setStatus(window._token, bookId, 'TO_READ');
      await setStatus(window._token, bookId, 'READING');
      await setStatus(window._token, bookId, 'READ');
      await createReview(window._token, bookId);
      await createPost(window._token, 'Post automático');
    }
    await getReviews();
    await getUsers();
    if (window._lastRegistered) await searchUsers(window._lastRegistered.name);
    await getFeed();
    log('Execução completa. Verifique as respostas acima.');
  };
});
