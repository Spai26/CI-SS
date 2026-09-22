const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch({
    headless: "new",
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });
  const page = await browser.newPage();
  
  page.on('console', msg => console.log('BROWSER CONSOLE:', msg.text()));
  page.on('pageerror', err => console.error('BROWSER ERROR:', err.toString()));
  
  await page.evaluateOnNewDocument(() => {
    window.addEventListener('error', event => {
      console.log('UNHANDLED ERROR:', event.message, event.error?.stack);
    });
    window.addEventListener('unhandledrejection', event => {
      console.log('UNHANDLED REJECTION:', event.reason);
    });
  });
  
  console.log('Navigating to http://localhost:5173/ ...');
  await page.goto('http://localhost:5173/');
  
  // mock local storage
  await page.evaluate(() => {
    localStorage.setItem("jwt_token", "fake-token");
    localStorage.setItem("user_data", JSON.stringify({
      id: 1, email: "test@test.com", nombre: "Test", apellido: "Test", telefono: "123", estado: "C", emailVerificado: true
    }));
  });
  
  console.log('Navigating to /dashboard-cuidador');
  await page.goto('http://localhost:5173/dashboard-cuidador', { waitUntil: 'networkidle2' });
  
  console.log('Taking screenshot...');
  await page.screenshot({ path: 'screenshot.png' });
  
  await browser.close();
})();
