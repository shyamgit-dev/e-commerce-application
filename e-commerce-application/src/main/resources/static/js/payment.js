async function payNow(){
const jwt=document.getElementById('jwt').value;
const id=Number(document.getElementById('orderId').value);
const btn=document.getElementById('payBtn');btn.disabled=true;btn.innerText='Processing...';
try{
let r=await fetch('http://localhost:8080/api/payments/create-order',{method:'POST',headers:{'Content-Type':'application/json','Authorization':'Bearer '+jwt},body:JSON.stringify({orderId:id})});
if(!r.ok){let e=await r.json();location='failure.html?msg='+encodeURIComponent(e.message);return;}
let o=await r.json();
new Razorpay({key:'rzp_test_TC9bGh8pw7xC0e',amount:o.amount*100,currency:o.currency,order_id:o.id,name:'E-Commerce Application',
handler:async function(resp){
let vr=await fetch('http://localhost:8080/api/payments/verify',{method:'POST',headers:{'Content-Type':'application/json','Authorization':'Bearer '+jwt},
body:JSON.stringify({razorpayOrderId:resp.razorpay_order_id,razorpayPaymentId:resp.razorpay_payment_id,razorpaySignature:resp.razorpay_signature})});
if(vr.ok){location='success.html?paymentId='+resp.razorpay_payment_id+'&orderId='+resp.razorpay_order_id;}
else{let e=await vr.json();location='failure.html?msg='+encodeURIComponent(e.message);}}
}).open();
}catch(e){location='failure.html?msg='+encodeURIComponent('Unable to connect');}
finally{btn.disabled=false;btn.innerText='💳 Proceed to Payment';}}