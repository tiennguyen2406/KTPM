const BASE='';
let cart=[], allFoods=[];

// ===== FOOD CATALOG =====
async function loadFoods(){
  try{ const r=await fetch(BASE+'/api/demo/foods'); allFoods=await r.json(); renderFoods(allFoods); renderCategories(); }
  catch(e){ document.getElementById('foodGrid').innerHTML='<p style="color:var(--red)">Không load được</p>'; }
}
function renderCategories(){
  const cats=[...new Set(allFoods.map(f=>f.category))];
  const bar=document.getElementById('catBar');
  bar.innerHTML='<button class="cat-btn active" onclick="filterCat(this,\'all\')">Tất cả</button>';
  cats.forEach(c=>{ bar.innerHTML+=`<button class="cat-btn" onclick="filterCat(this,'${c}')">${c}</button>`; });
}
function filterCat(el,cat){
  document.querySelectorAll('.cat-btn').forEach(b=>b.classList.remove('active')); el.classList.add('active');
  renderFoods(cat==='all'?allFoods:allFoods.filter(f=>f.category===cat));
}
function renderFoods(foods){
  document.getElementById('foodGrid').innerHTML=foods.map(f=>`
    <div class="food-card"><div class="food-emoji">${f.image}</div>
    <div class="food-name">${f.name}</div><div class="food-desc">${f.description}</div>
    <div class="food-bottom"><div class="food-price">${Number(f.price).toLocaleString('vi-VN')}đ</div>
    <div class="qty-ctrl"><button onclick="changeQty(${f.id},-1)">−</button><span id="qty-${f.id}">1</span><button onclick="changeQty(${f.id},1)">+</button></div></div>
    <button class="add-btn" onclick="addToCart(${f.id})">🛒 Thêm vào giỏ</button></div>`).join('');
}
function changeQty(id,d){ const el=document.getElementById('qty-'+id); el.textContent=Math.max(1,parseInt(el.textContent)+d); }
function addToCart(id){
  const food=allFoods.find(f=>f.id===id), qty=parseInt(document.getElementById('qty-'+id).textContent);
  const ex=cart.find(c=>c.id===id); if(ex) ex.qty+=qty; else cart.push({...food,qty});
  renderCart(); showToast(`Đã thêm ${food.name} x${qty}`);
}
function renderCart(){
  const el=document.getElementById('cartItems'), badge=document.getElementById('cartBadge'), total=document.getElementById('cartTotal');
  badge.textContent=cart.reduce((s,c)=>s+c.qty,0);
  if(!cart.length){ el.innerHTML='<p class="cart-empty">Giỏ hàng trống</p>'; total.textContent='0đ'; return; }
  el.innerHTML=cart.map((c,i)=>`<div class="cart-item"><span>${c.image} ${c.name} x${c.qty}</span><span style="color:var(--green)">${(c.price*c.qty).toLocaleString('vi-VN')}đ <button onclick="removeCart(${i})" style="background:none;border:none;color:var(--red);cursor:pointer">✕</button></span></div>`).join('');
  total.textContent=cart.reduce((s,c)=>s+c.price*c.qty,0).toLocaleString('vi-VN')+'đ';
}
function removeCart(i){ cart.splice(i,1); renderCart(); }
function clearCart(){ cart=[]; renderCart(); }

// ===== PLACE ORDER (through Resilience4j) =====
async function placeOrder(){
  if(!cart.length){ showToast('Giỏ hàng trống!'); return; }
  const items=cart.map(c=>({foodId:c.id, quantity:c.qty}));
  appendLog('→ Đặt hàng '+cart.length+' món qua CircuitBreaker + Retry + RateLimiter...','log-info');
  try{
    const r=await fetch(BASE+'/api/demo/order',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({items})});
    const d=await r.json();
    if(d.status==='SUCCESS'){
      appendLog(`✅ ĐẶT HÀNG THÀNH CÔNG! Order #${d.orderId} - Tổng: ${Number(d.totalAmount).toLocaleString('vi-VN')}đ`,'log-success');
      appendLog(`   Patterns: ${d.patterns}`, 'log-success');
      cart=[]; renderCart(); showToast('Đặt hàng thành công! #'+d.orderId);
    } else if(d.status==='RATE_LIMITED'){
      appendLog(`🚫 RATE LIMITED: ${d.message}`,'log-warn');
      showToast('Quá nhiều request!');
    } else {
      appendLog(`⚠️ FALLBACK [${d.errorType}]: ${d.message}`,'log-error');
      appendLog(`   Hint: ${d.hint} | Calls: ${d.callCount}, Retries: ${d.retryCount}`,'log-error');
      showToast('Đặt hàng thất bại - Service lỗi');
    }
  }catch(e){ appendLog(`❌ ERROR: ${e.message}`,'log-error'); }
  refreshMonitor();
}

// ===== BURST TEST =====
async function burstOrder(count){
  appendLog(`⚡ BURST: Gửi ${count} đơn hàng đồng thời (test RateLimiter)...`,'log-warn');
  const body=JSON.stringify({items:[{foodId:1,quantity:1}]});
  const ps=[];
  for(let i=0;i<count;i++){
    ps.push(fetch(BASE+'/api/demo/order',{method:'POST',headers:{'Content-Type':'application/json'},body})
      .then(r=>r.json()).then(d=>{
        if(d.status==='SUCCESS') appendLog(`✅ [${i+1}/${count}] Order #${d.orderId} OK`,'log-success');
        else if(d.status==='RATE_LIMITED') appendLog(`🚫 [${i+1}/${count}] RATE LIMITED`,'log-warn');
        else appendLog(`⚠️ [${i+1}/${count}] FALLBACK: ${d.message}`,'log-error');
      }).catch(e=>appendLog(`❌ [${i+1}/${count}] ${e.message}`,'log-error')));
  }
  await Promise.all(ps);
  appendLog(`📊 BURST ${count} đơn hoàn tất`,'log-info');
  refreshMonitor();
}

// ===== FAILURE TOGGLE =====
async function toggleFailure(on){
  try{
    const r=await fetch(BASE+'/api/demo/toggle-failure',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({simulate:on})});
    const d=await r.json();
    updateFailureUI(d.simulateFailure);
    appendLog(d.simulateFailure?'🔴 Lỗi giả lập: BẬT - Đơn hàng sẽ thất bại':'🟢 Lỗi giả lập: TẮT - Đơn hàng bình thường', d.simulateFailure?'log-error':'log-success');
  }catch(e){}
}
async function resetDemo(){
  try{ await fetch(BASE+'/api/demo/reset',{method:'POST'}); updateFailureUI(false); clearLog(); appendLog('🔄 Đã reset tất cả','log-info'); }catch(e){}
}
function updateFailureUI(on){
  const el=document.getElementById('failureStatus');
  el.textContent=on?'BẬT 🔴':'TẮT 🟢'; el.style.color=on?'#ef4444':'#10b981';
}

// ===== LOG =====
function ts(){ return new Date().toLocaleTimeString('vi-VN',{hour12:false})+'.'+String(Date.now()%1000).padStart(3,'0'); }
function appendLog(msg,cls='log-info'){
  const el=document.getElementById('logArea');
  if(el.querySelector('.log-muted:only-child')) el.innerHTML='';
  el.innerHTML+=`<span class="${cls}">[${ts()}] ${msg}</span>\n`; el.scrollTop=el.scrollHeight;
}
function clearLog(){ document.getElementById('logArea').innerHTML='<span class="log-muted">Sẵn sàng nhận log...</span>'; }

// ===== MONITOR =====
async function refreshMonitor(){
  try{
    const r=await fetch(BASE+'/actuator/health'); if(!r.ok)return; const h=await r.json();
    if(h.components?.circuitBreakers){
      for(const[n,info]of Object.entries(h.components.circuitBreakers.details)){
        const el=document.getElementById('m-cb-'+n);
        if(el){ el.textContent=info.details.state; el.className='metric-value '+(info.details.state==='CLOSED'?'closed':info.details.state==='OPEN'?'open':'half'); }
        const det=document.getElementById('m-det-'+n);
        if(det) det.textContent=`fail=${info.details.failedCalls} buf=${info.details.bufferedCalls} rate=${info.details.failureRate}`;
      }
    }
    // Status
    const s=await fetch(BASE+'/api/demo/status').then(r=>r.json()).catch(()=>null);
    if(s){ document.getElementById('callCount').textContent=s.callCount; updateFailureUI(s.simulateFailure); }
  }catch(e){}
}

function showToast(msg){ const t=document.getElementById('toast'); t.textContent=msg; t.classList.add('show'); setTimeout(()=>t.classList.remove('show'),2000); }

// Init
document.addEventListener('DOMContentLoaded',()=>{ loadFoods(); renderCart(); refreshMonitor(); setInterval(refreshMonitor,4000); });
